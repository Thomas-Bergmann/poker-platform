package de.hatoka.poker.bot.strategy.neural;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.hatoka.basicneuralnetwork.NeuralNetwork;
import de.hatoka.basicneuralnetwork.utilities.FileReaderAndWriter;
import de.hatoka.poker.base.Card;
import de.hatoka.poker.bot.remote.client.RemotePlayer;
import de.hatoka.poker.bot.strategy.PokerStrategyDecisionMaker;
import de.hatoka.poker.remote.GameRO;
import de.hatoka.poker.remote.PlayerGameActionRO;
import de.hatoka.poker.remote.SeatGameInfoRO;
import de.hatoka.poker.remote.SeatRO;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NeuralDecisionMaker implements PokerStrategyDecisionMaker
{
    private final RemotePlayer remotePlayer;
    private final FileReaderAndWriter networkReader = new FileReaderAndWriter();
    private final NetworkHelper networkHelper = new NetworkHelper();

    public NeuralDecisionMaker(RemotePlayer remotePlayer)
    {
        this.remotePlayer = remotePlayer;
    }

    @Override
    public PlayerGameActionRO calculateAction(GameRO game)
    {
        List<Card> boardCards = Card.deserialize(game.getInfo().getBoardCards());
        try
        {
            if (boardCards.isEmpty())
            {
                return calculatePreFlopAction();
            }
            else if (boardCards.size() == 3)
            {
                return calculateAction(boardCards, "flop.json");
            }
            else if (boardCards.size() == 4)
            {
                return calculateAction(boardCards, "turn.json");
            }
            else if (boardCards.size() == 5)
            {
                return calculateAction(boardCards, "river.json");
            }
        }
        catch(IOException e)
        {
            LoggerFactory.getLogger(getClass()).error("Can't calculate next step", e);
            remotePlayer.fold();
        }
        return remotePlayer.call();
    }

    private PlayerGameActionRO calculatePreFlopAction() throws IOException
    {
        List<Card> cards = getHoleCards();
        NeuralNetwork nn = read("preflop.json");
        return map(NeuralResult.mapToResult(nn.guess(networkHelper.getInputs(cards))));
    }

    private PlayerGameActionRO calculateAction(List<Card> boardCards, String resource) throws IOException
    {
        List<Card> cards = new ArrayList<>();
        cards.addAll(getHoleCards());
        cards.addAll(boardCards);
        NeuralNetwork nn = read(resource);
        return map(NeuralResult.mapToResult(nn.guess(networkHelper.getInputs(cards))));
    }

    private PlayerGameActionRO map(NeuralResult result)
    {
        return switch(result)
        {
            case FOLD -> remotePlayer.fold();
            case CALL -> remotePlayer.call();
            case RAISE -> raiseOrBet(getRaiseToPotSize());
        };
    }

    private NeuralNetwork read(String resource) throws IOException
    {
        return networkReader.read(NeuralDecisionMaker.class.getClassLoader().getResourceAsStream(resource));
    }

    private PlayerGameActionRO raiseOrBet(int raiseTo)
    {
        if (getLastBet() > 0)
        {
            return remotePlayer.raiseTo(raiseTo);
        }
        return remotePlayer.betTo(raiseTo);
    }

    private List<Card> getHoleCards()
    {
        List<Card> cards = remotePlayer.getHoleCards();
        if (cards.size() != 2)
        {
            throw new IllegalArgumentException("Player doesn't have two cards");
        }
        return cards;
    }

    private int getRaiseToPotSize()
    {
        return remotePlayer.getPotSize() + getLastBet() * 3;
    }

    private int getLastBet()
    {
        return remotePlayer.getSeats().stream().map(SeatRO::getGame).mapToInt(SeatGameInfoRO::getInPlay).max().orElse(0);
    }
}
