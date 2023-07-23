package de.hatoka.poker.bot.strategy;

import de.hatoka.poker.remote.GameRO;
import de.hatoka.poker.remote.PlayerGameActionRO;

public interface PokerStrategyDecisionMaker
{
    PlayerGameActionRO calculateAction(GameRO game);
}
