package de.hatoka.poker.table.internal.event;

import java.util.Optional;

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
        if(inGameOpt.isPresent() && inGameOpt.get().canTransfer())
        {
            onTable.transfer(inGameOpt.get().getTransfer());
        }
        if (onTable.canInitialize())
        {
            onTable.init();
            inGameOpt = getDealerInGame();
        }
        if (inGameOpt.isEmpty())
        {
            return;
        }
        if (inGameOpt.get().doWhatEverYouNeed())
        {
            doWhatEverYouNeed();
        }
    }
}
