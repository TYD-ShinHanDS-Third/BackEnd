package com.shinhan.education.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Urlconnection {
    public static void run() throws IOException {
        String testurl = "https://apis.data.go.kr/B552555/lhLeaseNoticeInfo1/lhLeaseNoticeInfo1?serviceKey=7KhE1xpaLNaHgKzHB0z7B6a1K4n4IgOGFCOpN6dFixZMHI1d0CyE2TCISvehO7bkNEOt2MArA3SxrU3JPf%2BOjw%3D%3D&UPP_AIS_TP_CD=06&PG_SZ=10&PAGE=1";

        URL url = new URL(testurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        int resCode = conn.getResponseCode();

        System.out.println(resCode);
        System.out.println(conn.getResponseMessage());
        System.out.println(conn.getContent().toString());

        Object result = conn.getContent();
        int ch;
        while((ch=((InputStream)result).read()) != -1) {
            System.out.print((char) ch);
        }
    }
}