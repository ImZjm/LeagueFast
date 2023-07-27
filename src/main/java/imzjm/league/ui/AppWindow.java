package imzjm.league.ui;

import imzjm.league.Main;
import imzjm.league.data.Summoner;
import imzjm.league.service.AppService;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;

public class AppWindow extends JFrame {

    public AppWindow() throws IOException {
        Font font = new Font("微软雅黑", Font.PLAIN, 30);

        AppService appService = new AppService();

        Summoner summonerData = Summoner.getSummoner();

        setTitle("LeagueFast v1.1 by 恨别鸟惊心");
        URL iconUrl = Main.class.getClassLoader().getResource("icon.jpg");
        if (iconUrl != null) {
            setIconImage(ImageIO.read(iconUrl));
        }

        setSize(520, 300);
        setResizable(false);
        setLocationRelativeTo(null);
        setFocusable(true);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocus();
            }
        });
        JFrame thisWindow = this;

        //顶部欢迎语句
        Panel welcome = new Panel(){
            @Override
            public void paint(Graphics g) {
                g.fillRect(0, getHeight()-1, getWidth(), getHeight()-1);
                super.paint(g);
            }
        };
        welcome.setFont(font);
        welcome.add(new Label("welcome!"));

        //左侧显示召唤师信息
        Panel summonerPanel = new Panel(){
            @Override
            public void paint(Graphics g) {
                g.fillRect(getWidth()-1, 0, getWidth()-1, getHeight());
                super.paint(g);
            }
        };

        summonerPanel.setLayout(null);
        summonerPanel.setPreferredSize(new Dimension(151, 0));

        //召唤师头像
        ImageView summonerIcon = new ImageView(summonerData.getIcon());
        summonerIcon.setBounds(25,25, 100, 100);
        summonerPanel.add(summonerIcon);

        //召唤师名
        Panel summonerName = new Panel();
        summonerName.setSize(150, 30);
        summonerName.setLocation(0, 125);
        JLabel summonerNameLabel = new JLabel(summonerData.getName());
        summonerNameLabel.setFont(font.deriveFont(Font.BOLD, 18.0F));
        summonerName.add(summonerNameLabel);
        summonerPanel.add(summonerName);

        //右侧功能区
        FlowLayout work_Layout = new FlowLayout();
        work_Layout.setAlignment(FlowLayout.LEFT);
        work_Layout.setVgap(15);
        work_Layout.setHgap(30);
        JPanel work = new JPanel(work_Layout);

        //自动锁定英雄
        JCheckBox autoPick = new JCheckBox("秒选英雄");
        autoPick.setFont(font.deriveFont(15.0F));
        autoPick.addItemListener(appService::changeAutoPickStatus);
        work.add(autoPick);

        JComboBox<String> pickChamps = new JComboBox<>();
        pickChamps.setPreferredSize(new Dimension(160, 25));
        appService.addChampionItem(pickChamps, "pick");
        pickChamps.setEditable(true);
        JTextField pickEditor = (JTextField) pickChamps.getEditor().getEditorComponent();
        pickEditor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER){
                    thisWindow.requestFocus();
                    return;
                }
                new Thread(() -> appService.comboBoxRetrieve(e, pickChamps, summonerData.getOwnedChampions(), "pick"))
                        .start();
            }
        });

        pickChamps.addItemListener(e -> new Thread(() -> appService.changeSelectedChamp(e, pickChamps, "pick")).start());
        work.add(pickChamps);

        //自动接受对局
        JCheckBox autoAccept = new JCheckBox("自动接受对局");
        autoAccept.setFont(font.deriveFont(15.0F));
        autoAccept.addItemListener(appService::changeAutoAcceptStatus);
        work.add(autoAccept);

        //空白区，先占位置
        Label space = new Label();
        space.setPreferredSize(new Dimension(160, 25));
        space.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocus();
            }
        });
        work.add(space);

        //自动禁英雄
        JCheckBox autoBan = new JCheckBox("自动禁用");
        autoBan.setFont(font.deriveFont(15.0F));
        autoBan.addItemListener(appService::changeAutoBanStatus);
        work.add(autoBan);

        JComboBox<String> banChamps = new JComboBox<>();
        banChamps.setPreferredSize(new Dimension(160, 25));
        appService.addChampionItem(banChamps, "ban");
        banChamps.setEditable(true);
        JTextField banEditor = (JTextField) banChamps.getEditor().getEditorComponent();
        banEditor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER){
                    thisWindow.requestFocus();
                    return;
                }
                new Thread(() -> appService.comboBoxRetrieve(e, banChamps, summonerData.getAllChampions(), "ban"))
                        .start();
            }
        });
        banChamps.addItemListener(e -> new Thread(() -> appService.changeSelectedChamp(e, banChamps, "ban")).start());

        work.add(banChamps);

        add(welcome, BorderLayout.NORTH);
        add(summonerPanel, BorderLayout.WEST);
        add(work, BorderLayout.CENTER);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

}
