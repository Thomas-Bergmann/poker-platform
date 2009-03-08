package de.hatoka.oidc.internal.remote;

import org.springframework.stereotype.Component;

import de.hatoka.oidc.capi.business.IdentityProviderBO;
import de.hatoka.oidc.capi.remote.IdentityProviderDataRO;
import de.hatoka.oidc.capi.remote.IdentityProviderInfoRO;
import de.hatoka.oidc.capi.remote.IdentityProviderRO;

@Component
public class IdentityProviderBO2RO
{
    public IdentityProviderRO apply(IdentityProviderBO idp, IdentityProviderInfoRO info)
    {
        IdentityProviderDataRO data = idp.getData();

        IdentityProviderRO result = new IdentityProviderRO();
        result.setLocalRef(idp.getRef().getLocalRef());
        result.setGlobalRef(idp.getRef().getGlobalRef());
        result.setData(data);
        result.setInfo(info);
        return result;
    }
}
