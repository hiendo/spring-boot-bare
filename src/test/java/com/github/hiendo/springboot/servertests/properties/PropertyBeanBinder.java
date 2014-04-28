package com.github.hiendo.springboot.servertests.properties;

import com.github.hiendo.springboot.config.AppServerProperties;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Bind properties read from class path or file system into a JavaBean instance.
 */
public class PropertyBeanBinder {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PropertyBeanBinder.class);

    private Properties properties = new Properties();
    private BeanUtilsBean beanUtilsBean = new BeanUtilsBean();

    /**
     * Register the properties from the classpath.
     *
     * @param classPath classpath to properties file
     * @throws IOException
     */
    public void registerPropertiesFromClassPath(String classPath) throws IOException {
        InputStream in = getClass().getClassLoader().getResourceAsStream(classPath);
        if (in == null) {
            logger.warn("Classpath has no file " + classPath);
            return;
        }

        try {
            properties.load(in);
        } finally {
            in.close();
        }
    }

    /**
     * Register the properties from the file system.
     *
     * @param filePath file path to the properties file
     * @throws IOException
     */
    public void registerPropertiesFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            logger.warn("File system has no file " + filePath);
            return;
        }

        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            properties.load(fileInputStream);
        } finally {
            fileInputStream.close();
        }
    }

    /**
     * Bind the registered properties to the specified JavaBean instance.
     *
     * @param propertiesObject properties javabean instance to bind the properties to.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void bind(Object propertiesObject) throws InvocationTargetException, IllegalAccessException {
        String prefix = "";

        ConfigurationProperties configAnnotation =
                propertiesObject.getClass().getAnnotation(ConfigurationProperties.class);

        if (configAnnotation != null) {
            String annotationPrefix = configAnnotation.value();
            if (annotationPrefix != null && annotationPrefix.trim() != "") {
                prefix = configAnnotation.value();
            }
        }

        bindStartingFromPrefix(propertiesObject, prefix);
    }


    /**
     * Bind the registered properties to the specified JavaBean instance.
     *
     * Property keys are read from the the specified prefix.
     *
     * @param propertiesObject properties javabean instance to bind the properties to.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void bindStartingFromPrefix(Object propertiesObject, String prefix) throws InvocationTargetException, IllegalAccessException {
        if (prefix != null && prefix.trim() != "" && !prefix.endsWith(".")) {
            prefix = prefix + ".";
        }

        Map<String, String> propertyMap = new HashMap<>();
        for(String key : properties.stringPropertyNames()) {
            propertyMap.put(key.replaceFirst(prefix, ""), properties.getProperty(key));
        }

        beanUtilsBean.populate(propertiesObject, propertyMap);
    }

    public static void main(String... args)
            throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        PropertyBeanBinder propertyBeanLoader = new PropertyBeanBinder();
        propertyBeanLoader.registerPropertiesFromClassPath("config/application.properties");

        AppServerProperties appServerProperties = new AppServerProperties();
        propertyBeanLoader.bind(appServerProperties);
    }
}
