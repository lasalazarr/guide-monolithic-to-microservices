package org.ecjug.hackday.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.IOException;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@ApplicationScoped
@Slf4j
public class MongoClientProducer {

    @Inject
    @ConfigProperty(name = "mongo.port", defaultValue = "27017")
    private int mongoPort;

    @Inject
    @ConfigProperty(name = "mongo.ip", defaultValue = "127.0.0.1")
    private String mongoIp;

    @Inject
    @ConfigProperty(name = "mongo.embedded", defaultValue = "true")
    private boolean embedded;


    @Inject
    @ConfigProperty(name = "mongo.database.name", defaultValue = "mongito")
    private String databaseName;

    private MongodExecutable mongodExecutable;
    private MongoClient mongoClient;


    @PostConstruct
    public void start() {
        mongoStart();
    }


    private void mongoStart() {
        if (embedded) {
            try {
                log.info("Creating MongodStarter Instance IP {}, port {}", mongoIp, mongoPort);
                MongodStarter starter = MongodStarter.getDefaultInstance();
                IMongodConfig mongoConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                        .net(new Net(mongoIp, mongoPort, Network.localhostIsIPv6()))
                        .build();

                mongodExecutable = starter.prepare(mongoConfig);
                mongodExecutable.start();
                log.info("MongodDB started on IP {}, port {}", mongoIp, mongoPort);

            } catch (IOException e) {
                log.error("error at trying to start mongo embedded", e);
            }
        }
    }

    private MongoClient mongoClient() {
        if (this.mongoClient == null) {
            //pojo support
            CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build()));

            MongoClientOptions mongoClientOptions = MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build();
            ServerAddress serverAddress = new ServerAddress(mongoIp, mongoPort);
            this.mongoClient = new MongoClient(serverAddress, mongoClientOptions);
            log.debug("MongoClient on IP {}, port {}", mongoIp, mongoPort);
        }
        return this.mongoClient;

    }

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {

    }

    @Produces
    @ApplicationScoped
    public MongoClient client() {
        return mongoClient();
    }

    @Produces
    @ApplicationScoped
    public MongoDatabase mongoDatabase() {
        log.debug("MongoDatabase  {}", databaseName);
        return mongoClient().getDatabase(databaseName);
    }

    @PreDestroy
    public void shutdown() {
        log.debug("MongoDatabase shutdown {}", databaseName);
        if (this.mongoClient != null) {
            this.mongoClient.close();
        }

        if (this.mongodExecutable != null) {
            this.mongodExecutable.stop();
        }
    }

}
