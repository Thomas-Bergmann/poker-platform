package de.hatoka.poker.bot.remote.client;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import de.hatoka.poker.remote.SeatRO;

@Component
public class RemotePlayerFactoryImpl implements RemotePlayerFactory
{
    @Lookup
    @Override
    public RemotePlayer create(SeatRO seat, PokerServiceClient client)
    {
        // done by @Lookup
        return null;
    }
}
