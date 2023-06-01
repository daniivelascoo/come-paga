package com.example.comepaga.config;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The type Ssl config.
 */
@Configuration
public class SSLConfig {

    /**
     * Servlet container customizer web server factory customizer.
     *
     * @return the web server factory customizer
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainerCustomizer() {
        return factory -> {
            Ssl ssl = new Ssl();
            ssl.setKeyStoreType("PKCS12");
            ssl.setKeyStore("classpath:certificates/cert-come-paga.p12");
            ssl.setKeyStorePassword("Dani0310+");
            factory.setSsl(ssl);
        };
    }
}
