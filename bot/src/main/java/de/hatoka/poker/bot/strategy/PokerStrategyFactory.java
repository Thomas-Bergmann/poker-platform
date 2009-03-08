package de.hatoka.poker.bot.strategy;

import de.hatoka.poker.bot.remote.client.PokerServiceClient;

public interface PokerStrategyFactory
{
    PokerStrategy create(PokerServiceClient client);
}
