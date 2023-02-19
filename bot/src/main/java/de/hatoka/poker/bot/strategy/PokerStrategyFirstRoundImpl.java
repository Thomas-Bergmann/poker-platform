package de.hatoka.poker.bot.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.base.Image;
import de.hatoka.poker.bot.remote.client.RemotePlayer;
import de.hatoka.poker.remote.SeatInfoRO;
import de.hatoka.poker.remote.SeatRO;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PokerStrategyFirstRoundImpl implements PokerStrategyFirstRound
{
    private final RemotePlayer remotePlayer;

    public PokerStrategyFirstRoundImpl(RemotePlayer remotePlayer)
    {
        this.remotePlayer = remotePlayer;
    }

    @Override
    public void run()
    {
        List<Card> cards = getSortedCardsByImage();
        LoggerFactory.getLogger(getClass()).debug("got cards {}.", cards);
        int raiseTo = getRaiseToPotSize();
        Optional<Image> pockets = getPocketPair(cards);
        // raise - if pocket pair >=8
        // call - if pocket pair < 8
        if (pockets.isPresent())
        {
            if (isGreater(pockets.get(), Image.SEVEN))
            {
                raiseOrBet(raiseTo);
            }
            else
            {
                remotePlayer.call();
            }
            return;
        }
        // raise - AK to A10
        if (Image.ACE.equals(cards.get(0).getImage()) && isGreater(cards.get(1).getImage(), Image.NINE))
        {
            raiseOrBet(raiseTo);
            return;
        }
        if (areConnected(cards))
        {
            if (areSuited(cards) && isGreater(cards.get(0).getImage(), Image.TEN))
            {
                raiseOrBet(raiseTo);
            }
            else
            {
                remotePlayer.call();
            }
            return;
        }
        remotePlayer.fold();
    }

    private boolean areSuited(List<Card> cards)
    {
        return cards.get(0).getColor().equals(cards.get(1).getColor());
    }

    private boolean areConnected(List<Card> cards)
    {
        Image image1 = cards.get(0).getImage();
        Image expected = Image.valueViaIndex(image1.getIndex() - 1);
        return expected.equals(cards.get(1).getImage());
    }

    private List<Card> getSortedCardsByImage()
    {
        List<Card> cards = new ArrayList<>();
        cards.addAll(remotePlayer.getHoleCards());
        cards.sort(Card.BY_IMAGE.reversed());
        if (cards.size() != 2)
        {
            throw new IllegalArgumentException("Player doesn't have two cards");
        }
        return cards;
    }

    private void raiseOrBet(int raiseTo)
    {
        if (getLastBet() > 0)
        {
            remotePlayer.raiseTo(raiseTo);
        }
        else
        {
            remotePlayer.betTo(raiseTo);
        }
    }

    private boolean isGreater(Image a, Image b)
    {
        return a.compareTo(b) > 0;
    }

    private int getRaiseToPotSize()
    {
        return remotePlayer.getPotSize() + getLastBet();
    }

    private int getLastBet()
    {
        return remotePlayer.getSeats().stream().map(SeatRO::getInfo).mapToInt(SeatInfoRO::getInPlay).max().orElse(0);
    }

    private Optional<Image> getPocketPair(List<Card> cards)
    {
        Image image = cards.get(0).getImage();
        if (image.equals(cards.get(1).getImage()))
        {
            return Optional.of(image);
        }
        return Optional.empty();
    }
}
