package de.midnightblue.assassin.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record StartRoundCommand(@TargetAggregateIdentifier String circleId) {
}
