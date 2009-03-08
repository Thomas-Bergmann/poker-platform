package de.hatoka.user.capi;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import de.hatoka.user.internal.business.UserBORepositoryImpl;
import de.hatoka.user.internal.persistence.UserDao;
import de.hatoka.user.internal.persistence.UserPO;

@Configuration
@EntityScan(basePackageClasses = { UserPO.class })
@EnableJpaRepositories(basePackageClasses = { UserDao.class })
@ComponentScan(basePackageClasses = { UserBORepositoryImpl.class })
public class UserConfiguration
{
}
