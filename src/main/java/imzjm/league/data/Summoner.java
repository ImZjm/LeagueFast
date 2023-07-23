package imzjm.league.data;

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
}
