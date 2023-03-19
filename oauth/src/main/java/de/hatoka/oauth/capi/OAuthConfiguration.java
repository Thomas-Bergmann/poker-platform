package de.hatoka.oauth.capi;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import de.hatoka.oauth.capi.business.TokenUtils;
import de.hatoka.oauth.internal.remote.PolicyEnforcerInterceptor;

@Configuration
@ComponentScan(basePackageClasses = { TokenUtils.class, PolicyEnforcerInterceptor.class })
public class OAuthConfiguration
{
}
