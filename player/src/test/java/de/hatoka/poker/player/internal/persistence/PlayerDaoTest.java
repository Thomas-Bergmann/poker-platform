package de.hatoka.poker.player.internal.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.hatoka.poker.player.capi.business.PlayerType;
import de.hatoka.user.capi.business.UserRef;
import tests.de.hatoka.poker.player.PlayerTestConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { PlayerTestConfiguration.class })
public class PlayerDaoTest
{
    private static final UserRef OWNER_REF = UserRef.localRef("owner-one");
    private static final String NAME = "PlayerDaoTest-N";

    @Autowired
    private PlayerDao projectDao;

    @Test
    public void testCRUD()
    {
        PlayerPO PlayerPO = newPlayerPO(NAME);
        projectDao.save(PlayerPO);
        Optional<PlayerPO> findPlayerPO= projectDao.findByNickname(NAME);
        assertEquals(PlayerPO, findPlayerPO.get());
        projectDao.delete(PlayerPO);
    }

    private PlayerPO newPlayerPO(String name)
    {
        PlayerPO result = new PlayerPO();
        result.setNickName(name);
        result.setType(PlayerType.COMPUTE.name());
        result.setUserRef(OWNER_REF.getGlobalRef());
        result.setBalance(0);
        return result;
    }
}
