package de.hatoka.common.capi;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import de.hatoka.common.internal.configuration.CurrentDateProvider;

@Configuration
@ComponentScan(basePackageClasses = {CommonConfiguration.class, CurrentDateProvider.class})
public class CommonConfiguration
{
}
