package de.midnightblue.assassin.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record RoundCanceledEvent(@TargetAggregateIdentifier String circleId) {}
