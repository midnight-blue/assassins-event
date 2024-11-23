package de.midnightblue.assassin.aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

import de.midnightblue.assassin.aggregate.domain.Circle;
import de.midnightblue.assassin.aggregate.domain.Player;
import de.midnightblue.assassin.aggregate.domain.Round;
import de.midnightblue.assassin.commands.AddCircleMemberCommand;
import de.midnightblue.assassin.commands.CancelRoundCommand;
import de.midnightblue.assassin.commands.CreateCircleCommand;
import de.midnightblue.assassin.commands.EliminateVictimCommand;
import de.midnightblue.assassin.commands.RemoveCircleMemberCommand;
import de.midnightblue.assassin.commands.StartRoundCommand;
import de.midnightblue.assassin.events.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class CircleAggregate {

  protected CircleAggregate() {}

  @AggregateIdentifier // 1.
  private String id;

  private Circle circle;

  @CommandHandler
  public CircleAggregate(CreateCircleCommand createCircleCommand) {
    apply(
        new CircleCreatedEvent(
            createCircleCommand.circleId(),
            createCircleCommand.circleName(),
            createCircleCommand.creatorName(),
            createCircleCommand.seed()));
  }

  @CommandHandler
  public void on(StartRoundCommand startRoundCommand) {
    if (!circle.canStartRound()) {
      throw new IllegalStateException("Cannot start round");
    }
    apply(new RoundStartedEvent(this.id));
  }

  @CommandHandler
  public void on(AddCircleMemberCommand addCircleMemberCommand) {
    if (circle == null || circle.containsMember(addCircleMemberCommand.userId())) {
      throw new IllegalStateException("Cannot add member");
    }
    apply(new CircleMemberAddedEvent(id, new Player(addCircleMemberCommand.userId())));
  }

  @CommandHandler
  public void on(RemoveCircleMemberCommand removeCircleMemberCommand) {
    if (circle == null
        || removeCircleMemberCommand.userId() == circle.getCreator().playerId()
        || !circle.containsMember(removeCircleMemberCommand.userId())) {
      throw new IllegalStateException("Cannot remove member");
    }
    apply(new CircleMemberRemovedEvent(id, new Player(removeCircleMemberCommand.userId())));
  }

  @CommandHandler
  public void on(EliminateVictimCommand eliminateVictimCommand) {
    var nextVictim = circle.getNextVictim(eliminateVictimCommand.killerId());
    if (nextVictim.isEmpty()) {
      throw new IllegalStateException("Cannot eliminate victim");
    }

    apply(new PlayerEliminated(id, nextVictim.get(), eliminateVictimCommand.killerId()));
  }

  @CommandHandler
  public void on(CancelRoundCommand cancelRoundCommand) {
    if (!circle.getCreator().playerId().equals(cancelRoundCommand.userId())) {
      throw new IllegalStateException("Cancelling Player is not admin");
    }

    if (!circle.isRunningRound()) {
      throw new IllegalStateException("Round is not running");
    }

    apply(new RoundCanceledEvent(cancelRoundCommand.circleId()));
  }

  @EventSourcingHandler
  public void handle(CircleCreatedEvent event) {
    this.id = event.circleId();
    this.circle = new Circle(event.seed(), event.name(), event.creatorName());
    apply(new CircleMemberAddedEvent(this.id, new Player(event.creatorName())));
    apply(new PlayerIsAdministratorEvent(this.id, event.creatorName()));
  }

  @EventSourcingHandler
  public void handle(CircleMemberAddedEvent event) {
    this.circle.addPlayer(event.player());
    if (this.circle.canStartRound()) {
      apply(new PlayerCanStartRoundEvent(event.circleId(), this.circle.getCreator().playerId()));
    }
  }

  @EventSourcingHandler
  public void handle(CircleMemberRemovedEvent event) {
    var wasAbleToStartBefore = this.circle.canStartRound();
    this.circle.removePlayer(event.player());
    if (!this.circle.isRunningRound() && !this.circle.canStartRound() && wasAbleToStartBefore) {
      apply(new PlayerCanNotStartRoundEvent(event.circleId(), this.circle.getCreator().playerId()));
    }
  }

  @EventSourcingHandler
  public void handle(RoundStartedEvent event) {
    Round round = this.circle.startNewRound();
    for (Player player : this.circle.getPlayers()) {
      apply(
          new PlayerHasNewVictim(
              id, player.playerId(), this.circle.getNextVictim(player.playerId()).get()));
    }
    apply(new PlayerCanCancelRoundEvent(event.circleId(), circle.getCreator()));
  }

  @EventSourcingHandler
  public void handle(PlayerEliminated event) {
    this.circle.removePlayerFromRound(event.victimId());

    if (!circle.isRunningRound()) {
      apply(new RoundEndedEvent(this.id, circle.getSinglePlayer()));
      return;
    }

    var newVictimEvent =
        new PlayerHasNewVictim(
            this.id, event.killerId(), this.circle.getNextVictim(event.killerId()).get());
    apply(newVictimEvent);
  }

  @EventSourcingHandler
  public void handle(RoundCanceledEvent ignoredEvent) {
    this.circle.endRound();
    apply(new RoundEndedEvent(this.id, null));
    if (circle.canStartRound()) {
      apply(new PlayerCanStartRoundEvent(this.id, this.circle.getCreator().playerId()));
    }
  }

  @EventSourcingHandler
  public void handle(RoundEndedEvent ignoredEvent) {
    if (circle.canStartRound()) {
      apply(new PlayerCanStartRoundEvent(this.id, this.circle.getCreator().playerId()));
    } else {
      apply(new PlayerCanNotStartRoundEvent(this.id, this.circle.getCreator().playerId()));
    }
  }
}
