package de.hatoka.poker.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import de.hatoka.common.capi.CommonConfiguration;
import de.hatoka.oidc.capi.IdentityProviderConfiguration;
import de.hatoka.poker.player.capi.PlayerConfiguration;
import de.hatoka.poker.security.CorsConfiguration;
import de.hatoka.poker.security.WebSecurityConfiguration;
import de.hatoka.poker.table.capi.TableConfiguration;
import de.hatoka.user.capi.UserConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@ComponentScan
@Import(value = { CorsConfiguration.class, WebSecurityConfiguration.class,
                IdentityProviderConfiguration.class, CommonConfiguration.class, UserConfiguration.class,
                PlayerConfiguration.class, TableConfiguration.class 
                })
public class Application extends SpringBootServletInitializer
{
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
    {
        return application.sources(Application.class);
    }

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }
}