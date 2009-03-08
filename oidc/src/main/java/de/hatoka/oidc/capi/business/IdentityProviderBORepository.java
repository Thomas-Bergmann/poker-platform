package de.hatoka.oidc.capi.business;

import java.util.List;
import java.util.Optional;

import de.hatoka.oidc.capi.remote.IdentityProviderDataRO;

public interface IdentityProviderBORepository
{
    /**
     * Creates an identityProvider
     *
     * @param externalRef
     *            (identifier declared by authentication provider)
     * @return identityProvider
     */
    IdentityProviderBO createIdentityProvider(IdentityProviderRef externalRef, IdentityProviderDataRO data);

    /**
     * Retrieves a identityProvider via identifier
     *
     * @param externalRef
     *            (identifier declared by authentication provider)
     * @return
     */
    Optional<IdentityProviderBO> findIdentityProvider(IdentityProviderRef externalRef);

    List<IdentityProviderBO> getAllIdentityProviders();

    /**
     * Removes all identityProviders of this repository
     */
    default void clear()
    {
        getAllIdentityProviders().forEach(IdentityProviderBO::remove);
    }
}
