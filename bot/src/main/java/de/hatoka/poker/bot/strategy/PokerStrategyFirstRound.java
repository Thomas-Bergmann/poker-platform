package de.hatoka.poker.bot.strategy;

import de.hatoka.poker.remote.PlayerGameActionRO;

public interface PokerStrategyFirstRound
{
    PlayerGameActionRO calculateAction();
}
