package de.hatoka.poker.bot.strategy;

import de.hatoka.poker.bot.remote.client.PokerServiceClient;
import de.hatoka.poker.bot.remote.client.RemotePlayer;

public interface PokerStrategyFactory
{
    PokerStrategy create(PokerServiceClient client);
    PokerStrategyFirstRound createFirstRoundStategy(RemotePlayer remotePlayer);
}
