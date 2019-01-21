package org.ecjug.hackday.api.impl.health;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.HttpURLConnection;
import java.net.URL;

@Health
@ApplicationScoped
@Slf4j
public class MeetupHealthCheck implements HealthCheck {

    @Inject
    @ConfigProperty(name = "meetup.url", defaultValue = "https://api.meetup.com")
    private String meetUpApiUrl;


    @Override
    public HealthCheckResponse call() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(meetUpApiUrl).openConnection();
            connection.setRequestMethod("HEAD");

            if (connection.getResponseCode() == 200) {
                return HealthCheckResponse.named(MeetupHealthCheck.class.getSimpleName()).up().build();
            }

        } catch (Exception exception) {
            log.error("Error checking health of" + meetUpApiUrl);
        }
        return HealthCheckResponse.named(MeetupHealthCheck.class.getSimpleName()).down().build();
    }


}
