package de.midnightblue.assassin.events;

import de.midnightblue.assassin.aggregate.domain.Player;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record PlayerCanCancelRoundEvent(
    @TargetAggregateIdentifier String circleId, Player player) {}
