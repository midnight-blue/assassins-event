package de.midnightblue.assassin.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record EliminateVictimCommand(@TargetAggregateIdentifier String circleId, String killerId) {}
