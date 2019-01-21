package org.ecjug.hackday.app.resources;

import org.ecjug.hackday.api.GroupService;
import org.ecjug.hackday.domain.model.Group;
import org.ecjug.hackday.domain.model.Member;

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
    public Response loadFromMeetUp() {
        List<Group> groupList = groupService.loadFromMeetUp();
        JsonObject jsonResponse;
        if (groupList.isEmpty()) {
            jsonResponse = friendlyResponse("At the moment is not possible to load groups, working with fallback");
        } else {
            jsonResponse = friendlyResponse("Loaded " + groupList.size() + " groups from meetup");
        }
        return Response.ok(jsonResponse).build();
    }


    @GET
    @Path("/list")
    public List<Group> listGroups() {
        return groupService.list();
    }

    @GET
    @Path("/{groupId}")
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
    public Group addGroup( Group group) {
        return groupService.add(group);
    }

    @GET
    @Path("/meetupmembers/{id}/{urlname}")
    public Response loadMembersFromMeetUpGroup(
            @PathParam("id")
                    String id,
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