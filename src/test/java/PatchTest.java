import imzjm.league.lcu.Connect;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PatchTest {
    public static void main(String[] args) throws Exception {
        HttpClient httpClient = HttpClient.newBuilder()
                .sslContext(Connect.createUnverifiedSslContext())
                .build();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://127.0.0.1:4575/lol-champ-select/v1/session/actions/1"))
                .header("Authorization", "Basic cmlvdDp3SzB0SV95VUpSZFJNTWk0bUR2UHJ3")
                .method("PATCH", HttpRequest.BodyPublishers.ofString("{\"championId\":777,\"completed\":true}"))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int i = response.statusCode();
        System.out.println("响应代码: " + i);

        System.out.println(response.body());
    }
}
