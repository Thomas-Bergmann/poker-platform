package de.hatoka.poker.table.internal.json;

import de.hatoka.poker.table.capi.event.history.GameEvent;
import de.hatoka.poker.table.capi.event.history.card.BoardCardsEvent;
import de.hatoka.poker.table.capi.event.history.card.BurnCardEvent;
import de.hatoka.poker.table.capi.event.history.card.HoleCardsEvent;
import de.hatoka.poker.table.capi.event.history.lifecycle.PublicStartEvent;
import de.hatoka.poker.table.capi.event.history.lifecycle.ShowdownEvent;
import de.hatoka.poker.table.capi.event.history.lifecycle.StartEvent;
import de.hatoka.poker.table.capi.event.history.lifecycle.TransferEvent;
import de.hatoka.poker.table.capi.event.history.pot.CollectCoinsEvent;
import de.hatoka.poker.table.capi.event.history.pot.PublicPotEvent;
import de.hatoka.poker.table.capi.event.history.seat.BetEvent;
import de.hatoka.poker.table.capi.event.history.seat.BlindEvent;
import de.hatoka.poker.table.capi.event.history.seat.CallEvent;
import de.hatoka.poker.table.capi.event.history.seat.CheckEvent;
import de.hatoka.poker.table.capi.event.history.seat.FoldEvent;
import de.hatoka.poker.table.capi.event.history.seat.RaiseEvent;

public enum GameEventType
{
    Start(StartEvent.class, new JsonSerializer<StartEvent>(StartEvent.class)),
    HiddenStart(PublicStartEvent.class, new JsonSerializer<PublicStartEvent>(PublicStartEvent.class)),
    Blind(BlindEvent.class, new JsonSerializer<BlindEvent>(BlindEvent.class)),
    HoleCard(HoleCardsEvent.class, new JsonSerializer<HoleCardsEvent>(HoleCardsEvent.class)),
    Bet(BetEvent.class, new JsonSerializer<BetEvent>(BetEvent.class)),
    Raise(RaiseEvent.class, new JsonSerializer<RaiseEvent>(RaiseEvent.class)),
    Call(CallEvent.class, new JsonSerializer<CallEvent>(CallEvent.class)),
    Check(CheckEvent.class, new JsonSerializer<CheckEvent>(CheckEvent.class)),
    Fold(FoldEvent.class, new JsonSerializer<FoldEvent>(FoldEvent.class)),
    BoardCards(BoardCardsEvent.class, new JsonSerializer<BoardCardsEvent>(BoardCardsEvent.class)),
    BurnCard(BurnCardEvent.class, new JsonSerializer<BurnCardEvent>(BurnCardEvent.class)),
    CollectCoins(CollectCoinsEvent.class, new JsonSerializer<CollectCoinsEvent>(CollectCoinsEvent.class)),
    Showdown(ShowdownEvent.class, new JsonSerializer<ShowdownEvent>(ShowdownEvent.class)),
    PublicPotEvent(PublicPotEvent.class, new JsonSerializer<PublicPotEvent>(PublicPotEvent.class)),
    TransferEvent(TransferEvent.class, new JsonSerializer<TransferEvent>(TransferEvent.class)),
    ;
    private final Class<? extends GameEvent> eventClass;
    private final JsonSerializer<? extends GameEvent> serializer;

    private GameEventType(Class<? extends GameEvent> eventClass, JsonSerializer<? extends GameEvent> serializer)
    {
        this.eventClass = eventClass;
        this.serializer = serializer;
    }

    public String serialize(Object o)
    {
        return serializer.serialize(o);
    }

    public GameEvent deserialize(String data)
    {
        return serializer.deserialize(data);
    }

    public static GameEventType valueOf(Class<?> objectClass)
    {
        for(GameEventType type : GameEventType.values())
        {
            if (type.eventClass.equals(objectClass))
            {
                return type;
            }
        }
        return null;
    }
    
    public static String serializeEvent(GameEvent event)
    {
        return GameEventType.valueOf(event.getClass()).serialize(event);
    }
}
