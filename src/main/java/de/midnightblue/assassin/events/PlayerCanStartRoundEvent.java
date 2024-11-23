package de.midnightblue.assassin.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record PlayerCanStartRoundEvent(
    @TargetAggregateIdentifier String circleId, String playerId) {}
