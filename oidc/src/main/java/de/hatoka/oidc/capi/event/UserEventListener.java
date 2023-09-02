package de.hatoka.oidc.capi.event;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import de.hatoka.oidc.capi.remote.OIDCUserInfo;
import de.hatoka.user.capi.business.UserBO;
import de.hatoka.user.capi.business.UserBORepository;
import de.hatoka.user.capi.business.UserRef;

@Component
public class UserEventListener implements ApplicationListener<OIDCUserTokenGeneratedEvent>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventListener.class);
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private UserBORepository userRepo;
    
    @Override
    public void onApplicationEvent(OIDCUserTokenGeneratedEvent event)
    {
        OIDCUserInfo info = event.getInfo();
        UserRef userRef = UserRef.localRef(info.getSubject());
        Optional<UserBO> userOpt = userRepo.findUser(userRef);
        if (userOpt.isEmpty())
        {
            UserBO user = userRepo.createUser(userRef);
            user.setNickName(info.getPreferredUsername());
            userOpt = Optional.of(user);
            LOGGER.info("new user created {}.", user.getRef().getGlobalRef());
        }
        userOpt.get().updateUserInfo(info);
        applicationEventPublisher.publishEvent(new UserTokenGeneratedEvent(this, userRef));
    }
}
