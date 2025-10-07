// src/main/java/com/srmist/academia/com.srmist.academia.dto/CalendarEvent.java
package com.srmist.academia.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalendarEvent {
    private String date; // yyyy-MM-dd
    private String day;
    private String event;
    private int doCount;
}
