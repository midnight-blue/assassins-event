package de.midnightblue.assassin.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record RoundEndedEvent(@TargetAggregateIdentifier String circleId, String winnerId) {}
