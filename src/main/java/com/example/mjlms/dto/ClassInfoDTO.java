package com.example.mjlms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@AllArgsConstructor
@Data
@Builder
public class ClassInfoDTO {
    private String className;
    private String classId;
    private ArrayList<String> classTime;
    private String url;

}
