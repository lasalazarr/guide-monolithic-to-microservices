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

## Lab 2

Now we are going to go to the directory lab02; where we are going to find two folders:

- start: Contains a base project we are going to work with.
- finish: Contains the project after making the laboratory.

Let's go to the start directory and we can import it into the IDE of your preference as a maven project.

The structure of the project should look like the following:

- api (Module that exposes the interfaces of our Business API, making use of Plugin Framework for Java (PF4J) http://www.pf4j.org)
- api-impl (Module that implements the interfaces of our API, is our business layer)
- application (Module that launches our monolithic application in openliberty and has our resources rest)
- domain (Module that includes the model of our business domain)
- mongito (Module that provides us with an integrated Mongo and MongoClient NoSQL Base)
- pom.xml (Descriptor of our project)
- repository (Module that implements our access and logic of our business model)

Many people will think that the application is overloaded or in English is known as the concept "Over-architected", but through the workshop we will explain why the separation of modules and layers that allow us to have the cleanest code and reusable in business applications of great magnitude. 

A view of the logical layers that our application has is shown below:

-------------    ----- --------       -------------    -----------
| APPLICATION|<->|API|<->API-IMPL|<->| REPOSITORY| <-> | MONGODB |
-------------    ----- --------       -------------    -----------

### Extending our business model using JPA and Lombok 

After understanding the structure of our project and having executed lab01, where we have explored Openliberty and how to create a simple application with REST resources, now we are going to include to our application the logic that will allow the registration of participants to our hackday; for this we are going to find objects that represent our business domain. 

Let's go to the domain module and find in the folder /start/domain/src/main/java/org/ecjug/hackday/domain/model the following classes: 

- Country.java
- Event.java
- Group.java

We are going to include the Member.java class in the /start/domain/src/main/java/org/ecjug/hackday/domain/model/Member.java file:

```
package org.ecjug.hackday.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class Member implements Serializable {

    @JsonIgnore
    private ObjectId id;
    @NotBlank
    private String name;
    private String country;
    private String city;
    private String comments;

    private String memberId;


    public String getMemberId() {
        if (id != null) {
            memberId = id.toString();
        }
        return memberId;
    }
}


```

Let's note that we are using notes that belong to the Lombok project (https://projectlombok.org/) which helps us to have our code cleaner as we will not find get methods, set, builders or declaration of loggers in our application.

### Creating the data access layer to MongoDB 

Now let's go to the repository module, where we are going to create the access layer of our new model of Members of a user group; for this we are going to create the file /start/repository/src/main/java/org/ecjug/hackday/repository/impl/MemberRepositoryImpl.java:

```
package org.ecjug.hackday.repository.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.types.ObjectId;
import org.ecjug.hackday.domain.model.Member;
import org.ecjug.hackday.repository.MemberRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class MemberRepositoryImpl implements MemberRepository {

    @Inject
    private MongoDatabase database;

    private MongoCollection<Member> collection;

    @Override
    public Member add(Member member) {
        Objects.requireNonNull(member, "Member can't be null");
        member.setId(new ObjectId(new Date()));
        dbCollection().insertOne(member);
        return member;
    }

    @Override
    public List<Member> memberByName(String name) {
        Objects.requireNonNull(name, "Name can't be null");
        return dbCollection().find(Filters.regex("name", name)).into(new ArrayList<>());
    }

    @Override
    public Member byId(String id) {
        return dbCollection().find(eq("_id", new ObjectId(id))).first();
    }

    @Override
    public List<Member> list() {
        List<Member> memberList = new ArrayList<>();
        MongoCursor<Member> mongoCursor = dbCollection().find().iterator();
        mongoCursor.forEachRemaining(memberList::add);
        return memberList;
    }


    private MongoCollection<Member> dbCollection() {
        if (this.collection == null) {
            this.collection = this.database.getCollection("Member", Member.class);
        }
        return this.collection;
    }
}

```

We are going to find that this implementation of the MemberRepository interface, includes dependency injection to use MongoDB as our data repository (@Inject private MongoDatabase database;).

### Creating our business logic layer

Next we are going to go to the api-impl module, where we are going to create our business logic layer for our new model of Members of a user group; for this we are going to create the file /start/api-impl/src/main/java/org/ecjug/hackday/api/impl/client/MembersServiceImpl.java:

```
package org.ecjug.hackday.api.impl.client;

import lombok.extern.slf4j.Slf4j;
import org.ecjug.hackday.api.MembersService;
import org.ecjug.hackday.domain.model.Member;
import org.ecjug.hackday.repository.MemberRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApplicationScoped
public class MembersServiceImpl implements MembersService {

    @Inject
    private MemberRepository memberRepository;

    @Override
    public List<Member> list() {
        return memberRepository.list();
    }

    @Override
    public Member add(Member member) {
        return memberRepository.add(member);
    }
}

```

### Creating a new resource JAX-RS 

And now we are going to expose our REST services using JAX-RS and JSON-P that exposes an API to list our members of a JUG; for this we are going to go to the application module and create the file /start/application/src/main/java/org/ecjug/hackday/app/resources/MemberResource.java:

```
package org.ecjug.hackday.app.resources;

import org.ecjug.hackday.api.MembersService;
import org.ecjug.hackday.domain.model.Member;

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
    public List<Member> listGroups() {
        return membersService.list();
    }

    @POST
    @Path("/add")
    public Member addMember(Member member) {
        return membersService.add(member);
    }
}
```

### Running our server

To compile the application we can execute:

``
mvn install
``

At the console we are going to see that several tests of unit and of integration are executed until arriving to have in console:

```
[INFO] Reactor Summary:
[INFO] 
[INFO] HackDay ::: Monolith Microlith Microservices ....... SUCCESS [  0.913 s]
[INFO] HackDay ::: Mongo Embedded ......................... SUCCESS [ 10.139 s]
[INFO] HackDay ::: Domain ................................. SUCCESS [  0.805 s]
[INFO] HackDay ::: Repository ............................. SUCCESS [ 14.106 s]
[INFO] HackDay ::: API .................................... SUCCESS [  0.112 s]
[INFO] HackDay ::: API Implementation ..................... SUCCESS [ 26.412 s]
[INFO] HackDay ::: Application ............................ SUCCESS [ 21.816 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 01:14 min
[INFO] Finished at: 2019-01-11T13:30:26-05:00
[INFO] Final Memory: 37M/137M
[INFO] ------------------------------------------------------------------------

```

Next, we can run our server in the application module we run liberty:start-server:

``
mvn liberty:start-server
``

To stop the server we can run:

``
mvn liberty:stop-server
``

### Test our service

You can test our new service manually by starting the server and pointing a web browser to the URL of http://localhost:9080/ and let's see our home page of our application as follows:

alt text](https://github.com/lasalazarr/workshop-05-monolith-microlith-microservices/blob/master/images/launchPage.png)

And our new resource which lists the members of the user group at the following URL http://localhost:9080/hackday/member/list which at the moment has no member.

To create new members we can run in console using the method post to the url http://localhost:9080/hackday/member/add

We have finished our second code exercise; now we are going to move on to laboratory 3 where we will include notes from the Microprofile project.

## Microprofile

Eclipse MicroProfile is a modular set of technologies designed to enable you to write native cloud microservices at Java™ In the next section of this workshop we will include several of the MicroProfile features that will help us develop and manage native cloud microservices.

In our next lab we will include the following Microprofile APIs:

alt text](https://github.com/lasalazarr/workshop-05-monolith-microlith-microservices/blob/master/images/microprofile-que-aprenderemos.png)

This modular approach we look for in our developments makes the application easy to understand, easy to develop, easy to test and easy to maintain.

