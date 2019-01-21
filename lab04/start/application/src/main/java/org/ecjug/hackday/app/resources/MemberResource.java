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
    @Counted(monotonic = true)
    @Operation(summary = "List all Members")
    public List<Member> listMembers() {
        return membersService.list();
    }

    @POST
    @Path("/add")
    @Counted(monotonic = true)
    @Operation(summary = "Creates a new Member")
    public Member addMember(@RequestBody(description = "Specify the values to create a new Member",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Member.class))) Member member) {
        return membersService.add(member);
    }
}
