package com.sxp;

/*
 *  Copyright (C) Techsophy, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Ravi Raja <ravi.m@techsophy.com>, April 2021
 *
 */


import java.util.List;



public class   JourneyLog {

    String eventName;
    String userId;
    String initJourneyId;
    String journeyId;
    String conversationId;
    String channel;
    String componentId;
    String projectId;
    boolean isChannelSwitched;
    String channelSwitchedFrom;
    String channelSwitchedTo;
    String isWaitingForInput;
    String isBillable;
    String utteranceRef;
    String userResponse;
    String sxpResponse;
    List<String> attachments;
    String userMessageType;
    String componentType;
    long timeStamp;
    String abPath;
    String currentConversationState;
    String humanChatCustomerId;
    String humanChatCustomerChannel;
    String humanChatAgentId;
    String humanChatAgentChannel;
    String apiStatus;
    String disconnectedByPeer;
    String channelSwitched;

    public String getChannelSwitched() {
        return channelSwitched;
    }
    public void setChannelSwitched(String channelSwitched) {
        this.channelSwitched = channelSwitched;
    }
    public String getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(String apiStatus) {
        this.apiStatus = apiStatus;
    }

    public String getDisconnectedByPeer() {
        return disconnectedByPeer;
    }

    public void setDisconnectedByPeer(String disconnectedByPeer) {
        this.disconnectedByPeer = disconnectedByPeer;
    }

    public String getHumanChatAgentChannel() {
        return humanChatAgentChannel;
    }

    public void setHumanChatAgentChannel(String humanChatAgentChannel) {
        this.humanChatAgentChannel = humanChatAgentChannel;
    }

    public String getHumanChatAgentId() {
        return humanChatAgentId;
    }

    public void setHumanChatAgentId(String humanChatAgentId) {
        this.humanChatAgentId = humanChatAgentId;
    }

    public String getHumanChatCustomerChannel() {
        return humanChatCustomerChannel;
    }

    public void setHumanChatCustomerChannel(String humanChatCustomerChannel) {
        this.humanChatCustomerChannel = humanChatCustomerChannel;
    }

    public String getHumanChatCustomerId() {
        return humanChatCustomerId;
    }

    public void setHumanChatCustomerId(String humanChatCustomerId) {
        this.humanChatCustomerId = humanChatCustomerId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInitJourneyId() {
        return initJourneyId;
    }

    public void setInitJourneyId(String initJourneyId) {
        this.initJourneyId = initJourneyId;
    }

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public boolean isChannelSwitched() {
        return isChannelSwitched;
    }

    public void setChannelSwitched(boolean channelSwitched) {
        isChannelSwitched = channelSwitched;
    }

    public String getChannelSwitchedFrom() {
        return channelSwitchedFrom;
    }

    public void setChannelSwitchedFrom(String channelSwitchedFrom) {
        this.channelSwitchedFrom = channelSwitchedFrom;
    }

    public String getChannelSwitchedTo() {
        return channelSwitchedTo;
    }

    public void setChannelSwitchedTo(String channelSwitchedTo) {
        this.channelSwitchedTo = channelSwitchedTo;
    }

    public String getIsWaitingForInput() {
        return isWaitingForInput;
    }

    public void setIsWaitingForInput(String isWaitingForInput) {
        this.isWaitingForInput = isWaitingForInput;
    }

    public String getIsBillable() {
        return isBillable;
    }

    public void setIsBillable(String isBillable) {
        this.isBillable = isBillable;
    }

    public String getUtteranceRef() {
        return utteranceRef;
    }

    public void setUtteranceRef(String utteranceRef) {
        this.utteranceRef = utteranceRef;
    }

    public String getUserResponse() {
        return userResponse;
    }

    public void setUserResponse(String userResponse) {
        this.userResponse = userResponse;
    }

    public String getSxpResponse() {
        return sxpResponse;
    }

    public void setSxpResponse(String sxpResponse) {
        this.sxpResponse = sxpResponse;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public String getUserMessageType() {
        return userMessageType;
    }

    public void setUserMessageType(String userMessageType) {
        this.userMessageType = userMessageType;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getAbPath() {
        return abPath;
    }

    public void setAbPath(String abPath) {
        this.abPath = abPath;
    }

    public String getCurrentConversationState() {
        return currentConversationState;
    }

    public void setCurrentConversationState(String currentConversationState) {
        this.currentConversationState = currentConversationState;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
