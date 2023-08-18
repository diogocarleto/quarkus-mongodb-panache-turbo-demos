package carleto.io.infra;

import io.quarkus.logging.Log;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;

public class MongoDBLifecycleManager implements QuarkusTestResourceLifecycleManager {

	private MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:5.0.10"));

	@Override
	public Map<String, String> start() {
		Log.info("starting mongoDB");
		mongoDBContainer.setEnv(List.of("w=majority"));
		mongoDBContainer.start();

		return Map.of(
				"quarkus.mongodb.connection-string", mongoDBContainer.getConnectionString(),
				"quarkus.mongodb.database", "panacheturboTestDB");
	}

	@Override
	public void stop() {
		Log.info("stopping mongoDB");
		mongoDBContainer.stop();
		Log.info("mongoDB stopped");
	}
}
