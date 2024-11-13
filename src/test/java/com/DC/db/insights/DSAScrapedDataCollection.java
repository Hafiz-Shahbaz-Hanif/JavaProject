package com.DC.db.insights;
import com.DC.objects.insights.RetailerScrapData;
import com.DC.utilities.MongoUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import static com.DC.utilities.SharedMethods.getJsonWriterSettings;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;

public class DSAScrapedDataCollection extends MongoUtility {

    public final MongoCollection<BsonDocument> DSA_SCRAPED_DATA_COLLECTION =
            mongoDb.getCollection("dsa-scraped-data", BsonDocument.class);

    public RetailerScrapData createListOfDocumentsAndAggregateCollection(String retailerDomain) throws IOException {
        List<Bson> pipeline = createAggregationPipelineWithFilter("domain", retailerDomain);
        AggregateIterable<BsonDocument> latestScrapData = DSA_SCRAPED_DATA_COLLECTION.aggregate(pipeline);
        JsonWriterSettings settings = getJsonWriterSettings();
        String document = Objects.requireNonNull(latestScrapData.first()).toJson(settings);
        return  new ObjectMapper().readValue(document, RetailerScrapData.class);
    }

    public List<RetailerScrapData> createListOfDocumentsByDateAndAggregateCollection(String dateToSort) throws IOException {
        List<Bson> pipeline = createAggregationPipelineWithFilter("date", dateToSort);
        AggregateIterable<BsonDocument> latestScrapData = DSA_SCRAPED_DATA_COLLECTION.aggregate(pipeline);
        List<RetailerScrapData> retailerScrapDataList = newArrayListWithExpectedSize(12);
        List<BsonDocument> listOfRetailersWithDate = Lists.newArrayList(latestScrapData);
        for (BsonDocument retailer : listOfRetailersWithDate) {
            JsonWriterSettings settings = getJsonWriterSettings();
            String document = Objects.requireNonNull(retailer).toJson(settings);
            retailerScrapDataList.add(new ObjectMapper().readValue(document, RetailerScrapData.class));
        }
        return  retailerScrapDataList;
    }

    public List<String> createStringListOfRetailersFromScrapDataList(List<RetailerScrapData> latestScrapData) throws IOException {
        List<String> retailerScrapDataList = newArrayListWithExpectedSize(12);
        for (RetailerScrapData retailer : latestScrapData) {
            retailerScrapDataList.add(retailer.domain);
        }
        return  retailerScrapDataList;
    }

    public List<Bson> createAggregationPipelineWithFilter(String filter, String filterValue) {
        Document date = new Document("date",
                new Document("$dateToString",
                        new Document("format", "%Y-%m-%d")
                                .append("date", "$dateScraped")))
                .append("domain", "$domain");
        Bson id = new Document("_id", date).append("NumberOfScrapes", new Document("$sum", 1));
        Bson group = new Document("$group", id);
        Bson project = new Document("$project",
                new Document("_id", 0)
                        .append("date", "$_id.date")
                        .append("domain", "$_id.domain")
                        .append("NumberOfScrapes", 1));
        Bson sort = new Document("$sort", new Document("date", -1));
        Bson match = new Document("$match", new Document(filter, filterValue));
        return Arrays.asList(group, project, match, sort);
    }
}
