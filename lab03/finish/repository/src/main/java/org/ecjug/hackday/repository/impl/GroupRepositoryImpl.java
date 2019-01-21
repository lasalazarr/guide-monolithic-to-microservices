package org.ecjug.hackday.repository.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.ecjug.hackday.domain.model.Group;
import org.ecjug.hackday.repository.GroupRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
@Slf4j
public class GroupRepositoryImpl implements GroupRepository {

    @Inject
    private MongoDatabase database;
    private MongoCollection<Group> collection;


    @Override
    public Group add(Group group) {
        Objects.requireNonNull(group, "Group can't be null");
        Optional<Group> optionalGroup = byUrlname(group.getUrlname());
        if (optionalGroup.isPresent()) {
            log.warn("Group " + group.getName() + " is already registered, updating it!");
            group.setId(optionalGroup.get().getId());
            update(group);
        } else {
            group.setId(new ObjectId(new Date()));
            dbCollection().insertOne(group);
        }

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

    @Override
    public Optional<Group> byUrlname(String urlname) {
        Objects.requireNonNull(urlname, "Urlname can't be null");
        Optional<Group> optionalGroup;
        List<Group> groupList = dbCollection().find(Filters.regex("urlname", urlname)).into(new ArrayList<>());
        if (groupList.isEmpty()) {
            optionalGroup = Optional.empty();
        } else {
            optionalGroup = Optional.of(groupList.get(0));
        }
        return optionalGroup;
    }

    private MongoCollection<Group> dbCollection() {
        if (this.collection == null) {
            this.collection = this.database.getCollection("Group", Group.class);
        }
        return this.collection;
    }
}
