package de.hatoka.poker.table.internal.remote;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import de.hatoka.poker.player.capi.business.PlayerBO;
import de.hatoka.poker.player.capi.business.PlayerRef;
import de.hatoka.poker.remote.SeatDataRO;
import de.hatoka.poker.remote.SeatInfoRO;
import de.hatoka.poker.remote.SeatRO;
import de.hatoka.poker.table.capi.business.SeatBO;

@Component
public class SeatBO2RO
{
    public SeatRO apply(SeatBO seat)
    {
        SeatInfoRO info = new SeatInfoRO();
        info.setPosition(seat.getPosition());
        info.setTableResourceURI("/tables/" + seat.getTable().getRef().getLocalRef());
        Optional<PlayerBO> playerOpt = seat.getPlayer();
        if (playerOpt.isPresent())
        {
            PlayerBO playerBO = playerOpt.get();
            PlayerRef playerRef = playerBO.getRef();
            if (!playerRef.isHuman())
            {
                info.setBotRef(playerRef.getGlobalRef());
            }
            info.setUserRef(playerRef.getUserRef().getGlobalRef());
            info.setName(playerBO.getNickName());
        }
        SeatDataRO data = new SeatDataRO();
        data.setPlayerRef(playerOpt.map(PlayerBO::getRef).map(PlayerRef::getGlobalRef).orElse(null));
        data.setCoinsOnSeat(seat.getAmountOfCoinsOnSeat());
        data.setSittingOut(seat.isSittingOut());

        SeatRO result = new SeatRO();
        result.setRefGlobal(seat.getRef().getGlobalRef());
        result.setRefLocal(seat.getRef().getLocalRef());
        result.setResourceURI("/tables/" + seat.getTable().getRef().getLocalRef() + "/seats/" + seat.getRef().getLocalRef());
        result.setInfo(info);
        result.setData(data);
        return result;
    }

    public List<SeatRO> apply(Collection<SeatBO> projects)
    {
        return projects.stream().map(this::apply).collect(Collectors.toList());
    }
}
