package imzjm.league.data;

import imzjm.league.lcu.Connect;
import imzjm.league.service.DataService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Summoner {
    private Image icon;

    private String name;

    private Long summonerId;

    private Map<String, Integer> ownedChampions;

    private Map<String, Integer> allChampions;

    private static final Summoner summoner = new Summoner();

    public static Summoner getSummoner(){
        return summoner;
    }

    private Summoner(){
        URL iconUrl = getClass().getClassLoader().getResource("icon.jpg");
        try {
            if (iconUrl != null)
                icon = ImageIO.read(iconUrl);
            else
                throw new FileNotFoundException();
        } catch (Exception e) {
            e.printStackTrace();
        }

        name = "Not Found";

        ownedChampions = new HashMap<>();
        ownedChampions.put("无", -1);

        allChampions = new HashMap<>();
        allChampions.put("无", -1);

    }

    public void setData() {
        if (Connect.getConnect().getStatus()) {
            DataService dataService = new DataService();
            dataService.freshSummoner();
            dataService.getOwnedChampions();
            dataService.getAllChampions();
        }
    }

    public Image getIcon() {
        return icon;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSummonerId() {
        return summonerId;
    }

    public void setSummonerId(Long summonerId) {
        this.summonerId = summonerId;
    }

    public Map<String, Integer> getOwnedChampions() {
        return ownedChampions;
    }

    public void setOwnedChampions(Map<String, Integer> ownedChampions) {
        this.ownedChampions = ownedChampions;
    }

    public Map<String, Integer> getAllChampions() {
        return allChampions;
    }

    public void setAllChampions(Map<String, Integer> allChampions) {
        this.allChampions = allChampions;
    }
}
