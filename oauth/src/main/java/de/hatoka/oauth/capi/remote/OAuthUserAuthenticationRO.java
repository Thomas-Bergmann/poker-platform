package de.hatoka.oauth.capi.remote;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthUserAuthenticationRO
{
    @JsonProperty("idp")
    @NotNull
    private String idpRef;

    public String getIdpRef()
    {
        return idpRef;
    }

    public void setIdpRef(String idpRef)
    {
        this.idpRef = idpRef;
    }
}
