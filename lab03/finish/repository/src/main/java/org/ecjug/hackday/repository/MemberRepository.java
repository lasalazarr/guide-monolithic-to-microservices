package org.ecjug.hackday.repository;

import org.ecjug.hackday.domain.model.Member;

import java.util.List;

public interface MemberRepository {

    Member add(Member member);

    List<Member> memberByName(String name);

    Member byId(String id);

    List<Member> list();

    void update(Member member);
}
