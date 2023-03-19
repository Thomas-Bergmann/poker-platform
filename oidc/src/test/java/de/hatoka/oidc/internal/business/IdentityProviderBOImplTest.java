package de.hatoka.oidc.internal.business;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.hatoka.oidc.capi.business.IdentityProviderBO;
import de.hatoka.oidc.capi.business.IdentityProviderBORepository;
import de.hatoka.oidc.capi.business.IdentityProviderRef;
import de.hatoka.oidc.capi.remote.IdentityProviderDataRO;
import tests.de.hatoka.oidc.OidcTestConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { OidcTestConfiguration.class })
class IdentityProviderBOImplTest
{
    private static final IdentityProviderRef TEST_REF = IdentityProviderRef.valueOfLocal("test");
    @Autowired
    private IdentityProviderBORepository repo;

    private IdentityProviderBO idp;

    @AfterEach
    public void removeIdentityProvider()
    {
        if (idp != null)
        {
            idp.remove();
        }
    }

    @Test
    void test()
    {
        // URI configURI = URI.create("https://keycloak/auth/realms/realm/.well-known/openid-configuration");
        URI authURI = URI.create("https://keycloak/auth/realms/realm/protocol/openid-connect/auth");
        URI tokenURI = URI.create("https://keycloak/auth/realms/realm/protocol/openid-connect/token");
        IdentityProviderDataRO data = new IdentityProviderDataRO();
        data.setName("Name to Select");
        data.setClientId("app");
        data.setPrivateClientId("service");
        data.setPrivateClientSecret("secret");
        // avoid lookup data.setOpenIDConfigurationURI(configURI.toString());
        data.setAuthenticationURI(authURI.toString());
        data.setTokenURI(tokenURI.toString());
        IdentityProviderBO idp = repo.createIdentityProvider(TEST_REF, data);
        assertEquals(authURI, URI.create(idp.getData().getAuthenticationURI()));
    }

}
