package com.example.mjlms.Service;

import com.example.mjlms.dto.ClassDocumentDTO;
import com.example.mjlms.dto.ClassInfoDTO;
import com.example.mjlms.dto.ResultDTO;
import com.example.mjlms.entity.ClassNoticeEntity;
import com.example.mjlms.entity.MjUserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService{

    @Autowired
    MongoTemplate mongoTemplate;


    String jSessionParser(String jSessionId){
        StringTokenizer st = new StringTokenizer(jSessionId.substring(11), ";");

        return st.nextToken();

    }
    Map<String, String> tokenParser(String access_token, String refresh_token){
        Map<String, String> map = new HashMap<>();
        StringTokenizer st = null;



        st = new StringTokenizer(access_token.substring(13),";");
        map.put("access_token", st.nextToken());

        st = new StringTokenizer(refresh_token.substring(14),";");
        map.put("refresh_token", st.nextToken());

        return map;
    }
    String makeCookieHeader(String jsession, String access_token, String refresh_token){
        return "JSESSIONID="+jsession+"; access_token="+access_token+"; refresh_token="+refresh_token;
    }
    @Override
    public boolean loginCheck(String id, String pwd) throws IOException {
        String url = "https://sso1.mju.ac.kr/mju/userCheck.do";


        HttpClient httpClient2 = HttpClientBuilder.create().disableRedirectHandling().build();
        String request_url = "https://sso1.mju.ac.kr/mju/userCheck.do";
        HttpPost httpPost = new HttpPost(request_url);
        httpPost.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");


        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("passwrd", pwd));
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse httpResponse = httpClient2.execute(httpPost);
        System.out.println("asdf");

        BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(),"UTF-8"));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while((inputLine = reader.readLine()) != null){
            response.append(inputLine);
        }
        reader.close();

        try{
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (org.json.simple.JSONObject) parser.parse(response.toString());

            if ( jsonObject.get("error").toString().equals("VL-3130") || jsonObject.get("error").toString().equals("0000") ){
                return true;
            }
        }catch (Exception e){

        }



        return false;


    }

    @Override
    public boolean isUser(String id){
        MjUserEntity entity = mongoTemplate.findOne(query(where("userId").is(id)), MjUserEntity.class);

        if (entity == null) return false;

        return true;

    }


    @Override
    public String getToken(String id, String pwd) throws IOException {

        /*
        apach HTTPClient를 사용해서 토큰 가져오는 로직
        기존  HTTPURLCONNECTION을 사용하면 Origin헤더를 추가해야하는데 보안상 추가가안돼서 api를 못가져오는 문제발생
         */
        String request_url = "https://home.mju.ac.kr/ssoChk.jsp";

        HttpClient httpClient1 = HttpClientBuilder.create().disableRedirectHandling().build();
        HttpGet httpGet = new HttpGet(request_url);
        httpGet.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        httpGet.addHeader("Host","home.mju.ac.kr");
        httpGet.addHeader("Referer","https://home.mju.ac.kr/user/index.action");
        httpGet.addHeader("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        HttpResponse httpResponse = httpClient1.execute(httpGet);
        String mainJsessionId = jSessionParser(httpResponse.getHeaders("Set-Cookie")[0].getValue());
        System.out.println("asdf");

        request_url = "https://sso1.mju.ac.kr/login.do?redirect_uri=https://home.mju.ac.kr/user/index.action";
        httpGet = new HttpGet(request_url);
        httpGet.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        httpGet.addHeader("Host","sso1.mju.ac.kr");
        httpGet.addHeader("Referer","https://home.mju.ac.kr/");
        httpGet.addHeader("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        httpResponse = httpClient1.execute(httpGet);
        String loginJsessionId = jSessionParser(httpResponse.getHeaders("Set-Cookie")[0].getValue());






        HttpClient httpClient2 = HttpClientBuilder.create().disableRedirectHandling().build();
        request_url = "https://sso1.mju.ac.kr/mju/userCheck.do";
        HttpPost httpPost = new HttpPost(request_url);
        httpPost.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        httpPost.addHeader("Host","sso1.mju.ac.kr");
        httpPost.addHeader("Origin","https://sso1.mju.ac.kr");
        httpPost.addHeader("Referer","https://sso1.mju.ac.kr/login.do?redirect_uri=https://home.mju.ac.kr/user/index.action");
        httpPost.addHeader("Cookie","JSESSIONID="+loginJsessionId);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.addHeader("sec-ch-ua","\"Chromium\";v=\"106\", \"Google Chrome\";v=\"106\", \"Not;A=Brand\";v=\"99\"");
        httpPost.addHeader("sec-ch-ua-mobile","?0");
        httpPost.addHeader("Sec-Fetch-Dest","empty");
        httpPost.addHeader("Sec-Fetch-Mode","cors");
        httpPost.addHeader("Sec-Fetch-Site","same-origin");
        httpPost.addHeader("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        httpPost.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");


        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("passwrd", pwd));
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        httpResponse = httpClient2.execute(httpPost);




        httpClient2 = HttpClientBuilder.create().disableRedirectHandling().build();
        request_url = "https://sso1.mju.ac.kr/login/ajaxActionLogin2.do";
        httpPost = new HttpPost(request_url);
        httpPost.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        httpPost.addHeader("Host","sso1.mju.ac.kr");
        httpPost.addHeader("Origin","https://sso1.mju.ac.kr");
        httpPost.addHeader("Referer","https://sso1.mju.ac.kr/login.do?redirect_uri=https://home.mju.ac.kr/user/index.action");
        httpPost.addHeader("Cookie","JSESSIONID="+loginJsessionId);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.addHeader("sec-ch-ua","\"Chromium\";v=\"106\", \"Google Chrome\";v=\"106\", \"Not;A=Brand\";v=\"99\"");
        httpPost.addHeader("Sec-Fetch-Dest","document");
        httpPost.addHeader("Sec-Fetch-Mode","navigate");
        httpPost.addHeader("Sec-Fetch-Site","same-origin");
        httpPost.addHeader("Sec-Fetch-User","?1");
        httpPost.addHeader("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        httpPost.addHeader("Upgrade-Insecure-Requests", "1");
        httpPost.addHeader("Sec-ch-ua-platform", "Windows");

        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", id));
        params.add(new BasicNameValuePair("user_pwd", pwd));
        params.add(new BasicNameValuePair("redirect_uri", "https://home.mju.ac.kr/user/index.action"));
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        httpResponse = httpClient2.execute(httpPost);








        request_url = "https://sso1.mju.ac.kr/oauth2/token2.do";
        httpPost = new HttpPost(request_url);
        httpPost.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        httpPost.addHeader("Host","sso1.mju.ac.kr");
        httpPost.addHeader("Origin","https://sso1.mju.ac.kr");
        httpPost.addHeader("Referer","https://sso1.mju.ac.kr/login.do?redirect_uri=https://home.mju.ac.kr/user/index.action");
        httpPost.addHeader("Cookie","JSESSIONID="+loginJsessionId);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.addHeader("sec-ch-ua","\"Chromium\";v=\"106\", \"Google Chrome\";v=\"106\", \"Not;A=Brand\";v=\"99\"");
        httpPost.addHeader("Sec-Fetch-Dest","document");
        httpPost.addHeader("Sec-Fetch-Mode","navigate");
        httpPost.addHeader("Sec-Fetch-Site","same-origin");
        httpPost.addHeader("Sec-Fetch-User","?1");
        httpPost.addHeader("Upgrade-Insecure-Requests", "1");
        httpPost.addHeader("Sec-ch-ua-platform", "Windows");
        httpPost.addHeader("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");


        httpPost.setEntity(new UrlEncodedFormEntity(params));
        httpResponse = httpClient2.execute(httpPost);
        Map<String, String> map = tokenParser(httpResponse.getHeaders("Set-Cookie")[0].getValue(), httpResponse.getHeaders("Set-Cookie")[1].getValue());
        String s_cookie = makeCookieHeader(mainJsessionId, map.get("access_token"), map.get("refresh_token"));

                /*
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while((inputLine = reader.readLine()) != null){
            response.append(inputLine);
        }
        reader.close();

                 */



        request_url = "https://home.mju.ac.kr/user/index.action";

        httpGet = new HttpGet(request_url);
        httpGet.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        httpGet.addHeader("Host","home.mju.ac.kr");
        httpGet.addHeader("Referer","https://sso1.mju.ac.kr/");
        httpGet.addHeader("Cookie", s_cookie);
        httpResponse = httpClient2.execute(httpGet);




        request_url = "https://home.mju.ac.kr/mainIndex/myHomeworkList.action?command=&tab=homework";

        httpGet = new HttpGet(request_url);
        httpGet.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        httpGet.addHeader("Host","home.mju.ac.kr");
        httpGet.addHeader("Referer","https://home.mju.ac.kr/user/index.action");
        httpGet.addHeader("Cookie", s_cookie);
        httpResponse = httpClient2.execute(httpGet);

        System.out.println("asdf");


        /*
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(),"UTF-8"));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while((inputLine = reader.readLine()) != null){
            response.append(inputLine);
        }
        reader.close();
*/
        System.out.println(s_cookie);
        return s_cookie;

    }

    @Override
    public void saveUserSchedule(String id, String pwd, String s_cookie) throws IOException {
        String htmlDocument = null;
        try {
            String request_url = "https://home.mju.ac.kr/course/courseList.action?command=main&tab=course";
            HttpClient httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
            HttpGet httpGet = new HttpGet(request_url);
            httpGet.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
            httpGet.addHeader("Host","home.mju.ac.kr");
            httpGet.addHeader("Referer","https://home.mju.ac.kr/user/index.action");
            httpGet.addHeader("Cookie", s_cookie);
            HttpResponse httpResponse = httpClient.execute(httpGet);

            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(),"UTF-8"));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while((inputLine = reader.readLine()) != null){
                response.append(inputLine);
            }
            reader.close();

            htmlDocument = response.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Document doc = Jsoup.parse(htmlDocument);
        System.out.println("final");
        Map<String, ClassInfoDTO> map = new HashMap<>();
        for (Element elem: doc.select(".list tbody").select("tr")){
            String key = elem.childNode(3).childNode(0).toString();
            if (map.containsKey(key)){
                map.get(key).getClassTime().add(elem.childNode(5).childNode(0).toString().replace("&nbsp;", " "));

            }else{
                String url = elem.childNode(7).childNode(1).childNode(0).attr("href").toString();
                map.put(key, ClassInfoDTO.builder()
                        .className(key)
                        .classTime(new ArrayList<String>())
                        .url("https://home.mju.ac.kr"+url.toString())
                        .classId(url.substring(url.indexOf("=")+1))
                        .build());
                map.get(key).getClassTime().add(elem.childNode(5).childNode(0).toString().replace("&nbsp;", " "));

            }

        }


        ArrayList<ClassInfoDTO> arrayList = new ArrayList<>();
        for (String key: map.keySet()){
            arrayList.add(map.get(key));
        }

        MjUserEntity entity = MjUserEntity.builder()
                .userId(id)
                .userPwd(pwd)
                .classInfo(arrayList)
                .build();

        //if ( mongoTemplate.findOne(query(where("userId").is(id)), MjUserEntity.class)
        mongoTemplate.insert(entity);





    }

    @Override
    public void saveClassDetailInfo(String id, String cookie) throws IOException {

        System.out.println("cookie");
        MjUserEntity entity = mongoTemplate.findOne(query(where("userId").is(id)), MjUserEntity.class);

        HttpClient httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
        for (ClassInfoDTO dto : entity.getClassInfo()){

            HttpGet httpGet = new HttpGet(dto.getUrl());
            httpGet.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
            httpGet.addHeader("Host","home.mju.ac.kr");
            httpGet.addHeader("Cookie", cookie);
            HttpResponse httpResponse = httpClient.execute(httpGet);

            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(),"UTF-8"));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while((inputLine = reader.readLine()) != null){
                response.append(inputLine);
            }
            reader.close();

            Document doc = Jsoup.parse(response.toString());


            ClassNoticeEntity classEntity = ClassNoticeEntity.builder()
                    .classId(dto.getClassId())
                    .className(dto.getClassName())
                    .noticeCount(doc.select("#MainContent > div > ul").get(0).childNodeSize())
                    .documentCount(doc.select("#MainContent > div > ul").get(1).childNodeSize())
                    .build();

            if (mongoTemplate.findOne(query(where("classId").is(dto.getClassId())), MjUserEntity.class) == null){
                mongoTemplate.insert(classEntity);
            }



        }




    }

    @Override
    public MjUserEntity getUserScheduleAtMongoDB(String studentNumber) throws IOException {
        MjUserEntity entity = mongoTemplate.findOne(query(where("userId").is(studentNumber)), MjUserEntity.class);
        return entity;

    }

    @Override
    public ArrayList<ResultDTO> getClassNoticeCount(String id) throws IOException {
        MjUserEntity entity = this.getUserScheduleAtMongoDB(id);
        ArrayList<ResultDTO> resultDTO = new ArrayList<ResultDTO>();

        for (ClassInfoDTO dto: entity.getClassInfo()){
            ClassNoticeEntity classInfo = mongoTemplate.findOne(query(where("classId").is(dto.getClassId())), ClassNoticeEntity.class);
            ResultDTO temp = ResultDTO.builder()
                    .className(dto.getClassName())
                    .classTimetable(dto.getClassTime())
                    .noticeCount(classInfo.getNoticeCount())
                    .documentCount(classInfo.getDocumentCount())
                    .build();

            resultDTO.add(temp);
        }



        return resultDTO;




    }
}
