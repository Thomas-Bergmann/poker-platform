package de.hatoka.poker.table.capi.business;

import java.util.Objects;

public class TableRef
{
    private static final String REF_PREFIX = "table:";
    private final String name;

    public static TableRef globalRef(String globalRef)
    {
        if (!globalRef.startsWith(REF_PREFIX))
        {
            throw new IllegalArgumentException("ref '"+globalRef+"' is not a table");
        }
        String[] find = globalRef.split(":");
        return new TableRef(find[1]);
    }

    public static TableRef localRef(String name)
    {
        return new TableRef(name);
    }

    private TableRef(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return getGlobalRef();
    }

    public String getLocalRef()
    {
        return name;
    }

    public String getGlobalRef()
    {
        return REF_PREFIX + getLocalRef();
    }

    public String getName()
    {
        return name;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TableRef other = (TableRef)obj;
        return Objects.equals(name, other.name);
    }
}
