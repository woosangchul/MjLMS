package com.example.mjlms.connection;

import java.io.IOException;
import java.net.URL;

public class CreateConnection {
    public static MJEclassConnection open(String url,String id, String pwd) throws IOException{
        return new MJEclassConnection(new URL(url), id, pwd);
    }
}
