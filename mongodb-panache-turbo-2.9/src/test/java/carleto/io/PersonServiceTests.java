package carleto.io;

import carleto.io.infra.MongoDBLifecycleManager;
import io.quarkus.mongodb.panache.common.audit.AuditableEntity;
import io.quarkus.mongodb.panache.common.exception.MongoEntityReferenceException;
import io.quarkus.mongodb.panache.common.exception.OptimisticLockException;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(MongoDBLifecycleManager.class)
public class PersonServiceTests {

	@Inject
	PersonService personService;

	@Inject
	CompanyService companyService;

	@Inject
	PersonAuditRepository personAuditRepository;

	@BeforeEach
	public void beforeEach() {
		personService.deleteAll();
	}

	@Test
	public void createRandom() {
		Person person = personService.addRandomPerson();
		assertNotNull(person);
		//informs that version is set
		assertEquals(0, person.version);
	}

	/**
	 * Here we see versiosing changing
	 */
	@Test
	public void createAndUpdate() {
		//first create
		Person person = personService.addRandomPerson();
		assertNotNull(person);
		//informs that version is set
		assertEquals(0, person.version);

		//then update
		person.name = "personName updated";
		Person personUpdate = personService.update(person);

		assertEquals(person.name, personUpdate.name);
		//informs that version is incremented
		assertEquals(1, person.version);
	}

	/**
	 * Try to update with an outdated version
	 */
	@Test
	public void createAndUpdateWithWrongVersion() {
		//first create
		Person person = personService.addRandomPerson();
		assertNotNull(person);
		//informs that version is set
		assertEquals(0, person.version);

		//then update with wrong version
		person.name = "personName updated";
		person.version = -1L;
		assertThrows(OptimisticLockException.class, () -> personService.update(person));
	}

	/**
	 * Here we are testing MongoEntityReference basically
	 * First we create a Person, then add the Person to a Company and try to delete the Person.
	 * The system will not allow delete the Person because it is referenced by a Company, so we need to remove the Person from the Company first.
	 */
	@Test
	public void createCompanyWithPersonAndTryToDeletePerson() {
		Person person = personService.addRandomPerson();

		Company company = new Company();
		company.id = UUID.randomUUID().toString();
		company.name = "fakeCompany";
		company.employeesIds = new ArrayList<>();
		company.employeesIds.add(person.id);

		Company companyDB = companyService.create(company);
		assertNotNull(companyDB);

		//try to delete the person
		assertThrows(MongoEntityReferenceException.class, () -> personService.delete(person));

		//remove the person from the company
		companyDB.employeesIds.remove(person.id);
		companyDB = companyService.update(companyDB);
		assertTrue(companyDB.employeesIds.isEmpty());
		assertEquals(1, companyDB.version);

		//now we can delete the person
		assertDoesNotThrow(() -> personService.delete(person));
	}

	/**
	 * Here we include and update the Person, and check the changes reflected in the audit.
	 */
	@Test
	public void auditChangesInPerson() {
		personAuditRepository.deleteAll();
		Person person = personService.addRandomPerson();
		person.name = "personName updated";

		personService.update(person);

		personService.delete(person);

		//we have 3 audit entries: create, update and delete
		List<PersonAudit> personAuditList = personAuditRepository.findAll().list();
		assertEquals(3, personAuditList.size());

		assertNotEquals(person.name, filter(personAuditList, AuditableEntity.RevType.ADD).name);
		assertEquals(person.name, filter(personAuditList, AuditableEntity.RevType.MOD).name);
		assertEquals(person.name, filter(personAuditList, AuditableEntity.RevType.DEL).name);
	}

	private Person filter(final List<PersonAudit> personAuditList, AuditableEntity.RevType revType) {
		PersonAudit personAudit = personAuditList.stream()
				.filter(_personAudit -> _personAudit.revType.equals(revType))
				.findFirst().get();

		return personAudit.extractContentAs(Person.class);
	}
}
