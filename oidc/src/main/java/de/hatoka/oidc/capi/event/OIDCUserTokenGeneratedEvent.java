package de.hatoka.oidc.capi.event;

import org.springframework.context.ApplicationEvent;

import de.hatoka.oidc.capi.remote.OIDCUserInfo;

/**
 * Contains hooks inside user registration
 */
public class OIDCUserTokenGeneratedEvent extends ApplicationEvent
{
    private static final long serialVersionUID = -4977845473624629649L;
    private final OIDCUserInfo info;

    public OIDCUserTokenGeneratedEvent(Object source, OIDCUserInfo info)
    {
        super(source);
        this.info = info;
    }

    public OIDCUserInfo getInfo()
    {
        return info;
    }
}