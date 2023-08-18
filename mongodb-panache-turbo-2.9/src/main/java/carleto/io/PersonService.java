package carleto.io;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class PersonService {

	@Inject
	PersonRepository personRepository;

	public Person addRandomPerson() {
		Person person = new Person();
		person.id = UUID.randomUUID().toString();
		person.name = "personName "+ person.id;
		person.age = 20;
		person.genderEnum = GenderEnum.MALE;
		personRepository.persist(person);
		return person;
	}

	public Person create(Person person) {
		personRepository.persist(person);
		return person;
	}

	public Person update(Person person) {
		personRepository.update(person);
		return person;
	}

	public void delete(Person person) {
		personRepository.delete(person);
	}

	public void deleteAll() {
		personRepository.deleteAll();
	}
}
