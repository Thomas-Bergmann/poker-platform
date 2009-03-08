package de.hatoka.poker.table.internal.business;

import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.hatoka.poker.player.capi.business.PlayerBO;
import de.hatoka.poker.player.capi.business.PlayerBORepository;
import de.hatoka.poker.table.capi.business.SeatBO;
import de.hatoka.poker.table.capi.business.SeatBORepository;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.business.TableBO;
import de.hatoka.poker.table.internal.persistence.SeatDao;
import de.hatoka.poker.table.internal.persistence.SeatPO;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SeatBOImpl implements SeatBO
{
    @Autowired
    private SeatDao seatDao;
    @Autowired
    private PlayerBORepository playerRepo;

    private final SeatRef seatRef;
    private final SeatBORepository seatRepository;

    public SeatBOImpl(SeatRef seatRef, SeatBORepository seatRepository)
    {
        this.seatRef = seatRef;
        this.seatRepository = seatRepository;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(seatRef);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SeatBOImpl other = (SeatBOImpl)obj;
        return Objects.equals(seatRef, other.seatRef);
    }

    @Override
    @Transactional
    public void remove()
    {
        seatDao.delete(getPO());
    }

    @Override
    public SeatRef getRef()
    {
        return seatRef;
    }

    @Override
    public Long getInternalId()
    {
        return getPO().getId();
    }

    @Override
    public Integer getPosition()
    {
        return seatRef.getPosition();
    }

    @Override
    public TableBO getTable()
    {
        return seatRepository.getTable();
    }

    @Override
    public Optional<PlayerBO> getPlayer()
    {
        SeatPO seatPO = getPO();
        return seatPO.getPlayerId() == null ? Optional.empty(): playerRepo.findPlayer(seatPO.getPlayerId());
    }

    @Override
    public void join(PlayerBO player)
    {
        if(seatRepository.isOnTable(player))
        {
            throw new IllegalStateException("player is at other seat of table");
        }
        SeatPO seatPO = getPO();
        seatPO.setPlayerId(player.getInternalId());
        seatDao.save(seatPO);
    }

    @Transactional
    @Override
    public void buyin(int coins)
    {
        if (coins < getTable().getBigBlind())
        {
            throw new IllegalArgumentException("buyin smaller as big blind are not allowed");
        }
        if (getTable().getMaxBuyIn() < coins + getAmountOfCoinsOnSeat())
        {
            throw new IllegalArgumentException("buyin greater than max buy in are not allowed");
        }
        SeatPO seatPO = getPO();
        Optional<PlayerBO> playerOpt = getPlayer();
        if (!playerOpt.isPresent())
        {
            throw new IllegalStateException("no player at seat");
        }
        PlayerBO player = playerOpt.get();
        player.moveCoinsFromPlayerToSeat(getRef().getGlobalRef(), coins);
        seatPO.setAmountOfCoinsOnSeat(seatPO.getAmountOfCoinsOnSeat() + coins);
        seatPO.setSittingOut(false);
        seatDao.save(seatPO);
    }

    @Override
    @Transactional
    public int leave()
    {
        SeatPO seatPO = getPO();
        Optional<PlayerBO> playerOpt = getPlayer();
        if (!playerOpt.isPresent())
        {
            return 0;
        }
        PlayerBO player = playerOpt.get();
        int coins = seatPO.getAmountOfCoinsOnSeat();
        player.moveCoinsFromSeatToPlayer(getRef().getGlobalRef(), coins);
        seatPO.setPlayerId(null);
        seatPO.setAmountOfCoinsOnSeat(0);
        seatPO.setSittingOut(true);
        seatDao.save(seatPO);
        return coins;
    }

    /**
     * @return the amount of chips of seat but not in play yet
     */
    @Override
    public int getAmountOfCoinsOnSeat()
    {
        return getPO().getAmountOfCoinsOnSeat();
    }

    @Override
    public boolean isSittingOut()
    {
        SeatPO seatPO = getPO();
        boolean sittingOut = seatPO.isSittingOut();
        if (!sittingOut && seatPO.getAmountOfCoinsOnSeat() <= 0)
        {
            // something was wrong before
            sittingOut = true;
            seatPO.setSittingOut(sittingOut);
            seatDao.save(seatPO);
        }
        return sittingOut;
    }

    @Override
    public void setSittingOut(boolean sittingOut)
    {
        SeatPO seatPO = getPO();
        if (!sittingOut && seatPO.getAmountOfCoinsOnSeat() <= 0)
        {
            return;
        }
        seatPO.setSittingOut(sittingOut);
        seatDao.save(seatPO);
    }

    @Override
    @Transactional
    public void updateCoinsOnSeat(int coins)
    {
        SeatPO seatPO = getPO();
        int newCoins = seatPO.getAmountOfCoinsOnSeat() + coins;
        seatPO.setAmountOfCoinsOnSeat(newCoins);
        if (newCoins <= 0)
        {
            seatPO.setSittingOut(true);
        }
        seatDao.save(seatPO);
    }

    private synchronized SeatPO getPO()
    {
        return seatDao.findByTableidAndPosition(seatRepository.getTable().getInternalId(), seatRef.getPosition()).get();
    }
}
