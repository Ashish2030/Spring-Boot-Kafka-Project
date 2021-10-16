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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;

public class    RtAnalyticsTest {

    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    MongoCollection mongoCollection;
    FindIterable<CountAnalytic> ex;

    ArgumentCaptor<CountAnalytic> countAnalyticArgumentCaptor;

    @Before
    public void setUp() {

        mongoClient = Mockito.mock(MongoClient.class);
        mongoDatabase = Mockito.mock(MongoDatabase.class);
        mongoCollection = Mockito.mock(MongoCollection.class);
        countAnalyticArgumentCaptor = ArgumentCaptor.forClass(CountAnalytic.class);
        ex = Mockito.mock(FindIterable.class);
        Mockito.when(mongoClient.getDatabase("sxp-dev")).thenReturn(mongoDatabase);
        Mockito.when(mongoDatabase.getCollection("counter-analytics", CountAnalytic.class)).thenReturn(mongoCollection);
        Mockito.when(mongoCollection.find(any(Bson.class))).thenReturn(ex);

    }

    @Test
    public void analyticCountTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        final CountAnalytic count = new CountAnalytic();
        count.setStarts(1);
        count.setAborts(2);
        count.setEnds(3);

        Mockito.when(ex.first()).thenReturn(count);
        JourneyLogListener listener = new JourneyLogListener(mongoClient);

        List<String> events = IOUtils.readLines(RtAnalyticsTest.class.getResourceAsStream("/events"), "UTF-8");
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

        listener.receiveList(logs);
        Mockito.verify(mongoCollection).insertOne(countAnalyticArgumentCaptor.capture());

        CountAnalytic countAnalytic = countAnalyticArgumentCaptor.getValue();
        Assert.assertArrayEquals(new long[]{5, 6, 7}, new long[]{countAnalytic.getStarts(), countAnalytic.getAborts(), countAnalytic.getEnds()});
        Assert.assertEquals(null, countAnalytic.getProjectId());
        Assert.assertEquals("2022-01-11", countAnalytic.getDate());
        Assert.assertEquals("webchat", countAnalytic.getChannel());
        Assert.assertEquals(null, countAnalytic.getType());
        Assert.assertEquals("123", countAnalytic.getJourneyId());

    }

    @Test
    public void eventCountTest() throws IOException {
        EventCount eventcount = new EventCount();

        JourneyLogListener listener = new JourneyLogListener(mongoClient);
        ObjectMapper mapper = new ObjectMapper();
        List<String> events = IOUtils.readLines(RtAnalyticsTest.class.getResourceAsStream("/events"), "UTF-8");
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
        listener.receiveList(logs);

        Assert.assertArrayEquals(new long[]{0, 0, 0}, new long[]{eventcount.getStartCount(), eventcount.getAbortedCount(), eventcount.getEndCount()});
    }
}
