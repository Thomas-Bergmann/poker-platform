package de.hatoka.poker.bot.strategy;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import de.hatoka.poker.bot.remote.client.BotServiceClient;
import de.hatoka.poker.bot.remote.client.RemotePlayer;

@Component
public class PokerStrategyFactoryImpl implements PokerStrategyFactory
{
    @Lookup
    @Override
    public PokerStrategy create(BotServiceClient client)
    {
        // done by @Lookup
        return null;
    }

    @Lookup
    @Override
    public PokerStrategyFirstRound createFirstRoundStategy(RemotePlayer remotePlayer)
    {
        // done by @Lookup
        return null;
    }
}
