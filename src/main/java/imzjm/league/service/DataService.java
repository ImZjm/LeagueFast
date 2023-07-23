package imzjm.league.service;

import imzjm.league.data.Summoner;
import imzjm.league.lcu.ApiRequest;

import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataService {
    private static final Summoner summoner = Summoner.getSummoner();

    //生成秒选英雄列表
    public void getOwnedChampions() {
        ApiRequest apiRequest = new ApiRequest();
        InputStream ownedChampions = apiRequest.getOwnedChampions();
        String s = convertStr(ownedChampions);

        Map<String, Integer> map = new HashMap<>();

        //正则表达式匹配
        Pattern namePattern = Pattern.compile("(?<=name\":\")([^\"]*)");
        Matcher nameMatcher = namePattern.matcher(s);

        Pattern titlePattern = Pattern.compile("(?<=title\":\")([^\"]*)");
        Matcher titleMatcher = titlePattern.matcher(s);

        Pattern idPattern = Pattern.compile("(?<=\"id\":)([^,]*)");
        Matcher idMatcher = idPattern.matcher(s);

        while (nameMatcher.find() && titleMatcher.find() && idMatcher.find()){
            map.put(nameMatcher.group() + " - " + titleMatcher.group(), Integer.parseInt(idMatcher.group()));
        }

        summoner.setOwnedChampions(map);

    }

    //获取召唤师头像和名称
    public void freshSummoner() throws IOException {
        ApiRequest apiRequest = new ApiRequest();
        InputStream currentSummoner = apiRequest.getCurrentSummoner();
        String s = convertStr(currentSummoner);

        //进行正则表达式匹配，以得到召唤师昵称
        Pattern namePattern = Pattern.compile("(?<=displayName\":\")([^\"]*)");
        Matcher nameMatcher = namePattern.matcher(s);
        if (!nameMatcher.find())
            return;
        summoner.setName(nameMatcher.group());

        //召唤师ID
        Pattern sIdPattern = Pattern.compile("(?<=summonerId\":)([^,]*)");
        Matcher sIdMatcher = sIdPattern.matcher(s);
        if (!sIdMatcher.find())
            return;
        summoner.setSummonerId(Long.parseLong(sIdMatcher.group()));

        //召唤师头像ID
        Pattern headPattern = Pattern.compile("(?<=profileIconId\":)([^,]*)");
        Matcher headMatcher = headPattern.matcher(s);
        if (!headMatcher.find())
            return;
        summoner.setIcon(ImageIO.read(apiRequest.getImg(Integer.parseInt(headMatcher.group()))));

    }


    //转换输入流为 String
    //方便进行正则表达式匹配
    public String convertStr(InputStream inputStream){
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        StringBuilder result = new StringBuilder();
        try {
            while (reader.read() != -1)
                result.append(reader.readLine()).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}
