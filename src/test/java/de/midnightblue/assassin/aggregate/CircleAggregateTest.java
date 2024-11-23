package de.midnightblue.assassin.aggregate;

import de.midnightblue.assassin.aggregate.domain.Player;
import de.midnightblue.assassin.commands.CreateCircleCommand;
import de.midnightblue.assassin.commands.EliminateVictimCommand;
import de.midnightblue.assassin.commands.StartRoundCommand;
import de.midnightblue.assassin.events.*;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.axonframework.test.aggregate.TestExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CircleAggregateTest {
  private FixtureConfiguration<CircleAggregate> fixture;

  @BeforeEach
  public void setUp() {
    fixture = new AggregateTestFixture<>(CircleAggregate.class);
  }

  @Test
  public void testCreateCircle() {
    fixture
        .givenNoPriorActivity()
        .when(new CreateCircleCommand("a", "a", "John", 1))
        .expectEvents(
            new CircleCreatedEvent("a", "a", "John", 1),
            new CircleMemberAddedEvent("a", new Player("John")));
  }

  @Test
  public void testNeedsPlayersToStartRound() {
    fixture
        .given(new CircleCreatedEvent("a", "x", "John", 1))
        .when(new StartRoundCommand("a"))
        .expectException(IllegalStateException.class);
  }

  @Test
  public void testCanStartRoundWithTwoPlayers() {
    var player1 = new Player("1");
    var player2 = new Player("2");
    var player3 = new Player("3");
    fixture
        .given(
            new CircleCreatedEvent("a", "x", "John", 1),
            new CircleMemberAddedEvent("a", player1),
            new CircleMemberAddedEvent("a", player2),
            new CircleMemberAddedEvent("a", player3))
        .when(new StartRoundCommand("a"))
        .expectSuccessfulHandlerExecution();
  }

  @Test
  public void testNeedsToStartToPerfomKill() {
    var player1 = new Player("1");
    var player2 = new Player("2");
    var player3 = new Player("3");
    fixture
        .given(
            new CircleCreatedEvent("a", "x", "John", 1),
            new CircleMemberAddedEvent("a", player1),
            new CircleMemberAddedEvent("a", player2),
            new CircleMemberAddedEvent("a", player3))
        .when(new EliminateVictimCommand("a", "1"))
        .expectException(IllegalStateException.class);
  }

  @Test
  public void testCanEliminatePlayer() {
    var player1 = new Player("1");
    var player2 = new Player("2");
    var player3 = new Player("3");
    TestExecutor<CircleAggregate> given =
        fixture.given(
            new CircleCreatedEvent("a", "x", "John", 1),
            new CircleMemberAddedEvent("a", player1),
            new CircleMemberAddedEvent("a", player2),
            new CircleMemberAddedEvent("a", player3),
            new RoundStartedEvent("a"));
    given
        .when(new EliminateVictimCommand("a", "1"))
        .expectEvents(new PlayerEliminated("a", "2", "1"), new PlayerHasNewVictim("a", "1", "3"));
  }

  @Test
  public void testCanWinGame() {
    var player1 = new Player("1");
    var player2 = new Player("2");
    var player3 = new Player("3");
    TestExecutor<CircleAggregate> given =
        fixture.given(
            new CircleCreatedEvent("a", "x", "John", 1),
            new CircleMemberAddedEvent("a", player1),
            new CircleMemberAddedEvent("a", player2),
            new CircleMemberAddedEvent("a", player3),
            new RoundStartedEvent("a"),
            new PlayerEliminated("a", "1", "3"));
    given
        .when(new EliminateVictimCommand("a", "2"))
        .expectEvents(new PlayerEliminated("a", "3", "2"), new RoundEndedEvent("a", "2"));
  }
}
