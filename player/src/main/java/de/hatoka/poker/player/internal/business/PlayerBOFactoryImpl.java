package de.hatoka.poker.player.internal.business;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import de.hatoka.poker.player.capi.business.PlayerBO;
import de.hatoka.poker.player.internal.persistence.PlayerPO;

@Component
public class PlayerBOFactoryImpl implements PlayerBOFactory
{
    @Lookup
    @Override
    public PlayerBO get(PlayerPO po)
    {
        // done by @Lookup
        return null;
    }
}
