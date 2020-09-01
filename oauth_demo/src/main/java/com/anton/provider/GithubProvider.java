package com.anton.provider;

import com.alibaba.fastjson.JSON;
import com.anton.dto.AccessTokenDTO;
import com.anton.dto.GithubUser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class GithubProvider {

    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .addHeader("Accept","application/json")
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            log.info("获得响应： " + string);
            String token = string.split(",")[0].split(":")[1].replace('"', ' ').trim();
            log.info("看看token： " + token);
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getAccessToken error,{}", accessTokenDTO, e);
        }

        return null;
    }

    public GithubUser getUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token=" + accessToken)
                .addHeader("Accept","application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (IOException e) {
        } catch (Exception e) {
            log.error("getUser error,{}", accessToken, e);
        }

        return null;
    }

}
