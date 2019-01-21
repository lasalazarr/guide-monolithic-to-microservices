package org.ecjug.hackday.api.impl.test;

import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.runtime.EeRuntime;
import com.kumuluz.ee.common.runtime.EeRuntimeInternal;
import com.kumuluz.ee.configuration.utils.ConfigurationImpl;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.fault.tolerance.config.IsEnabledConfig;
import org.ecjug.hackday.api.GroupService;
import org.ecjug.hackday.domain.model.Group;
import org.ecjug.hackday.domain.model.Member;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;


public class GroupServiceTest {

    @Rule
    public WeldInitiator weld = WeldInitiator.from(new Weld()).inject(this).build();

    private Group ecGroup = Group.builder().
            link("https://www.meetup.com/ecuadorjug").
            country("EC").
            name("EcuadorJUG").urlname("ecuadorjug").build();

    private Group medellinGroup = Group.builder().
            link("https://medellinjug.org").
            country("CO").
            name("Medellin").build();

    @Inject
    private GroupService groupService;

    @BeforeClass
    public static void config() {
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
    }

    @Test
    public void shouldLoadFromMeetUpTest() {
        System.setProperty("meetup.url", "https://api.meetup.com");
        List<Group> groupList = groupService.loadFromMeetUp();
        assertTrue(groupList.size() > 0);

        List<Group> groupFromMongoList = groupService.list();
        assertTrue(groupFromMongoList.size() > 0);
    }

    @Test
    public void shouldAddMemberToGroupTest() {
        Group addedGroup = groupService.add(ecGroup);

        Member member = Member.builder().
                name("Foo Far").
                country("Ecuador").
                comments("Foo Bar Test Expert").
                build();

        groupService.addMemberToGroup(addedGroup.getId().toString(), member);

        Optional<Group> ecJugOptional = groupService.byId(addedGroup.getId().toString());
        assertTrue(ecJugOptional.isPresent());
        Group ecJug = ecJugOptional.get();
        List<Member> membersList = ecJug.getMembersList();
        membersList.stream().map(Member::getId).forEach(Assert::assertNotNull);
        Member memberFromDB = membersList.stream().findFirst().get();

        assertThat(memberFromDB.getName(), is("Foo Far"));
    }

    @Test
    public void shouldLoadMembersFromMeetUpGroupTest() {
        Group groupFromDB = groupService.add(ecGroup);
        groupService.loadMembersFromMeetUpGroup(groupFromDB);
        Optional<Group> optionalGroup = groupService.byId(groupFromDB.getId().toString());
        assertTrue(optionalGroup.isPresent());
        Group ecJug = optionalGroup.get();
        List<Member> memberList = ecJug.getMembersList();
        assertTrue(memberList.size() > 0);
        memberList.stream().map(Member::getId).forEach(Assert::assertNotNull);
    }

}
