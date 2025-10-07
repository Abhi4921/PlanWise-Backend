// src/main/java/com/srmist/academia/com.srmist.academia.dto/TimetableEntry.java
package com.srmist.academia.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimetableEntry {
    private String slot;
    private String startTime;
    private String endTime;
    // minimal course info for the slot
    private String courseCode;
    private String courseType;
    private String title;
    private String faculty;
    private String room;
}
