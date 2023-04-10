package de.hatoka.poker.bot.remote.client;

public interface PokerClientFactory
{
    BotServiceClient create(String serviceURI, String botRef, String botKey);
}
