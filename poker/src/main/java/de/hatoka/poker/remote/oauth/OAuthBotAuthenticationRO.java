package de.hatoka.poker.remote.oauth;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthBotAuthenticationRO
{
    @JsonProperty("bot")
    @NotNull
    private String botRef;

    @JsonProperty("key")
    @NotNull
    private String apiKey;

    public String getApiKey()
    {
        return apiKey;
    }

    public void setApiKey(String apiKey)
    {
        this.apiKey = apiKey;
    }

    public String getBotRef()
    {
        return botRef;
    }

    public void setBotRef(String botRef)
    {
        this.botRef = botRef;
    }

}
