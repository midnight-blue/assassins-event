package de.midnightblue.assassin.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record PlayerEliminated(@TargetAggregateIdentifier String circleId, String victimId, String killerId) {
}
