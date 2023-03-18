package de.hatoka.poker.security;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import de.hatoka.oauth.internal.remote.PolicyEnforcerInterceptor;

@Configuration
@ComponentScan(basePackageClasses = { PolicyEnforcerInterceptor.class})
@Order(100)
public class CorsConfiguration
{
    @Value("${intershop.security.cors.allowedOrigins}")
    private String allowedOrigins;
    @Autowired
    private PolicyEnforcerInterceptor interceptor;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                LoggerFactory.getLogger(getClass()).debug("CORS enabled for {}.", allowedOrigins.split(" "));
                registry.addMapping("/**").allowedOrigins(allowedOrigins.split(" ")).allowedMethods("GET", "PUT", "DELETE", "POST", "PATCH", "OPTIONS");
            }

            @Override
            public void addInterceptors(InterceptorRegistry registry)
            {
                registry.addInterceptor(interceptor);
            }
        };
    }
}
