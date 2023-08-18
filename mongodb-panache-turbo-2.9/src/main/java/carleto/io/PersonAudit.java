package carleto.io;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.common.audit.AuditableEntity;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@MongoEntity(collection = "Person_AUD")
public class PersonAudit extends AuditableEntity {

	@BsonProperty("_id")
	@BsonId
	public ObjectId _id;

	@BsonCreator
	public PersonAudit(
			@BsonProperty("createdTime") Long createdTime,
			@BsonProperty("revType") RevType revType,
			@BsonProperty("content") Document content,
			@BsonProperty("_id") ObjectId _id) {
		super(createdTime, revType, content);
		this._id = _id;
	}
}
