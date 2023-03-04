package de.hatoka.poker.remote;

import javax.validation.constraints.NotNull;

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
