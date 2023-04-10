package de.hatoka.poker.table.internal.event;

import java.util.List;
import java.util.Optional;

import de.hatoka.poker.base.Deck;
import de.hatoka.poker.base.Pot;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.game.DealerOnTable;
import de.hatoka.poker.table.capi.event.history.lifecycle.ShowdownEvent;
import de.hatoka.poker.table.capi.event.history.lifecycle.StartEvent;
import de.hatoka.poker.table.capi.event.history.lifecycle.TransferEvent;

public class DealerOnTableImpl implements DealerOnTable
{
    private final TableInfo table;
    private Optional<GameInfo> lastGame;
    private Optional<GameInfo> currentGame;

    public DealerOnTableImpl(TableInfo table)
    {
        this.table = table;
        this.lastGame = table.getLastGame();
        this.currentGame = table.getCurrentGame();
    }

    @Override
    public void init()
    {
        GameInfo game = table.newGame();
        StartEvent startEvent = new StartEvent();
        startEvent.setPot(Pot.generate());
        startEvent.setDeck(Deck.generate());
        startEvent.setOnButton(getNextOnButton());
        startEvent.setSeats(table.getReadySeats());
        startEvent.setSmallBlind(table.getNextSmallBlind());
        startEvent.setBigBlind(table.getNextBigBlind());
        game.publishEvent(startEvent);
    }

    private SeatRef getNextOnButton()
    {
        List<SeatRef> allSeats = table.getAllSeats();
        if (lastGame.isEmpty())
        {
            return allSeats.get(0);
        }
        Optional<SeatRef> lastDealerOpt = lastGame.get().getOnButton();
        if (lastDealerOpt.isEmpty())
        {
            return allSeats.get(0);
        }
        // find old dealer and get next position in list
        int newPosition = getPositionOfDealer(lastDealerOpt.get(), allSeats) + 1;
        if (newPosition < 0 || newPosition >= allSeats.size())
        {
            newPosition = 0;
        }
        return allSeats.get(newPosition);
    }

    /**
     * @param seat seat reference to last dealer
     * @param seats list of all seats on table 
     * @return position of seat on table or 0 if seat (old dealer was not found on list)
     */
    private int getPositionOfDealer(SeatRef seat, List<SeatRef> seats)
    {
        int result = -1;
        for (int i = 0; result < 0 && i < seats.size(); i++)
        {
            if (seats.get(i).equals(seat))
            {
                result = i;
            }
        }
        // can't found dealer
        if (result < 0)
        {
            result = 0;
        }
        return result;
    }

    @Override
    public boolean canInitialize()
    {
        if (table.getReadySeats().size() < 2)
        {
            return false;
        }
        if (currentGame.isEmpty() || currentGame.get().getEvents(StartEvent.class).findAny().isEmpty())
        {
            return true;
        }
        if (currentGame.isPresent() && currentGame.get().getEvents(ShowdownEvent.class).findAny().isPresent())
        {
            lastGame = currentGame;
            currentGame = Optional.empty();
            return true;
        }
        return false;
    }

    @Override
    public void transfer(TransferEvent transfer)
    {
        GameInfo game = currentGame.get();
        game.publishEvent(transfer);
    }

    @Override
    public boolean isOnTable(SeatRef seatRef)
    {
        return table.getAllSeats().stream().filter(seatRef::equals).findAny().isPresent();
    }
}
