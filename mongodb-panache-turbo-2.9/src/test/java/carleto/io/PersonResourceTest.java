package carleto.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import static org.junit.jupiter.api.Assertions.*;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class PersonResourceTest {

    @Inject
    PersonService personService;

    @BeforeEach
    public void beforeEach() {
        personService.deleteAll();
    }

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/person")
          .then()
             .statusCode(200)
             .body(is("Hello RESTEasy"));
    }

    @Test
    public void addRandom() {
        Person person = given()
                .contentType(ContentType.JSON)
                .when().post("/person/random")
                .then()
                .statusCode(200)
                .extract()
                .as(Person.class);
        assertNotNull(person);
    }

    @Test
    public void create() {
        Person person = new Person();
        person.id = UUID.randomUUID().toString();
        person.name = "personName "+ person.id;
        person.age = 25;
        person.genderEnum = GenderEnum.FEMALE;

        Person personDB = given()
                .contentType(ContentType.JSON)
                .body(MyObjectMapper.instance.toJson(person))
                .when().post("/person")
                .then()
                .statusCode(200)
                .extract()
                .as(Person.class);
        assertNotNull(person);
        assertEquals(person.id, personDB.id);
        assertEquals(0, personDB.version);
    }

    @Test
    public void update() {
        //create
        Person person = new Person();
        person.id = UUID.randomUUID().toString();
        person.name = "personName "+ person.id;
        person.age = 25;
        person.genderEnum = GenderEnum.FEMALE;

        Person personDB = given()
                .contentType(ContentType.JSON)
                .body(MyObjectMapper.instance.toJson(person))
                .when().post("/person")
                .then()
                .statusCode(200)
                .extract()
                .as(Person.class);
        assertEquals(person.id, personDB.id);
        assertEquals(0, personDB.version);

        //update
        personDB.name = "personNameUpdated";

        Person personDB2 = given()
                .contentType(ContentType.JSON)
                .body(MyObjectMapper.instance.toJson(personDB))
                .when().put("/person")
                .then()
                .statusCode(200)
                .extract()
                .as(Person.class);
        assertEquals(person.id, personDB2.id);
        assertEquals(1, personDB2.version);
    }

    static class MyObjectMapper extends ObjectMapper {

        public static MyObjectMapper instance;

        static {
            instance = new MyObjectMapper();
        }

        private MyObjectMapper() {
            this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }

        public Map<String, Object> convertToMap(String json) throws Exception {
            return MyObjectMapper.instance.readValue(json, Map.class);
        }

        public String toJson(Object object) {
            try {
                return this.writeValueAsString(object);
            } catch (JsonProcessingException e) {
                return null;
            }
        }
    }

}