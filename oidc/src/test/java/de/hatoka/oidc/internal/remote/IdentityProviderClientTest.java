package de.hatoka.oidc.internal.remote;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import tests.de.hatoka.oidc.OidcTestConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { OidcTestConfiguration.class })
class IdentityProviderClientTest
{
    private static final String ISH_AZURE_CONFIG_URI = "https://login.microsoftonline.com/13a9f4be-a9d5-4987-bdf2-f31cc074034f/v2.0/.well-known/openid-configuration";
    private static final String ISH_AZURE_TOKEN_URI = "https://login.microsoftonline.com/13a9f4be-a9d5-4987-bdf2-f31cc074034f/oauth2/v2.0/token";

    @Autowired
    private IdentityProviderClient underTest;
    @Test
    void test()
    {
        IdentityProviderMetaDataResponse metaData = underTest.getMetaData(ISH_AZURE_CONFIG_URI);
        assertEquals(ISH_AZURE_TOKEN_URI, metaData.getTokenEndpoint());
    }
}
