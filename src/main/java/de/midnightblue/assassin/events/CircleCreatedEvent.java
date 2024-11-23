package de.midnightblue.assassin.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record CircleCreatedEvent(
    @TargetAggregateIdentifier String circleId, String name, String creatorName, long seed) {}
