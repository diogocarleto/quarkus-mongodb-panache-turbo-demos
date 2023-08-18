package carleto.io;


import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/person")
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {

    @Inject
    PersonService personService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello RESTEasy";
    }

    @POST
    @Path("/random")
    public Person addRandomPerson() {
        return personService.addRandomPerson();
    }

    @POST
    @Path("/")
    public Person create(final Person person) {
        return personService.create(person);
    }

    @PUT
    @Path("/")
    public Person update(final Person person) {
        return personService.update(person);
    }
}
