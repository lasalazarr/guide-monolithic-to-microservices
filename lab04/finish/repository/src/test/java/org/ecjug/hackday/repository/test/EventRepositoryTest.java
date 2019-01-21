package org.ecjug.hackday.repository.test;

import com.kumuluz.ee.configuration.utils.ConfigurationImpl;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import org.ecjug.hackday.domain.model.Event;
import org.ecjug.hackday.domain.model.Member;
import org.ecjug.hackday.repository.EventRepository;
import org.ecjug.hackday.repository.MemberRepository;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Kleber Ayala
 */
public class EventRepositoryTest {


    @Rule
    public WeldInitiator weld = WeldInitiator.from(new Weld()).inject(this).build();

    @Inject
    private EventRepository eventRepository;

    @Inject
    private MemberRepository memberRepository;

    private Event event1;
    private Event event2;
    private Event event3;

    @BeforeClass
    public static void config() {
        ConfigurationUtil.initialize(new ConfigurationImpl());
    }

    @Before
    public void setUp() {

        event1 = Event.builder().
                title("Test Java 1").
                date(LocalDate.now()).
                description("Test Java 1 Description").build();

        event2 = Event.builder().
                title("Test Java 2").
                date(LocalDate.now()).
                description("Test Java 2 Description").build();

        event3 = Event.builder().
                title("Test Microlito Monolito Micro").
                date(LocalDate.now()).
                description("Test Microlito Monolito Micro Description").build();

    }

    @Test(expected = NullPointerException.class)
    public void nullInsert() {
        eventRepository.addEvent(null);
    }

    @Test
    public void insertAndListTest() {

        eventRepository.addEvent(event1);
        eventRepository.addEvent(event2);

        List<Event> eventList = eventRepository.list();
        assertThat(eventList.size(), is(2));
        eventList.stream().map(Event::getId).forEach(Assert::assertNotNull);

    }

    @Test
    public void hackDaysByTitleTest() {
        eventRepository.addEvent(event1);
        eventRepository.addEvent(event2);
        eventRepository.addEvent(event3);

        List<Event> eventList = eventRepository.eventsByTitle("Java");
        assertThat(eventList.size(), is(2));

        List<Event> eventListMicro = eventRepository.eventsByTitle("Microlito");
        assertThat(eventListMicro.size(), is(1));

    }

    @Test
    public void hackDaysByDescriptionTest() {

        eventRepository.addEvent(event1);
        eventRepository.addEvent(event2);
        eventRepository.addEvent(event3);

        List<Event> eventList = eventRepository.events("Test Java");
        assertThat(eventList.size(), is(2));

        List<Event> eventListMicro = eventRepository.events("Microlito");
        assertThat(eventListMicro.size(), is(1));

    }

    @Test
    public void updateHackDayTest() {
        eventRepository.addEvent(event1);
        List<Event> eventList = eventRepository.list();
        assertThat(eventList.size(), is(1));
        Event eventFromMongo = eventList.get(0);
        String hackDayId = eventFromMongo.getId().toString();

        Member member = Member.builder().
                name("Foo Far").
                country("Ecuador").
                comments("Foo Bar Test Expert").
                build();

        member = memberRepository.add(member);
        member = memberRepository.byId(member.getId().toString());

        eventFromMongo.setDescription("Description updated!");
        eventFromMongo.addMember(member);
        eventRepository.updateEvent(eventFromMongo);

        eventFromMongo = eventRepository.eventById(hackDayId);
        assertThat(eventFromMongo.getDescription(), is("Description updated!"));

    }


}
