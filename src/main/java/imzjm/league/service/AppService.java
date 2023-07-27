package imzjm.league.service;

import imzjm.league.data.AppFunctionStatus;
import imzjm.league.data.Summoner;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

public class AppService {
    Summoner summoner = Summoner.getSummoner();
    AppFunctionStatus appData = AppFunctionStatus.getINSTANCE();

    //根据 BP 类型填充combobox列表
    //选英雄列表填充已有英雄
    //禁用列表填充所有英雄
    public void addChampionItem(JComboBox<String> comboBox, String type){
        if (type.equals("pick")) {
            summoner.getOwnedChampions().forEach((champion, id) -> comboBox.addItem(champion));

            appData.setAutoPickedChamp(summoner.getOwnedChampions().get((String) comboBox.getSelectedItem()));
        }

        else if (type.equals("ban")) {
            summoner.getAllChampions().forEach((champion, id) -> comboBox.addItem(champion));

            appData.setAutoBanChamp(summoner.getAllChampions().get((String) comboBox.getSelectedItem()));
        }
    }

    //切换 秒选英雄选项 状态
    public void changeAutoPickStatus(ItemEvent e){
        appData.setAutoPick(e.getStateChange() == ItemEvent.SELECTED);
    }

    //当用户在列表中选择其他英雄时，更改 app 内的 被选英雄 的 id
    public void changeSelectedChamp(ItemEvent e, JComboBox<String> comboBox, String type){
        if (e.getStateChange() != ItemEvent.SELECTED)
            return;

        Integer champId = null;
        Map<String, Integer> champPool = null;
        int getAutoChamp = -1;

        if (type.equals("pick")) {
            champPool = summoner.getOwnedChampions();
            champId = summoner.getOwnedChampions().get((String) e.getItem());
            getAutoChamp = appData.getAutoPickedChamp();
        }
        else if (type.equals("ban")) {
            champPool = summoner.getAllChampions();
            champId = summoner.getAllChampions().get((String) e.getItem());
            getAutoChamp = appData.getAutoBanChamp();
        }
        JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();

        //当选择了一个无效值 (eg. 输入框中输入了一个不存在的英雄 并按下回车)
        //自动选择回最近一次选择的英雄，并更新输入框

        if (champId == null) {
            String checkingChampName = "";
            if (champPool != null) {
                for (String champName :
                        champPool.keySet()) {
                    if (champPool.get(champName) == getAutoChamp)
                        checkingChampName = champName;
                }
            }
            ArrayList<String> defaultChamps;
            if (champPool != null) {
                defaultChamps = new ArrayList<>(champPool.keySet());
                comboBox.setModel(new DefaultComboBoxModel<>(defaultChamps.toArray(new String[0])));
            }
            editor.setText(checkingChampName);
        }
        else {
            if (type.equals("pick"))
                appData.setAutoPickedChamp(champPool.get((String) e.getItem()));
            else
                appData.setAutoBanChamp(champPool.get((String) e.getItem()));
            //选完英雄后，再次刷新下拉框
            ArrayList<String> defaultChamps = new ArrayList<>(champPool.keySet());
            comboBox.setModel(new DefaultComboBoxModel<>(defaultChamps.toArray(new String[0])));
            editor.setText(e.getItem().toString());
        }

    }

    //切换 自动接受对局 状态
    public void changeAutoAcceptStatus(ItemEvent e){
        appData.setAutoAccept(e.getStateChange() == ItemEvent.SELECTED);
    }

    //切换 自动禁英雄 状态
    public void changeAutoBanStatus(ItemEvent e){
        appData.setAutoBan(e.getStateChange() == ItemEvent.SELECTED);
    }

    //下拉选择框 根据输入检索列表内容
    public void comboBoxRetrieve(KeyEvent e, JComboBox<String> comboBox, Map<String, Integer> pool, String type) {
        //获取处在可输入状态下 的 下拉选择框的输入框
        JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();
        //将 "del" 符号删除
        String currentText = editor.getText().replace("\u007F", "");

        //更新下拉框数据
        ArrayList<String> filteredChamps = new ArrayList<>();
        for (String oneChamp : pool.keySet()) {
            Pattern pattern = Pattern.compile(currentText);
            if (!pattern.matcher(oneChamp).find())
                continue;
            filteredChamps.add(oneChamp);
        }
        comboBox.setModel(new DefaultComboBoxModel<>(filteredChamps.toArray(new String[0])));
        if (filteredChamps.size() == 1) {
            if (type.equals("pick"))
                appData.setAutoPickedChamp(pool.get(filteredChamps.get(0)));
            else if (type.equals("ban"))
                appData.setAutoBanChamp(pool.get(filteredChamps.get(0)));
        }
        //执行setModel方法后，输入框的文本会被自动重置
        //这里手动将输入框的数据更改为用户修改后的
        editor.setText(currentText);
        //并将光标移到最右边
        editor.setCaretPosition(currentText.length());

        //执行setModel后会关闭下拉框，这里手动打开
        comboBox.setPopupVisible(true);
    }

}
