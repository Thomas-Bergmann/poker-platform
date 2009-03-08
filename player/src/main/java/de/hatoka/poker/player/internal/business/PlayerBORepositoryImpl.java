package de.hatoka.poker.player.internal.business;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.hatoka.poker.player.capi.business.PlayerBO;
import de.hatoka.poker.player.capi.business.PlayerBORepository;
import de.hatoka.poker.player.capi.business.PlayerRef;
import de.hatoka.poker.player.capi.business.PlayerType;
import de.hatoka.poker.player.internal.persistence.PlayerDao;
import de.hatoka.poker.player.internal.persistence.PlayerPO;
import de.hatoka.user.capi.business.UserBO;
import de.hatoka.user.capi.business.UserBORepository;
import de.hatoka.user.capi.business.UserRef;

@Component
public class PlayerBORepositoryImpl implements PlayerBORepository
{
    @Autowired
    private PlayerDao playerDao;    
    @Autowired
    private PlayerBOFactory playerBOFactory;
    @Autowired
    private UserBORepository userRepository;

    @Override
    public PlayerBO createHumanPlayer(UserRef userRef)
    {
        PlayerPO po = new PlayerPO();
        po.setNickName(getNickName(userRef));
        po.setType(PlayerType.HUMAN.name());
        po.setUserRef(userRef.getGlobalRef());
        return playerBOFactory.get(playerDao.save(po));
    }

    private String getNickName(UserRef userRef)
    {
        String nickName = userRepository.findUser(userRef).map(UserBO::getNickName).orElse(userRef.getLocalRef());
        return nickName;
    }

    @Override
    public PlayerBO createBotPlayer(UserRef userRef, String botName)
    {
        PlayerPO po = new PlayerPO();
        po.setNickName(botName);
        po.setType(PlayerType.COMPUTE.name());
        po.setUserRef(userRef.getGlobalRef());
        return playerBOFactory.get(playerDao.save(po));
    }

    @Override
    public Optional<PlayerBO> findHuman(UserRef userRef)
    {
        return playerDao.getByUserref(userRef.getGlobalRef()).stream().filter(po -> PlayerType.HUMAN.name().equals(po.getType())).findAny().map(playerBOFactory::get);
    }

    @Override
    public List<PlayerBO> getBots(UserRef userRef)
    {
        return playerDao.getByUserref(userRef.getGlobalRef()).stream().filter(po -> PlayerType.COMPUTE.name().equals(po.getType())).map(playerBOFactory::get).toList();
    }

    @Override
    public Collection<PlayerBO> getAllPlayers()
    {
        return playerDao.findAll().stream().map(playerBOFactory::get).collect(Collectors.toList());
    }

    @Override
    public Optional<PlayerBO> findPlayer(Long internalPlayerId)
    {
        return playerDao.findById(internalPlayerId).map(playerBOFactory::get);
    }

    @Override
    public void updateHumanPlayer(UserRef userRef)
    {
        Optional<PlayerBO> playerOpt = findPlayer(PlayerRef.humanRef(userRef));
        if (playerOpt.isPresent())
        {
            playerOpt.get().setNickName(getNickName(userRef));
        }
    }
}
