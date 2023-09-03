package de.hatoka.poker.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SeatRO
{
    @JsonProperty("refLocal")
    private String refLocal;
    @JsonProperty("refGlobal")
    private String refGlobal;
    @JsonProperty("resourceURI")
    private String resourceURI;
    private SeatDataRO data;
    private SeatInfoRO info;
    private SeatGameInfoRO game;

    public SeatRO()
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


	public SeatInfoRO getInfo() {
		return info;
	}


	public void setInfo(SeatInfoRO info) {
		this.info = info;
	}

    public SeatDataRO getData()
    {
        return data;
    }

    public void setData(SeatDataRO data)
    {
        this.data = data;
    }

    public String getResourceURI()
    {
        return resourceURI;
    }

    public void setResourceURI(String resourceURI)
    {
        this.resourceURI = resourceURI;
    }

    public SeatGameInfoRO getGame()
    {
        return game;
    }

    public void setGame(SeatGameInfoRO game)
    {
        this.game = game;
    }
}
