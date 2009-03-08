package de.hatoka.poker.table.capi;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import de.hatoka.poker.table.internal.business.TableBORepositoryImpl;
import de.hatoka.poker.table.internal.event.TableEventListener;
import de.hatoka.poker.table.internal.persistence.TableDao;
import de.hatoka.poker.table.internal.persistence.TablePO;
import de.hatoka.poker.table.internal.remote.TableController;

@Configuration
@EntityScan(basePackageClasses = { TablePO.class })
@EnableJpaRepositories(basePackageClasses = { TableDao.class })
@ComponentScan(basePackageClasses = { TableBORepositoryImpl.class, TableController.class, TableEventListener.class })
public class TableConfiguration
{
    /**
     * Allow matrix parameter
     * @return adapted http fire wall
     */
    @Bean
    public HttpFirewall getHttpFirewall() {
        StrictHttpFirewall strictHttpFirewall = new StrictHttpFirewall();
        strictHttpFirewall.setAllowSemicolon(true);
        return strictHttpFirewall;
    }
}
