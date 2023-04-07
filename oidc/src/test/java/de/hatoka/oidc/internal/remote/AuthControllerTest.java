/*
 * Copyright (C) Intershop Communications AG - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * The content is proprietary and confidential.
 * Intershop Communication AG, Intershop Tower, 07740 Jena, Germany, 2018-04-05
 */
package de.hatoka.oidc.internal.remote;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MultiValueMap;

import de.hatoka.oidc.capi.IdentityProviderConfiguration;
import de.hatoka.oidc.capi.business.IdentityProviderRef;
import de.hatoka.oidc.capi.remote.IdentityProviderDataRO;
import de.hatoka.oidc.capi.remote.IdentityProviderRO;
import tests.de.hatoka.oidc.OidcTestApplication;
import tests.de.hatoka.oidc.OidcTestConfiguration;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { OidcTestApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { OidcTestConfiguration.class })
@ActiveProfiles("test")
public class AuthControllerTest
{
    private static final URI URI_CONFIG_KEYCLOAK = URI.create("https://keycloak/auth/realms/realm/.well-known/openid-configuration");
    private static final URI URI_AUTH_KEYCLOAK = URI.create("https://keycloak/auth/realms/realm/openid-connect/auth");
    private static final URI URI_TOKEN_KEYCLOAK = URI.create("https://keycloak/auth/realms/realm/openid-connect/token");
    private static final IdentityProviderRef IDP_REF_KEYCLOAK = IdentityProviderRef.valueOfLocal("keycloak");
    private static final IdentityProviderRef IDP_REF_AZURE = IdentityProviderRef.valueOfLocal("microsoft");
    private static final String BEARER_LC = IdentityProviderController.BEARER_PREFIX;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private IdentityProviderConfiguration configuration;

    @Test
    public void testGetIdentityProviders()
    {
        IdentityProviderDataRO keycloakData = getProviderInfo(
                        "Keycloak",
                        URI_CONFIG_KEYCLOAK,
                        URI_AUTH_KEYCLOAK,
                        URI_TOKEN_KEYCLOAK,
                        "keycloak", "geheim");
        putIdentityProvider(IDP_REF_KEYCLOAK, keycloakData);

        IdentityProviderDataRO azureData = getProviderInfo(
                        "Microsoft",
                        URI.create("https://login.microsoftonline.com/tenantid/oauth2/v2.0/.well-known/openid-configuration"),
                        URI.create("https://login.microsoftonline.com/tenantid/oauth2/v2.0/authorize"),
                        URI.create("https://login.microsoftonline.com/tenantid/oauth2/v2.0/token"),
                        "azure", "secret");
        putIdentityProvider(IDP_REF_AZURE, azureData);

        List<IdentityProviderRO> publicIPS = getIdentityProviders();
        assertEquals(2, publicIPS.size());
        IdentityProviderRO idpKeycloak = publicIPS.get(0);
        assertEquals(IDP_REF_KEYCLOAK.getGlobalRef(), idpKeycloak.getGlobalRef());
        assertEquals("Keycloak", idpKeycloak.getData().getName());
        assertEquals(URI_AUTH_KEYCLOAK, URI.create(idpKeycloak.getData().getAuthenticationURI()));
        assertEquals(URI_TOKEN_KEYCLOAK, URI.create(idpKeycloak.getData().getTokenURI()));
        // assertEquals(URI_CONFIG_KEYCLOAK, URI.create(idpKeycloak.getData().getOpenIDConfigurationURI()));
        // test controller in localhost context
        URI accessTokenURI = URI.create(idpKeycloak.getInfo().getAuthorizationUri());
        assertEquals("localhost", accessTokenURI.getHost());
        assertEquals("/auth/users/token", accessTokenURI.getPath());

        deleteIdentityProvider(IDP_REF_KEYCLOAK);
        deleteIdentityProvider(IDP_REF_AZURE);
    }

    private IdentityProviderDataRO getProviderInfo(String name, URI openIdConfigURI, URI authURI, URI tokenURI, String client, String secret)
    {
        IdentityProviderDataRO data = new IdentityProviderDataRO();
        data.setName(name);
        data.setClientId(client + "-app");
        data.setPrivateClientId(client + "-service");
        data.setPrivateClientSecret(secret);
        // data.setOpenIDConfigurationURI(openIdConfigURI.toString());
        data.setAuthenticationURI(authURI.toString());
        data.setTokenURI(tokenURI.toString());
        return data;
    }
    private List<IdentityProviderRO> getIdentityProviders()
    {
        return Arrays.asList(this.restTemplate.getForObject(IdentityProviderController.QUERY_PATH_IDPS, IdentityProviderRO[].class));
    }

    private MultiValueMap<String, String> getHeaders()
    {
        HttpHeaders result = new HttpHeaders();
        result.add(HttpHeaders.AUTHORIZATION, BEARER_LC + configuration.getOidcAuthenticationToken());
        return result;
    }

    private void putIdentityProvider(IdentityProviderRef ref, IdentityProviderDataRO data)
    {
        HttpEntity<IdentityProviderDataRO> httpEntity = new HttpEntity<>(data, getHeaders());
        ResponseEntity<Void> response = this.restTemplate.exchange(IdentityProviderController.PATH_IDP, HttpMethod.PUT, httpEntity, Void.class, ref.getLocalRef());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    private void deleteIdentityProvider(IdentityProviderRef ref)
    {
        HttpEntity<Void> httpEntity = new HttpEntity<>(getHeaders());
        ResponseEntity<Void> response = this.restTemplate.exchange(IdentityProviderController.PATH_IDP, HttpMethod.DELETE, httpEntity, Void.class, ref.getLocalRef());
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }
}
