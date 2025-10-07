// src/main/java/com/srmist/academia/com.srmist.academia.dto/TimetableDay.java
package com.srmist.academia.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TimetableDay {
    private int dayOrder;
    private List<TimetableEntry> schedule;
}
