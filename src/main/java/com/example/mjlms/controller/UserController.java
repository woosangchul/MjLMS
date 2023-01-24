package com.example.mjlms.controller;

import com.example.mjlms.Service.UserService;
import com.example.mjlms.dto.ClassInfoDTO;
import com.example.mjlms.dto.IdPwdDTO;
import com.example.mjlms.dto.ResultDTO;
import com.example.mjlms.entity.ClassNoticeEntity;
import com.example.mjlms.entity.MjUserEntity;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

@Log4j2
@RestController
@RequestMapping("/user/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/checkLogin")
    public boolean loginCheck(IdPwdDTO dto) throws IOException {
        if (userService.loginCheck(dto.getId(), dto.getPwd())) return true;

        return false;

    }

    @PostMapping("/classInfo")
    public String getUserClassInfo(IdPwdDTO dto) throws IOException {
        if (userService.loginCheck(dto.getId(), dto.getPwd()) == false) return "error";


        if (userService.isUser(dto.getId()) == false) {
            String cookie = userService.getToken(dto.getId(), dto.getPwd());
            userService.saveUserSchedule(dto.getId(), dto.getPwd(), cookie);
            userService.saveClassDetailInfo(dto.getId(), cookie);
        }

        ArrayList<ResultDTO> resultDTO = userService.getClassNoticeCount(dto.getId());

        JSONArray arr = new JSONArray();
        for(ResultDTO result: resultDTO){
            JSONObject obj = new JSONObject();
            obj.put("letureName", result.getClassName());
            obj.put("lectureSchedule", result.getClassTimetable());
            obj.put("noticeCount", result.getNoticeCount());
            obj.put("documentCount", result.getDocumentCount());

            arr.add(obj);
        }

        return arr.toString();


    }

    @GetMapping("/pushTest")
    public void pushTest(String msg) throws IOException, FirebaseMessagingException {
        FileInputStream serviceAccount =
                new FileInputStream("D://server_key.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);

        Notification.Builder builder = Notification.builder();
        Message message = Message.builder()
                .setNotification(builder.build())
                .putData("title", "this title")
                .putData("body", msg)
                .setToken("dfOLj0eARr-KdZU4Cuwez4:APA91bEcij65yz5BunABw9mqIxNEaJGe56Izgb1m8i2KqEEaY5-MgcKSATe9Ivtipbo4M36-MNMWlAGQ4TNebIun7R_YeNhVuP2qQ1YjITERk5mVNbQx34K4AKkatX422wr6Hkoc09OZ")
                .build();
        FirebaseMessaging.getInstance().send(message);
    }

}
