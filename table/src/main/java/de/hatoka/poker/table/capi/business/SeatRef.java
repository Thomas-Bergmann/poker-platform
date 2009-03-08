package de.hatoka.poker.table.capi.business;

import java.util.Objects;

public class SeatRef
{
    private static final String REF_PREFIX = "seat:";
    private final TableRef tableRef;
    private final Integer position;

    public static SeatRef globalRef(String globalRef)
    {
        if (!globalRef.startsWith(REF_PREFIX))
        {
            throw new IllegalArgumentException("ref '"+globalRef+"' is not a seat");
        }
        int firstAt = globalRef.indexOf("@");
        TableRef tableRef = TableRef.globalRef(globalRef.substring(firstAt + 1));
        Integer position = Integer.valueOf(globalRef.substring(REF_PREFIX.length(), firstAt));
        return new SeatRef(tableRef, position);
    }

    public static SeatRef localRef(TableRef tableRef, Integer position)
    {
        return new SeatRef(tableRef, position);
    }

    private SeatRef(TableRef tableRef, Integer position)
    {
        this.tableRef = tableRef;
        this.position = position;
    }

    @Override
    public String toString()
    {
        return getGlobalRef();
    }

    public String getLocalRef()
    {
        return getPosition().toString();
    }

    public String getGlobalRef()
    {
        return REF_PREFIX + getLocalRef() + "@" + getTableRef().getGlobalRef();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getPosition(), getTableRef());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SeatRef other = (SeatRef)obj;
        return Objects.equals(getPosition(), other.getPosition()) && Objects.equals(getTableRef(), other.getTableRef());
    }

    public TableRef getTableRef()
    {
        return tableRef;
    }

    public Integer getPosition()
    {
        return position;
    }
}
