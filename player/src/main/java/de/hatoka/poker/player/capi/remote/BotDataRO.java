package de.hatoka.poker.player.capi.remote;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BotDataRO
{
    @JsonProperty("owner-ref")
    @NotNull
    private String ownerRef;

    @JsonProperty("nick-name")
    @NotNull
    private String nickName;

    @JsonProperty("api-key")
    @NotNull
    private String apiKey;

    public String getOwnerRef()
    {
        return ownerRef;
    }

    public void setOwnerRef(String ownerRef)
    {
        this.ownerRef = ownerRef;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getApiKey()
    {
        return apiKey;
    }

    public void setApiKey(String apiKey)
    {
        this.apiKey = apiKey;
    }
}
