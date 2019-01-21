package org.ecjug.hackday.api.impl;

import org.ecjug.hackday.domain.model.Member;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.Dependent;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Kleber Ayala
 */
@Dependent
@RegisterRestClient
@Produces({MediaType.APPLICATION_JSON})
@Consumes(MediaType.APPLICATION_JSON)
public interface MemberApi {

    @POST
    @Path("/member/add/")
    Member add(Member member);
}
