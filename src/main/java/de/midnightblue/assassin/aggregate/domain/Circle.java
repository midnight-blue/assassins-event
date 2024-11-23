package de.midnightblue.assassin.aggregate.domain;

import java.util.*;
import lombok.Getter;

public class Circle {
  private final long seed;
  private final String name;
  private Round currentRound = null;
  private final Set<Player> players = new HashSet<>();
  @Getter private final Player creator;

  public Circle(long seed, String name, String creatorId) {
    this.seed = seed;
    this.name = name;
    this.creator = new Player(creatorId);
  }

  public Optional<String> getNextVictim(String playerId) {
    if (currentRound == null || currentRound.isGameFinished()) {
      return Optional.empty();
    }
    return currentRound.getNextVictim(playerId);
  }

  public boolean canAddPlayer(Player player) {
    return players.stream().filter(p -> p.equals(player)).findAny().isEmpty();
  }

  public boolean canStartRound() {
    return players.size() > 2 && (currentRound == null || currentRound.isGameFinished());
  }

  public void addPlayer(Player player) {
    players.add(player);
  }

  public void removePlayer(Player player) {
    players.remove(player);
  }

  public Round startNewRound() {
    List<Player> list = new java.util.ArrayList<>(players.stream().toList());
    Collections.shuffle(list, new java.util.Random(seed));
    currentRound = new Round(list);
    return currentRound;
  }

  public List<Player> getPlayers() {
    return this.players.stream().toList();
  }

  public boolean isRunningRound() {
    return currentRound != null && !currentRound.isGameFinished();
  }

  public void removePlayerFromRound(String player) {
    if (currentRound == null) {
      throw new AssertionError("No round is started");
    }
    currentRound.removePlayer(player);
  }

  public String getSinglePlayer() {
    return this.currentRound.getSinglePlayer();
  }

  public boolean containsMember(String playerId) {
    return players.stream().anyMatch(p -> p.playerId().equals(playerId));
  }

  public void endRound() {
    this.currentRound = null;
  }
}
