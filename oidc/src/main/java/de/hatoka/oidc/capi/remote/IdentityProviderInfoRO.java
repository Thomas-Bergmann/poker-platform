package de.hatoka.oidc.capi.remote;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdentityProviderInfoRO
{
    @NotNull
    @JsonProperty("authorizationURI")
    private String authorizationUri;

    @NotNull
    @JsonProperty("userInfoURI")
    private String userInfoUri;

    public String getAuthorizationUri()
    {
        return authorizationUri;
    }

    public void setAuthorizationUri(String authorizationUri)
    {
        this.authorizationUri = authorizationUri;
    }

    public String getUserInfoUri()
    {
        return userInfoUri;
    }

    public void setUserInfoUri(String userInfoUri)
    {
        this.userInfoUri = userInfoUri;
    }
}
