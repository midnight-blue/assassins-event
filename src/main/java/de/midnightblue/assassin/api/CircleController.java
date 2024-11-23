package de.midnightblue.assassin.api;

import de.midnightblue.assassin.commands.AddCircleMemberCommand;
import de.midnightblue.assassin.commands.CancelRoundCommand;
import de.midnightblue.assassin.commands.CreateCircleCommand;
import de.midnightblue.assassin.commands.EliminateVictimCommand;
import de.midnightblue.assassin.commands.RemoveCircleMemberCommand;
import de.midnightblue.assassin.commands.StartRoundCommand;
import java.security.Principal;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CircleController {

  private final CommandGateway commandBus; // 1.

  public CircleController(CommandGateway commandBus) {
    this.commandBus = commandBus;
  }

  @PostMapping("/circle")
  public ResponseEntity<String> createCircle(
      @RequestBody CreateCircleRequestDto circleRequestDto, Principal principal)
      throws ExecutionException, InterruptedException {
    String id = String.valueOf((new Random()).nextInt(1, 20000));
    // FIXME make seed random in production
    commandBus
        .send(new CreateCircleCommand(id, circleRequestDto.name(), principal.getName(), 1L))
        .get();
    return ResponseEntity.ok(id);
  }

  @PostMapping("/circle/{circleId}/members/{userId}")
  public void addToCircle(
      @PathVariable("circleId") String circleId, @PathVariable("userId") String userId)
      throws ExecutionException, InterruptedException {
    commandBus.send(new AddCircleMemberCommand(circleId, userId)).get();
  }

  @DeleteMapping("/circle/{circleId}/members/{userId}")
  public void removeFromCircle(
      @PathVariable("circleId") String circleId, @PathVariable("userId") String userId)
      throws ExecutionException, InterruptedException {
    commandBus.send(new RemoveCircleMemberCommand(circleId, userId)).get();
  }

  @PutMapping("/circle/{circleId}/start")
  public void startRound(@PathVariable("circleId") String circleId)
      throws ExecutionException, InterruptedException {
    commandBus.send(new StartRoundCommand(circleId)).get();
  }

  @PutMapping("/circle/{circleId}/stop")
  public void stopRound(@PathVariable("circleId") String circleId, Principal principal)
      throws ExecutionException, InterruptedException {
    commandBus.send(new CancelRoundCommand(circleId, principal.getName())).get();
  }

  @PutMapping("/circle/{circleId}/eliminate")
  public void eliminateVictimCommand(@PathVariable("circleId") String circleId, Principal principal)
      throws ExecutionException, InterruptedException {
    commandBus.send(new EliminateVictimCommand(circleId, principal.getName())).get();
  }
}
