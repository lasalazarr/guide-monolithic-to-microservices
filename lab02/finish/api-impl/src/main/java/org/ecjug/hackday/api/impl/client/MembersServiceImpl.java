package org.ecjug.hackday.api.impl.client;

import lombok.extern.slf4j.Slf4j;
import org.ecjug.hackday.api.MembersService;
import org.ecjug.hackday.domain.model.Member;
import org.ecjug.hackday.repository.MemberRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApplicationScoped
public class MembersServiceImpl implements MembersService {

    @Inject
    private MemberRepository memberRepository;

    @Override
    public List<Member> list() {
        return memberRepository.list();
    }

    @Override
    public Member add(Member member) {
        return memberRepository.add(member);
    }
}
