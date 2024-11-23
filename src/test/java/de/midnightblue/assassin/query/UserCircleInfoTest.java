package de.midnightblue.assassin.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class UserCircleInfoTest {

  @Test
  void testMapper() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    var info = new UserCircleInfo(new QueryCircle("1", "1"));
  }
}
