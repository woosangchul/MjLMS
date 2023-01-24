package com.example.mjlms.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Builder
@Data
public class ResultDTO {
    private String className;
    private ArrayList<String> classTimetable;
    private int noticeCount;
    private int documentCount;
}
