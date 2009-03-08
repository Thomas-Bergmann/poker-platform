package de.hatoka.oidc.internal.persistence;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

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
    name = "oidc_id_provider",
    uniqueConstraints={ @UniqueConstraint(columnNames= "idp_ref") }
)
public class IdentityProviderPO implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identityprovider_id")
    private Long internalId;

    @NotNull
    @Column(name = "idp_ref", nullable = false)
    private String globalRef;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * token validity period in seconds
     */
    @NotNull
    @Column(name = "token_validity", nullable = false)
    private long tokenValidityPeriod = 60;

    /**
     * APP and SERVICE AUTHENTICATION
     */
    @Column(name = "openid_config_uri", nullable = true, length=4095)
    private String openIDConfigurationURI;

    /**
     * APP AUTHENTICATION
     */
    @NotNull
    @Column(name = "public_clientid", nullable = false)
    private String publicClientId;

    /**
     * public URIs to override the openid config
     */
    @Column(name = "public_auth_uri", nullable = true, length=4095)
    private String publicAuthenticationURI;
    @Column(name = "public_token_uri", nullable = true, length=4095)
    private String publicTokenURI;
    @Column(name = "public_token_issuer", nullable = true, length=4095)
    private String publicTokenIssuer;
    @Column(name = "public_userinfo_uri", nullable = true, length=4095)
    private String publicUserInfoURI;

    /**
     * SERVICE AUTHENTICATION
     */
    @NotNull
    @Column(name = "private_clientid", nullable = false)
    private String privateClientId;
    @NotNull
    @Column(name = "private_client_secret", nullable = false)
    private String privateClientSecret;

    @NotNull
    @Column(name = "access_token_secret", nullable = false)
    private String accessTokenSecret = UUID.randomUUID().toString();

    public Long getInternalId()
    {
        return internalId;
    }

    public void setInternalId(Long internalId)
    {
        this.internalId = internalId;
    }

    public String getGlobalRef()
    {
        return globalRef;
    }

    public void setGlobalRef(String globalRef)
    {
        this.globalRef = globalRef;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPublicClientId()
    {
        return publicClientId;
    }

    public void setPublicClientId(String publicClientId)
    {
        this.publicClientId = publicClientId;
    }

    public String getPublicAuthenticationURI()
    {
        return publicAuthenticationURI;
    }

    public void setPublicAuthenticationURI(String publicAuthenticationURI)
    {
        this.publicAuthenticationURI = publicAuthenticationURI;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(globalRef);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IdentityProviderPO other = (IdentityProviderPO)obj;
        return Objects.equals(globalRef, other.globalRef);
    }

    public long getTokenValidityPeriod()
    {
        return tokenValidityPeriod;
    }

    public void setTokenValidityPeriod(long tokenValidityPeriod)
    {
        this.tokenValidityPeriod = tokenValidityPeriod;
    }

    public String getPublicTokenURI()
    {
        return publicTokenURI;
    }

    public void setPublicTokenURI(String publicTokenURI)
    {
        this.publicTokenURI = publicTokenURI;
    }

    public String getAccessTokenSecret()
    {
        return accessTokenSecret;
    }

    public void setAccessTokenSecret(String accessTokenSecret)
    {
        this.accessTokenSecret = accessTokenSecret;
    }

    public String getPublicUserInfoURI()
    {
        return publicUserInfoURI;
    }

    public void setPublicUserInfoURI(String publicUserInfoURI)
    {
        this.publicUserInfoURI = publicUserInfoURI;
    }

    public String getPublicTokenIssuer()
    {
        return publicTokenIssuer;
    }

    public void setPublicTokenIssuer(String publicTokenIssuer)
    {
        this.publicTokenIssuer = publicTokenIssuer;
    }

    public String getPrivateClientId()
    {
        return privateClientId;
    }

    public void setPrivateClientId(String privateClientId)
    {
        this.privateClientId = privateClientId;
    }

    public String getPrivateClientSecret()
    {
        return privateClientSecret;
    }

    public void setPrivateClientSecret(String privateClientSecret)
    {
        this.privateClientSecret = privateClientSecret;
    }

    public String getOpenIDConfigurationURI()
    {
        return openIDConfigurationURI;
    }

    public void setOpenIDConfigurationURI(String openIDConfigurationURI)
    {
        this.openIDConfigurationURI = openIDConfigurationURI;
    }
}
