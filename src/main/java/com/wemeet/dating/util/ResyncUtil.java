package com.wemeet.dating.util;

import com.wemeet.dating.config.WemeetConfig;
import com.wemeet.dating.util.configuration.CommonsConfigurationFactoryBean;
import org.apache.commons.configuration.DatabaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.Properties;

@Component
public class ResyncUtil {
    private final DataSource configDataSource;
    private final Environment environment;
    private final WemeetConfig wemeetConfig;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public ResyncUtil(@Qualifier("dataSource") DataSource configDataSource,
                      Environment environment,
                      WemeetConfig wemeetConfig) {
        this.configDataSource = configDataSource;
        this.environment = environment;
        this.wemeetConfig = wemeetConfig;
    }

    public void resyncDataBaseProperties() {
        try {
            MutablePropertySources propertySources = ((ConfigurableEnvironment) environment).getPropertySources();
            DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration(configDataSource, "settings", "setting_key", "setting_value");
            CommonsConfigurationFactoryBean commonsConfigurationFactoryBean = new CommonsConfigurationFactoryBean(databaseConfiguration);
            Properties dbProps = (Properties) commonsConfigurationFactoryBean.getObject();
            PropertiesPropertySource dbPropertySource = new PropertiesPropertySource("dbPropertySource", dbProps);
            propertySources.remove("dbPropertySource");
            propertySources.addFirst(dbPropertySource);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void resyncConfigProperties() {
        try {
            for (Field field : wemeetConfig.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String annotatedValue = field.getAnnotation(Value.class).value();
                String index = annotatedValue.substring(2, annotatedValue.length() - 1);
                field.set(wemeetConfig, environment.getProperty(index, field.get(wemeetConfig).getClass()));
            }
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NullPointerException ex) {
            throw new RuntimeException(ex);
        }
    }
}

