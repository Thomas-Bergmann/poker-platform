package de.hatoka.poker.table.internal.remote;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.base.Hand;
import de.hatoka.poker.remote.GameEventRO;
import de.hatoka.poker.remote.GameInfoRO;
import de.hatoka.poker.remote.GameRO;
import de.hatoka.poker.remote.SeatDataRO;
import de.hatoka.poker.remote.SeatInfoRO;
import de.hatoka.poker.remote.SeatRO;
import de.hatoka.poker.remote.GameEventRO.Action;
import de.hatoka.poker.table.capi.business.GameBO;
import de.hatoka.poker.table.capi.business.SeatBO;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.game.PlayerFactory;
import de.hatoka.poker.table.capi.event.history.GameEvent;
import de.hatoka.poker.table.internal.event.GameInfo;
import de.hatoka.poker.table.internal.event.PlayerGameInfo;
import de.hatoka.poker.table.internal.json.GameEventType;

@Component
public class GameBO2RO
{
    @Autowired
    private SeatBO2RO seatBO2RO;
    @Autowired
    private PlayerFactory playerFactory;

    public GameRO apply(GameBO game, SeatBO seat)
    {
        GameRO result = apply(game);
        getPlayerEnrichedInfo(game, seat, result.getInfo().getSeats());
        return result;
    }

    private void getPlayerEnrichedInfo(GameBO game, SeatBO seat, List<SeatRO> seats)
    {
        PlayerGameInfo playerInfo = playerFactory.getInfo(seat);
        Map<SeatRef, Hand> hands = playerInfo.getHands();
        for (SeatRO seatRO : seats)
        {
            SeatInfoRO info = seatRO.getInfo();
            // player specific
            SeatRef seatRef = SeatRef.globalRef(seatRO.getRefGlobal());
            Hand hand = hands.get(seatRef);
            if (hand != null)
            {
                info.setHoleCards(Card.serialize(hand.getHoleCards()));
                info.setRank(playerInfo.getRank(hand).name());
            }
        }
    }

    public GameRO apply(GameBO game)
    {
        GameInfo dealerInfo = new GameInfo(game);

        GameInfoRO info = new GameInfoRO();
        info.setTableResourceURI("/tables/" + game.getTable().getRef().getLocalRef());
        List<GameEventRO> events = getEvents(dealerInfo);
        info.setEvents(events);
        if (!events.isEmpty())
        {
            info.setSeats(getDealerInfoAboutSeats(game, dealerInfo));
            info.setBoardCards(Card.serialize(dealerInfo.getBoardCards()));
            info.setPotSize(dealerInfo.getPotSize());
            info.setBigBlind(dealerInfo.getBigBlind());
        }
        GameRO result = new GameRO();
        result.setRefGlobal(game.getRef().getGlobalRef());
        result.setRefLocal(game.getRef().getLocalRef());
        result.setResourceURI(
                        "/tables/" + game.getTable().getRef().getLocalRef() + "/games/" + game.getRef().getLocalRef());
        result.setInfo(info);
        return result;
    }

    List<GameEventRO> getEvents(GameInfo dealerInfo)
    {
        return dealerInfo.getEvents(GameEvent.class)
                         .map(GameEvent::getPublicEvent)
                         .filter(Optional::isPresent)
                         .map(Optional::get)
                         .map(this::mapToRO)
                         .toList();
    }

    private GameEventRO mapToRO(GameEvent event)
    {
        GameEventRO result = new GameEventRO();
        result.setAction(mapAction(event));
        result.setInfo(GameEventType.valueOf(event.getClass()).serialize(event));
        return result;
    }

    private Action mapAction(GameEvent event)
    {
        switch(event.getClass().getSimpleName())
        {
            case "PublicStartEvent":
                return Action.start;
            case "BlindEvent":
                return Action.blind;
            case "CallEvent":
                return Action.call;
            case "CheckEvent":
                return Action.check;
            case "RaiseEvent":
                return Action.raise;
            case "BetEvent":
                return Action.bet;
            case "FoldEvent":
                return Action.fold;
            case "PublicPotEvent":
                return Action.pot;
            case "BoardCardsEvent":
                return Action.cards;
            case "ShowdownEvent":
                return Action.showdown;
            case "TransferEvent":
                return Action.transfer;
            default:
                throw new IllegalArgumentException("Unexpected value: " + event.getClass().getSimpleName());
        }
    }

    /**
     * Add or Replace game specific information to seats
     */
    private List<SeatRO> getDealerInfoAboutSeats(GameBO game, GameInfo dealerInfo)
    {
        List<SeatRef> allIns = dealerInfo.getAllIns();
        Map<SeatRef, Integer> coinsInPlay = dealerInfo.getCoinsInPlay();
        List<SeatRO> result = seatBO2RO.apply(game.getTable().getSeatRepository().getSeats());
        SeatRef onButton = dealerInfo.getOnButton().orElse(null);
        List<SeatRef> seatsInAction = dealerInfo.getSeats();
        SeatRef action = dealerInfo.getSeatHasAction().orElse(null);
        for (SeatRO seatRO : result)
        {
            SeatRef seatRef = SeatRef.globalRef(seatRO.getRefGlobal());
            if (seatsInAction.contains(seatRef))
            {
                SeatDataRO data = seatRO.getData();
                data.setCoinsOnSeat(dealerInfo.getCoinsOnSeat(seatRef));
                SeatInfoRO info = seatRO.getInfo();
                info.setInPlay(coinsInPlay.getOrDefault(seatRef, 0));
                info.setAllIn(allIns.contains(seatRef));
                info.setOnButton(seatRef.equals(onButton));
                info.setHasAction(seatRef.equals(action));
            }
            else
            {
                SeatInfoRO info = seatRO.getInfo();
                info.setOut(true);
            }
        }
        return result;
    }
}
