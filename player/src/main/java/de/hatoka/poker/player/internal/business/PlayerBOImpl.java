package de.hatoka.poker.player.internal.business;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.hatoka.poker.player.capi.business.PlayerBO;
import de.hatoka.poker.player.capi.business.PlayerRef;
import de.hatoka.poker.player.capi.business.PlayerType;
import de.hatoka.poker.player.internal.persistence.PlayerDao;
import de.hatoka.poker.player.internal.persistence.PlayerPO;
import de.hatoka.user.capi.business.UserRef;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlayerBOImpl implements PlayerBO
{
    private PlayerPO playerPO;
    @Autowired
    private PlayerDao playerDao;

    public PlayerBOImpl(PlayerPO playerPO)
    {
        this.playerPO = playerPO;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((playerPO == null) ? 0 : playerPO.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PlayerBOImpl other = (PlayerBOImpl)obj;
        if (playerPO == null)
        {
            if (other.playerPO != null)
                return false;
        }
        else if (!playerPO.equals(other.playerPO))
            return false;
        return true;
    }

    @Override
    public String getNickName()
    {
        return playerPO.getNickName();
    }

    @Override
    @Transactional
    public void remove()
    {
        playerDao.delete(playerPO);
        playerPO = null;
    }

    private void savePO()
    {
        playerPO = playerDao.save(playerPO);
    }

    @Override
    public PlayerRef getRef()
    {
        UserRef userRef = UserRef.globalRef(playerPO.getUserRef());
        if (PlayerType.HUMAN.name().equals(playerPO.getType()))
        {
            return PlayerRef.humanRef(userRef);
        }
        return PlayerRef.botRef(userRef, playerPO.getNickName());
    }

    @Override
    public Long getInternalId()
    {
        return playerPO.getId();
    }

    @Override
    public int getBalance()
    {
        return playerPO.getBalance();
    }

    @Override
    public PlayerType getType()
    {
        return PlayerType.valueOf(playerPO.getType());
    }

    @Override
    public UserRef getOwnerRef()
    {
        return UserRef.globalRef(playerPO.getUserRef());
    }

    @Override
    public void moveCoinsFromPlayerToSeat(String seatRef, int coins)
    {
        playerPO.setBalance(playerPO.getBalance() - coins);
        savePO();
    }

    @Override
    public void moveCoinsFromSeatToPlayer(String globalRef, int coins)
    {
        playerPO.setBalance(playerPO.getBalance() + coins);
        savePO();
    }

    @Override
    public void setNickName(String nickName)
    {
        playerPO.setNickName(nickName);
        savePO();
    }

    @Override
    public String getApiKey()
    {
        checkAPIKey();
        return playerPO.getApiKey();
    }

    private void checkAPIKey()
    {
        String key = playerPO.getApiKey();
        if (key == null || key.isBlank())
        {
            playerPO.setApiKey(UUID.randomUUID().toString());
            savePO();
        }
    }
}

