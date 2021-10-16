package com.sxp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
public class NewStoryTest {
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    MongoCollection mongoCollection;
    FindIterable<Document> ex;
    ArgumentCaptor<Document> documentArgumentCaptor;
    @Before
    public void setUp() {
        mongoClient = Mockito.mock(MongoClient.class);
        mongoDatabase = Mockito.mock(MongoDatabase.class);
        mongoCollection = Mockito.mock(MongoCollection.class);
        documentArgumentCaptor = ArgumentCaptor.forClass(Document.class);
        ex = Mockito.mock(FindIterable.class);
        Mockito.when(mongoClient.getDatabase("sxp-dev")).thenReturn(mongoDatabase);
        Mockito.when(mongoDatabase.getCollection("user-data")).thenReturn(mongoCollection);
        Mockito.when(mongoCollection.find(any(Bson.class))).thenReturn(ex);
    }
    @Test
    public void verifyingUserDataInsertion() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        NewStory userData = new NewStory(mongoClient);
        List<String> events = IOUtils.readLines(NewStory.class.getResourceAsStream("/events"), "UTF-8");
        List<String> logs = events.stream().map(event -> {
            FluentBitLog fluentBitLog = new FluentBitLog();
            fluentBitLog.setLog(event);
            fluentBitLog.setTimestamp(1234);
            try {
                return mapper.writeValueAsString(fluentBitLog);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
        userData.receiveList123(logs);
        Mockito.verify(mongoCollection).insertOne(documentArgumentCaptor.capture());
        assertThat(documentArgumentCaptor.getValue().size(), is(7));
        assertThat(documentArgumentCaptor.getValue(), IsMapContaining.hasEntry("name", "shankar"));
        assertThat(documentArgumentCaptor.getValue(), IsMapContaining.hasEntry("gender", "male"));
        assertThat(documentArgumentCaptor.getValue(), IsMapContaining.hasValue("38ac4c7c-d840-4c28-acf0-35e21c0a3baa"));
        assertThat(documentArgumentCaptor.getValue(), not(IsMapContaining.hasEntry("name", "ravi")));
        assertThat(documentArgumentCaptor.getValue(), IsMapContaining.hasEntry("isCompleted", false));
    }
}