package de.hatoka.oidc.capi.event;

import org.springframework.context.ApplicationEvent;

import de.hatoka.user.capi.business.UserRef;

/**
 * Contains hooks inside user registration
 */
public class UserTokenGeneratedEvent extends ApplicationEvent 
{
    private static final long serialVersionUID = -4977845473624629649L;
    private final UserRef userRef;
    
    public UserTokenGeneratedEvent(Object source, UserRef userRef)
    {
        super(source);
        this.userRef = userRef;
    }

    public UserRef getUserRef()
    {
        return userRef;
    }
}
