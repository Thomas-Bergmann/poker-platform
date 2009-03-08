package de.hatoka.oidc.capi.event;

import org.springframework.context.ApplicationEvent;

import de.hatoka.oidc.capi.remote.TokenResponse;
import de.hatoka.user.capi.business.UserRef;

/**
 * Contains hooks inside user registration
 */
public class UserTokenGeneratedEvent extends ApplicationEvent 
{
    private static final long serialVersionUID = -4977845473624629649L;
    private final UserRef userRef;
    private final TokenResponse token;
    
    public UserTokenGeneratedEvent(Object source, UserRef userRef, TokenResponse token)
    {
        super(source);
        this.userRef = userRef;
        this.token = token;
    }

    public UserRef getUserRef()
    {
        return userRef;
    }

    public TokenResponse getToken()
    {
        return token;
    }
}
