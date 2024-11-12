package com.project.erpre.event;

import org.springframework.context.ApplicationEvent;

public class StatusUpdateEvent extends ApplicationEvent {

    private final String status;

    public StatusUpdateEvent(Object source, String status) {
        super(source);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
