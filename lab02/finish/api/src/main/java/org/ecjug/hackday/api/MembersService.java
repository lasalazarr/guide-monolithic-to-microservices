package org.ecjug.hackday.api;

import org.ecjug.hackday.domain.model.Member;

import java.util.List;

public interface MembersService {

    List<Member> list();

    Member add(Member member);
}
