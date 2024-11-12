package com.project.erpre.event;

import org.springframework.context.ApplicationEvent;

public class StatusMessageUpdateEvent extends ApplicationEvent {

    private final String statusMessage;

    public StatusMessageUpdateEvent(Object source, String statusMessage) {
        super(source);
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
