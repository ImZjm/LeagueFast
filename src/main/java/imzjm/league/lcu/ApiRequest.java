package imzjm.league.lcu;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiRequest {
    private static final Connect leagueCon =  Connect.getConnect();

    //Api: /lol-matchmaking/v1/ready-check/accept
    //接受对局
    public void autoAccept(){
        String url = leagueCon.getAddress() + "/lol-matchmaking/v1/ready-check/accept";
        request(url, "POST");
    }


    //Api: /lol-champ-select/v1/session/actions/{ActionId}
    //Body: {"championId": {id},"completed": {true/false}}
    //选择 或 禁用英雄, 选择后是否 确认选择或确认禁用
    //选英雄 和 禁英雄 通用方法
    public void banOrPickChamp(int championId, int actionsId) {
        String url = leagueCon.getAddress() + "/lol-champ-select/v1/session/actions/" + actionsId;
        String body = "{" +
                "\"championId\":" + championId + "," +
                "\"completed\":true" +
                "}";
        request(url, "PATCH", body);

    }

    //Api: /lol-champions/v1/owned-champions-minimal
    //获取所有已拥有的英雄(包括周免英雄)
    public InputStream getOwnedChampions() {
        String url = leagueCon.getAddress() + "/lol-champions/v1/owned-champions-minimal";
        return request(url, "GET");
    }

    //Api: /lol-summoner/v1/current-summoner
    //获取当前召唤师的详细信息
    public InputStream getCurrentSummoner() {
        String url = leagueCon.getAddress() + "/lol-summoner/v1/current-summoner";
        return request(url, "GET");
    }

    //Api: /lol-game-data/assets/v1/profile-icons/{HeadId}.jpg
    //获取头像图片输入流
    public InputStream getImg(int HeadId) {
        String url = leagueCon.getAddress() + "/lol-game-data/assets/v1/profile-icons/" + HeadId + ".jpg";
        return request(url, "GET");
    }

     private InputStream request(String apiUrl, String method) {
        return request(apiUrl, method, "");
    }

    private InputStream request(String apiUrl, String method, String requestBody) {
        HttpClient httpClient = HttpClient.newBuilder()
                .sslContext(Connect.createUnverifiedSslContext())
                .build();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", leagueCon.getAuthorization())
                .method(method, HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<InputStream> httpResponse = null;

        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        if (httpResponse == null)
            return null;

        return httpResponse.body();
    }

}
