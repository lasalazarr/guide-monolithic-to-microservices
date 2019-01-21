package org.ecjug.hackday.repository.impl;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.SneakyThrows;
import org.bson.types.ObjectId;
import org.ecjug.hackday.domain.model.Event;
import org.ecjug.hackday.repository.EventRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;


@ApplicationScoped
public class EventRepositoryImpl implements EventRepository {


    @Inject
    private MongoDatabase database;
    private MongoCollection<Event> collection;

    @Override
    public Event addEvent(Event event) {
        Objects.requireNonNull(event, "Event can't be null");
        event.setId(new ObjectId(new Date()));
        dbCollection().insertOne(event);
        return event;
    }

    @Override
    @SneakyThrows
    public void updateEvent(Event event) {
        Objects.requireNonNull(event, "Event can't be null");
        dbCollection().replaceOne(eq("_id", event.getId()), event);
    }

    @Override
    public List<Event> list() {
        List<Event> eventList = new ArrayList<>();
        MongoCursor<Event> mongoCursor = dbCollection().find().iterator();
        mongoCursor.forEachRemaining(eventList::add);
        return eventList;
    }

    @Override
    public List<Event> eventsByTitle(String title) {
        return filter("title", title);
    }

    @Override
    public List<Event> events(String description) {
        return filter("description", description);
    }

    @Override
    public Event eventById(String id) {
        return dbCollection().find(eq("_id", new ObjectId(id))).first();
    }


    private List<Event> filter(final String fieldName, final String pattern) {
        return dbCollection().find(Filters.regex(fieldName, pattern)).into(new ArrayList<>());
    }

    private MongoCollection<Event> dbCollection() {
        if (this.collection == null) {
            this.collection = this.database.getCollection("Event", Event.class);
        }
        return this.collection;
    }
}
