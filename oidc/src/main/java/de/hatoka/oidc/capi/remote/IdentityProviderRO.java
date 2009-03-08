package de.hatoka.oidc.capi.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdentityProviderRO
{
    @JsonProperty("localRef")
    private String localRef;
    @JsonProperty("gobalRef")
    private String globalRef;
    @JsonProperty("data")
    private IdentityProviderDataRO data;
    @JsonProperty("info")
    private IdentityProviderInfoRO info;

    public String getGlobalRef()
    {
        return globalRef;
    }

    public void setGlobalRef(String globalRef)
    {
        this.globalRef = globalRef;
    }

    public IdentityProviderDataRO getData()
    {
        return data;
    }

    public void setData(IdentityProviderDataRO data)
    {
        this.data = data;
    }

    public IdentityProviderInfoRO getInfo()
    {
        return info;
    }

    public void setInfo(IdentityProviderInfoRO info)
    {
        this.info = info;
    }

    public String getLocalRef()
    {
        return localRef;
    }

    public void setLocalRef(String localRef)
    {
        this.localRef = localRef;
    }
}
