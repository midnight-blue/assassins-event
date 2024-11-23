package de.midnightblue.assassin.query;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class UserCircleInfo {
  private QueryCircle circle;
  private UserCircleStatus status = UserCircleStatus.INACTIVE;
  private String nextVictim;
  private String eliminatedBy;
  private String roundWonBy;
  private Actions action;
  private boolean isAdmin;

  private UserCircleInfo() {}

  public UserCircleInfo(QueryCircle circle) {
    this.circle = circle;
  }

  public void end(String winner) {
    this.status = UserCircleStatus.INACTIVE;
    this.nextVictim = null;
    this.roundWonBy = winner;
  }

  public void start() {
    this.status = UserCircleStatus.ACTIVE;
    this.eliminatedBy = null;
  }

  public void kill(String killer) {
    this.status = UserCircleStatus.ELIMINATED;
    this.eliminatedBy = killer;
  }

  public QueryCircle circle() {
    return circle;
  }

  public UserCircleStatus status() {
    return status;
  }

  public Optional<String> nextVictim() {
    return Optional.ofNullable(nextVictim);
  }

  public Optional<String> eliminatedBy() {
    return Optional.ofNullable(eliminatedBy);
  }

  public Optional<String> roundWonBy() {
    return Optional.ofNullable(roundWonBy);
  }
}
