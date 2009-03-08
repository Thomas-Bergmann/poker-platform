package de.hatoka.poker.bot.strategy;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import de.hatoka.poker.bot.remote.client.PokerServiceClient;

@Component
public class PokerStrategyFactoryImpl implements PokerStrategyFactory
{
    @Lookup
    @Override
    public PokerStrategy create(PokerServiceClient client)
    {
        // done by @Lookup
        return null;
    }
}
