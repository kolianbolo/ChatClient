package ru.bolobanov.chat_client;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ru.bolobanov.chat_client.db.mapping.Message;

/**
 * Created by Bolobanov Nikolay on 25.12.15.
 */
public class HttpHelper {
    private final static String POSTFIX = "/ru.bolobanov.chat-1.0-SNAPSHOT/rest/";
    private final static String POSTFIX_LOGIN = "login/";
    private final static String POSTFIX_USERS = "users/";
    private final static String POSTFIX_SET = "receiver/";
    private final static String POSTFIX_GET = "sender/";

    private static final MediaType MEDIA_TYPE_JSON
            = MediaType.parse("application/json; charset=utf-8");

    /**
     * @param pLogin    - логин
     * @param pPassword - пароль
     * @param baseUrl   - адрес сервера
     * @return - ответ от сервера
     * @throws JSONException
     * @throws IOException
     */
    public String login(String pLogin, String pPassword, String baseUrl) throws JSONException, IOException {
        JSONObject rootJSON = new JSONObject();
        rootJSON.put("login", pLogin);
        rootJSON.put("password", pPassword);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(baseUrl + POSTFIX + POSTFIX_LOGIN)
                .post(RequestBody.create(MEDIA_TYPE_JSON, rootJSON.toString()))
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            throw new IOException();
        }
        return response.body().string();

    }

    public String getUsers(String baseUrl) throws IOException {
        JSONObject rootJSON = new JSONObject();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(baseUrl + POSTFIX + POSTFIX_USERS)
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
                .url(baseUrl + POSTFIX + POSTFIX_GET)
                .post(RequestBody.create(MEDIA_TYPE_JSON, rootJSON.toString()))
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            throw new IOException();
        }

        return response.body().string();
    }

    public String putMessages(String baseUrl, Message pMessage, String pSession) throws JSONException, IOException {
        JSONObject rootJSON = new JSONObject();
        rootJSON.put("message", pMessage.getMessage());
        rootJSON.put("session", pSession);
        rootJSON.put("recipient", pMessage.getReceiver());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(baseUrl + POSTFIX + POSTFIX_SET)
                .post(RequestBody.create(MEDIA_TYPE_JSON, rootJSON.toString()))
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            throw new IOException();
        }

        return response.body().string();
    }
}
