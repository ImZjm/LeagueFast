package imzjm.league.service;

import imzjm.league.data.AppFunctionStatus;
import imzjm.league.data.Summoner;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.regex.Pattern;

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
    public void changePickedChamp(ItemEvent e, JComboBox<String> comboBox){
        if (e.getStateChange() != ItemEvent.SELECTED)
            return;

        Integer checkedId = summoner.getOwnedChampions().get((String) e.getItem());
        JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();

        //当选择了一个无效值 (eg. 输入框中输入了一个不存在的英雄 并按下回车)
        //自动选择回最近一次选择的英雄，并更新输入框

        if (checkedId == null) {
            String checkingChampName = "";
            for (String champName :
                    summoner.getOwnedChampions().keySet()) {
                if (summoner.getOwnedChampions().get(champName) == appData.getAutoPickedChamp())
                    checkingChampName = champName;
            }
            ArrayList<String> defaultChamps = new ArrayList<>(summoner.getOwnedChampions().keySet());
            comboBox.setModel(new DefaultComboBoxModel<>(defaultChamps.toArray(new String[0])));
            editor.setText(checkingChampName);
        }
        else {
            appData.setAutoPickedChamp(summoner.getOwnedChampions().get((String) e.getItem()));
            //选完英雄后，再次刷新下拉框
            ArrayList<String> defaultChamps = new ArrayList<>(summoner.getOwnedChampions().keySet());
            comboBox.setModel(new DefaultComboBoxModel<>(defaultChamps.toArray(new String[0])));
            editor.setText(e.getItem().toString());
        }

    }

    //切换 自动接受对局 状态
    public void changeAutoAcceptStatus(ItemEvent e){
        appData.setAutoAccept(e.getStateChange() == ItemEvent.SELECTED);
    }

    //下拉选择框 根据输入检索列表内容
    public void comboBoxRetrieve(KeyEvent e, JComboBox<String> comboBox) {
        //获取处在可输入状态下 的 下拉选择框的输入框
        JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();
        //将 "del" 符号删除
        String currentText = editor.getText().replace("\u007F", "");

        //更新下拉框数据
        ArrayList<String> filteredChamps = new ArrayList<>();
        for (String oneChamp :
                summoner.getOwnedChampions().keySet()) {
            Pattern pattern = Pattern.compile(currentText);
            if (!pattern.matcher(oneChamp).find())
                continue;
            filteredChamps.add(oneChamp);
        }
        comboBox.setModel(new DefaultComboBoxModel<>(filteredChamps.toArray(new String[0])));

        //执行setModel方法后，输入框的文本会被自动重置
        //这里手动将输入框的数据更改为用户修改后的
        editor.setText(currentText);
        //并将光标移到最右边
        editor.setCaretPosition(currentText.length());

        //执行setModel后会关闭下拉框，这里手动打开
        comboBox.setPopupVisible(true);
    }

}
