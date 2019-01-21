package org.ecjug.hackday.app.resources;

import org.ecjug.hackday.api.GroupService;
import org.ecjug.hackday.domain.model.Group;
import org.ecjug.hackday.domain.model.Member;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Produces({MediaType.APPLICATION_JSON})
@Consumes(MediaType.APPLICATION_JSON)
@Path("/group")
public class GroupResource {

    @Inject
    private GroupService groupService;

    @GET
    @Path("/meetupload")
    @Counted(monotonic = true)
    @Operation(summary = "Load all tech groups from Meetup")
    public Response loadFromMeetUp() {
        List<Group> groupList = groupService.loadFromMeetUp();
        JsonObject jsonResponse;
        if (groupList.isEmpty()) {
            jsonResponse = friendlyResponse("At the moment is not possible to load groups, working with fallback");
        } else {
            jsonResponse = friendlyResponse("Loaded or updated " + groupList.size() + " groups from meetup");
        }
        return Response.ok(jsonResponse).build();
    }


    @GET
    @Path("/list")
    @Counted(monotonic = true)
    @Operation(summary = "List all groups")
    public List<Group> listGroups() {
        return groupService.list();
    }

    @GET
    @Path("/{groupId}")
    @Counted(monotonic = true)
    @Operation(summary = "Find a group by ID")
    public Response groupById(@PathParam("groupId") String groupId) {
        Response response;
        Optional<Group> groupOptional = groupService.byId(groupId);
        if (groupOptional.isPresent()) {
            response = Response.ok(groupOptional.get()).build();
        } else {
            response = Response.status(404, "There are not group with id " + groupId).build();
        }
        return response;
    }

    @POST
    @Path("/add")
    @Counted(monotonic = true)
    @Operation(summary = "Creates a new Group")
    public Group addGroup(@RequestBody(description = "Specify the values to create a new Group",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Group.class))) Group group) {
        return groupService.add(group);
    }

    @GET
    @Path("/meetupmembers/{id}/{urlname}")
    @Counted(monotonic = true)
    @Operation(summary = "Load all members from a specific meetup group")
    public Response loadMembersFromMeetUpGroup(
            @Parameter(description = "Group id", required = true)
            @PathParam("id")
                    String id,
            @Parameter(description = "Meetup urlname", required = true)
            @PathParam("urlname") String urlname) {
        Optional<Group> groupOptional = groupService.byId(id);
        Response response;
        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            List<Member> memberList = groupService.loadMembersFromMeetUpGroup(group);
            response = Response.ok(friendlyResponse("Loaded " + memberList.size() + " members from " + group.getName())).build();
        } else {
            response = Response.status(404, "There are not group with id " + id).build();
        }

        return response;
    }

    private JsonObject friendlyResponse(String message) {
        JsonObject jsonResponse = Json.createObjectBuilder()
                .add("message", message).build();
        return jsonResponse;
    }
}
