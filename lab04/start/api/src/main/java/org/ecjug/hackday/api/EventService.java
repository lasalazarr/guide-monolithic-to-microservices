package org.ecjug.hackday.api;

import org.ecjug.hackday.domain.model.Event;
import org.ecjug.hackday.domain.model.Member;

import java.util.List;

public interface EventService {

    Event add(Event event);

    List<Event> list();

    void addMemberToEvent(String eventId, Member member);
}
