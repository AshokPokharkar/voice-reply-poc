package com.twilio.clicktocall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.twilio.clicktocall.twilio.TwilioLine;
import com.twilio.clicktocall.twilio.TwilioRequestValidator;
import com.twilio.http.TwilioRestClient;
import com.twilio.security.RequestValidator;

@EnableAutoConfiguration
@ComponentScan
@Configuration
public class Application {

	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public TwilioRestClient twilioRestClient(@Value("${notificationService.ACCOUNT_SID}") String accountSid,
                                      @Value("${notificationService.AUTH_TOKEN}") String authToken){
		LOGGER.info(">>twilioRestClient() : accountSid: {}", accountSid);

        return new TwilioRestClient.Builder(accountSid, authToken).build();
    }

    @Bean
    public TwilioRequestValidator twilioRequestValidator(@Value("${notificationService.AUTH_TOKEN}") String authToken) {
		LOGGER.info(">>twilioRequestValidator() : authToken: {}", authToken);
        return new TwilioRequestValidator(new RequestValidator(authToken));
    }

    @Bean
    public TwilioLine twilioLine(TwilioRestClient restClient, @Value("${notificationService.FROM_NUMBER}") String twilioNumber) {
		LOGGER.info(">>twilioLine() : twilioNumber: {}", twilioNumber);

        return new TwilioLine(restClient, twilioNumber);
    }
}
