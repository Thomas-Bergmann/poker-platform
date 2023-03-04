package de.hatoka.poker.player.capi.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BotRO
{
    @JsonProperty("refLocal")
    private String refLocal;
    @JsonProperty("refGlobal")
    private String refGlobal;
    private BotDataRO data;
    private BotInfoRO info;

    public BotRO()
    {
    }

    public String getRefLocal()
    {
        return refLocal;
    }

    public void setRefLocal(String refLocal)
    {
        this.refLocal = refLocal;
    }


    public String getRefGlobal()
    {
        return refGlobal;
    }


    public void setRefGlobal(String refGlobal)
    {
        this.refGlobal = refGlobal;
    }

    public BotDataRO getData()
    {
        return data;
    }

    public void setData(BotDataRO data)
    {
        this.data = data;
    }

    public BotInfoRO getInfo()
    {
        return info;
    }

    public void setInfo(BotInfoRO info)
    {
        this.info = info;
    }
}
