package de.midnightblue.assassin.config;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MockUsersService {
  public List<String> getUserNames() {
    return List.of("John", "Alice", "Jim", "Mark");
  }
}
