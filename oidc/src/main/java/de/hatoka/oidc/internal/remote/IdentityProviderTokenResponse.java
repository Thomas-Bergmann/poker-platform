package de.hatoka.oidc.internal.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdentityProviderTokenResponse
{
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("id_token")
    private String idToken;

    @JsonProperty("expires_in")
    private Long expiresIn;
    @JsonProperty("refresh_expires_in")
    private Long refreshExpiresIn;

    @JsonProperty("not-before-policy")
    private Long notBeforePolicy;

    @JsonProperty("session_state")
    private String sessionState;

    @JsonProperty("scope")
    private String scope;

    public String getTokenType()
    {
        return tokenType;
    }

    public void setTokenType(String tokenType)
    {
        this.tokenType = tokenType;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
    }

    public String getRefreshToken()
    {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken)
    {
        this.refreshToken = refreshToken;
    }

    public String getIdToken()
    {
        return idToken;
    }

    public void setIdToken(String idToken)
    {
        this.idToken = idToken;
    }

    public Long getExpiresIn()
    {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn)
    {
        this.expiresIn = expiresIn;
    }

    public Long getRefreshExpiresIn()
    {
        return refreshExpiresIn;
    }

    public void setRefreshExpiresIn(Long refreshExpiresIn)
    {
        this.refreshExpiresIn = refreshExpiresIn;
    }

    public Long getNotBeforePolicy()
    {
        return notBeforePolicy;
    }

    public void setNotBeforePolicy(Long notBeforePolicy)
    {
        this.notBeforePolicy = notBeforePolicy;
    }

    public String getSessionState()
    {
        return sessionState;
    }

    public void setSessionState(String sessionState)
    {
        this.sessionState = sessionState;
    }

    public String getScope()
    {
        return scope;
    }

    public void setScope(String scope)
    {
        this.scope = scope;
    }
}
