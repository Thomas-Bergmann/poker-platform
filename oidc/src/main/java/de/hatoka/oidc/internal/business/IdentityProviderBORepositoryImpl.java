package de.hatoka.oidc.internal.business;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.hatoka.common.capi.exceptions.DuplicateObjectException;
import de.hatoka.oidc.capi.business.IdentityProviderBO;
import de.hatoka.oidc.capi.business.IdentityProviderBORepository;
import de.hatoka.oidc.capi.business.IdentityProviderRef;
import de.hatoka.oidc.capi.remote.IdentityProviderDataRO;
import de.hatoka.oidc.internal.persistence.IdentityProviderDao;
import de.hatoka.oidc.internal.persistence.IdentityProviderPO;

@Component
public class IdentityProviderBORepositoryImpl implements IdentityProviderBORepository
{
    @Autowired
    private IdentityProviderDao identityProviderDao;
    @Autowired
    private IdentityProviderBOFactory factory;

    @Override
    public IdentityProviderBO createIdentityProvider(IdentityProviderRef globalRef, IdentityProviderDataRO data)
    {
        Optional<IdentityProviderPO> identityProviderOpt = identityProviderDao.findByGlobalRef(globalRef.getGlobalRef());
        if (identityProviderOpt.isPresent())
        {
            throw new DuplicateObjectException("IdentityProvider", "externalRef", globalRef.getGlobalRef());
        }
        IdentityProviderPO identityProviderPO = new IdentityProviderPO();
        identityProviderPO.setGlobalRef(globalRef.getGlobalRef());
        identityProviderPO.setOpenIDConfigurationURI(data.getOpenIDConfigurationURI());

        identityProviderPO.setPublicClientId(data.getClientId());
        identityProviderPO.setPublicAuthenticationURI(data.getAuthenticationURI());
        identityProviderPO.setPublicTokenURI(data.getTokenURI());
        identityProviderPO.setPublicTokenIssuer(data.getTokenIssuer());
        identityProviderPO.setPublicUserInfoURI(data.getUserInfoURI());

        identityProviderPO.setPrivateClientId(data.getPrivateClientId());
        identityProviderPO.setPrivateClientSecret(data.getPrivateClientSecret());

        identityProviderPO.setName(data.getName());
        return factory.get(identityProviderDao.save(identityProviderPO));
    }

    @Override
    public Optional<IdentityProviderBO> findIdentityProvider(IdentityProviderRef externalRef)
    {
        return identityProviderDao.findByGlobalRef(externalRef.getGlobalRef()).map(factory::get);
    }

    @Override
    public List<IdentityProviderBO> getAllIdentityProviders()
    {
        return identityProviderDao.findAll().stream().map(factory::get).collect(Collectors.toList());
    }
}
