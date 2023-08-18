package carleto.io;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PersonAuditRepository implements PanacheMongoRepositoryBase<PersonAudit, String> {
}
