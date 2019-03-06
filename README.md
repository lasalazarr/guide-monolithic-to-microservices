# guide-monolithic-to-microservices
Step by step examples of migration phases from a Monolithic Java EE Application to Microservices .

## Introduction 

In the era that we are of containers, cloud and many tools to choose at the time of creating new applications or evolving those we have, we present tips and emphasize patterns and elementary practices that apply in our day to day development of business applications. Through this 2 hour workshop we are going to create an application from Monolithic to Microlitic and finally Microservices.

## Requirements:

- Open JDK 8 or higher.
- Maven.
- The IDE of your choice.
- An application server or Microprofile distribution in this case for the workshop we will use OpenLiberty (https://openliberty.io). 
- Docker (https://www.docker.com/get-started).

We would appreciate having the pre-requisites ready prior to the start of the next section. 

## That we will use:

You will learn how to run and update a simple application based on REST services and deployed on an Open Liberty server. We will use Maven throughout the guide to create, implement and interact with the running server instance.

### Open Liberty

Open Liberty is an application server designed for the cloud. It is small, lightweight and designed with the development of native cloud applications in mind. It supports all MicroProfile and Jakarta EE (Java EE) APIs. It also deploys on all major cloud platforms, including Docker, Kubernetes and Foundry Cloud.

### Maven

Maven is an automation creation tool that provides an efficient way to develop Java applications. Using Maven, we will build our services. You will then perform the server configuration and code changes and see how a running server picks them up. You'll also explore how to package your application with the server runtime so that it can be deployed anywhere at once. Finally, we'll pack the application along with the server configuration into a Docker image and run it as a container.

### Notes:

The repository is composed by a project that can be deployed as monolith, microlith or microservices; also in the first laboratories we will create simple applications without the need of the pre elaborated code. The beginning of the workshop begins with the file readme.md that contains a summary of the work to be done. 

The repository is composed of 4 folders:

- lab01: Includes our first workshop with a simple application that exposes an API subtracted from the list of Java user groups participating in the Hackday. 
- lab02: It includes the structure of our project that exposes an API rest of administration of the members and groups of Hackday participating users, manages persistence using mongo as NoSQL base. 
- lab03: In the same way that the monotilo branch is composed of two folders start and finish.
- lab04: Contains the initial project to base it on microservices and the folder finish the whole workshop.

## Monoliths

Most developers who have been coding in java for some years are very familiar with a common pattern of multi-layer enterprise application development called Monolithic application separated into layers as follows:

---------- ----------- ----------------
| WEB | <-> | BUSINESS| <-> | DATABASE |
---------- ----------- -----------------

The web layer is developed using Java Server Faces or in recent years is used Angular, Angular JS, React, VueJS or other framework.

We have commonly developed the business layer using Enterprise Java Beans and the Java Persistence Api weight layer and every day we use these specifications / apis accompanied by the application server of your choice (JBOSS, WEBSPHERE, WEBLOGIC, Others) that implements it. 

Next we will create a simple monolith in the laboratory 1 and we will continue with an application with pre ready code to explain concepts of the new wave of enterprise application development with Java:

## Lab 1

Each laboratory starts with the folder of the laboratory number; that is, in this case we are going to go to the lab01 directory.

Let's find two folders:

- start: Contains a base project with which we are going to work.
- finish: Contains the project after making the laboratory.

Let's go to the start directory and we can import it into the IDE of your preference as a maven project.

### Lift Openliberty and run a Simple application

Now let's create a JAX-RS application from 0 and learn the main considerations to configure an Open Liberty application. 

### Creating a Rest application with JAX-RS 

When creating a new REST application, API design is important. JAX-RS APIs could be created with JSON-RPC, or XML-RPC APIs, but it would not be a RESTful service. A good RESTful service is designed around the resources that are exposed, and how to create, query, update and remove resources.

Let's create a simple service would respond to GET requests to the path /hackday/group. The GET request must return a 200 OK response containing a set of user groups participating from our hackday.

Let's create a JAX-RS application class in the file src/main/java/org/ecjug/hackday/app/HackDayApplication.java:

```
package org.ecjug.hackday.app;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/hackday")
public class HackDayApplication extends Application {

}
```

The HackDayApplication class extends the Application class, which in turn associates all classes of JAX-RS resources from the WAR file with this JAX-RS application, making them available under the common path specified in the HackDayApplication class. The @ApplicationPath annotation has a value that indicates the path within the WAR from which the JAX-RS application accepts requests, i.e. (/hackday).

### Creating a JAX-RS resource 

In JAX-RS, a single class must represent a single resource or a group of resources of the same type. In our application, a resource can be a system property or a set of system properties. It is easy to have a single class that handles multiple different resources, but maintaining a clean separation between resource types helps the long-term maintenance of the application.

Let's create resource class JAX-RS in the src/main/java/org/ecjug/hackday/app/resources/GroupResource.java folder:

```
package org.ecjug.hackday.app.resources;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/group")
public class GroupResource {

    @GET
    @Path("/list")
    @Produces({MediaType.APPLICATION_JSON})
    public JsonObject listGroups() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("ECJUG","Ecuador Java User Group");
        builder.add("MEDJUG","Medellin Java User Group");
        builder.add("BJUG","Barcelona Java User Group");
        builder.add("MJUG","Madrid Java User Group");
        builder.add("MalagaJUG","Malaga Java User Group");

        return builder.build();
    }
}
```

The @Path annotation in the class indicates that this resource responds to the properties path in the JAX-RS application. 

The @GET annotation in the method indicates that this method should be called for the HTTP GET method. The @Produce notation indicates the format of the content to be returned, the value of the @Produces notation will be specified in the Content-Type HTTP response header. For this application, a JSON structure must be returned. The desired content type for a JSON response is application/json with MediaType.APPLICATION_JSON instead of the String content type. Using literal code such as MediaType.APPLICATION_JSON is better because in the case of a spelling error, a compile failure occurs.

JAX-RS supports several ways to bring JSON together. The JAX-RS specification requires JSON (JSON-P) and JAX-B processing. Most JAX-RS implementations also support a Java POJO to JSON conversion, which allows the Properties object to be returned in place. Although this conversion would allow a simpler implementation, it limits the portability of the code since the conversion from POJO to JSON is not standard. This gap in the specification is fixed in Java EE 8 with the inclusion of JSON-B.

The body of the method performs the following actions: Creates a JsonObjectBuilder object using the Json class. The JsonObjectBuilder is used to fill a JsonObject with values.

It calls the entrySet method in the Properties object to get a Set of all entries.
Convert the Set to a Stream (new in Java SE 8) by calling the stream method. Sequences make working with all entries in a list very easy. Return JsonObject by calling the build method in JsonObjectBuilder.

### Configuring the Server

For the service to work, the Liberty server must be configured correctly.

Let's edit the src/main/liberty/config/server.xml file and it should look like this:

```
<server description="Laboratorio 01 Hackday Open Liberty Server">

  <featureManager>
      <feature>jaxrs-2.1</feature>
      <feature>jsonp-1.1</feature>
  </featureManager>

  <httpEndpoint httpPort="${default.http.port}" httpsPort="${default.https.port}"
      id="defaultHttpEndpoint" host="*" />

  <webApplication location="application.war" contextRoot="${app.context.root}"/>
</server>
```

The configuration performs the following actions:

- Configures the server to support both JAX-RS and JSON-P. This is specified in the featureManager element.

- Configure the server to collect the HTTP port numbers of the variables, which are then specified in Maven's pom.xml file. This is specified in the httpEndpoint element. Variables use the ${variableName} syntax.

- Configure the server to run the Web application produced in a context root specified in Maven's pom.xml file. This is specified in the webApplication element.

The variables used in the server.xml file are provided by the bootstrapProperties section of Maven's pom.xml:

``
<bootstrapProperties>
    <default.http.port>${testServerHttpPort}</default.http.port>
    <default.https.port>${testServerHttpsPort}</default.https.port>
    <app.context.root>${warContext}</app.context.root>
</bootstrapProperties>
``

### Running our server

To create the application, run the Maven installation phase from the command line in the home directory:

``
mvn install
``

This command builds the application and creates a.war file in the destination directory. It also configures and installs Open Liberty in the target/liberty/wlp directory.

Then run the Maven liberty:start-server target:

``
mvn liberty:start-server
``

To stop the server we can run:

``
mvn liberty:stop-server
``

This target starts an Open Liberty server instance. Your Maven pom.xml is already configured to start the application on this server instance.

### Test our service

You can test this service manually by starting a server and pointing a web browser to the URL of http://localhost:9080/application/hackday/group/list resulting in a list of user groups participating in this hackday (if your JUG is not listed include it):

``
{"ECJUG":"Ecuador Java User Group","MEDJUG":"Medellin Java User Group","BJUG":"Barcelona Java User Group","MADRIDJUG":"Madrid Java User Group","MalagaJUG":"Malaga Java User Group"}
``

Automated testing is necessary in our day-to-day life to avoid failures if a change introduces an error. JUnit and the JAX-RS client API provide a very simple environment to test the application.

Let's create a test class in the src/test/java/org/ecjug/hackday/app/rest/GroupRestTest.java file:

```
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
```

This kind of test has more lines of code than the resource implementation. This situation is common. The test method is indicated by the annotation @Test. The test code needs to know some information about the application in order to make requests. The server port and the context root of the application are key, and are dictated by the server configuration. 

We have finished our first code exercise; now let's move on to lab 2.



