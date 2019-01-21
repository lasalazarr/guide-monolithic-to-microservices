package org.ecjug.hackday.mongo.health;

import com.mongodb.MongoClient;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Health
@ApplicationScoped
@Slf4j
public class MongoHealthCheck implements HealthCheck {

    @Inject
    private MongoClient mongoClient;

    @Override
    public HealthCheckResponse call() {
        log.info("Health check for mongo database  ");

        HealthCheckResponse healthCheckResponse = null;
        if (mongoClient != null) {
            log.info("Mongo database active!");
            healthCheckResponse = HealthCheckResponse.named(MongoHealthCheck.class.getSimpleName()).up().build();
        } else {
            log.error("Mongo database is not active!");
            healthCheckResponse = HealthCheckResponse.named(MongoHealthCheck.class.getSimpleName()).down().build();
        }
        return healthCheckResponse;
    }


}
