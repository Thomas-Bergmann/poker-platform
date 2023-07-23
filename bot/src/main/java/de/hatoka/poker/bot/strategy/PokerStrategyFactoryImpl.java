package de.hatoka.poker.bot.strategy;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import de.hatoka.poker.bot.remote.client.BotServiceClient;
import de.hatoka.poker.bot.remote.client.RemotePlayer;
import de.hatoka.poker.bot.strategy.neural.NeuralDecisionMaker;

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

    @Override
    public PokerStrategyDecisionMaker createDecisionMaker(RemotePlayer remotePlayer)
    {
        return new NeuralDecisionMaker(remotePlayer);
    }
}
