package org.ecjug.hackday.mongo.test;

import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.runtime.EeRuntime;
import com.kumuluz.ee.common.runtime.EeRuntimeInternal;
import com.kumuluz.ee.configuration.utils.ConfigurationImpl;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import static com.mongodb.client.model.Filters.eq;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MongoClientProducerTest {


    @Rule
    public WeldInitiator weld = WeldInitiator.from(new Weld()).inject(this).build();

    @Inject
    private MongoDatabase database;


    @BeforeClass
    public static void config() {
        ConfigurationUtil.initialize(new ConfigurationImpl());
        EeConfig eeConfig = new EeConfig.Builder().build();
        EeConfig.initialize(eeConfig);
        EeRuntime.initialize(new EeRuntimeInternal());
    }

    @Test
    public void shouldInsertDocumentTest() {
        System.setProperty("mongo.ip", "127.0.0.1");
        MongoCollection<Document> collection = database.getCollection("testDocument");
        Document doc = new Document("jugName", "EcuadorJUG")
                .append("country", "Ecuador")
                .append("membersList", 500);
        collection.insertOne(doc);

        Document jugDoc = collection.find(eq("jugName", "EcuadorJUG")).first();
        assertThat(jugDoc.get("country"), is("Ecuador"));
    }

    @Test
    public void shouldInsertPojoTest() {
        System.setProperty("mongo.ip", "127.0.0.1");
        MongoCollection<JUG> jugCollection = database.getCollection("pojoTest", JUG.class);

        JUG jug = JUG.builder().jugName("EcuadorJUG").country("Ecuador").members(500).build();
        jugCollection.insertOne(jug);
        JUG jugFromMongo = jugCollection.find(eq("jugName", "EcuadorJUG")).first();

        assertThat(jugFromMongo.getCountry(), is("Ecuador"));

    }

}