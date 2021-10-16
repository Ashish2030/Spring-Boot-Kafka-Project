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
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.sxp.HierarchyEnum.*;

//kafka consumer config
@KafkaListener(batch = true,
        properties = {
                @Property(name = ConsumerConfig.FETCH_MIN_BYTES_CONFIG, value = "1000000"),
                @Property(name = ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, value = "5000")
        })
public class JourneyLogListener {
    String previousTimeStamp = null;
    Logger logger = LoggerFactory.getLogger(JourneyLogListener.class);
    ObjectMapper mapper;
    private MongoClient mongoClient;

    public JourneyLogListener(MongoClient mongoClient) {
        this.mapper = new ObjectMapper();
        this.mongoClient = mongoClient;
    }

    /**
     * Consumes the logs from the kafka and deserializes it to the journey log class
     */
    @Topic("sxp_logs")
    public void receiveList(List<String> events) throws JsonProcessingException {
        System.out.println("START");
        List<JourneyLog> journeyLogs = new ArrayList<>();

        logger.info("Number of event {}", events.size());
        //deserializes the fluent bit log to journey log
        for (String event : events) {
            FluentBitLog log = mapper.readValue(event, FluentBitLog.class);
            JourneyLog journeyLog = mapper.readValue(log.getLog(), JourneyLog.class);
            journeyLogs.add(journeyLog);
        }

        //grouping journey logs based on projectId and initjourneyId
        Map<String, Map<String, List<JourneyLog>>> journeyLevelLogs =
                journeyLogs.stream().collect(Collectors.groupingBy(JourneyLog::getProjectId, Collectors.groupingBy(JourneyLog::getInitJourneyId)));
        System.out.println("projectId  -> "+ journeyLevelLogs.keySet());
        System.out.println("initjourneyId  -> "+ journeyLevelLogs.get("1234").keySet());
        System.out.println(journeyLevelLogs.get("1234").get("123").get(0).getClass());
        System.out.println(journeyLevelLogs.get("1234").get("123").get(1).getClass());
        System.out.println(journeyLevelLogs.get("1234").get("123").get(2).getClass());
        System.out.println(journeyLevelLogs.get("1234").get("123").get(3).getClass());
        System.out.println(journeyLevelLogs.get("1234").get("123").get(4).getClass());
        System.out.println(journeyLevelLogs.get("1234").get("123").get(5).getClass());
        System.out.println(journeyLevelLogs.get("1234").get("123").get(6).getClass());



        //grouping journey logs based on projectId and channel
        Map<String, Map<String, List<JourneyLog>>> channelLevelLogs = journeyLogs.stream().collect(Collectors.groupingBy(JourneyLog::getProjectId, Collectors.groupingBy(JourneyLog::getChannel)));
        evaluateGroupedLogs(journeyLevelLogs, JOURNEY);
        evaluateGroupedLogs(channelLevelLogs, CHANNEL);
        System.out.println("END");
    }

    /**
     * Evaluates the grouped logs and process the events
     */

    public void evaluateGroupedLogs(Map<String, Map<String, List<JourneyLog>>> journeyLevelLogs, String type) {
        for (Map.Entry<String, Map<String, List<JourneyLog>>> journeyGroupedLogs : journeyLevelLogs.entrySet()) {
            Map<String, EventCount> eventCountMap = new HashMap<>();
            final String projectId = journeyGroupedLogs.getKey();
            for (Map.Entry<String, List<JourneyLog>> kv : journeyGroupedLogs.getValue().entrySet()) {
                var eventCount = new EventCount();

                List<JourneyLog> eventss = kv.getValue();
                System.out.println(eventss.size()+" size");

                final String id = kv.getKey();
                int count=0;
                for (JourneyLog logs : eventss) {
                    System.out.println(++count+" iteration");
                       System.out.println(previousTimeStamp+" "+epocTimeToLocal(logs.getTimeStamp()));
                        if (previousTimeStamp == null)
                        {
                            previousTimeStamp = epocTimeToLocal(logs.getTimeStamp());
                            System.out.println(previousTimeStamp);
                            System.out.println(logs.getCurrentConversationState());
                            processEvents(logs.getCurrentConversationState(), eventCount);
                            System.out.println(eventCount.toString());
                            eventCountMap.put(id, eventCount);
                            checkDocumentExistsOrNot(type, projectId, id, epocTimeToLocal(logs.getTimeStamp()), eventCount,logs.getCurrentConversationState());
                            eventCount.setStartCount(0);
                            eventCount.setAbortedCount(0);
                            eventCount.setEndCount(0);

                        } else if (previousTimeStamp.equalsIgnoreCase(epocTimeToLocal(logs.getTimeStamp()))) {
                            System.out.println(logs.getCurrentConversationState()+" "+eventCount.toString());
                            processEvents(logs.getCurrentConversationState(), eventCount);
                            eventCountMap.put(id, eventCount);
                            checkDocumentExistsOrNot(type, projectId, id, epocTimeToLocal(logs.getTimeStamp()), eventCount,logs.getCurrentConversationState());
                            eventCount.setStartCount(0);
                            eventCount.setAbortedCount(0);
                            eventCount.setEndCount(0);

                        } else {
                            System.out.println(logs.getCurrentConversationState());
                            processEvents(logs.getCurrentConversationState(), eventCount);
                            previousTimeStamp = epocTimeToLocal(logs.getTimeStamp());
                            System.out.println(previousTimeStamp);
                            eventCountMap.put(id, eventCount);
                            checkDocumentExistsOrNot(type, projectId, id, epocTimeToLocal(logs.getTimeStamp()), eventCount,logs.getCurrentConversationState());
                            eventCount.setStartCount(0);
                            eventCount.setAbortedCount(0);
                            eventCount.setEndCount(0);

                        }
                    }


                logger.info("ID: {}", id);
            }
            logger.info("Project ID: {}", projectId);
            logger.info("{}", eventCountMap);
        }
    }
    /**
     * Checks whether the document exists in the db or not.
     */
    public void checkDocumentExistsOrNot(String type, String projectId, String id, String timeStamp, EventCount eventCount,String conversationState)
    {
        System.out.println(type+" "+projectId+" "+ id+" "+timeStamp+" "+eventCount.toString()+" "+conversationState);
        if(conversationState.equalsIgnoreCase("CONVERSATION_START") ||conversationState.equalsIgnoreCase("ABORTED")|| conversationState.equalsIgnoreCase("CONVERSATION_END"))
        {
            switch (type) {
                case JOURNEY:
                    //returns null when there is no document with given parameters in db
                    //returns not null when there is a document with given parameters in db
                    var jouneycountAnalytic = getCollection().find(and(
                            eq("projectId", projectId),
                            eq("type", type),
                            eq("journeyId", id),
                            eq("date", timeStamp)
                    ))
                            .first();
                    System.out.println(jouneycountAnalytic);
                    updateData(type, projectId, eventCount, id, jouneycountAnalytic, timeStamp);
                    break;
                case CHANNEL:
                    var channelCountAnalytic = getCollection().find(and(
                            eq("projectId", projectId),
                            eq("type", type),
                            eq("channel", id),
                            eq("date", timeStamp)
                    ))
                            .first();
                    System.out.println(channelCountAnalytic);
                    updateData(type, projectId, eventCount, id, channelCountAnalytic, timeStamp);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Update the count in the db if document exists otherwise it inserts the document in to the db
     */

    private void updateData(String type, String projectId, EventCount eventCount, String id, CountAnalytic jouneyCountAnalytic, String timeStamp) {
        System.out.println("updateDate "+type+" "+projectId+" "+eventCount+" "+id+" "+jouneyCountAnalytic+" "+timeStamp);
        if (jouneyCountAnalytic == null) {
            jouneyCountAnalytic = new CountAnalytic();
            jouneyCountAnalytic.setProjectId(projectId);
            jouneyCountAnalytic.setType(type);
            jouneyCountAnalytic.setStarts(eventCount.getStartCount());
            jouneyCountAnalytic.setAborts(eventCount.getAbortedCount());
            jouneyCountAnalytic.setEnds(eventCount.getEndCount());
            System.out.println("Date"+jouneyCountAnalytic.getDate()+"id"+jouneyCountAnalytic.getId());

        } else {
            jouneyCountAnalytic.setStarts(jouneyCountAnalytic.getStarts() + eventCount.getStartCount());
            jouneyCountAnalytic.setAborts(jouneyCountAnalytic.getAborts() + eventCount.getAbortedCount());
            jouneyCountAnalytic.setEnds(jouneyCountAnalytic.getEnds() + eventCount.getEndCount());
        }
        switch (type) {
            case JOURNEY:
                jouneyCountAnalytic.setJourneyId(id);
                break;
            case CHANNEL:
                jouneyCountAnalytic.setChannel(id);
                break;
            default:
                break;
        }

        switch (type) {

            case JOURNEY:
                if (jouneyCountAnalytic.getDate() == null && jouneyCountAnalytic.getId() == null) {
                    jouneyCountAnalytic.setDate(timeStamp);
                    getCollection().insertOne(jouneyCountAnalytic);
                } else {
                    System.out.println("Updating database");
                    getCollection().findOneAndReplace(and(
                            eq("_id", jouneyCountAnalytic.getId()),
                            eq("date", jouneyCountAnalytic.getDate()),
                            eq("projectId", projectId),
                            eq("journeyId", id),
                            eq("type", type)
                    ), jouneyCountAnalytic, new FindOneAndReplaceOptions().upsert(true));
                }
                break;
            case CHANNEL:
                if (jouneyCountAnalytic.getDate() == null) {
                    jouneyCountAnalytic.setDate(timeStamp);
                    getCollection().insertOne(jouneyCountAnalytic);
                } else {
                    getCollection().findOneAndReplace(and(
                            eq("date", jouneyCountAnalytic.getDate()),
                            eq("projectId", projectId),
                            eq("channel", id),
                            eq("type", type)
                    ), jouneyCountAnalytic, new FindOneAndReplaceOptions().upsert(true));
                }
                break;
            default:
                break;
        }
    }

    /**
     * Process the logs to increase the event  count according the current conversation state
     */

    public static void processEvents(String conversationState, EventCount eventCount) {
        switch (conversationState) {
            case "CONVERSATION_START":
                eventCount.setStartCount(0);
                eventCount.setStartCount(eventCount.getStartCount() + 1);
                break;
            case "ABORTED":
                eventCount.setAbortedCount(0);
                eventCount.setAbortedCount(eventCount.getAbortedCount() + 1);
                break;
            case "CONVERSATION_END":
                eventCount.setEndCount(0);
                eventCount.setEndCount(eventCount.getEndCount() + 1);
                break;
            default:
                break;
        }
    }


    private MongoCollection<CountAnalytic> getCollection() {
        return mongoClient
                .getDatabase("sxp-dev")
                .getCollection("counter-analytics", CountAnalytic.class);
    }

    /**
     * Converts epoch timestamp to local timestamp
     */

    public static String epocTimeToLocal(long timestamp) {
        LocalDate date =
                Instant.ofEpochMilli(timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
        return date.toString();
    }
}

