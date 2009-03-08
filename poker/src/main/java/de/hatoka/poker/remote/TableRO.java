package de.hatoka.poker.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TableRO
{
    @JsonProperty("refLocal")
    private String refLocal;
    @JsonProperty("refGlobal")
    private String refGlobal;
    @JsonProperty("resourceURI")
    private String resourceURI;

    private TableInfoRO info;

    public TableRO()
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


	public TableInfoRO getInfo() {
		return info;
	}


	public void setInfo(TableInfoRO info) {
		this.info = info;
	}

    public String getResourceURI()
    {
        return resourceURI;
    }

    public void setResourceURI(String resourceURI)
    {
        this.resourceURI = resourceURI;
    }
}
