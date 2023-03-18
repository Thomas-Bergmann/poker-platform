package de.hatoka.oauth.internal.remote;

import java.net.URI;
import java.util.Enumeration;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import de.hatoka.oauth.capi.business.TokenUsage;
import de.hatoka.oauth.capi.business.TokenUtils;
import de.hatoka.oidc.capi.IdentityProviderConfiguration;
import de.hatoka.oidc.internal.remote.IdentityProviderController;

@Component
public class PolicyEnforcerInterceptor implements HandlerInterceptor
{
    private static final Logger logger = LoggerFactory.getLogger(PolicyEnforcerInterceptor.class);
    public static final String BEARER_LC = "bearer ";
    private static int BEARER_LENGTH = BEARER_LC.length();

    @Autowired
    private IdentityProviderConfiguration oidcConfig;

    @Autowired
    private TokenUtils tokenUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        // allow preflight requests
        if (HttpMethod.OPTIONS.matches(request.getMethod()))
        {
            return true;
        }
        URI uri = URI.create(request.getRequestURI());
        Optional<String> bearer = getBearerToken(request);
        if (uri.getPath().startsWith(IdentityProviderController.PATH_ROOT))
        {
            // allow retrieving all information about identity providers
            if (HttpMethod.GET.matches(request.getMethod()))
            {
                logger.trace("Client allowed to access uri '{}'' with method '{}'", uri.getPath(), request.getMethod());
                return true;
            }
            // allow retrieving token from identity provider
            if (HttpMethod.POST.matches(request.getMethod()) && uri.getPath().endsWith(IdentityProviderController.METHOD_TOKEN))
            {
                logger.trace("Client allowed to access uri '{}'' with method '{}'", uri.getPath(), request.getMethod());
                return true;
            }
            if(bearer.isPresent() && bearer.get().equals(oidcConfig.getOidcAuthenticationToken()))
            {
                return true;
            }
        }
        else
        {
            if(bearer.isPresent() && tokenUtils.isTokenValid(bearer.get(), TokenUsage.access))
            {
                return true;
            }
        }
        response.setStatus(bearer.isPresent() ? HttpStatus.FORBIDDEN.value() : HttpStatus.UNAUTHORIZED.value());
        logger.trace("Client not allowed to access uri '{}'' with method '{}'", uri.getPath(), request.getMethod());
        return false;
    }
    
    private Optional<String> getBearerToken(HttpServletRequest request)
    {
        Enumeration<String> authHeaders = request.getHeaders(HttpHeaders.AUTHORIZATION);
        while(authHeaders.hasMoreElements())
        {
            String authHeader = authHeaders.nextElement();
            if (authHeader.toLowerCase().startsWith(BEARER_LC))
            {
                return Optional.of(authHeader.substring(BEARER_LENGTH));
            }
        }
        return Optional.empty();
    }
}
