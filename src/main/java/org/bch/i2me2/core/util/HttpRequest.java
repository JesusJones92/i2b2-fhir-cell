package org.bch.i2me2.core.util;

import org.bch.i2me2.core.config.AppConfig;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Abstract functionality for REST calls
 * Created by CH176656 on 3/20/2015.
 */
public class HttpRequest {

    public Response doPostGeneric(String urlStr,  String headerAuth) throws IOException {
        return doPostGeneric(urlStr, null, headerAuth, null);
    }

    public Response doPostGeneric(String urlStr, String content, String headerAuth,
                                  String headerContentType) throws IOException {

        OutputStream out;
        HttpURLConnection con;

        URL url = new URL(urlStr);
        con = (HttpURLConnection) url.openConnection();

        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestMethod("POST");
        if (content!=null) {
            con.setRequestProperty("Content-length", String.valueOf(content.length()));
        }
        if (headerContentType != null) {
            con.setRequestProperty("Content-type: ", headerContentType);
        }
        if (headerAuth!=null) {
            con.setRequestProperty("Authorization", headerAuth);
        }
        if (content!=null) {
            out = con.getOutputStream();
            out.write(content.getBytes());
            out.flush();
            out.close();
        }
        Response resp = new ResponseJava(con);
        con.disconnect();
        return resp;
    }

    public static String urlParam(String key, String value) {
        return key + "=" + value;
    }

    public static class ResponseJava implements Response {
        private HttpURLConnection con;
        private String content;
        private int status;

        ResponseJava(HttpURLConnection con) throws IOException {
            OutputStream out;
            BufferedReader in;
            String response = "";
            this.con = con;
            this.status = con.getResponseCode();
            if (this.status > 203) {
                this.content=null;
                return;
            }

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            char[] buffer = new char[AppConfig.HTTP_TRANSPORT_BUFFER_SIZE + 1];
            while (true) {
                int numCharRead = in.read(buffer, 0, AppConfig.HTTP_TRANSPORT_BUFFER_SIZE);
                if (numCharRead == -1) {
                    break;
                }
                String line = new String(buffer, 0, numCharRead);
                response += line;
            }
            in.close();
            this.content = response;
        }

        @Override public int getResponseCode() {
            return this.status;
        }

        @Override public String getContent() {
            return this.content;
        }
    }

/*
    public static class ResponseApache implements Response {
        private HttpResponse httpResponse;
        String content;

        ResponseApache(HttpResponse httpResponse) throws IOException {
            this.httpResponse = httpResponse;
            if (this.httpResponse.getEntity()==null) {
                this.content=null;
            } else {
                StringWriter buffer = new StringWriter();
                IOUtils.copy(this.httpResponse.getEntity().getContent(), buffer, "UTF-8");
                this.content = buffer.toString();
            }
        }

        @Override
        public int getResponseCode() {
            return this.httpResponse.getStatusLine().getStatusCode();
        }

        @Override
        public String getContent() {
            return this.content;
        }

    }
*/

}

