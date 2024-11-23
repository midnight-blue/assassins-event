package de.midnightblue.assassin.api;

import de.midnightblue.assassin.config.MockUsersService;
import de.midnightblue.assassin.query.FetchUserCirclesInfoQuery;
import de.midnightblue.assassin.query.UserCircleQueryResult;
import java.util.concurrent.ExecutionException;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class PlayerController {

  private final QueryGateway circleProjection;
  private final MockUsersService names;

  public PlayerController(QueryGateway circleProjection, MockUsersService userDetailsService) {
    this.circleProjection = circleProjection;
    this.names = userDetailsService;
  }

  @GetMapping("/players/{playerId}/circles")
  public ResponseEntity<UserCircleInfoDto> getCircles(@PathVariable("playerId") String playerId)
      throws ExecutionException, InterruptedException {
    UserCircleQueryResult info =
        circleProjection
            .query(
                new FetchUserCirclesInfoQuery(playerId),
                ResponseTypes.instanceOf(UserCircleQueryResult.class))
            .get();
    return ResponseEntity.ok(new UserCircleInfoDto(info));
  }

  @GetMapping("/players/")
  public ResponseEntity<UsersInfoDto> getUserNames() {
    return ResponseEntity.ok(new UsersInfoDto(names.getUserNames()));
  }
}
