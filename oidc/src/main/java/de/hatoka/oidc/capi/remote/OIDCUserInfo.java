package de.hatoka.oidc.capi.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.hatoka.user.capi.business.UserInfo;

/**
 * OIDCUserInfo contains information about the user. Retrieved from identity token.
 * <ul>
 * <li>subject - identifier at identity provider</li>
 * <li>name - full name of user</li>
 * <li>given name</li>
 * <li>family name</li>
 * <li>email</li>
 */
public class OIDCUserInfo implements UserInfo
{
    @JsonProperty("sub")
    private String subject;
    @JsonProperty("name")
    private String fullName;
    @JsonProperty("given_name")
    private String givenName;
    @JsonProperty("family_name")
    private String familyName;
    @JsonProperty("email")
    private String eMail;
    @JsonProperty("preferred_username")
    private String preferredUsername;

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    @Override
    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    @Override
    public String getGivenName()
    {
        return givenName;
    }

    public void setGivenName(String givenName)
    {
        this.givenName = givenName;
    }

    @Override
    public String getFamilyName()
    {
        return familyName;
    }

    public void setFamilyName(String familyName)
    {
        this.familyName = familyName;
    }

    @Override
    public String geteMail()
    {
        return eMail;
    }

    public void seteMail(String eMail)
    {
        this.eMail = eMail;
    }

    @Override
    public String toString()
    {
        return "OIDCUserInfo [subject=" + subject + ", fullName=" + fullName + ", givenName=" + givenName
                        + ", familyName=" + familyName + ", eMail=" + eMail + "]";
    }

    public String getPreferredUsername()
    {
        return preferredUsername;
    }

    public void setPreferredUsername(String preferredUsername)
    {
        this.preferredUsername = preferredUsername;
    }

}
