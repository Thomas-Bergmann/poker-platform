package de.hatoka.oidc.internal.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityProviderDao extends JpaRepository<IdentityProviderPO, Long>
{
    /**
     * @param externalRef
     * @return identityProvider with the given login or external reference
     */
    public Optional<IdentityProviderPO> findByGlobalRef(String externalRef);

}