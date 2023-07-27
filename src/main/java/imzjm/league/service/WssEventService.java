package imzjm.league.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import imzjm.league.data.AppFunctionStatus;
import imzjm.league.data.Summoner;
import imzjm.league.lcu.ApiRequest;
import imzjm.league.lcu.websocket.WssConnect;

import java.net.http.WebSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WssEventService {
    Summoner summoner = Summoner.getSummoner();

    AppFunctionStatus appData = AppFunctionStatus.getINSTANCE();

    //订阅监听事件
    public void subscribeEvent(){
        WebSocket webSocket = WssConnect.getConnect().getWebSocket();
        String[] subscribe = {
                //BP环节订阅
                "OnJsonApiEvent_lol-champ-select_v1_session",
                //游戏流程订阅
                "OnJsonApiEvent_lol-gameflow_v1_gameflow-phase",
        };

        for (String s : subscribe) {
            webSocket.sendText("[5,\"" + s + "\"]", true);
        }

    }

    //监听到文本后的处理
    public void WssEventHandler (WebSocket webSocket, CharSequence data, boolean last) {
        if (data.isEmpty())
            return;
        //System.out.println("message: " + data);

        //判断是否为已订阅的事件类型
        Pattern eventPtn = Pattern.compile("(?<=OnJsonApiEvent_)[^\"]*");
        Matcher eventMch = eventPtn.matcher(data);
        if (!eventMch.find())
            return;

        switch (eventMch.group()) {
            case "lol-champ-select_v1_session" -> autoPick(data);
            case "lol-gameflow_v1_gameflow-phase" -> autoAccept(data);
            default -> System.out.println("未知错误!");
        }
    }

    //自动选英雄
    public void autoPick(CharSequence s){

        //获取data节点json数据
        JsonNode dataNode = getDataNode(s.toString());

        //遍历 myTeam 获取自己的cellId
        int cellId = -1;

        JsonNode myTeamNode = dataNode.get("myTeam");
        for (JsonNode oneSummoner : myTeamNode) {
            long summonerId = oneSummoner.get("summonerId").asLong();
            if (summonerId != summoner.getSummonerId())
                continue;

            cellId = oneSummoner.get("cellId").asInt();
        }

        //遍历 actions
        int actionsId = -1;
        String actionType = null;   // pick 或 ban
        JsonNode actionsNode = dataNode.get("actions");

        //bp环节，更新一次动作为一个action
        //遍历 actions 中所有内容
        for (JsonNode oneAction : actionsNode) {
            //每个 action 数组中， 可能有多组数据
            for (JsonNode oneActionData : oneAction) {
                if (oneActionData.get("actorCellId").asInt() != cellId)
                    continue;
                if (!oneActionData.get("isInProgress").asBoolean())
                    break;
                actionsId = oneActionData.get("id").asInt();
                actionType = oneActionData.get("type").asText();
            }
        }

        if (actionsId == -1 || actionType == null)
            return;

        ApiRequest apiRequest = new ApiRequest();

        if (AppFunctionStatus.getINSTANCE().isAutoPick() && actionType.equals("pick")) {
            apiRequest.banOrPickChamp(appData.getAutoPickedChamp(), actionsId);
        }

    }

    //自动接受对局
    public void autoAccept(CharSequence data){
        if (!AppFunctionStatus.getINSTANCE().isAutoAccept())
            return;
        if (!Pattern.compile("\"data\":\"ReadyCheck\"").matcher(data).find())
            return;

        //接受对局
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.autoAccept();
    }

    public JsonNode getDataNode(String jsonStr){
        JsonNode jsonNode = null;
        try {
            jsonNode = new ObjectMapper().readTree(jsonStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return jsonNode==null ? null : jsonNode.get(2).get("data");
    }

}
