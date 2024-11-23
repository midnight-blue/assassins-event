package de.midnightblue.assassin.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record CreateCircleCommand(
    @TargetAggregateIdentifier String circleId, String circleName, String creatorName, long seed) {}
