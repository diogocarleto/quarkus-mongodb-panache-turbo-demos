package carleto.io;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.common.Version;
import org.bson.codecs.pojo.annotations.BsonId;

import java.util.List;

@MongoEntity
public class Company {

	@BsonId
	public String id;

	public String name;

	public List<String> employeesIds;

	@Version
	public Long version;
}
