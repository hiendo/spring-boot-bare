package com.github.hiendo.springboot.servertests.properties;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class PropertyBeanBinderTests {

    private PropertyBeanBinder propertyBeanBinder;

    @BeforeMethod
    public void beforeMethod() throws IOException {
        propertyBeanBinder = new PropertyBeanBinder();
    }

    @Test
    public void canRegisterPropertiesFromFilePath() throws Exception {
        File tempFile = File.createTempFile("spring-boot-template", "testing");
        tempFile.deleteOnExit();
        FileUtils.write(tempFile, "port=8899\n");

        propertyBeanBinder.registerPropertiesFromFile(tempFile.getAbsolutePath());

        PropertiesWithOutPrefix propertiesWithOutPrefix = new PropertiesWithOutPrefix();
        propertyBeanBinder.bindStartingFromPrefix(propertiesWithOutPrefix, "");

        assertThat(propertiesWithOutPrefix.getPort(), is(8899));
    }

    @Test
    public void canRegisterPropertiesFromClassPath() throws Exception {
        propertyBeanBinder.registerPropertiesFromClassPath("config/application.properties");

        PropertiesWithOutPrefix propertiesWithOutPrefix = new PropertiesWithOutPrefix();
        propertyBeanBinder.bindStartingFromPrefix(propertiesWithOutPrefix, "app.server");
        assertThat(propertiesWithOutPrefix.getPort(), is(9999));

        propertiesWithOutPrefix = new PropertiesWithOutPrefix();
        propertyBeanBinder.bindStartingFromPrefix(propertiesWithOutPrefix, "app.server.");
        assertThat(propertiesWithOutPrefix.getPort(), is(9999));
    }

    @Test
    public void canDetectConfigurationPropertiesAnnotationPrefix() throws Exception {
        File tempFile = File.createTempFile("spring-boot-template", "testing");
        tempFile.deleteOnExit();
        FileUtils.write(tempFile, "app.server.port=8899\n");

        propertyBeanBinder.registerPropertiesFromFile(tempFile.getAbsolutePath());

        PropertiesWithPrefix propertiesWithPrefix = new PropertiesWithPrefix();
        propertyBeanBinder.bind(propertiesWithPrefix);

        assertThat(propertiesWithPrefix.getPort(), is(8899));
    }

    @Test
    public void canOverrideProperties() throws Exception {
        propertyBeanBinder.registerPropertiesFromClassPath("config/application.properties");

        File tempFile = File.createTempFile("spring-boot-template", "testing");
        tempFile.deleteOnExit();
        FileUtils.write(tempFile, "app.server.port=8899\n");
        propertyBeanBinder.registerPropertiesFromFile(tempFile.getAbsolutePath());

        PropertiesWithPrefix propertiesWithPrefix = new PropertiesWithPrefix();
        propertyBeanBinder.bind(propertiesWithPrefix);

        assertThat(propertiesWithPrefix.getPort(), is(8899));
    }

    @Test
    public void willIgnoreClasspathIfItDoesNotExist() throws Exception {
        propertyBeanBinder.registerPropertiesFromClassPath(UUID.randomUUID().toString());
        propertyBeanBinder.registerPropertiesFromClassPath("config/application.properties");
        propertyBeanBinder.registerPropertiesFromClassPath(UUID.randomUUID().toString());

        PropertiesWithPrefix propertiesWithPrefix = new PropertiesWithPrefix();
        propertyBeanBinder.bind(propertiesWithPrefix);

        assertThat(propertiesWithPrefix.getPort(), is(9999));
    }


    @Test
    public void willIgnoreFilePathIfItDoesNotExist() throws Exception {
        File tempFile = File.createTempFile("spring-boot-template", "testing");
        tempFile.deleteOnExit();
        FileUtils.write(tempFile, "app.server.port=8899\n");

        propertyBeanBinder.registerPropertiesFromFile(UUID.randomUUID().toString());
        propertyBeanBinder.registerPropertiesFromFile(tempFile.getAbsolutePath());
        propertyBeanBinder.registerPropertiesFromFile(UUID.randomUUID().toString());

        PropertiesWithPrefix propertiesWithPrefix = new PropertiesWithPrefix();
        propertyBeanBinder.bind(propertiesWithPrefix);

        assertThat(propertiesWithPrefix.getPort(), is(8899));
    }

    @ConfigurationProperties(value = "app.server")
    public class PropertiesWithPrefix {

        private int port;

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public class PropertiesWithOutPrefix {

        private int port;

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }
}
