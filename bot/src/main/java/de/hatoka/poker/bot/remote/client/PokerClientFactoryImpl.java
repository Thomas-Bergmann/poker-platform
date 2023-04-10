package de.hatoka.poker.bot.remote.client;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

@Component
public class PokerClientFactoryImpl implements PokerClientFactory
{
    @Lookup
    @Override
    public BotServiceClient create(String serviceURI, String botRef, String botKey)
    {
        // done by @Lookup
        return null;
    }
}
