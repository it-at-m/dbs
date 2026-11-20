/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2023
 */
package de.muenchen.dbs.ticketing.dprs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;

/**
 * To do some base configuration for the non blocking client-server framework
 * named Netty via properties use the properties listed in the link down below:
 *
 * @see <a href=
 *      "https://projectreactor.io/docs/netty/release/api/constant-values.html">https://projectreactor.io/docs/netty/release/api/constant-values.html</a>
 *      <p>
 *      As listed below, this above mentioned properties should be set before the application
 *      startup:
 *
 *      <ul>
 *      <li>As command line argument: e.g. -Dreactor.netty.pool.maxConnections=1000.
 *      <li>As environmental property in Openshift: e.g. with key REACTOR_NETTY_POOL_MAXCONNECTIONS
 *      and value 1000.
 *      <li>As programatically set property before call {@link SpringApplication#run} in
 *      {@link PasswordResetServiceApplication#main}: e.g.
 *      <code>System.setProperty("reactor.netty.pool.maxConnections", "1000");</code>.
 *      </ul>
 *      <p>
 *      To get more information about Spring Cloud Gateway visit the following link:
 * @see <a href=
 *      "https://cloud.spring.io/spring-cloud-gateway/reference/html/">https://cloud.spring.io/spring-cloud-gateway/reference/html/</a>
 */
@SpringBootApplication(scanBasePackages = { "de.muenchen.dbs.ticketing" })
public class PasswordResetServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PasswordResetServiceApplication.class, args);
    }

    /**
     * Workaround until new Spring Boot is released:
     * https://github.com/spring-projects/spring-security/issues/15989#issuecomment-2442660753
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    WebFilter writeableHeaders() {
        return (exchange, chain) -> {
            HttpHeaders writeableHeaders = HttpHeaders.writableHttpHeaders(
                    exchange.getRequest().getHeaders());
            ServerHttpRequestDecorator writeableRequest = new ServerHttpRequestDecorator(
                    exchange.getRequest()) {
                @Override
                public HttpHeaders getHeaders() {
                    return writeableHeaders;
                }
            };
            ServerWebExchange writeableExchange = exchange.mutate()
                    .request(writeableRequest)
                    .build();
            return chain.filter(writeableExchange);
        };
    }
}
