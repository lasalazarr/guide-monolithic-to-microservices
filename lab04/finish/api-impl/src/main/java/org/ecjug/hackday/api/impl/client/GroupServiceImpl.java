package org.ecjug.hackday.api.impl.client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ecjug.hackday.api.GroupService;
import org.ecjug.hackday.api.impl.CountryApi;
import org.ecjug.hackday.api.impl.MeetUpApi;
import org.ecjug.hackday.api.impl.MemberApi;
import org.ecjug.hackday.domain.model.Country;
import org.ecjug.hackday.domain.model.Group;
import org.ecjug.hackday.domain.model.Member;
import org.ecjug.hackday.repository.GroupRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.pf4j.Extension;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.net.ssl.*;
import javax.ws.rs.client.Client;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
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
    @RestClient //RestClient with injection
    private CountryApi countryApi;

    @Inject
    @RestClient
    private MemberApi memberApi;

    @Inject
    @ConfigProperty(name = "meetup.url", defaultValue = "https://api.meetup.com")
    private String meetUpApiUrl;
    @Inject
    @ConfigProperty(name = "meetup.key")
    private String meetUpApiKey;
    private Client restClient;

    @Override
    @SneakyThrows
    @Metered(name = "addGroup")
    public Group add(Group group) {
        String countryCode = group.getCountry();
        Country country = countryApi.countryByCode(countryCode);
        group.setCountryInformation(country);
        group = groupRepository.add(group);
        return group;
    }

    @Override
    @Metered
    public List<Group> list() {
        return groupRepository.list();
    }

    @Override
    @Metered //measuring the rate of events over time
    @Timed(name = "loadFromMeetUpTime") //measures how long a method or block of code takes to execute
    @CircuitBreaker
    @Retry(maxRetries = 1)
    @Fallback(fallbackMethod = "loadFromMeetUpOnError")
    public List<Group> loadFromMeetUp() {
        //34--> tech category on meetup
        List<HashMap> techGroups = meetUpApi().techGroups("34", meetUpApiKey);
        List<Group> groupList = toGroupList(techGroups);
        groupList.forEach(this::add);
        return groupList;
    }

    @Override
    @Metered
    public List<Member> loadMembersFromMeetUpGroup(Group group) {
        List<HashMap> membersFromMeetUp = meetUpApi().members(group.getUrlname());
        List<Member> memberList = toMemberList(membersFromMeetUp);
        memberList.forEach(memberApi::add);
        group.setMembersList(memberList);
        groupRepository.update(group);
        return memberList;
    }

    @Override
    @Metered
    public void addMemberToGroup(String groupId, Member member) {
        final Member memberFromDB = memberApi.add(member);
        Optional<Group> groupOptional = groupRepository.byId(groupId);
        groupOptional.ifPresent(group -> {
            group.addMember(memberFromDB);
            groupRepository.update(group);
        });
    }

    @Override
    @Metered
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

    @PostConstruct
    public void sslHack() {
        try {
            //this is a hack to enable untrusted ssl handshake,
            //metrics use a secure connection, and we are enabling it
            //with  <quickStartSecurity userName="admin" userPassword="password"/>
            //    <keyStore id="defaultKeyStore" password="mpKeystore"/>
            // on server.xml

            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new java.security.SecureRandom());

            HostnameVerifier allowAll = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };


            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(allowAll);
        } catch (Exception e) {
            log.error("Errorr-->", e);
        }
    }
}
