package de.hatoka.poker.bot.remote.client;

public interface PokerClientFactory
{
    PokerServiceClient create(String serviceURI, String botRef, String botKey);
}
