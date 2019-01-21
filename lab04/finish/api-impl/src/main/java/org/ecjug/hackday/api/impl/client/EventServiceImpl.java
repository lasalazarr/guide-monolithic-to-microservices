package org.ecjug.hackday.api.impl.client;

import lombok.extern.slf4j.Slf4j;
import org.ecjug.hackday.api.EventService;
import org.ecjug.hackday.domain.model.Event;
import org.ecjug.hackday.domain.model.Member;
import org.ecjug.hackday.repository.EventRepository;
import org.ecjug.hackday.repository.MemberRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApplicationScoped
public class EventServiceImpl implements EventService {

    @Inject
    private EventRepository eventRepository;

    @Inject
    private MemberRepository memberRepository;

    @Override
    public Event add(Event event) {
        return eventRepository.addEvent(event);
    }

    @Override
    public List<Event> list() {
        return eventRepository.list();
    }

    @Override
    public void addMemberToEvent(String eventId, Member member) {

    }
}
