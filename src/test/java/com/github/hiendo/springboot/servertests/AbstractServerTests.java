package com.github.hiendo.springboot.servertests;

import com.github.hiendo.springboot.config.AppConfiguration;
import com.github.hiendo.springboot.config.AppServerProperties;
import com.github.hiendo.springboot.servertests.operations.RestTestOperations;
import com.github.hiendo.springboot.servertests.operations.StaticFileOperations;
import com.github.hiendo.springboot.servertests.properties.PropertyBeanBinder;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class AbstractServerTests {
    protected static ConfigurableApplicationContext context;

    private static AppServerProperties appServerProperties;

    protected static StaticFileOperations staticFileOperations;
    protected static RestTestOperations restTestOperations;

    @BeforeSuite
	public void startupEmbeddedServer() throws Exception {
        loadProperties();

        String serverBaseUrl = "http://localhost:" + appServerProperties.getPort();

        Future<ConfigurableApplicationContext> startupFuture = startupServer();
        WebTarget webTarget = setupClient(serverBaseUrl);
        setupOperationClasses(webTarget);

        context = startupFuture.get(30, TimeUnit.SECONDS);
    }

    @AfterSuite
	public void shutdownEmbeddedServer() throws Exception {
        if (context != null) {
            context.close();
        }
    }

    private void loadProperties() throws Exception {
        // Spring boot's default properties loading
        PropertyBeanBinder propertyBeanLoader = new PropertyBeanBinder();
        propertyBeanLoader.registerPropertiesFromClassPath("application.properties");
        propertyBeanLoader.registerPropertiesFromClassPath("config/application.properties");
        propertyBeanLoader.registerPropertiesFromFile("application.properties");

        appServerProperties = new AppServerProperties();

        propertyBeanLoader.bind(appServerProperties);
    }

    private void setupOperationClasses(WebTarget webTarget) {
        staticFileOperations = new StaticFileOperations(webTarget);
        restTestOperations = new RestTestOperations(webTarget);
    }

    private Future<ConfigurableApplicationContext> startupServer() throws Exception {
        Future<ConfigurableApplicationContext> future =
                Executors.newSingleThreadExecutor().submit(new Callable<ConfigurableApplicationContext>() {
                    @Override
                    public ConfigurableApplicationContext call() throws Exception {
                        return SpringApplication.run(AppConfiguration.class);
                    }
                });

        return future;
    }

    private WebTarget setupClient(String serverBaseUrl) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.connectorProvider(new ApacheConnectorProvider());
        Client client = ClientBuilder.newClient(clientConfig).register(JacksonFeature.class);
        return client.target(serverBaseUrl);
    }
}
