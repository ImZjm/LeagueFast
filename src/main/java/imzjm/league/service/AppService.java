package imzjm.league.service;

import imzjm.league.data.AppFunctionStatus;
import imzjm.league.data.Summoner;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class AppService {
    Summoner summoner = Summoner.getSummoner();
    AppFunctionStatus appData = AppFunctionStatus.getINSTANCE();

    //根据 获取到的 当前召唤师可用的 英雄 动态填充菜单
    //并 初始化 被选英雄id
    public void addChampionItem(JComboBox<String> comboBox){
        summoner.getOwnedChampions().forEach((champion, id) -> comboBox.addItem(champion));

        appData.setAutoPickedChamp(summoner.getOwnedChampions().get((String) comboBox.getSelectedItem()));
    }

    //切换 秒选英雄选项 状态
    public void changeAutoPickStatus(ItemEvent e){
        appData.setAutoPick(e.getStateChange() == ItemEvent.SELECTED);
    }

    //当用户在列表中选择其他英雄时，更改 app 内的 被选英雄 的 id
    public void changePickedChamp(ItemEvent e){
        if (e.getStateChange() != ItemEvent.SELECTED)
            return;
        appData.setAutoPickedChamp(summoner.getOwnedChampions().get((String) e.getItem()));
    }

    //切换 自动接受对局 状态
    public void changeAutoAcceptStatus(ItemEvent e){
        appData.setAutoAccept(e.getStateChange() == ItemEvent.SELECTED);
    }

}
