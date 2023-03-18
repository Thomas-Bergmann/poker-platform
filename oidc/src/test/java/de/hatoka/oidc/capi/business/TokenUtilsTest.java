package de.hatoka.oidc.capi.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.hatoka.oidc.capi.business.TokenUsage;
import de.hatoka.oidc.capi.business.TokenUtils;
import de.hatoka.oidc.internal.remote.IdentityProviderTokenResponse;
import tests.de.hatoka.oidc.OidcTestConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { OidcTestConfiguration.class })
class TokenUtilsTest
{
    private static final String SUBJECT = "a-subject";
    private static final String TOKEN_PREFIX = "ey";

    @Autowired
    private TokenUtils underTest;
    @Test
    void testCreateTokenForSubject()
    {
        IdentityProviderTokenResponse tokenReponse = underTest.createTokenForSubject(SUBJECT);
        String accessToken = tokenReponse.getAccessToken();
        assertEquals(TOKEN_PREFIX, accessToken.substring(0, 2));
        assertTrue(underTest.isTokenValid(accessToken, TokenUsage.access, SUBJECT, Collections.emptyMap()));
    }

    @Test
    void testCreateTokenFromRefreshToken() throws InterruptedException
    {
        IdentityProviderTokenResponse tokenResponse = underTest.createTokenForSubject(SUBJECT);
        Thread.sleep(1200);
        IdentityProviderTokenResponse refreshResponse = underTest.createTokenFromRefreshToken(tokenResponse.getRefreshToken());
        assertTrue(underTest.isTokenValid(refreshResponse.getAccessToken(), TokenUsage.access, SUBJECT, Collections.emptyMap()));
        assertEquals(underTest.getAuthenticatedAt(tokenResponse.getAccessToken()), underTest.getAuthenticatedAt(refreshResponse.getAccessToken()));
    }
}
