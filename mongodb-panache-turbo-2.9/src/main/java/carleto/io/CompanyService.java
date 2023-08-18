package carleto.io;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CompanyService {

	@Inject
	CompanyRepository companyRepository;

	public Company create(Company company) {
		companyRepository.persist(company);
		return company;
	}

	public Company update(Company company) {
		companyRepository.update(company);
		return company;
	}

	public void delete(Company company) {
		companyRepository.delete(company);
	}

	public void deleteAll() {
		companyRepository.deleteAll();
	}
}
