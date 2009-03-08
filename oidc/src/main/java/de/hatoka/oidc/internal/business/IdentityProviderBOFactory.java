package de.hatoka.oidc.internal.business;

import de.hatoka.oidc.capi.business.IdentityProviderBO;
import de.hatoka.oidc.internal.persistence.IdentityProviderPO;

public interface IdentityProviderBOFactory
{
    IdentityProviderBO get(IdentityProviderPO identityProviderPO);
}
