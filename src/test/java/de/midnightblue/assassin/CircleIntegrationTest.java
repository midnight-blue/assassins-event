package de.midnightblue.assassin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.midnightblue.assassin.api.CreateCircleRequestDto;
import de.midnightblue.assassin.api.UserCircleInfoDto;
import de.midnightblue.assassin.query.Actions;
import io.holixon.axon.testcontainer.AxonServerContainer;
import io.holixon.axon.testcontainer.spring.AxonServerContainerSpring;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class CircleIntegrationTest {

  @Autowired private MockMvc webMvc;

  @Autowired private ObjectMapper objectMapper; // For converting Java objects to JSON

  @Container
  public static final AxonServerContainer AXON =
      AxonServerContainer.builder().enableDevMode().build();

  @DynamicPropertySource
  public static void axonProperties(final DynamicPropertyRegistry registry) {
    AxonServerContainerSpring.addDynamicProperties(AXON, registry);
  }

  @Test
  @WithMockUser
  public void test() throws Exception {
    var post = post("/circle");
    var dto = new CreateCircleRequestDto("cool kids clubs");
    post.contentType("application/json");
    post.content(objectMapper.writeValueAsString(dto));
    var res = webMvc.perform(post);
    res.andExpect(status().isOk());
    var id = res.andReturn().getResponse().getContentAsString();

    var addMember = post("/circle/" + id + "/members/1");
    ResultActions actions = webMvc.perform(addMember).andExpect(status().isOk());

    var creatCircleInfo =
        webMvc.perform(get("/players/user/circles")).andReturn().getResponse().getContentAsString();
    var info = new ObjectMapper().readValue(creatCircleInfo, UserCircleInfoDto.class);
    assertNull(info.info().circles().stream().findFirst().get().getAction());

    var addMember2 = post("/circle/" + id + "/members/2");
    webMvc.perform(addMember2).andExpect(status().isOk());

    var addMember3 = post("/circle/" + id + "/members/3");
    webMvc.perform(addMember3).andExpect(status().isOk());

    creatCircleInfo =
        webMvc.perform(get("/players/user/circles")).andReturn().getResponse().getContentAsString();
    info = new ObjectMapper().readValue(creatCircleInfo, UserCircleInfoDto.class);
    assertEquals(Actions.START, info.info().circles().stream().findFirst().get().getAction());

    var start = put("/circle/" + id + "/start");
    webMvc.perform(start).andExpect(status().isOk());

    var killPlayer1 = put("/circle/" + id + "/eliminate/3");
    webMvc.perform(killPlayer1).andExpect(status().isOk());

    var killPlayer3 = put("/circle/" + id + "/eliminate/2");
    webMvc.perform(killPlayer3).andExpect(status().isOk());

    var getInfo = get("/players/" + 1 + "/circles");
    var result = webMvc.perform(getInfo);
    result.andExpect(status().isOk());
    var data = result.andReturn().getResponse().getContentAsString();
    return;
  }
}
