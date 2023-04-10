package de.hatoka.poker.bot.strategy;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.bot.remote.client.RemotePlayer;

@ExtendWith(MockitoExtension.class)
class PokerStrategyFirstRoundImplTest
{
    @Mock
    private RemotePlayer remotePlayer;
    private PokerStrategyFirstRoundImpl underTest;

    @BeforeEach
    public void createTestObject()
    {
        underTest = new PokerStrategyFirstRoundImpl(remotePlayer);
    }

    @Test
    void testBetWithPocketsEights()
    {
        when(remotePlayer.getHoleCards()).thenReturn(Card.deserialize("8h 8s"));
        underTest.calculateAction();
        verify(remotePlayer, times(1)).betTo(anyInt());
        verify(remotePlayer, times(0)).fold();
    }

    @Test
    void testBetWithAce()
    {
        when(remotePlayer.getHoleCards()).thenReturn(Card.deserialize("Ah Ts"));
        underTest.calculateAction();
        verify(remotePlayer, times(1)).betTo(anyInt());
        verify(remotePlayer, times(0)).fold();
    }

    @Test
    void testBetSuitedConnectorsHigh()
    {
        when(remotePlayer.getHoleCards()).thenReturn(Card.deserialize("Js Ts"));
        underTest.calculateAction();
        verify(remotePlayer, times(1)).betTo(anyInt());
        verify(remotePlayer, times(0)).fold();
    }

    @Test
    void testCallWithPocketsSeven()
    {
        when(remotePlayer.getHoleCards()).thenReturn(Card.deserialize("7h 7s"));
        underTest.calculateAction();
        verify(remotePlayer, times(0)).betTo(anyInt());
        verify(remotePlayer, times(1)).call();
        verify(remotePlayer, times(0)).fold();
    }

    @Test
    void testBetSuitedConnectorsLow()
    {
        when(remotePlayer.getHoleCards()).thenReturn(Card.deserialize("4s 3s"));
        underTest.calculateAction();
        verify(remotePlayer, times(1)).call();
        verify(remotePlayer, times(0)).fold();
    }

    @Test
    void testFoldSuitedBelowQueen()
    {
        when(remotePlayer.getHoleCards()).thenReturn(Card.deserialize("Js 9s"));
        underTest.calculateAction();
        verify(remotePlayer, times(0)).betTo(anyInt());
        verify(remotePlayer, times(1)).fold();
    }
}
