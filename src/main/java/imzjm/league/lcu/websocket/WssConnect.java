package imzjm.league.lcu.websocket;

import imzjm.league.lcu.Connect;
import imzjm.league.service.WssEventService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class WssConnect {

    private final WebSocket webSocket;

    private static WssConnect connect = new WssConnect();

    private WssConnect() {
        Connect httpsConnect = Connect.getConnect();

        HttpClient client = HttpClient.newBuilder().sslContext(Connect.createUnverifiedSslContext()).build();
        this.webSocket = client.newWebSocketBuilder()
                .header("Authorization", httpsConnect.getAuthorization())
                .buildAsync(URI.create("wss://127.0.0.1:" + httpsConnect.getPort()), new WebSocket.Listener() {
                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {

                        new WssEventService().WssEventHandler(webSocket, data, last);

                        webSocket.request(1);
                        return null;
                    }
                }).join();

    }

    public static WssConnect getConnect() {
        return connect;
    }

    public static WssConnect reConnect() {
        connect = new WssConnect();
        return connect;
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }
}
