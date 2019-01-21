package org.ecjug.hackday.repository.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;
import org.ecjug.hackday.domain.model.Group;
import org.ecjug.hackday.repository.GroupRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class GroupRepositoryImpl implements GroupRepository {

    @Inject
    private MongoDatabase database;
    private MongoCollection<Group> collection;


    @Override
    public Group add(Group group) {
        Objects.requireNonNull(group, "Group can't be null");
        group.setId(new ObjectId(new Date()));
        dbCollection().insertOne(group);
        return group;
    }

    @Override
    public List<Group> list() {
        List<Group> groupList = new ArrayList<>();
        MongoCursor<Group> mongoCursor = dbCollection().find().iterator();
        mongoCursor.forEachRemaining(groupList::add);
        return groupList;
    }

    @Override
    public Optional<Group> byId(String groupId) {
        return Optional.ofNullable(dbCollection().find(eq("_id", new ObjectId(groupId))).first());
    }


    @Override
    public void update(Group group) {
        Objects.requireNonNull(group, "Group can't be null");
        dbCollection().replaceOne(eq("_id", group.getId()), group);
    }

    private MongoCollection<Group> dbCollection() {
        if (this.collection == null) {
            this.collection = this.database.getCollection("Group", Group.class);
        }
        return this.collection;
    }
}
