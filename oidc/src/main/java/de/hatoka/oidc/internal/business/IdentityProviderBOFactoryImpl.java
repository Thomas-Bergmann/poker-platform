package de.hatoka.oidc.internal.business;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import de.hatoka.oidc.capi.business.IdentityProviderBO;
import de.hatoka.oidc.internal.persistence.IdentityProviderPO;

@Component
public class IdentityProviderBOFactoryImpl implements IdentityProviderBOFactory
{
    @Lookup
    @Override
    public IdentityProviderBO get(IdentityProviderPO groupPO)
    {
        // done by @Lookup
        return null;
    }
}
