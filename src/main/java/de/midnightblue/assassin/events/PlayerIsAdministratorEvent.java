package de.midnightblue.assassin.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record PlayerIsAdministratorEvent(
    @TargetAggregateIdentifier String circleId, String playerId) {}
