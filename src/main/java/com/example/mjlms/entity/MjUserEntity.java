package com.example.mjlms.entity;

import com.example.mjlms.dto.ClassInfoDTO;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

@Data
@Builder
@Document(collection = "mfUserInfo")
public class MjUserEntity {
    @Id
    private String id;
    private String userId;
    private String userPwd;
    private ArrayList<ClassInfoDTO> classInfo;


}
