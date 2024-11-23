package de.midnightblue.assassin.query;

import de.midnightblue.assassin.events.*;
import java.util.*;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class CircleProjection {

  Map<String, Map<String, UserCircleInfo>> circlesOfUsers = new HashMap<>();
  Map<String, List<String>> usersInCircles = new HashMap<>();

  Map<String, QueryCircle> circles = new HashMap<>();

  @QueryHandler // 1.
  public UserCircleQueryResult handle(FetchUserCirclesInfoQuery query) { // 2.
    return new UserCircleQueryResult(
        circlesOfUsers.getOrDefault(query.userId(), Map.of()).values());
  }

  @EventHandler
  public void on(CircleCreatedEvent circleCreatedEvent) {
    this.circles.put(
        circleCreatedEvent.circleId(),
        new QueryCircle(circleCreatedEvent.circleId(), circleCreatedEvent.name()));
  }

  @EventHandler
  public void on(PlayerHasNewVictim playerHasNewVictim) {
    var info = circlesOfUsers.get(playerHasNewVictim.playerId()).get(playerHasNewVictim.circleId());
    info.setNextVictim(playerHasNewVictim.victimId());
  }

  @EventHandler
  public void on(PlayerEliminated playerEliminated) {
    var circle = circles.get(playerEliminated.circleId());
    var info = circlesOfUsers.get(playerEliminated.victimId()).get(circle.getId());
    info.kill(playerEliminated.killerId());
  }

  @EventHandler
  public void on(RoundEndedEvent roundEndedEvent) {
    for (String user : usersInCircles.get(roundEndedEvent.circleId())) {
      var info = circlesOfUsers.get(user).get(roundEndedEvent.circleId());
      info.end(roundEndedEvent.winnerId());
    }
  }

  @EventHandler
  public void on(RoundStartedEvent roundStarted) {
    for (String user : usersInCircles.get(roundStarted.circleId())) {
      var info = circlesOfUsers.get(user).get(roundStarted.circleId());
      info.start();
    }
  }

  @EventHandler
  public void on(PlayerCanStartRoundEvent canStartRound) {
    var circle = circles.get(canStartRound.circleId());
    var info = circlesOfUsers.get(canStartRound.playerId()).get(circle.getId());
    info.setAction(Actions.START);
  }

  @EventHandler
  public void on(PlayerCanNotStartRoundEvent canStartRound) {
    var circle = circles.get(canStartRound.circleId());
    var info = circlesOfUsers.get(canStartRound.playerId()).get(circle.getId());
    info.setAction(null);
  }

  @EventHandler
  public void on(PlayerCanCancelRoundEvent canCancelRound) {
    var circle = circles.get(canCancelRound.circleId());
    var info = circlesOfUsers.get(canCancelRound.player().playerId()).get(circle.getId());
    info.setAction(Actions.CANCEL);
  }

  @EventHandler
  public void on(PlayerIsAdministratorEvent playerIsAdministratorEvent) {
    var circle = circles.get(playerIsAdministratorEvent.circleId());
    var info = circlesOfUsers.get(playerIsAdministratorEvent.playerId()).get(circle.getId());
    info.setAdmin(true);
  }

  @EventHandler
  public void on(CircleMemberAddedEvent circleMemberAddedEvent) {
    QueryCircle circle = circles.get(circleMemberAddedEvent.circleId());
    Map<String, UserCircleInfo> circles =
        circlesOfUsers.computeIfAbsent(
            circleMemberAddedEvent.player().playerId(), k -> new HashMap<>());
    var circleInfo = new UserCircleInfo(circle);
    circles.put(circle.getId(), circleInfo);
    circle.addMember(circleMemberAddedEvent.player().playerId());

    List<String> users =
        usersInCircles.computeIfAbsent(circleMemberAddedEvent.circleId(), k -> new ArrayList<>());

    users.add(circleMemberAddedEvent.player().playerId());
  }

  @EventHandler
  public void on(CircleMemberRemovedEvent circleMemberRemovedEvent) {
    circles
        .get(circleMemberRemovedEvent.circleId())
        .removeMember(circleMemberRemovedEvent.player().playerId());
    Map<String, UserCircleInfo> userCircles =
        circlesOfUsers.get(circleMemberRemovedEvent.player().playerId());
    userCircles.remove(circleMemberRemovedEvent.circleId());
    List<String> users = usersInCircles.get(circleMemberRemovedEvent.circleId());
    users.remove(circleMemberRemovedEvent.player().playerId());
  }
}
