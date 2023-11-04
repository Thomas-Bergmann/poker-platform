package de.hatoka.poker.player.capi.remote;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerDataRO
{
    @JsonProperty("nick-name")
    @NotNull
    private String nickName;

    @JsonProperty("owner-ref")
    @NotNull
    private String ownerRef;

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
}
