package org.ecjug.hackday.api.impl.test;

import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.runtime.EeRuntime;
import com.kumuluz.ee.common.runtime.EeRuntimeInternal;
import com.kumuluz.ee.configuration.utils.ConfigurationImpl;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.fault.tolerance.config.IsEnabledConfig;
import lombok.extern.slf4j.Slf4j;
import org.ecjug.hackday.api.GroupService;
import org.ecjug.hackday.domain.model.Group;
import org.ecjug.hackday.domain.model.Member;
import org.eclipse.microprofile.metrics.Meter;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Timer;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@Slf4j
public class GroupServiceTest {


    private static ClientAndServer mockServer;
    private static int port = 9081;
    @Rule
    public WeldInitiator weld = WeldInitiator.from(new Weld()).inject(this).build();
    private Member member = Member.builder().
            name("Foo Far").
            country("Ecuador").
            comments("Foo Bar Test Expert").
            build();
    private Group ecGroup = Group.builder().
            link("https://www.meetup.com/ecuadorjug").
            country("EC").urlname("ecuadorjug").
            name("EcuadorJUG").urlname("ecuadorjug").build();

    private Group medellinGroup = Group.builder().
            link("https://medellinjug.org").
            country("CO").urlname("medejinjug").
            name("Medellin").build();

    @Inject
    private GroupService groupService;
    @Inject
    private MetricRegistry injectedRegistry;


    @BeforeClass
    public static void config() {
        System.setProperty("org.ecjug.hackday.api.impl.MemberApi/mp-rest/url","http://127.0.0.1:9081");
        mockServer = startClientAndServer(port);
        //kumuluzee stuff just for test

        ConfigurationUtil.initialize(new ConfigurationImpl());
        IsEnabledConfig.setEnabled(true);
        EeConfig eeConfig = new EeConfig.Builder().build();
        EeConfig.initialize(eeConfig);
        EeRuntime.initialize(new EeRuntimeInternal());

    }


    @Test
    public void shouldAddJugsTest() {


        Group addedGroup = groupService.add(ecGroup);

        assertThat(addedGroup.getId(), is(notNullValue()));
        assertThat(addedGroup.getCountryInformation().getAlpha3Code(), is("ECU"));

        groupService.add(medellinGroup);

        List<Group> groupList = groupService.list();

        assertThat(groupList.size(), is(2));

        SortedMap<String, Meter> meterSortedMap = injectedRegistry.getMeters();
        Meter addMeter = meterSortedMap.get("org.ecjug.hackday.api.impl.client.GroupServiceImpl.addGroup");
        assertTrue(addMeter.getMeanRate() > 0);
    }

    @Test
    public void shouldLoadFromMeetUpTest() {
        mockMembers();
        System.setProperty("meetup.url", "https://api.meetup.com");
        List<Group> groupList = groupService.loadFromMeetUp();
        assertTrue(groupList.size() > 0);

        List<Group> groupFromMongoList = groupService.list();
        assertTrue(groupFromMongoList.size() > 0);
        SortedMap<String, Timer> timerSortedMap = injectedRegistry.getTimers();
        Timer timer = timerSortedMap.get("org.ecjug.hackday.api.impl.client.GroupServiceImpl.loadFromMeetUpTime");
        assertTrue(timer.getMeanRate() > 0);

    }

    @Test
    public void shouldLoadFromMeetUpWithFallBackTest() {
        //replace original meetup.url property to force fallback
        System.setProperty("meetup.url", "127.0.0.1");
        List<Group> groupList = groupService.loadFromMeetUp();
        assertTrue(groupList.isEmpty());
    }

    @Test
    public void shouldAddMemberToGroupTest() {
        mockMembers();
        Group addedGroup = groupService.add(ecGroup);

        groupService.addMemberToGroup(addedGroup.getId().toString(), member);

        Optional<Group> ecJugOptional = groupService.byId(addedGroup.getId().toString());
        assertTrue(ecJugOptional.isPresent());
        Group ecJug = ecJugOptional.get();
        List<Member> membersList = ecJug.getMembersList();
        Member memberFromDB = membersList.stream().findFirst().get();

        assertThat(memberFromDB.getName(), is("Foo Far"));

    }

    @Test
    public void shouldLoadMembersFromMeetUpGroupTest() {
        mockMembers();
        Group groupFromDB = groupService.add(ecGroup);
        groupService.loadMembersFromMeetUpGroup(groupFromDB);
        Optional<Group> optionalGroup = groupService.byId(groupFromDB.getId().toString());
        assertTrue(optionalGroup.isPresent());
        Group ecJug = optionalGroup.get();
        List<Member> memberList = ecJug.getMembersList();
        assertTrue(memberList.size() > 0);
    }

    private void mockMembers() {

        Jsonb jsonb = JsonbBuilder.create();


        new MockServerClient("127.0.0.1", port)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/member/add/")
                                .withHeader("\"Content-type\", \"application/json\"")

                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(new Header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(jsonb.toJson(member))
                );
    }

}
