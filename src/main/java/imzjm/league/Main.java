package imzjm.league;

import com.formdev.flatlaf.FlatLightLaf;
import imzjm.league.lcu.Connect;
import imzjm.league.service.DataService;
import imzjm.league.service.WssEventService;
import imzjm.league.ui.AppWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception {
        //全局主题
        FlatLightLaf.setup();

        Connect connect = Connect.getConnect();
        if (!connect.getStatus()) {
            System.out.println("请先启动 英雄联盟客户端! ");
            AppWindow app = new AppWindow();
            JOptionPane.showMessageDialog(app, "未检测到英雄联盟客户端. . . ", "连接失败", JOptionPane.WARNING_MESSAGE);

            app.setVisible(true);

        }
        else {
            //初始化数据
            DataService dataService = new DataService();
            dataService.freshSummoner();
            dataService.getOwnedChampions();

            //创建 WSS 连接
            WssEventService wssEventService = new WssEventService();
            //订阅 事件
            wssEventService.subscribeEvent();

            //基本 APP 配置
            AppWindow app = new AppWindow();


            //启动
            app.setVisible(true);


            //打印连接信息
            showConnectInfo();
        }
    }

    static void showConnectInfo(){
        System.out.println("客户端连接成功! ");
        Connect connect = Connect.getConnect();
        System.out.println("Port: " + connect.getPort());
        System.out.println("Token: " + connect.getToken());
    }
}
