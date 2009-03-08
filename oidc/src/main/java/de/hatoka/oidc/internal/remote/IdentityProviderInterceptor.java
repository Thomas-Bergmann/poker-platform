package de.hatoka.oidc.internal.remote;

import java.net.URI;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class IdentityProviderInterceptor implements HandlerInterceptor
{
    private static final Logger logger = LoggerFactory.getLogger(IdentityProviderInterceptor.class);
    public static final String BEARER_LC = "bearer ";
    private static int BEARER_LENGTH = BEARER_LC.length();
    private String oidcAuthenticationToken;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        // allow preflight requests
        if (HttpMethod.OPTIONS.matches(request.getMethod()))
        {
            return true;
        }
        URI uri = URI.create(request.getRequestURI());
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
            boolean foundHeader = false;
            Enumeration<String> authHeaders = request.getHeaders(HttpHeaders.AUTHORIZATION);
            while(authHeaders.hasMoreElements())
            {
                foundHeader = true;
                String authHeader = authHeaders.nextElement();
                if (authHeader.toLowerCase().startsWith(BEARER_LC)
                                && authHeader.substring(BEARER_LENGTH).equals(oidcAuthenticationToken))
                {
                    return true;
                }
            }
            response.setStatus(foundHeader ? HttpStatus.FORBIDDEN.value() : HttpStatus.UNAUTHORIZED.value());
            logger.trace("Client not allowed to access uri '{}'' with method '{}'", uri.getPath(), request.getMethod());
            return false;
        }
        return true;
    }

    public void setOidcAuthenticationToken(String oidcAuthenticationToken)
    {
        this.oidcAuthenticationToken = oidcAuthenticationToken;
    }
}
