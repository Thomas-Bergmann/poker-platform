package de.hatoka.poker.table.capi.business;

import java.util.Objects;

public class GameRef
{
    private static final String REF_PREFIX = "game:";
    private final TableRef tableRef;
    private final Long gameNo;

    public static GameRef globalRef(String globalRef)
    {
        if (!globalRef.startsWith(REF_PREFIX))
        {
            throw new IllegalArgumentException("ref '"+globalRef+"' is not a game");
        }
        int firstAt = globalRef.indexOf("@");
        TableRef tableRef = TableRef.globalRef(globalRef.substring(firstAt + 1));
        Long gameNo = Long.valueOf(globalRef.substring(REF_PREFIX.length(), firstAt));
        return new GameRef(tableRef, gameNo);
    }

    public static GameRef localRef(TableRef tableRef, Long gameNo)
    {
        return new GameRef(tableRef, gameNo);
    }

    private GameRef(TableRef tableRef, Long gameNo)
    {
        this.tableRef = tableRef;
        this.gameNo = gameNo;
    }

    @Override
    public String toString()
    {
        return getGlobalRef();
    }

    public String getLocalRef()
    {
        return gameNo.toString();
    }

    public String getGlobalRef()
    {
        return REF_PREFIX + getLocalRef() + "@" + getTableRef().getGlobalRef();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(gameNo, getTableRef());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        GameRef other = (GameRef)obj;
        return Objects.equals(gameNo, other.gameNo) && Objects.equals(getTableRef(), other.getTableRef());
    }

    public TableRef getTableRef()
    {
        return tableRef;
    }

    public Long getGameNo()
    {
        return gameNo;
    }
}
