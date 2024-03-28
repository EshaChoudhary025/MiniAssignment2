package com.miniassignment.config;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

	private static final int SIZE = 16 * 1024 * 1024;//maximum in-memory size
 
	
	@Bean
	public WebClient api1() {
		return WebClient.builder()
				.baseUrl("https://randomuser.me/api")//configuration of webclient
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(SIZE))
				.clientConnector(new ReactorClientHttpConnector(
						//creating new instance of http client
						// maximum time to wait for a response from the server
						HttpClient.newConnection().responseTimeout(Duration.ofMillis(2000)) // connection_timeout
						//callback that gets executed when the connection is established
								.doOnConnected(conn -> {
									//timeouts for reading from the server and writing to the server
									conn.addHandlerLast(new ReadTimeoutHandler(2000, TimeUnit.MILLISECONDS));
									conn.addHandlerLast(new WriteTimeoutHandler(2000, TimeUnit.MILLISECONDS));
								}).wiretap("api1", LogLevel.DEBUG) // wiretap logging:logs the request and response details
				)).build();
	}

	@Bean
	public WebClient api2() {
		return WebClient.builder().baseUrl("https://api.nationalize.io")
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(SIZE))
				.clientConnector(new ReactorClientHttpConnector(
						HttpClient.newConnection().responseTimeout(Duration.ofMillis(1000)) // connection_timeout
								.doOnConnected(conn -> {
									conn.addHandlerLast(new ReadTimeoutHandler(1000, TimeUnit.MILLISECONDS));
									conn.addHandlerLast(new WriteTimeoutHandler(1000, TimeUnit.MILLISECONDS));
								}).wiretap("api2", LogLevel.DEBUG) 
				)).build();//building the WebClient instance
	}

	@Bean
	public WebClient api3() {
		return WebClient.builder().baseUrl("https://api.genderize.io")
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(SIZE))
				.clientConnector(new ReactorClientHttpConnector(
						HttpClient.newConnection().responseTimeout(Duration.ofMillis(1000)) // connection_timeout
								.doOnConnected(conn -> {
									conn.addHandlerLast(new ReadTimeoutHandler(1000, TimeUnit.MILLISECONDS));
									conn.addHandlerLast(new WriteTimeoutHandler(1000, TimeUnit.MILLISECONDS));
								}).wiretap("api3", LogLevel.DEBUG) 
				)).build();
	}
}
