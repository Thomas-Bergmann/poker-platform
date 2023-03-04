package de.hatoka.poker.player.internal.remote;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import de.hatoka.poker.player.capi.business.PlayerBO;
import de.hatoka.poker.player.capi.remote.BotDataRO;
import de.hatoka.poker.player.capi.remote.BotInfoRO;
import de.hatoka.poker.player.capi.remote.BotRO;

@Component
public class PlayerBO2BotRO
{
    public BotRO apply(PlayerBO player)
    {
        BotDataRO data = new BotDataRO();
        data.setOwnerRef(player.getOwnerRef().getGlobalRef());
        data.setNickName(player.getNickName());
        data.setApiKey(player.getApiKey());

        BotInfoRO info = new BotInfoRO();
        info.setBalance(player.getBalance());

        BotRO result = new BotRO();
        result.setRefGlobal(player.getRef().getGlobalRef());
        result.setRefLocal(player.getRef().getLocalRef());
        result.setData(data);
        result.setInfo(info);
        return result;
    }

    public List<BotRO> apply(Collection<PlayerBO> bots)
    {
        return bots.stream().map(this::apply).collect(Collectors.toList());
    }
}
