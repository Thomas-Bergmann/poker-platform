package de.hatoka.oidc.internal.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityProviderUserMapDao extends JpaRepository<IdentityProviderUserMapPO, Long>
{
    /**
     * @param externalRef
     * @return identityProvider with the given login or external reference
     */
    public Optional<IdentityProviderUserMapPO> findByIdentityProviderIDAndSubject(Long identityProviderID, String subject);

}