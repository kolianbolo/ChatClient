package ru.bolobanov.chatclient;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.androidannotations.annotations.EBean;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okio.BufferedSink;

/**
 * Created by Bolobanov Nikolay on 25.12.15.
 */
@EBean
public class HttpHelper {
    public final static String POSTFIX = "/ru.bolobanov.chat-1.0-SNAPSHOT/";
    public final static String POSTFIX_LOGIN = "login/";
    public final static String POSTFIX_USERS = "users/";
    public final static String POSTFIX_SET = "receiver/";
    public final static String POSTFIX_GET = "sender/";

    public static final MediaType MEDIA_TYPE_JSON
            = MediaType.parse("application/json; charset=utf-8");

    /**
     * @param pLogin
     * @param pPassword
     * @param baseUrl
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public String login(String pLogin, String pPassword, String baseUrl) throws JSONException, IOException {
        JSONObject rootJSON = new JSONObject();
        rootJSON.put("login", pLogin);
        rootJSON.put("password", pPassword);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(new StringBuilder(baseUrl).append(POSTFIX).append(POSTFIX_LOGIN).toString())
                .post(RequestBody.create(MEDIA_TYPE_JSON, rootJSON.toString()))
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            throw new IOException();
        }
        return response.body().string();

    }

    public String getUsers(String baseUrl) throws JSONException, IOException {
        JSONObject rootJSON = new JSONObject();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(new StringBuilder(baseUrl).append(POSTFIX).append(POSTFIX_USERS).toString())
                .post(RequestBody.create(MEDIA_TYPE_JSON, rootJSON.toString()))
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            throw new IOException();
        }

        return response.body().string();

    }

    public String getMessages(String baseUrl, String pSession) throws JSONException, IOException {
        JSONObject rootJSON = new JSONObject();
        rootJSON.put("session", pSession);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(new StringBuilder(baseUrl).append(POSTFIX).append(POSTFIX_GET).toString())
                .post(RequestBody.create(MEDIA_TYPE_JSON, rootJSON.toString()))
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            throw new IOException();
        }

        return response.body().string();
    }

    public String putMessages(String baseUrl, MessageDAO pMessage, String pSession) throws JSONException, IOException {
        JSONObject rootJSON = new JSONObject();
        rootJSON.put("message", pMessage.mMessage);
        rootJSON.put("session", pSession);
        rootJSON.put("recipient", pMessage.mReceiver);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(new StringBuilder(baseUrl).append(POSTFIX).append(POSTFIX_SET).toString())
                .post(RequestBody.create(MEDIA_TYPE_JSON, rootJSON.toString()))
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            throw new IOException();
        }

        return response.body().string();
    }
}
