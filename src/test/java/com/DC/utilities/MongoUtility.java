package com.DC.utilities;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoUtility {
    protected static MongoDatabase mongoDb;
    protected String database;

    protected static Logger logger = Logger.getLogger("mongo utility logger");

    public MongoUtility() {
        PropertyConfigurator.configure("log4j.properties");
        
        ConnectionString connectionString = new ConnectionString(getMongoConnectionString());
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(codecRegistry)
                .build();
        MongoClient client = MongoClients.create(clientSettings);
        mongoDb = client.getDatabase(database);
        var command = new BsonDocument("ping", new BsonInt64(1));
        var commandResult = mongoDb.runCommand(command);
        logger.info("Successfully connected to mongodb");
    }

    private String getMongoConnectionString() {
        ReadConfig readConfig = ReadConfig.getInstance();
        String dbHosts = readConfig.getMongodbHosts();
        database = readConfig.getMongoDatabase();
        String username = readConfig.getMongodbUsername();
        String password = readConfig.getMongodbPassword();
        String replicaSet = readConfig.getMongodbReplicaSet();
        
        return "mongodb://"
                + username + ":" + password
                + "@" + dbHosts + "/" + database + "?ssl=true&replicaSet="
                + replicaSet + "&authSource=admin";
    }
}
