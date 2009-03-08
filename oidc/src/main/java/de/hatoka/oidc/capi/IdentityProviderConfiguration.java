package de.hatoka.oidc.capi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import de.hatoka.oidc.internal.business.IdentityProviderBORepositoryImpl;
import de.hatoka.oidc.internal.persistence.IdentityProviderDao;
import de.hatoka.oidc.internal.persistence.IdentityProviderPO;
import de.hatoka.oidc.internal.remote.IdentityProviderController;
import de.hatoka.oidc.internal.remote.IdentityProviderInterceptor;

@Configuration
@EntityScan(basePackageClasses = { IdentityProviderPO.class })
@EnableJpaRepositories(basePackageClasses = { IdentityProviderDao.class })
@ComponentScan(basePackageClasses = { IdentityProviderBORepositoryImpl.class, IdentityProviderController.class })
public class IdentityProviderConfiguration implements WebMvcConfigurer
{
    @Value("${intershop.oidc.identityprovider.authenticationToken}")
    private String oidcAuthenticationToken;

    @Autowired
    private IdentityProviderInterceptor identityProviderInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        identityProviderInterceptor.setOidcAuthenticationToken(getOidcAuthenticationToken());
        registry.addInterceptor(identityProviderInterceptor);
    }

    public String getOidcAuthenticationToken()
    {
        return oidcAuthenticationToken;
    }
}
