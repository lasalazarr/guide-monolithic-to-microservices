package org.ecjug.hackday.app.resources;

import org.ecjug.hackday.api.MembersService;
import org.ecjug.hackday.domain.model.Member;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
@Produces({MediaType.APPLICATION_JSON})
@Consumes(MediaType.APPLICATION_JSON)
@Path("/member")
public class MemberResource {

    @Inject
    private MembersService membersService;

    @GET
    @Path("/list")
    public List<Member> listMembers() {
        return membersService.list();
    }

    @POST
    @Path("/add")
    public Member addMember(Member member) {
        return membersService.add(member);
    }
}