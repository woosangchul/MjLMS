package com.example.mjlms.entity;

import com.example.mjlms.dto.ClassDocumentDTO;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Data
@Builder
@Document (collection = "mjClassInfo")
public class ClassNoticeEntity {
    @Id
    private String id;
    private String classId;
    private String className;
    private int noticeCount;
    private int documentCount;


}
