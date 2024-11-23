package de.midnightblue.assassin.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record RemoveCircleMemberCommand(
    @TargetAggregateIdentifier String circleId, String userId) {}
