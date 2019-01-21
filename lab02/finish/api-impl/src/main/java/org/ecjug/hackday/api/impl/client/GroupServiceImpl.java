package org.ecjug.hackday.api.impl.client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ecjug.hackday.api.GroupService;
import org.ecjug.hackday.api.impl.CountryApi;
import org.ecjug.hackday.api.impl.MeetUpApi;
import org.ecjug.hackday.domain.model.Country;
import org.ecjug.hackday.domain.model.Group;
import org.ecjug.hackday.domain.model.Member;
import org.ecjug.hackday.repository.GroupRepository;
import org.ecjug.hackday.repository.MemberRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.pf4j.Extension;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Extension
@Slf4j
@ApplicationScoped
public class GroupServiceImpl implements GroupService {


    @Inject
    private GroupRepository groupRepository;

    @Inject
    private MemberRepository memberRepository;

    @Inject
    @RestClient //RestClient with injection
    private CountryApi countryApi;

    @Inject
    @ConfigProperty(name = "meetup.url", defaultValue = "https://api.meetup.com")
    private String meetUpApiUrl;

    @Inject
    @ConfigProperty(name = "meetup.key")
    private String meetUpApiKey;


    private Client restClient;

    @Override
    @SneakyThrows
    public Group add(Group group) {
        String countryCode = group.getCountry();
        Country country = countryApi.countryByCode(countryCode);
        group.setCountryInformation(country);
        group = groupRepository.add(group);
        return group;
    }

    @Override
    public List<Group> list() {
        return groupRepository.list();
    }

    @Override
    public List<Group> loadFromMeetUp() {
        //34--> tech category on meetup
        List<HashMap> techGroups = meetUpApi().techGroups("34", meetUpApiKey);
        List<Group> groupList = toGroupList(techGroups);
        groupList.forEach(this::add);
        return groupList;
    }

    @Override
    public List<Member> loadMembersFromMeetUpGroup(Group group) {
        List<HashMap> membersFromMeetUp = meetUpApi().members(group.getUrlname());
        List<Member> memberList = toMemberList(membersFromMeetUp);
        memberList.forEach(memberRepository::add);
        group.setMembersList(memberList);
        groupRepository.update(group);
        return memberList;
    }


    @Override
    public void addMemberToGroup(String groupId, Member member) {
        final Member memberFromDB = memberRepository.add(member);
        Optional<Group> groupOptional = groupRepository.byId(groupId);
        groupOptional.ifPresent(group -> {
            group.addMember(memberFromDB);
            groupRepository.update(group);
        });
    }

    @Override
    public Optional<Group> byId(String groupId) {
        return groupRepository.byId(groupId);
    }


    /**
     * FallBack on Error
     */
    public List<Group> loadFromMeetUpOnError() {
        log.warn("FallBack of loadFromMeetUp, MeetUp API does not respond");
        return new ArrayList<>();
    }

    public List<Group> listGroupsOnOverhead() {
        log.warn("FallBack because overhead of list groups, ");
        return new ArrayList<>();
    }

    private List<Member> toMemberList(List<HashMap> membersFromMeetUp) {

        return membersFromMeetUp.stream().map(memberMap ->
                Member.builder().
                        name(String.valueOf(memberMap.get("name"))).
                        country(String.valueOf(memberMap.get("country"))).
                        city(String.valueOf(memberMap.get("city"))).
                        comments(String.valueOf(memberMap.get("bio"))).
                        build()).
                collect(Collectors.toList());
    }

    private List<Group> toGroupList(List<HashMap> techGroups) {
        return techGroups.stream().map(groupMap ->
                Group.builder().urlname(String.valueOf(groupMap.get("urlname"))).
                        name(String.valueOf(groupMap.get("name"))).
                        description(String.valueOf(groupMap.get("description"))).
                        link(String.valueOf(groupMap.get("link"))).
                        country(String.valueOf(groupMap.get("country"))).
                        build()).
                collect(Collectors.toList());
    }

    @SneakyThrows
    private URL apiUrl() {
        return new URL(meetUpApiUrl);
    }

    private MeetUpApi meetUpApi() {
        //rest client with builder
        return RestClientBuilder.newBuilder().baseUrl(apiUrl()).build(MeetUpApi.class);
    }
}