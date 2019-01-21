package org.ecjug.hackday.api;

import org.ecjug.hackday.domain.model.Group;
import org.ecjug.hackday.domain.model.Member;
import org.pf4j.ExtensionPoint;

import java.util.List;
import java.util.Optional;

public interface GroupService extends ExtensionPoint {

    Group add(Group group);

    List<Group> list();

    List<Group> loadFromMeetUp();

    void addMemberToGroup(String groupId, Member member);

    Optional<Group> byId(String ecjug);

    List<Member> loadMembersFromMeetUpGroup(Group group);
}
