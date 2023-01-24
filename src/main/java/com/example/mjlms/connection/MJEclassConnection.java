package com.example.mjlms.connection;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MJEclassConnection {
    private HttpURLConnection con;
    private String cookie;
    private String id;
    private String pwd;

    public MJEclassConnection(URL url,String id, String pwd) throws IOException {
        con = (HttpURLConnection) url.openConnection();
        this.id = id;
        this.pwd = pwd;

    }

    public String getToken(){
        if (cookie != null) return cookie;



        return "d";


    }

    public void resetCookie(){
        this.cookie = null;
    }

    public boolean sessionAvailableCheck(){
        if (this.cookie == null) return false;
        String url = "https://home.mju.ac.kr/mainIndex/myHomeworkList.action?command=&tab=homework";

        return false;
    }

    public void getHtmlDocument(String url, Map<String, String> header) throws IOException{
        CloseableHttpClient httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
        HttpGet httpGet = new HttpGet(url);




    }
}
