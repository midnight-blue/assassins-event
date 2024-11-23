package de.midnightblue.assassin.aggregate.domain;

import java.util.List;
import java.util.Optional;

public class Round {
  private final List<Player> players;

  public Round(List<Player> players) {
    this.players = players;
  }

  public void removePlayer(String player) {
    players.removeIf(p -> p.playerId().equals(player));
  }

  public boolean isGameFinished() {
    return players.size() == 1;
  }


  public Optional<String> getNextVictim(String playerId) {
    Optional<Player> player = players.stream().filter(p -> p.playerId().equals(playerId)).findFirst();
    if (player.isEmpty()) {
      return Optional.empty();
    }
    var nextIndex = players.indexOf(player.get()) + 1;
    nextIndex = nextIndex % players.size();
    return Optional.of(players.get(nextIndex).playerId());

  }

  public String getSinglePlayer() {
    return players.get(0).playerId();
  }
}
