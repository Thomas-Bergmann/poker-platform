package de.hatoka.poker.table.internal.event;

import java.util.Optional;

import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.game.Dealer;
import de.hatoka.poker.table.capi.event.game.DealerInGame;
import de.hatoka.poker.table.capi.event.game.DealerOnTable;

public class DealerImpl implements Dealer
{
    private final TableInfo table;

    public DealerImpl(TableInfo table)
    {
        this.table = table;
    }

    @Override
    public DealerOnTable getDealerOnTable()
    {
        return new DealerOnTableImpl(table);
    }

    @Override
    public Optional<DealerInGame> getDealerInGame()
    {
        return table.getCurrentGame().map(DealerInGameImpl::new);
    }

    @Override
    public void doWhatEverYouNeed()
    {
        Optional<DealerInGame> inGameOpt = getDealerInGame();
        DealerOnTable onTable = getDealerOnTable();
        // game is ongoing, but is finished can transfer of coins can start
        if(inGameOpt.isPresent() && inGameOpt.get().canTransfer())
        {
            onTable.transfer(inGameOpt.get().getTransfer());
        }
        // no game started yet?
        if (onTable.canInitialize())
        {
            onTable.init();
            inGameOpt = getDealerInGame();
        }
        // game still not started?
        if (inGameOpt.isEmpty())
        {
            return;
        }
        // check that seat which has action is still on table
        Optional<SeatRef> seatWithAction = inGameOpt.get().getSeatWithAction();
        if (seatWithAction.isPresent() && !onTable.isOnTable(seatWithAction.get()))
        {
            inGameOpt.get().abort(seatWithAction.get());
        }
        // dealer do what you can
        if (inGameOpt.get().doWhatEverYouNeed())
        {
            doWhatEverYouNeed();
        }
    }
}
