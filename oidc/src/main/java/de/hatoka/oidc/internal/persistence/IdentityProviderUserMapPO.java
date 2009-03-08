package de.hatoka.oidc.internal.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(
    name = "oidc_id_provider_map",
    uniqueConstraints={ @UniqueConstraint(columnNames= {"identityprovider_id", "subject"}) }
)
public class IdentityProviderUserMapPO implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identityprovider_map_id")
    private Long internalId;

    @NotNull
    @Column(name = "identityprovider_id", nullable = false)
    private Long identityProviderID;

    @NotNull
    @Column(name = "subject", nullable = false)
    private String subject;

    @NotNull
    @Column(name = "user_ref", nullable = false)
    private String userRef;

    public Long getInternalId()
    {
        return internalId;
    }

    public void setInternalId(Long internalId)
    {
        this.internalId = internalId;
    }

    public Long getIdentityProviderID()
    {
        return identityProviderID;
    }

    public void setIdentityProviderID(Long identityProviderID)
    {
        this.identityProviderID = identityProviderID;
    }

    public String getUserRef()
    {
        return userRef;
    }

    public void setUserRef(String userRef)
    {
        this.userRef = userRef;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }
}
