package de.hatoka.poker.player.capi;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import de.hatoka.poker.player.internal.business.PlayerBORepositoryImpl;
import de.hatoka.poker.player.internal.event.PlayerEventListener;
import de.hatoka.poker.player.internal.persistence.PlayerDao;
import de.hatoka.poker.player.internal.persistence.PlayerPO;
import de.hatoka.poker.player.internal.remote.PlayerController;

@Configuration
@EntityScan(basePackageClasses = { PlayerPO.class })
@EnableJpaRepositories(basePackageClasses = { PlayerDao.class })
@ComponentScan(basePackageClasses = { PlayerBORepositoryImpl.class, PlayerController.class, PlayerEventListener.class })
public class PlayerConfiguration
{
}
