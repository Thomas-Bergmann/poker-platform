package de.hatoka.poker.player.internal.event;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import de.hatoka.oidc.capi.event.UserTokenGeneratedEvent;
import de.hatoka.poker.player.capi.business.PlayerBO;
import de.hatoka.poker.player.capi.business.PlayerBORepository;
import de.hatoka.user.capi.business.UserRef;

@Component
public class PlayerEventListener implements ApplicationListener<UserTokenGeneratedEvent>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerEventListener.class);
    @Autowired
    private PlayerBORepository playerRepo;
    
    @Override
    public void onApplicationEvent(UserTokenGeneratedEvent event)
    {
        UserRef userRef = event.getUserRef();
        Optional<PlayerBO> playerOpt = playerRepo.findHuman(userRef);
        if (playerOpt.isEmpty())
        {
            PlayerBO player = playerRepo.createHumanPlayer(userRef);
            LOGGER.info("new player created {}.", player.getRef().getGlobalRef());
        }
        else
        {
            playerRepo.updateHumanPlayer(userRef);
        }
    }
}
