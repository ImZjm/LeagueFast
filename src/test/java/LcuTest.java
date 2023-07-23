import imzjm.league.lcu.websocket.WssConnect;
import imzjm.league.service.DataService;

import java.net.http.WebSocket;
import java.util.Scanner;

public class LcuTest {
    public static void main(String[] args) throws Exception {
        DataService service = new DataService();
        service.freshSummoner();

        WssConnect connect = WssConnect.getConnect();
        WebSocket webSocket = connect.getWebSocket();

        webSocket.sendText("[5,\"OnJsonApiEvent_lol-champ-select_v1_session\"]", true);

        Scanner scanner = new Scanner(System.in);
        scanner.next();

    }
}
