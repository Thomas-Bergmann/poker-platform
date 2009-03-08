package de.hatoka.oidc.capi.business;

import java.util.Set;

import de.hatoka.oidc.capi.remote.IdentityProviderDataRO;
import de.hatoka.oidc.capi.remote.OIDCUserInfo;
import de.hatoka.oidc.capi.remote.TokenResponse;

public interface IdentityProviderBO
{
    /**
     * @return the identifier of the identityProvider
     */
    IdentityProviderRef getRef();

    /**
     * Removes this identityProvider
     */
    void remove();

    /**
     * @return the name of the identity provider so user can select a provider
     */
    String getName();

    /**
     * @param issuer URI generates the token
     * @param idToken identity_token from identity provider
     * @param scopes resource URIs used for this token
     * @return tokens (access_token, refresh_token, identity_token)
     */
    TokenResponse generateToken(String issuer, String idToken, Set<String> scopes);

    /**
     * Extracts user information from id token
     * @param idToken identity token from identity provider
     * @return user information stored in token
     */
    OIDCUserInfo getUserInfo(String idToken);

    Long getTokenValidityPeriod();

    IdentityProviderDataRO getData();
}
