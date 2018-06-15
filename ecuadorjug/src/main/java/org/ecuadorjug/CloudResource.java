package org.ecuadorjug;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("clouds")
public class CloudResource {

    @Inject
    private BusinessLogic businessLogic;

    @Inject
    Event<Cloud> newClouds;

    @GET
    public JsonArray getClouds(){
        return businessLogic.getClouds().stream().map(this::createCloudJson).collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void saveCloud(@NotNull @Valid Cloud cloud){
        businessLogic.saveCloud(cloud);
        newClouds.fire(cloud);
    }

    private JsonObject createCloudJson(Cloud cloud){
        return Json.createObjectBuilder().add("name", cloud.getName()).add("hype", cloud.getHype()).build();
    }
}
