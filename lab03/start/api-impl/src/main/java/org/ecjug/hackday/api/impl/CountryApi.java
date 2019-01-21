package org.ecjug.hackday.api.impl;

import org.ecjug.hackday.domain.model.Country;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.Dependent;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Dependent
@RegisterRestClient
@Produces({MediaType.APPLICATION_JSON})
@Consumes(MediaType.APPLICATION_JSON)
public interface CountryApi {

    @GET
    @Path("/rest/v2/alpha/{code}")
    Country countryByCode(@PathParam("code") String code);
}