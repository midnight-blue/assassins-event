package de.midnightblue.assassin.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record PlayerCanNotStartRoundEvent(
    @TargetAggregateIdentifier String circleId, String playerId) {}
