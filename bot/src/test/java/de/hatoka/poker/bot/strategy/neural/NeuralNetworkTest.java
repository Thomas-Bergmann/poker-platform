package de.hatoka.poker.bot.strategy.neural;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.RepeatedTest;
import org.slf4j.LoggerFactory;

import de.hatoka.basicneuralnetwork.NetworkBuilder;
import de.hatoka.basicneuralnetwork.NeuralNetwork;
import de.hatoka.basicneuralnetwork.utilities.FileReaderAndWriter;
import de.hatoka.poker.base.Card;
import de.hatoka.poker.base.Deck;
import de.hatoka.poker.rules.HandToIndex;

class NeuralNetworkTest
{
    private final int AMOUNT_OUTPUTNODES = 3;
    private final int AMOUNT_INPUTNODES_PREFLOP = 4;
    private final int AMOUNT_INPUTNODES_FLOP = 19;
    private final int AMOUNT_INPUTNODES_TURN = 21;
    private final int AMOUNT_INPUTNODES_RIVER = 23;
    private final boolean LOAD_NETWORKS = true;
    private final HandToIndex index = new HandToIndex();
    private final FileReaderAndWriter networkWriter = new FileReaderAndWriter();
    private final NetworkHelper networkHelper = new NetworkHelper();

    @RepeatedTest(2)
    void testPreFlopAction() throws IOException
    {
        NeuralNetwork network = getPreflopNetwork();
        assertEquals(NeuralResult.RAISE, NeuralResult.mapToResult(network.guess(networkHelper.getInputs(Card.deserialize("Ah As")))));
        assertEquals(NeuralResult.FOLD, NeuralResult.mapToResult(network.guess(networkHelper.getInputs(Card.deserialize("7h 2s")))));
        networkWriter.write(network, new File("src/main/resources/preflop.json").toPath());
    }

    private NeuralNetwork getPreflopNetwork() throws IOException
    {
        if (LOAD_NETWORKS)
        {
            return networkWriter.read(getClass().getClassLoader().getResourceAsStream("preflop.json"));
        }
        NeuralNetwork network = NetworkBuilder.create(AMOUNT_INPUTNODES_PREFLOP, AMOUNT_OUTPUTNODES).setHiddenLayers(1, AMOUNT_INPUTNODES_PREFLOP*AMOUNT_OUTPUTNODES).build();
        for (int i = 0; i < 20_000; i++)
        {
            trainPreFlopWithOtherNetwork(network);
        }
        return network;
    }

    @RepeatedTest(2)
    void testFlopAction() throws IOException
    {
        NeuralNetwork network = getFlopNetwork();
        assertEquals(NeuralResult.RAISE, NeuralResult.mapToResult(network.guess(networkHelper.getInputs(Card.deserialize("Ah As Th As Ts")))));
        assertEquals(NeuralResult.RAISE, NeuralResult.mapToResult(network.guess(networkHelper.getInputs(Card.deserialize("7h 5h 6h 4h 3h")))));
        assertEquals(NeuralResult.FOLD, NeuralResult.mapToResult(network.guess(networkHelper.getInputs(Card.deserialize("7h Tc Jd 4s 3h")))));
        networkWriter.write(network, new File("src/main/resources/flop.json").toPath());
    }

    private NeuralNetwork getFlopNetwork() throws IOException
    {
        if (LOAD_NETWORKS)
        {
            return networkWriter.read(getClass().getClassLoader().getResourceAsStream("flop.json"));
        }
        NeuralNetwork network = NetworkBuilder.create(AMOUNT_INPUTNODES_FLOP, AMOUNT_OUTPUTNODES).setHiddenLayers(1, AMOUNT_INPUTNODES_FLOP * AMOUNT_OUTPUTNODES).build();
        for (int i = 0; i < 40_000; i++)
        {
            trainFlopWithOtherNetwork(network);
        }
        return network;
    }

    @RepeatedTest(2)
    void testTurnAction() throws IOException
    {
        NeuralNetwork network = getTurnNetwork();
        assertEquals(NeuralResult.RAISE, NeuralResult.mapToResult(network.guess(networkHelper.getInputs(Card.deserialize("Ah As Th As Ts 9s")))));
        assertEquals(NeuralResult.RAISE, NeuralResult.mapToResult(network.guess(networkHelper.getInputs(Card.deserialize("7h 5h 6h 4h 3h 9s")))));
        assertEquals(NeuralResult.FOLD, NeuralResult.mapToResult(network.guess(networkHelper.getInputs(Card.deserialize("7h Tc Jd 4s 3h 9s")))));
        networkWriter.write(network, new File("src/main/resources/turn.json").toPath());
    }

    private NeuralNetwork getTurnNetwork() throws IOException
    {
        if (LOAD_NETWORKS)
        {
            return networkWriter.read(getClass().getClassLoader().getResourceAsStream("turn.json"));
        }
        NeuralNetwork network = NetworkBuilder.create(AMOUNT_INPUTNODES_TURN, AMOUNT_OUTPUTNODES).setHiddenLayers(1, AMOUNT_INPUTNODES_TURN * AMOUNT_OUTPUTNODES).build();
        for (int i = 0; i < 40_000; i++)
        {
            trainTurnWithOtherNetwork(network);
        }
        return network;
    }

    @RepeatedTest(2)
    void testRiverAction() throws IOException
    {
        NeuralNetwork network = getRiverNetwork();
        assertEquals(NeuralResult.RAISE, NeuralResult.mapToResult(network.guess(networkHelper.getInputs(Card.deserialize("Ah As Th As Ts 9s 2d")))));
        assertEquals(NeuralResult.RAISE, NeuralResult.mapToResult(network.guess(networkHelper.getInputs(Card.deserialize("7h 5h 6h 4h 3h 9s 2d")))));
        assertEquals(NeuralResult.FOLD, NeuralResult.mapToResult(network.guess(networkHelper.getInputs(Card.deserialize("7h Tc Jd 4s 3h 9s 2d")))));
        networkWriter.write(network, new File("src/main/resources/river.json").toPath());
    }

    private NeuralNetwork getRiverNetwork() throws IOException
    {
        if (LOAD_NETWORKS)
        {
            return networkWriter.read(getClass().getClassLoader().getResourceAsStream("river.json"));
        }
        NeuralNetwork network = NetworkBuilder.create(AMOUNT_INPUTNODES_RIVER, AMOUNT_OUTPUTNODES).setHiddenLayers(1, AMOUNT_INPUTNODES_RIVER * AMOUNT_OUTPUTNODES).build();
        for (int i = 0; i < 80_000; i++)
        {
            trainRiverWithOtherNetwork(network);
        }
        return network;
    }

    static
    {
        Deck.initializeRandom(10L);
    }

    private void trainPreFlopWithOtherNetwork(NeuralNetwork network)
    {
        Deck deck = Deck.generate();
        List<Card> myCards = new ArrayList<>();
        List<Card> otherCards = new ArrayList<>();
        deal(deck, Arrays.asList(myCards, otherCards));
        NeuralResult myResult = NeuralResult.mapToResult(network.guess(networkHelper.getInputs(myCards)));
        NeuralResult otherResult = NeuralResult.mapToResult(network.guess(networkHelper.getInputs(otherCards)));
        LoggerFactory.getLogger(getClass()).trace("I do {} on {}", myResult, myCards);
        LoggerFactory.getLogger(getClass()).trace("O do {} on {}", otherResult, otherCards);
        List<Card> myPreFlopCards = new ArrayList<>(myCards);
        List<Card> otherPreFlopCards = new ArrayList<>(otherCards);
        List<Card> flop = deck.getNextCards(3);
        assign(flop, Arrays.asList(myCards, otherCards));
        List<Card> turnAndRiver = deck.getNextCards(2);
        assign(turnAndRiver, Arrays.asList(myCards, otherCards));
        boolean iWon = index.apply(myCards) > index.apply(otherCards);
        LoggerFactory.getLogger(getClass()).trace("I {}", iWon ? "won" : "loose");
        trainByResult(network, myPreFlopCards, myResult, iWon);
        trainByResult(network, otherPreFlopCards, otherResult, !iWon);
    }

    private void trainFlopWithOtherNetwork(NeuralNetwork network)
    {
        Deck deck = Deck.generate();
        List<Card> myCards = new ArrayList<>();
        List<Card> otherCards = new ArrayList<>();
        deal(deck, Arrays.asList(myCards, otherCards));
        List<Card> flop = deck.getNextCards(3);
        assign(flop, Arrays.asList(myCards, otherCards));
        List<Card> myFlopCards = new ArrayList<>(myCards);
        List<Card> otherFlopCards = new ArrayList<>(otherCards);
        NeuralResult myResult = NeuralResult.mapToResult(network.guess(networkHelper.getInputs(myCards)));
        NeuralResult otherResult = NeuralResult.mapToResult(network.guess(networkHelper.getInputs(otherCards)));
        LoggerFactory.getLogger(getClass()).trace("I do {} on {}", myResult, myCards);
        LoggerFactory.getLogger(getClass()).trace("O do {} on {}", otherResult, otherCards);
        List<Card> turnAndRiver = deck.getNextCards(2);
        assign(turnAndRiver, Arrays.asList(myCards, otherCards));
        boolean iWon = index.apply(myCards) > index.apply(otherCards);
        LoggerFactory.getLogger(getClass()).trace("I {}", iWon ? "won" : "loose");
        trainByResult(network, myFlopCards, myResult, iWon);
        trainByResult(network, otherFlopCards, otherResult, !iWon);
    }

    private void trainTurnWithOtherNetwork(NeuralNetwork network)
    {
        Deck deck = Deck.generate();
        List<Card> myCards = new ArrayList<>();
        List<Card> otherCards = new ArrayList<>();
        deal(deck, Arrays.asList(myCards, otherCards));
        List<Card> flop = deck.getNextCards(3);
        assign(flop, Arrays.asList(myCards, otherCards));
        List<Card> turn = deck.getNextCards(1);
        assign(turn, Arrays.asList(myCards, otherCards));
        List<Card> myTurnCards = new ArrayList<>(myCards);
        List<Card> otherTurnCards = new ArrayList<>(otherCards);
        NeuralResult myResult = NeuralResult.mapToResult(network.guess(networkHelper.getInputs(myCards)));
        NeuralResult otherResult = NeuralResult.mapToResult(network.guess(networkHelper.getInputs(otherCards)));
        LoggerFactory.getLogger(getClass()).trace("I do {} on {}", myResult, myCards);
        LoggerFactory.getLogger(getClass()).trace("O do {} on {}", otherResult, otherCards);
        List<Card> river= deck.getNextCards(1);
        assign(river, Arrays.asList(myCards, otherCards));
        boolean iWon = index.apply(myCards) > index.apply(otherCards);
        LoggerFactory.getLogger(getClass()).trace("I {}", iWon ? "won" : "loose");
        trainByResult(network, myTurnCards, myResult, iWon);
        trainByResult(network, otherTurnCards, otherResult, !iWon);
    }

    private void trainRiverWithOtherNetwork(NeuralNetwork network)
    {
        Deck deck = Deck.generate();
        List<Card> myCards = new ArrayList<>();
        List<Card> otherCards = new ArrayList<>();
        deal(deck, Arrays.asList(myCards, otherCards));
        List<Card> flop = deck.getNextCards(3);
        assign(flop, Arrays.asList(myCards, otherCards));
        List<Card> turn = deck.getNextCards(1);
        assign(turn, Arrays.asList(myCards, otherCards));
        List<Card> river= deck.getNextCards(1);
        assign(river, Arrays.asList(myCards, otherCards));
        NeuralResult myResult = NeuralResult.mapToResult(network.guess(networkHelper.getInputs(myCards)));
        NeuralResult otherResult = NeuralResult.mapToResult(network.guess(networkHelper.getInputs(otherCards)));
        LoggerFactory.getLogger(getClass()).trace("I do {} on {}", myResult, myCards);
        LoggerFactory.getLogger(getClass()).trace("O do {} on {}", otherResult, otherCards);
        boolean iWon = index.apply(myCards) > index.apply(otherCards);
        LoggerFactory.getLogger(getClass()).trace("I {}", iWon ? "won" : "loose");
        trainByResult(network, myCards, myResult, iWon);
        trainByResult(network, otherCards, otherResult, !iWon);
    }

    private void deal(Deck deck, List<List<Card>> hands)
    {
        for(List<Card> cards : hands)
        {
            cards.add(deck.getNextCard());
        }
        for(List<Card> cards : hands)
        {
            cards.add(deck.getNextCard());
        }
    }

    private void assign(List<Card> newCards, List<List<Card>> hands)
    {
        for(List<Card> cards : hands)
        {
            cards.addAll(newCards);
        }
    }

    private void trainByResult(NeuralNetwork network, List<Card> cards, NeuralResult result, boolean isWinner)
    {
        if (isWinner)
        {
            switch(result)
            {
                case FOLD:
                    LoggerFactory.getLogger(getClass()).trace("train {} : {} -> {}", cards, NeuralResult.FOLD, NeuralResult.CALL);
                    network.train(networkHelper.getInputs(cards), NeuralResult.CALL.getOutputs());
                    break;
                case CALL:
                    LoggerFactory.getLogger(getClass()).trace("train {} : {} -> {}", cards, NeuralResult.CALL, NeuralResult.RAISE);
                    network.train(networkHelper.getInputs(cards), NeuralResult.RAISE.getOutputs());
                    break;
                case RAISE:
                    LoggerFactory.getLogger(getClass()).trace("train {} : {} -> {}", cards, NeuralResult.RAISE, NeuralResult.RAISE);
                    network.train(networkHelper.getInputs(cards), NeuralResult.RAISE.getOutputs());
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected value: " + result);
            }
        }
        else
        {
            switch(result)
            {
                case FOLD:
                    LoggerFactory.getLogger(getClass()).trace("train {} : {} -> {}", cards, NeuralResult.FOLD, NeuralResult.FOLD);
                    network.train(networkHelper.getInputs(cards), NeuralResult.FOLD.getOutputs());
                    break;
                case CALL:
                    LoggerFactory.getLogger(getClass()).trace("train {} : {} -> {}", cards, NeuralResult.CALL, NeuralResult.FOLD);
                    network.train(networkHelper.getInputs(cards), NeuralResult.FOLD.getOutputs());
                    break;
                case RAISE:
                    LoggerFactory.getLogger(getClass()).trace("train {} : {} -> {}", cards, NeuralResult.RAISE, NeuralResult.CALL);
                    network.train(networkHelper.getInputs(cards), NeuralResult.CALL.getOutputs());
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected value: " + result);
            }
        }
    }
}
