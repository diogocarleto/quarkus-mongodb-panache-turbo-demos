package carleto.io;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntityReference;
import io.quarkus.mongodb.panache.common.Reference;
import io.quarkus.mongodb.panache.common.Version;
import io.quarkus.mongodb.panache.common.audit.annotation.Auditable;
import org.bson.codecs.pojo.annotations.BsonId;

@MongoEntity
@MongoEntityReference(@Reference(value = Company.class, foreignKeyPath = "employeesIds"))
@Auditable
public class Person {

	@BsonId
	public String id;

	public String name;

	public Integer age;

	public GenderEnum genderEnum;

	@Version
	public Long version;
}
