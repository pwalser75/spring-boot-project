package org.test.spring.boot.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.test.spring.boot.project.notes.client.NoteClient;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.glassfish.jersey.client.ClientProperties.CONNECT_TIMEOUT;
import static org.glassfish.jersey.client.ClientProperties.READ_TIMEOUT;
import static org.glassfish.jersey.logging.LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_CLIENT;
import static org.glassfish.jersey.logging.LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT;
import static org.glassfish.jersey.logging.LoggingFeature.Verbosity.PAYLOAD_ANY;

/**
 * Rest client configuration, provides the {@link ClientBuilder}
 */
public final class RestClientConfig {

    private RestClientConfig() {

    }

    public static ClientBuilder clientBuilder() {

        try {

            Properties clientProperties = loadClientProperties();
            String truststorePath = Optional.ofNullable(clientProperties.getProperty("truststore"))
                    .map(String::trim).filter(it -> it.length() > 0)
                    .orElseThrow(() -> new NoSuchElementException("missing property 'truststore'"));
            String truststorePassword = Optional.ofNullable(clientProperties.getProperty("truststore-password"))
                    .map(String::trim).filter(it -> it.length() > 0)
                    .orElseThrow(() -> new NoSuchElementException("missing property 'truststore-password'"));
            int connectTimeout = Optional.ofNullable(clientProperties.getProperty("connect-timeout-ms"))
                    .map(String::trim).map(Integer::parseInt)
                    .orElse(1000);
            int readTimeout = Optional.ofNullable(clientProperties.getProperty("read-timeout-ms"))
                    .map(String::trim).map(Integer::parseInt)
                    .orElse(5000);

            if (!truststorePath.startsWith("/")) truststorePath = "/" + truststorePath;
            KeyStore truststore = loadTruststore(truststorePath, truststorePassword);

            JacksonJaxbJsonProvider jsonProvider = new JacksonJaxbJsonProvider();
            jsonProvider.setMapper(objectMapper());

            return ClientBuilder.newBuilder()
                    .trustStore(truststore)
                    .property(CONNECT_TIMEOUT, connectTimeout)
                    .property(READ_TIMEOUT, readTimeout)
                    .property(LOGGING_FEATURE_VERBOSITY_CLIENT, PAYLOAD_ANY)
                    .property(LOGGING_FEATURE_LOGGER_LEVEL_CLIENT, "WARNING")
                    .register(jsonProvider);

        } catch (Exception ex) {
            throw new RuntimeException("Unable to load client truststore due to " + ex.getClass().getSimpleName() + ": " + ex.getMessage(), ex);
        }
    }

    public static ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(new StdDateFormat());
        mapper.disable(WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    private static Properties loadClientProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream in = RestClientConfig.class.getResourceAsStream("/client.properties")) {
            properties.load(in);
        }
        return properties;
    }

    private static KeyStore loadTruststore(String resourceName, String password) throws IOException, GeneralSecurityException {
        try (InputStream in = NoteClient.class.getResourceAsStream(resourceName)) {
            KeyStore truststore = KeyStore.getInstance(KeyStore.getDefaultType());
            truststore.load(in, password.toCharArray());
            return truststore;
        }
    }
}