package de.midnightblue.assassin.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record CancelRoundCommand(@TargetAggregateIdentifier String circleId, String userId) {}
