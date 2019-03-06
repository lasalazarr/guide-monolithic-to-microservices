# Monolith to Microlith and Microservices

En la era que estamos de contenedores, cloud y muchas herramientas que escoger en el momento de crear nuevas aplicaciones o evolucionar las que tenemos; presentamos tips y hacemos enfasis en patrones y practicas elementales que aplican en nuestro dia a dia de desarrollo de aplicaciones empresariales.  A travez de este taller de 2 horas vamos a crear una aplicacion desde pasando de Monolitica a Microlitica y finalmente Microservicios.

## Requisitos:

- Open JDK 8 o superior.
- Maven.
- El IDE de su preferencia.
- Un servidor de aplicaciones o distribucion de Microprofile en este caso para el taller vamos a utilizar OpenLiberty (https://openliberty.io). 
- Docker (https://www.docker.com/get-started).

Agradeceriamos tener listo los pre requisitos previo al inicio de la siguiente seccion. 

## Que usaremos:

Aprenderas a ejecutar y actualizar una aplicacion simple basada en servicios REST y desplegada en un servidor Open Liberty. Usaremos Maven a lo largo de toda la guía para crear, implementar e interactuar con la instancia de servidor en ejecución.

### Open Liberty

Open Liberty es un servidor de aplicaciones diseñado para la nube. Es pequeño, ligero y diseñado pensando en el desarrollo de aplicaciones nativas de la nube. Soporta todas las APIs de MicroProfile y Jakarta EE (Java EE). También se despliega en todas las principales plataformas de nube, incluyendo Docker, Kubernetes y Cloud Foundry.

### Maven

Maven es una herramienta de creación de automatización que proporciona una forma eficiente de desarrollar aplicaciones Java. Usando Maven, vamos construir nuestros servicios. A continuación, realizará la configuración del servidor y los cambios de código y verá cómo los recoge un servidor en ejecución. También explorará cómo empaquetar su aplicación con el tiempo de ejecución del servidor para que se pueda implementar en cualquier lugar de una sola vez. Finalmente, empaquetaremos la aplicación junto con la configuración del servidor en una imagen Docker y la ejecutará como un contenedor.

###Notas:

El repositorio esta compuesto por un proyecto que puede ser desplegado como monolito, microlito o microservicios; ademas en los primeros laboratorios vamos a crear aplicaciones simples sin necesidad del codigo pre elaborado. El inicio del taller comienza con el archivo readme.md que contine un resumen del trabajo a realizar. 

El repositorio esta compuesto por 4 carpetas:

- lab01: Incluye nuestro primer taller con una aplicacion simple que expone un API rest del listado de grupos de usuarios Java participantes del Hackday. 
- lab02: Incluye la estructura de nuestro proyecto que expone un API rest de administracion de los miembros y grupos de usuarios participantes del Hackday, maneja persistencia usando mongo como base NoSQL. 
- lab03: De igual maneera que la rama monotilo esta compuesta de dos folders start y finish.
- lab04: Contiene el proyecto inicial para basarlo en microservicios y la carpeta finish el taller completo.

Cada rama tiene un archivo readme.md por el que comienza el taller de cada patron de construccion de aplicaciones empresariales.

## Monolitos

La mayoria de desarrolladores que llevan algunos años codificando en java estan muy familiarizados con un patron comun de desarrollo de aplicaciones empresariales multi capa que se denomina aplicacion Monolitica separada en capas de la siguiente manera:

----------     -----------     ----------------
| WEB    | <-> | BUSINESS| <-> | BASE DE DATOS |
----------     -----------     -----------------

La capa web es desarrollada usando Java Server Faces u en los ultimos años se usa Angular, Angular JS, React, VueJS u otro framework.

La capa de negocio comunmente la hemos desarrollado usando Enterprise Java Beans y la Capa de pesistencia Java Persistence Api y  dia a dia utilizamos estas especificaciones / apis acompanado  del servidor de aplicaciones de su preferencia (JBOSS, WEBSPHERE, WEBLOGIC, Others) que lo implementa. 

A continuacion vamos a crear un monolito simple en el laboratorio 1 y seguiremos con una aplicacion con codigo pre listo para explicar conceptos de la nueva ola de desarrollo de aplicaciones Empresariales con Java:

## Laboratorio 1

Cada laboratorio inicia con el folder del numero de laboratorio; es decir en esta caso vamos a dirigirnos al directorio lab01.

Vamos a encontrar dos carpetas:

- start: Contiene un proyecto base con el que vamos a trabajar.
- finish: Contiene el proyecto luego de realizar el laboratorio.

Vamos a ir al directorio start y podemos importarlo en el IDE de su preferencia como proyecto maven.

### Levantar Openliberty y correr una aplicacion Simple

Ahora vamos a crear una aplicacion JAX-RS desde 0 y aprederas las consideraciones principales para configurar una aplicacion sobre Open Liberty. 

### Creando una aplicación Rest con JAX-RS 

Al crear una nueva aplicación REST, el diseño de la API es importante. Las APIs de JAX-RS podrían ser creadas con JSON-RPC, o APIs XML-RPC, pero no sería un servicio RESTful. Un buen servicio RESTful está diseñado en torno a los recursos que se exponen, y cómo crear, consultar, actualizar y eliminar recursos.

Vamos a crear un servicio simple responderia a las peticiones de GET a la ruta /hackday/group. La solicitud GET debe devuelve una respuesta 200 OK que contiene un conjunto de grupos de usuario participantes de nuestro hackday.

Vamos a crear una clase de aplicación JAX-RS en el archivo src/main/java/org/ecjug/hackday/app/HackDayApplication.java:

```
package org.ecjug.hackday.app;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/hackday")
public class HackDayApplication extends Application {

}
```

La clase HackDayApplication extiende la clase Application, que a su vez asocia todas las clases de recursos JAX-RS del archivo WAR con esta aplicación JAX-RS, esta haciendo disponibles bajo la ruta común especificada en la clase HackDayApplication. La anotación @ApplicationPath tiene un valor que indica la ruta dentro de la WAR de la que la aplicación JAX-RS acepta peticiones, es decir (/hackday).

### Creando un recurso JAX-RS 

En JAX-RS, una sola clase debe representar un solo recurso o un grupo de recursos del mismo tipo. En nuestra aplicación, un recurso puede ser una propiedad del sistema o un conjunto de propiedades del sistema. Es fácil tener una sola clase que maneje múltiples recursos diferentes, pero mantener una separación limpia entre los tipos de recursos ayuda al mantenimiento a largo plazo de la aplicacion.

Vamos a crear la clase de recurso JAX-RS en la carpeta src/main/java/org/ecjug/hackday/app/resources/GroupResource.java:

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


La anotación @Path en la clase indica que este recurso responde a la ruta de propiedades en la aplicación JAX-RS. 

La anotación @GET en el método indica que este método debe llamarse para el método HTTP GET. La anotación @Produce indica el formato del contenido que se devolverá, el valor de la anotación @Produces se especificará en el encabezado de respuesta HTTP Content-Type. Para esta aplicación, se debe devolver una estructura JSON. El tipo de contenido deseado para una respuesta JSON es application/json con MediaType.APPLICATION_JSON en lugar del tipo de contenido String. Usar código literal como MediaType.APPLICATION_JSON es mejor porque en el caso de un error ortográfico, ocurre una falla de compilación.

JAX-RS soporta varias formas de reunir a JSON. La especificación JAX-RS exige el procesamiento JSON (JSON-P) y JAX-B. La mayoría de las implementaciones JAX-RS también soportan una conversión de Java POJO a JSON, que permite devolver el objeto Properties en su lugar. Aunque esta conversión permitiría una implementación más simple, limita la portabilidad del código ya que la conversión de POJO a JSON no es estándar. Esta laguna en la especificación está fijada en Java EE 8 con la inclusión de JSON-B.

El cuerpo del método realiza las siguientes acciones: Crea un objeto JsonObjectBuilder utilizando la clase Json. El JsonObjectBuilder se utiliza para rellenar un JsonObject con valores.

Llama al método entrySet en el objeto Properties para obtener un Set de todas las entradas.
Convierta el Set a un Stream (nuevo en Java SE 8) llamando al método stream. Las secuencias hacen que trabajar con todas las entradas de una lista sea muy sencillo. Devuelve el JsonObject llamando al método build en el JsonObjectBuilder.

### Configurando el Servidor

Para que el servicio funcione, el servidor de Liberty debe estar configurado correctamente.

Vamos a editar el archivo src/main/liberty/config/server.xml y debe quedar de la siguiente manera:
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
La configuración realiza las siguientes acciones:

- Configura el servidor para soportar tanto JAX-RS como JSON-P. Esto se especifica en el elemento featureManager.

- Configura el servidor para que recoja los números de puerto HTTP de las variables, que luego se especifican en el archivo pom.xml de Maven. Esto se especifica en el elemento httpEndpoint. Las variables utilizan la sintaxis ${variableName}.

- Configura el servidor para que ejecute la aplicación Web producida en una raíz de contexto especificada en el archivo pom.xml de Maven. Esto se especifica en el elemento webApplication.

Las variables que se utilizan en el archivo server.xml son proporcionadas por la sección bootstrapProperties del pom.xml de Maven:

``
<bootstrapProperties>
    <default.http.port>${testServerHttpPort}</default.http.port>
    <default.https.port>${testServerHttpsPort}</default.https.port>
    <app.context.root>${warContext}</app.context.root>
</bootstrapProperties>
``

### Ejecutando nuestro servidor

Para crear la aplicación, ejecute la fase de instalación de Maven desde la línea de comandos en el directorio de inicio:

``
mvn install
``
Este comando construye la aplicación y crea un archivo.war en el directorio de destino. También configura e instala Open Liberty en el directorio target/liberty/wlp.

A continuación, ejecute el objetivo de Maven liberty:start-server:

``
mvn liberty:start-server
``

Para parar el servidor podemos ejecutar:

``
mvn liberty:stop-server
``


Este objetivo inicia una instancia de servidor Open Liberty. Su pom.xml de Maven ya está configurado para iniciar la aplicación en esta instancia de servidor.

### Test de nuestro servicio

Puede probar este servicio manualmente iniciando un servidor y apuntando un navegador web a la URL de http://localhost:9080/application/hackday/group/list obteniendo como resultado el listado de grupos de usuarios participantes en este hackday (si tu JUG no esta listado incluyelo):

``
{"ECJUG":"Ecuador Java User Group","MEDJUG":"Medellin Java User Group","BJUG":"Barcelona Java User Group","MADRIDJUG":"Madrid Java User Group","MalagaJUG":"Malaga Java User Group"}
``

Las pruebas automatizadas son necesarias en nuestro dia a dia para evitar  fallas si un cambio introduce un error. JUnit y el API del cliente JAX-RS proporcionan un entorno muy sencillo para probar la aplicación.

Vamos a crear una clase de prueba en el archivo src/test/java/org/ecjug/hackday/app/rest/GroupRestTest.java:

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

Esta clase de prueba tiene más líneas de código que la implementación del recurso. Esta situación es común. El método de prueba se indica con la anotación @Test. El código de prueba necesita saber alguna información sobre la aplicación para poder hacer solicitudes. El puerto del servidor y la raíz del contexto de la aplicación son clave, y están dictados por la configuración del servidor. 

Hemos terminado nuestro primer ejercicio de codigo; ahora vamos a pasar al laboratorio 2.

## Laboratorio 2

Ahora vamos a dirigirnos al directorio lab02; donde vamos a encontrar dos carpetas:

- start: Contiene un proyecto base con el que vamos a trabajar.
- finish: Contiene el proyecto luego de realizar el laboratorio.

Vamos a ir al directorio start y podemos importarlo en el IDE de su preferencia como proyecto maven.

La estructura del proyecto debe verse como la siguiente:

- api (Modulo que expone las interfaces de nuestro API de Negocio, haciendo uso de Plugin Framework for Java (PF4J) http://www.pf4j.org)
- api-impl (Modulo que implementa las interfaces de nuestro API, es nuestra capa de negocio)
- application (Modulo que lanza nuestra aplicación monolitica en openliberty y tiene nuestros recursos rest)
- domain (Modulo que incluye el modelo de nuestro dominio de negocio)
- mongito (Modulo que nos provee de Produce una Base NoSQL Mongo y MongoClient integrado)
- pom.xml (Descriptor de nuestro proyecto)
- repository (Modulo que implementa nuestro acceso y logica de nuestro modelo de negocio)

Muchas personas van a pensar que la aplicacion esta sobre cargada o en ingles se conoce el concepto como "Over-architected"; pero atravez del taller vamos a ir explicando el por que la separacion de modulos y layers que nos permiten tener el codigo mas limpio y reusable en aplicaciones empresariales de gran magnitud. 

Una vista de las capas logicas que tiene nuestra aplicacion se muestra a continuacion:

-------------    -----   --------    -------------     -----------
| APPLICATION|<->|API|<->API-IMPL|<->| REPOSITORY| <-> | MONGODB |
-------------    -----   --------    -------------     -----------

### Extendiendo nuestro modelo de negocio usando JPA y Lombok 

Luego de entender la estructura de nuestro proyecto y haber ejecutado el lab01, donde hemos explorado Openliberty y como crear una aplicacion simple con recursos REST, ahora vamos incluir a nuestra aplicacion la logica que que permitira el registro de participantes a nuestros hackday; para ello vamos a encontrar objetos que representen nuestro dominio de negocio. 

Vamos dirigirnos al modulo domain y encontraremos en el folder /start/domain/src/main/java/org/ecjug/hackday/domain/model las siguientes clases: 

- Country.java
- Event.java
- Group.java

Vamos a incluir al modelo la clase Member.java en el archivo /start/domain/src/main/java/org/ecjug/hackday/domain/model/Member.java:

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
Vamos a denotar que estamos usando anotaciones que pertencen al proyecto Lombok (https://projectlombok.org/) el cual nos ayuda a tener nuestro codigo mas limpio ya que no vamos a encontrar metodos get, set, constructores o declaracion de loggers en nuestra aplicacion.

### Creando la capa de acceso a datos a MongoDB 

Ahora vamos a ir al modulo repository, donde vamos a crear la capa de acceso de nuestro nuevo modelo de Miembros de un grupo de usuarios; para ello vamos a crear el archivo /start/repository/src/main/java/org/ecjug/hackday/repository/impl/MemberRepositoryImpl.java:

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

Vamos a encontrar que esta implementacion de la interface MemberRepository, incluye inyeccion de dependencia para usar MongoDB como nuestro repositorio de datos (@Inject private MongoDatabase database;).

### Creando nuestra capa de logica de negocio

A continuacion vamos a ir al modulo api-impl, donde vamos a crear nuestra capa de logica de negocio para nuestro nuevo modelo de Miembros de un grupo de usuarios; para ello vamos a crear el archivo /start/api-impl/src/main/java/org/ecjug/hackday/api/impl/client/MembersServiceImpl.java:

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

### Creando un nuevo recurso JAX-RS 

Y ahora vamos a exponer nuestros servicios REST usando JAX-RS y JSON-P que exponga un API para listar nuestros miembros de un JUG; para esto vamos a ir al modulo application y crear el archivo /start/application/src/main/java/org/ecjug/hackday/app/resources/MemberResource.java:

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

### Ejecutando nuestro servidor

Para compilar la aplicacion podemos ejecutar:

``
mvn install
``

En consola vamos a ver que se ejecutan varias pruebas de unidad y de integracion hasta llegar a tener en consola:

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
A continuación, podemos ejecutar nuestro servidor en el modulo application ejecutamos liberty:start-server:

``
mvn liberty:start-server
``

Para parar el servidor podemos ejecutar:

``
mvn liberty:stop-server
``


### Test de nuestro servicio

Puede probar nuestro nuevo servicio manualmente iniciando el servidor y apuntando un navegador web a la URL de http://localhost:9080/ y vamos ver nuestra pagina de inicio de nuestra aplicacion de la siguiente manera:

![alt text](https://github.com/lasalazarr/workshop-05-monolith-microlith-microservices/blob/master/images/launchPage.png)

Y nuestro nuevo recurso que lista los miembros del grupo de usuarios en la siguiente URL http://localhost:9080/hackday/member/list que al momento no tiene ningun miembro.

Para crear nuevos miembros podemos ejecutar en consola mediante el metodo post a la url http://localhost:9080/hackday/member/add

Hemos terminado nuestro segundo ejercicio de codigo; ahora vamos a pasar al laboratorio 3 donde incluiremos anotaciones del proyecto Microprofile.

## Microprofile

Eclipse MicroProfile es un conjunto modular de tecnologías diseñadas para que pueda escribir microservicios nativos de la nube en Java™. En la siguiente seccion de este taller vamos a incluir varias de las caracteristicas de MicroProfile que nos ayudaran a desarrollar y gestionar microservicios nativos de la nube.

En nuestro proximo laboratorio incluiremos los siguientes APIs de Microprofile:

![alt text](https://github.com/lasalazarr/workshop-05-monolith-microlith-microservices/blob/master/images/microprofile-que-aprenderemos.png)

Este enfoque modular que buscamos en nuestros desarrollos hace que la aplicación sea fácil de entender, fácil de desarrollar, fácil de probar y fácil de mantener.

## Laboratorio 3

Ahora vamos a dirigirnos al directorio lab03; donde vamos a encontrar dos carpetas:

- start: Contiene un proyecto base con el que vamos a trabajar e incluiremos anotaciones de varios de los modulos que nos provee microprofile.
- finish: Contiene el proyecto luego de realizar el laboratorio.

Vamos a ir al directorio start y podemos importarlo en el IDE de su preferencia como proyecto maven.

### Microprofile Health Check

Health checks (Estado de Salud) se utilizan para sondear el estado de un nodo desde otra máquina o de nuestra applicación(por ejemplo, el controlador de servicio de kubernetes).

En nuestro escenario, vamos a incluir un Health Check para comprobar que nuestra instancia embebida de MongoDB esta funcionando correctamente; para ello vamos a ir a nuestro proyecto mongito y crear el archivo /start/mongito/src/main/java/org/ecjug/hackday/mongo/health/MongoHealthCheck.java:

```
package org.ecjug.hackday.mongo.health;

import com.mongodb.MongoClient;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Health
@ApplicationScoped
@Slf4j
public class MongoHealthCheck implements HealthCheck {

    @Inject
    private MongoClient mongoClient;

    @Override
    public HealthCheckResponse call() {
        log.info("Health check for mongo database  ");

        HealthCheckResponse healthCheckResponse = null;
        if (mongoClient != null) {
            log.info("Mongo database active!");
            healthCheckResponse = HealthCheckResponse.named(MongoHealthCheck.class.getSimpleName()).up().build();
        } else {
            log.error("Mongo database is not active!");
            healthCheckResponse = HealthCheckResponse.named(MongoHealthCheck.class.getSimpleName()).down().build();
        }
        return healthCheckResponse;
    }


}

```
Vamos a ir descomponiendo el uso del API de Microprofile; usando el contexto de CDI, nuestra clase que implementan HealthCheck y esta anotada con @Health se descubren automáticamente y son invocados por el framework o runtime (microprofile). 

Luego de complementar las demas caracteristicas de Microprofile que estamos incorporando a nuestro proyecto podremos verificar el estado de nuestro HealthCheck accediendo en el browser a la URL: http://localhost:9080/health teniendo como resultado:

```
{"checks":[{"data":{},"name":"MongoHealthCheck","state":"UP"},{"data":{},"name":"MeetupHealthCheck","state":"DOWN"}],"outcome":"DOWN"}
```
### Microprofile Metrics

Esta especificación tiene como objetivo proporcionar una forma unificada para que los servidores de microprofile exporten datos de monitoreo ("telemetría") a los agentes de gestión y también una API unificado de Java, que todos los programadores (de aplicaciones) pueden utilizar para exponer sus datos de monitoreo automático.

La anotacion @Counted nos provee un contador es un simple incremento y disminución de tiempo; en nuestro caso queremos saber cuantas veces ha sido llamdo nuestro API Rest; para ello vamos a ir al archivo /start/application/src/main/java/org/ecjug/hackday/app/resources/MemberResource.java y vamos a incluir en nuestros metodos la anotacion: 

``
@Counted(monotonic = true)
``

Luego de complementar las demas caracteristicas de Microprofile que estamos incorporando a nuestro proyecto podremos verificar las metricas de nuestros metodos accediendo en el browser a la URL: https://localhost:9443/metrics 

Donde nos va a pedir el user y password que definimos en nuestro server.xml del proyecto application:

![alt text](https://github.com/lasalazarr/workshop-05-monolith-microlith-microservices/blob/master/images/loginmetrics.png)

Con ello verificaremos las metricas que nos provee Microprofile.

### Microprofile Open Api

Esta especificación de MicroProfile tiene como objetivo proporcionar una API unificada de Java para la especificación OpenAPI v3, que todos los desarrolladores de aplicaciones pueden utilizar para exponer su documentación de API. Al aplicar esta caracteristica de Microprofile podremos ver la documentación de nuestro API en una interfaz de usuario fácil de usar.

Para ello vamos a ir al archivo /start/application/src/main/java/org/ecjug/hackday/app/resources/MemberResource.java y vamos a incluir en nuestros metodos las anotaciones:

- En nuestro metodo listMethods incluiremos: @Operation(summary = "List all Members")
- Y en nuestro metodo addMember incluiremos: @Operation(summary = "Creates a new Member") y en los parametros del metodo: 

``
@RequestBody(description = "Specify the values to create a new Member",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Member.class))) Member member
``

Nuestra clase /start/application/src/main/java/org/ecjug/hackday/app/resources/MemberResource.java quedaria de la siguiente manera: 

```
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

```

Luego de complementar las demas caracteristicas de Microprofile que estamos incorporando a nuestro proyecto podremos verificar el la documentacion de nuestro API accediendo en el browser a la URL: http://localhost:9080/openapi/ui/ y tendremos como resultado:

![alt text](https://github.com/lasalazarr/workshop-05-monolith-microlith-microservices/blob/master/images/openapi.png)

### Microprofile Fault Tolerance

La tolerancia a fallos consiste en aprovechar diferentes estrategias para guiar la ejecución y el resultado de cierta lógica. 

Microprofile incluye el soporte a Tolerancia a Fallos con los siguientes aspectos: 

Timeout: Definir una duración para el timeout

RetryPolicy: Definir un criterio sobre cuándo volver a intentarlo

Fallback: proporciona una solución alternativa para una ejecución fallida.

Bulkhead: Nos permite aislar las fallas en parte del sistema mientras que el resto del sistema todavía puede funcionar.

CircuitBreaker: Ofrece una forma de fallar rápidamente al fallar la ejecución automáticamente para prevenir la sobrecarga del sistema y la espera o tiempo de espera indefinido por parte de los clientes.

A continuacion vamos a implementar en nuestro proyecto algunas de estas caracteristicas; para esto vamos a ir al archivo /start/api-impl/src/main/java/org/ecjug/hackday/api/impl/client/GroupServiceImpl.java especificamente al metodo loadFromMeetUp:

```
    @Override
    @Metered //measuring the rate of events over time
    @Timed(name = "loadFromMeetUpTime") //measures how long a method or block of code takes to execute
    @CircuitBreaker
    @Retry(maxRetries = 1)
    @Fallback(fallbackMethod = "loadFromMeetUpOnError")
    public List<Group> loadFromMeetUp() {
        //34--> tech category on meetup
        List<HashMap> techGroups = meetUpApi().techGroups("34", meetUpApiKey);
        List<Group> groupList = toGroupList(techGroups);
        groupList.forEach(this::add);
        return groupList;
    }
```

Vamos a encontrar en nuestro metodo la aplicacion de @Timed, @CircuitBreaker, @Retry y @Fallback. Te invitamos a analizar cada una de ellas y discutir donde podemos aplicarlas en nuestro dia a dia como desarrolladores; adicional puedes verificar nuestro test de esta capa de negocio donde puedes verificar el comportamiento de cada una de estas caracteristicas de Microprofile en el archivo /start/api-impl/src/test/java/org/ecjug/hackday/api/impl/test/GroupServiceTest.java

Finalmente te invitamos a navegar en el codigo de nuestra aplicacion y citar en donde estamos usando caracteristicas como:

- Config
- Rest Client
- CDI

### Ejecutando nuestro servidor

Para compilar la aplicacion podemos ejecutar:

``
mvn install
``

En consola vamos a ver que se ejecutan varias pruebas de unidad y de integracion hasta llegar a tener en consola:

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
A continuación, podemos ejecutar nuestro servidor en el modulo application ejecutamos liberty:start-server:

``
mvn liberty:start-server
``

Para parar el servidor podemos ejecutar:

``
mvn liberty:stop-server
``


### Ejecutando como docker container

Al momento de compilar la aplicacion denotamos una demora; que tenia que ver con la creacion de la imagen de nuestro servidor openliberty; para ejecutar la imagen creada en consola podemos ver el id con el que se creo ejecutando: 

``
docker images
``

En consola vamos a encontrar nuestra imagen creada en el momento que compilamos nuestro proyecto de la siguiente manera:

```
REPOSITORY                                TAG                 IMAGE ID            CREATED             SIZE
application                               1.0.0-SNAPSHOT      32dcf8b6bc25        11 minutes ago      538MB
```

En consola para ejecutar nuestro contenedor podemos ejecutar (verificar el IMAGE ID al listar el comando anterior):

``
docker run -d --name hack-hay-app -p 9080:9080 -p 9443:9443 32dcf8b6bc25
``

Hemos terminado nuestro tercer ejercicio de codigo; ahora vamos a pasar al laboratorio 4 donde simularemos el que nuestra aplicacion a crecido y vamos a separarla en Microservicios ya que al momento hemos estado trabajando en el concepto de Monolito o como la mayoria de organizaciones terminan con Microlitos.

## Microlitos

Muchas organizaciones y equipos de desarrollo en su intento de llegar a tener microservicios han terminado en una estilo de aplicaciones denominado Microlito. 

Muchos no estan familiarizados con el termino; pero si han buscado el realizar Microservicios pero no cumplen caracteristicas como:

- Resiliance
- Scaling
- Independent deployment
- Your own schema
- Accessing data through APIs

Han llegado a tener un Microlito en lugar de Microservicios.

En resumen muchas instituciones y empresas tienen un intento de realizar microservicios pero lo que hemos llegado es a tener nuestro Monolitos desplegados en contenedores y con dependencias a puntos unicos de falla.

## Microservicios

"Microservicios" es la palabra de moda desde que Netflix la popularizo, es un patron de arquitectura que todos quieren tener, pero que pocos logran realizar. Como citamos en la definicion de Microlitos es facil verificar que llegamos a cumplir este patron y tiene que ver principalmente con responder a las caractericas que acabamos de citar; es decir:

- Resiliance
- Scaling
- Independent deployment
- Your own schema
- Accessing data through APIs

No vamos a entrar en la definicion teorica de Microservicios; y vamos a tomar nuestro caso practico donde vamos imaginar que por el alto uso de nuestra aplicacion y nuestro equipo de desarrollo a escalado en tamaño ya que hemos tenido exito con nuestra aplicacion de registro de eventos de miembros de usuarios java necesitamos dividirla en microservicios. 

## Laboratorio 4

Ahora vamos a dirigirnos al directorio lab04; donde vamos a encontrar dos carpetas:

- start: Contiene un proyecto base con el que vamos a trabajar y lo vamos a dividir en microservicios que se basa en nuestra aplicacion terminada del lab03; para dividirlo en modulos y despliegarlo haciendo uso de docker compose.

- finish: Contiene el proyecto luego de realizar el laboratorio.

Vamos a ir al directorio start y podemos importarlo en el IDE de su preferencia como proyecto maven.

## Separar el API de Miembros de JUGs

Vamos a mover nuestro API de miembros especificamente el archivo /application/src/main/java/org/ecjug/hackday/app/resources/MemberResource.java a una nueva aplicacion al archivo /member-application/src/main/java/org/ecjug/member/app/resource/MemberResource.java

Es decir; en nuestro modulo application ya no tendra el archivo /application/src/main/java/org/ecjug/hackday/app/resources/MemberResource.java y estara en un nuevo modulo denominado member-application.


## Cambio en nuestra capa de logica de negocio

Ahora vamos a realizar un refactor de nuestro modulo api-impl donde haciamos uso de miembros de usuario; en el archivo /api-impl/src/main/java/org/ecjug/hackday/api/impl/client/GroupServiceImpl.java

Vamos a cambiar en lugar de usar el repositorio de private MemberRepository memberRepository; vamos incluir el Rest Client de miembros de usuarios:

```
@Inject
    @RestClient //RestClient with injection
    private CountryApi countryApi;
```

Y los metodos donde usabamos el repositorio ahora hacen uso del servicios de la siguiente manera:

```
@Override
    @Metered
    public List<Member> loadMembersFromMeetUpGroup(Group group) {
        List<HashMap> membersFromMeetUp = meetUpApi().members(group.getUrlname());
        List<Member> memberList = toMemberList(membersFromMeetUp);
        memberList.forEach(memberApi::add);
        group.setMembersList(memberList);
        groupRepository.update(group);
        return memberList;
    }

    @Override
    @Metered
    public void addMemberToGroup(String groupId, Member member) {
        final Member memberFromDB = memberApi.add(member);
        Optional<Group> groupOptional = groupRepository.byId(groupId);
        groupOptional.ifPresent(group -> {
            group.addMember(memberFromDB);
            groupRepository.update(group);
        });
    }

```




### Compilar nuestras aplicaciones

Para compilar nuestras aplicaciones podemos ejecutar en la raiz del proyecto:

``
mvn install
``

### Ejecutando con docker compose

Al momento de compilar la aplicacion denotamos una demora; que tenia que ver con la creacion de las imagenes de nuestros servidorer openliberty; para ejecutar las imagenes creadas usando docker compose en consola podemos ejecutar: 

``
docker-compose up
``

Podemos ver la documentacion de nuestros APIs corriendo en las URLs:

- http://localhost:9080/openapi/ui/
- http://localhost:9081/openapi/ui/

O las paginas de inicio de cada una de nuestras aplicaciones en las URLs:

- http://localhost:9080/

![alt text](https://github.com/lasalazarr/workshop-05-monolith-microlith-microservices/blob/master/images/launchPage.png)

- http://localhost:9081/

![alt text](https://github.com/lasalazarr/workshop-05-monolith-microlith-microservices/blob/master/images/launchPage2.png)


Hemos terminado nuestro cuarto ejercicio de codigo; hemos pasado por el concepto de Monolito, Microlitos y Microservicios; agradecemos su interes y participacion. 

# Agradecimientos

Conoce un poco mas de los autores de estos talleres siguiendo en twitter a:

- [Alberto Salazar](https://twitter.com/betoSalazar)
- [Kleber Ayala](https://twitter.com/keal_)

Miembros fundadores del Grupo de Usuarios Java del Ecuador [EcuadorJug](https://twitter.com/EcuadorJUG)







