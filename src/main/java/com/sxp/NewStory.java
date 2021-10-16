package com.sxp;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Property;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.sxp.HierarchyEnum.CHANNEL;
import static com.sxp.HierarchyEnum.JOURNEY;

//kafka consumer config
@KafkaListener(batch = true,
        properties = {
                @Property(name = ConsumerConfig.FETCH_MIN_BYTES_CONFIG, value = "1000000"),
                @Property(name = ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, value = "5000")
        })
public class NewStory {
    String previousTimeStamp = null;
    Logger logger = LoggerFactory.getLogger(NewStory.class);
    ObjectMapper mapper;
    private MongoClient mongoClient;

    public NewStory(MongoClient mongoClient) {
        this.mapper = new ObjectMapper();
        this.mongoClient = mongoClient;
    }
    /**
     * Consumes the logs from the kafka and deserializes it to the journey log class
     */
    @Topic("sxp_logs")
    public void receiveList123(List<String> events) throws JsonProcessingException {
        System.out.println("START New Story");
        List<JourneyLog> journeyLogs = new ArrayList<>();

        logger.info("Number of event {}", events.size());
        //deserializes the fluent bit log to journey log
        for (String event : events) {
            FluentBitLog log = mapper.readValue(event, FluentBitLog.class);
            JourneyLog journeyLog = mapper.readValue(log.getLog(), JourneyLog.class);
            journeyLogs.add(journeyLog);
        }

        checkResponse(journeyLogs);
    }
    public    void checkResponse(List<JourneyLog> journeyLogs)
    {
        for(int i=0;i<journeyLogs.size();i++)
        {
            JourneyLog curr=journeyLogs.get(i);
            if(curr.utteranceRef!=null && curr.userResponse!=null)
            {
                  if(checkcurrentConversationState(curr))
                  {
                      pushintodb(curr);
                  }
            }
        }
    }
    public static boolean checkcurrentConversationState(JourneyLog journeyLog)
    {
        if(journeyLog.currentConversationState.equals("CONVERSATION_END"))
        {
         return true;
        }
        else
        {
            return false;
        }
    }
    private void  pushintodb(JourneyLog curr)
    {
        getCollection().insertOne(curr);

    }
    private MongoCollection<JourneyLog> getCollection() {
        return mongoClient
                .getDatabase("sxp-dev")
                .getCollection("counter-analytics", JourneyLog.class);
    }

}
