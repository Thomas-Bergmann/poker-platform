package de.hatoka.poker.remote.oauth;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthRefreshRO
{
    @JsonProperty("token")
    @NotNull
    private String refreshToken;

    public String getRefreshToken()
    {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken)
    {
        this.refreshToken = refreshToken;
    }
}
