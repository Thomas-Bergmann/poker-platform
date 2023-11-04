package de.hatoka.oidc.capi.remote;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdentityProviderDataRO
{
    @NotNull
    @JsonProperty("name")
    private String name;

    /**
     * Client Identifier
     */
    @NotNull
    @JsonProperty("clientId")
    private String clientId;

    /**
     * URI to authenticate user ( https://keycloak/auth/realms/icp-realm/protocol/openid-connect/auth)
     */
    @NotNull
    @JsonProperty("authenticationURI")
    private String authenticationURI;

    /**
     * URI to get token from user for code flow ( https://keycloak/auth/realms/icp-realm/protocol/openid-connect/token)
     */
    @NotNull
    @JsonProperty("tokenURI")
    private String tokenURI;

    /**
     * issuer token from user for code flow ( https://keycloak/auth/realms/icp-realm/protocol/openid-connect/token)
     */
    @JsonProperty("tokenIssuer")
    private String tokenIssuer;

    /**
     * URI to user information after code flow ( https://keycloak/auth/realms/icp-realm/protocol/openid-connect/userinfo)
     */
    @JsonProperty("userinfoURI")
    private String userInfoURI;

    /**
     * Client Identifier
     */
    @NotNull
    @JsonProperty("privateClientId")
    private String privateClientId;

    /**
     * Client Secret
     */
    @NotNull
    @JsonProperty("privateClientSecret")
    private String privateClientSecret;

    /**
     * .MetaData .well-known/openid-configuration
     */
    @NotNull
    @JsonProperty("openIDConfigurationURI")
    private String openIDConfigurationURI;

    public String getClientId()
    {
        return clientId;
    }

    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    public String getAuthenticationURI()
    {
        return authenticationURI;
    }

    public void setAuthenticationURI(String authenticationURI)
    {
        this.authenticationURI = authenticationURI;
    }

    public String getTokenURI()
    {
        return tokenURI;
    }

    public void setTokenURI(String tokenURI)
    {
        this.tokenURI = tokenURI;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUserInfoURI()
    {
        return userInfoURI;
    }

    public void setUserInfoURI(String userInfoURI)
    {
        this.userInfoURI = userInfoURI;
    }

    public String getTokenIssuer()
    {
        return tokenIssuer;
    }

    public void setTokenIssuer(String tokenIssuer)
    {
        this.tokenIssuer = tokenIssuer;
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
