package org.ecjug.hackday.app.rest;

import static org.junit.Assert.assertEquals;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.Test;

public class GroupRestTest {

    @Test
    public void testGetProperties() {
        String url = "http://localhost:9080/application/";

        Client client = ClientBuilder.newClient();
        client.register(JsrJsonpProvider.class);

        WebTarget target = client.target(url + "hackday/group/list");
        Response response = target.request().get();

        assertEquals("Incorrect response code from " + url, Response.Status.OK.getStatusCode(), response.getStatus());

        JsonObject obj = response.readEntity(JsonObject.class);

        assertEquals("Ecuador Java User Group",
                    obj.getString("ECJUG"));
        response.close();
    }
}
