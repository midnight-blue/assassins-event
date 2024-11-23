package de.midnightblue.assassin.query;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryCircle {
  private final String id;
  private final String name;
  private final List<String> members = new ArrayList<>();

  public QueryCircle(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public void addMember(String member) {
    this.members.add(member);
  }

  public void removeMember(String member) {
    this.members.remove(member);
  }
}
