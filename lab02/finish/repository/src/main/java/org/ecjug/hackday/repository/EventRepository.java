package org.ecjug.hackday.repository;

import org.ecjug.hackday.domain.model.Event;

import java.util.List;

/**
 * Simple Repository for Event Model
 *
 * @author Kleber Ayala
 */
public interface EventRepository {

    Event addEvent(Event event);

    void updateEvent(Event event);

    List<Event> list();

    List<Event> eventsByTitle(String title);

    List<Event> events(String title);

    Event eventById(String id);

}
