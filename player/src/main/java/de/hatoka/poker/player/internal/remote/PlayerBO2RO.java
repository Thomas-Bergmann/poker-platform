package de.hatoka.poker.player.internal.remote;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import de.hatoka.poker.player.capi.business.PlayerBO;
import de.hatoka.poker.player.capi.remote.PlayerDataRO;
import de.hatoka.poker.player.capi.remote.PlayerInfoRO;
import de.hatoka.poker.player.capi.remote.PlayerRO;

@Component
public class PlayerBO2RO
{
    public PlayerRO apply(PlayerBO player)
    {
        PlayerDataRO data = new PlayerDataRO();
        data.setOwnerRef(player.getOwnerRef().getGlobalRef());

        PlayerInfoRO info = new PlayerInfoRO();
        info.setNickName(player.getNickName());
        info.setBalance(player.getBalance());
        info.setType(player.getType().name());

        PlayerRO result = new PlayerRO();
        result.setRefGlobal(player.getRef().getGlobalRef());
        result.setRefLocal(player.getRef().getLocalRef());
        result.setData(data);
        result.setInfo(info);
        return result;
    }

    public List<PlayerRO> apply(Collection<PlayerBO> projects)
    {
        return projects.stream().map(this::apply).collect(Collectors.toList());
    }
}
