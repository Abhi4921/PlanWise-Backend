// src/main/java/com/srmist/academia/com.srmist.academia.dto/StudentDetails.java
package com.srmist.academia.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudentDetails {
    private String regNumber;
    private String name;
    private int batch;
    private String mobile;
    private String department;
    private int semester;
    private List<Course> courses;
}
