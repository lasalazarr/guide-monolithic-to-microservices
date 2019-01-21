package org.ecjug.hackday.api.impl.client;

import lombok.extern.slf4j.Slf4j;
import org.ecjug.hackday.api.EventService;
import org.ecjug.hackday.domain.model.Event;
import org.ecjug.hackday.repository.EventRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApplicationScoped
public class EventServiceImpl implements EventService {

    @Inject
    private EventRepository eventRepository;

    @Override
    public Event add(Event event) {
        return eventRepository.addEvent(event);
    }

    @Override
    public List<Event> list() {
        return eventRepository.list();
    }
}
