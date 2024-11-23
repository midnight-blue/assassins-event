package de.midnightblue.assassin.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record RoundStartedEvent(@TargetAggregateIdentifier String circleId) {
}
