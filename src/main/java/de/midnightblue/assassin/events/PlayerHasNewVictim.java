package de.midnightblue.assassin.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record PlayerHasNewVictim (@TargetAggregateIdentifier String circleId, String playerId, String victimId){
}
