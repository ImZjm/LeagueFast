package imzjm.league.service;

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
        if (!AppFunctionStatus.getINSTANCE().isAutoPick())
            return;
        //确认当前为 选英雄环节
        if (!Pattern.compile("\"type\":\"pick\"").matcher(s).find())
            return;
        Pattern pickPtn = Pattern.compile("\"eventType\":\"Create\"");
        if (!pickPtn.matcher(s).find())
            return;
        //已确认 当前状态进入bp环节的初始状态, 开始执行 选择并锁定英雄

        //遍历 召唤师id, 直到找到自己
        Long currentSmeId = summoner.getSummonerId();
        Pattern smeIdPtn = Pattern.compile("(?<=summonerId\":)([^,]*)");
        Matcher smeIdMch = smeIdPtn.matcher(s);

        int tempCount = 0;
        while (smeIdMch.find()){
            if (Long.parseLong(smeIdMch.group()) == currentSmeId){
                break;
            }
            tempCount++;
        }

        //cellId 和 召唤师id 是成对出现的
        //根据自己的召唤师id 找到 自己的cellId
        Pattern cellIdPtn = Pattern.compile("(?<=cellId\":)([^,]*)");
        Matcher cellIdMch = cellIdPtn.matcher(s);
        for (int i = 0; i <= tempCount; i++) {
            if (!cellIdMch.find())
                return;
        }
        //这时候得到的 cellId 就是自己的
        int currentCellId = Integer.parseInt(cellIdMch.group());

        //actorCellId == cellId
        //actorCellId 和 id(即actionsId) 也是成对出现的
        //同理 先找到自己的 actorCellId
        Matcher actorCellIdMch = Pattern.compile("(?<=actorCellId\":)([^,]*)").matcher(s);
        tempCount = 0;
        while (actorCellIdMch.find()){
            if (Integer.parseInt(actorCellIdMch.group()) == currentCellId)
                break;
            tempCount++;
        }

        //同理，根据出现顺序，匹配自己的actionsId
        Matcher actionsIdMch = Pattern.compile("(?<=\"id\":)([^,]*)").matcher(s);
        for (int i = 0; i <= tempCount; i++) {
            if (!actionsIdMch.find())
                return;
        }

        //拿到 actionsId 后, 就拿到了 选择并锁定英雄api 的第一个参数
        int currentActionsId = Integer.parseInt(actionsIdMch.group());

        //被选择的 英雄id
        int championId = appData.getAutoPickedChamp();
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.banOrPickChamp(championId, currentActionsId);

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

}
