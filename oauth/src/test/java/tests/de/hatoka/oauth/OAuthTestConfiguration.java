package tests.de.hatoka.oauth;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import de.hatoka.common.capi.CommonConfiguration;
import de.hatoka.oauth.capi.OAuthConfiguration;
import de.hatoka.oidc.capi.IdentityProviderConfiguration;
import de.hatoka.poker.player.capi.PlayerConfiguration;
import de.hatoka.user.capi.UserConfiguration;

@Configuration
@PropertySource("classpath:application-test.properties")
@EnableAutoConfiguration
@ImportAutoConfiguration({ IdentityProviderConfiguration.class, UserConfiguration.class, CommonConfiguration.class,
                PlayerConfiguration.class, OAuthConfiguration.class })
public class OAuthTestConfiguration
{
}
