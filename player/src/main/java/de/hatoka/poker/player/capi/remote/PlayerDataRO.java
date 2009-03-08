package de.hatoka.poker.player.capi.remote;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerDataRO
{
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
}
