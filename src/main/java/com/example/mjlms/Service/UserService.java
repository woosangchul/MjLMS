package com.example.mjlms.Service;

import com.example.mjlms.dto.ClassInfoDTO;
import com.example.mjlms.dto.ResultDTO;
import com.example.mjlms.entity.ClassNoticeEntity;
import com.example.mjlms.entity.MjUserEntity;

import java.io.IOException;
import java.util.ArrayList;

public interface UserService {




    boolean loginCheck(String id, String pwd) throws IOException;

    boolean isUser(String id);



    String getToken(String id, String pwd) throws IOException;

    MjUserEntity getUserScheduleAtMongoDB(String studentNumber) throws IOException;

    void saveUserSchedule(String id, String pwd, String s_cookie) throws IOException;

    void saveClassDetailInfo(String id, String cookie) throws IOException;

    ArrayList<ResultDTO> getClassNoticeCount(String id) throws IOException;

}
