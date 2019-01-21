package org.ecjug.hackday.repository;

import org.ecjug.hackday.domain.model.Group;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {

    Group add(Group group);

    List<Group> list();

    Optional<Group> byId(String groupId);

    void update(Group group);

    Optional<Group> byUrlname(String urlname);
}
