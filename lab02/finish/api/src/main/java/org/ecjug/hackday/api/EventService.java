package org.ecjug.hackday.api;

import org.ecjug.hackday.domain.model.Event;

import java.util.List;

public interface EventService {

    Event add(Event event);

    List<Event> list();
}
