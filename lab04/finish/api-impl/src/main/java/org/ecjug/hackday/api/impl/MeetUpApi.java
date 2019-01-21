package org.ecjug.hackday.api.impl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.HashMap;
import java.util.List;

/**
 * @author Kleber Ayala
 */

public interface MeetUpApi {

    @GET
    @Path("/find/groups/")
    List<HashMap> techGroups(@QueryParam("category") String category, @QueryParam("key") String key);

    @GET
    @Path("/{urlname}/members/")
    List<HashMap> members(@PathParam("urlname") String urlname);

}
