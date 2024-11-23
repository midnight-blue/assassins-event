package de.midnightblue.assassin.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record AddCircleMemberCommand(@TargetAggregateIdentifier String circleId, String userId) {
}
