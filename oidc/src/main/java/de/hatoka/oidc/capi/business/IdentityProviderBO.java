package de.hatoka.oidc.capi.business;

import de.hatoka.oidc.capi.remote.IdentityProviderDataRO;
import de.hatoka.oidc.capi.remote.OIDCUserInfo;

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
     * Extracts user information from id token
     * @param idToken identity token from identity provider
     * @return user information stored in token
     */
    OIDCUserInfo getUserInfo(String idToken);

    /**
     * @return data which can be set to identify users at external identity provider
     */
    IdentityProviderDataRO getData();
}
