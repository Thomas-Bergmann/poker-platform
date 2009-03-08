package de.hatoka.poker.player.internal.business;

import de.hatoka.poker.player.capi.business.PlayerBO;
import de.hatoka.poker.player.internal.persistence.PlayerPO;

public interface PlayerBOFactory
{
    PlayerBO get(PlayerPO po);
}
