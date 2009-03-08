package de.hatoka.oidc.capi.business;

import java.util.Objects;

public class IdentityProviderRef
{
    private static final String USER_REF_PREFIX = "identityProvider:";
    private final String localRef;

    /**
     * @param localRef local identityProvider reference (like "abc")
     * @return identityProvider ref
     */
    public static IdentityProviderRef valueOfLocal(String localRef)
    {
        return new IdentityProviderRef(localRef);
    }

    /**
     * @param globalRef global identityProvider reference (like "identityProvider:abc")
     * @return
     */
    public static IdentityProviderRef valueOfGlobal(String globalRef)
    {
        if (globalRef.startsWith(USER_REF_PREFIX))
        {
            return new IdentityProviderRef(globalRef.substring(USER_REF_PREFIX.length()));
        }
        throw new IllegalArgumentException("ref '"+globalRef+"' is not a identityProvider");
    }

    private IdentityProviderRef(String localRef)
    {
        this.localRef = localRef;
    }

    public String getLocalRef()
    {
        return localRef;
    }

    public String getGlobalRef()
    {
        return USER_REF_PREFIX + localRef;
    }

    @Override
    public String toString()
    {
        return getGlobalRef();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(localRef);
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
        IdentityProviderRef other = (IdentityProviderRef)obj;
        return Objects.equals(localRef, other.localRef);
    }
}
