package de.hatoka.poker.bot.strategy;

import de.hatoka.poker.bot.remote.client.BotServiceClient;
import de.hatoka.poker.bot.remote.client.RemotePlayer;

public interface PokerStrategyFactory
{
    PokerStrategy create(BotServiceClient client);
    PokerStrategyFirstRound createFirstRoundStategy(RemotePlayer remotePlayer);
}
