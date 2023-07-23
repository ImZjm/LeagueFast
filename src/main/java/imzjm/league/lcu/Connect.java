package imzjm.league.lcu;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Connect {

    private String token;

    private String port;

    private String address;

    private String authorization;

    private Boolean status = false;

    private static Connect connect = new Connect();

    private Connect(){
        String s = getCommandline();
        if (s.trim().equals(""))
            return;
        if (!matchConnectInfo(s))
            return;
        address = "https://127.0.0.1:" + port;
        authorization = "Basic " +
                base64Encrypt("riot:" + token);

        status = true;
    }

    //在windows上执行cmd命令，返回命令执行结果
    private String getCommandline() {
        try {
            Process p = Runtime.getRuntime().exec("wmic PROCESS WHERE name='LeagueClientUx.exe' GET commandline");
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("GBK")));
            String line;
            StringBuilder outPutStr = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                outPutStr.append(line).append("\n");
            }
            return outPutStr.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //进行base64加密
    private static String base64Encrypt(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    //通过正则表达式，匹配通过cmd命令得到的进程详情，从中拿到 port， token
    private Boolean matchConnectInfo(String s) {

        Pattern portPattern = Pattern.compile("(?<=--app-port=)([^\"]*)");
        Matcher matcher = portPattern.matcher(s);
        if (matcher.find()){
            port = matcher.group();
        }
        else
            return false;

        Pattern tokenPattern = Pattern.compile("(?<=--remoting-auth-token=)([^\"]*)");
        matcher = tokenPattern.matcher(s);
        if (matcher.find()) {
            token = matcher.group();
        }
        else
            return false;

        return true;

    }

    public static Connect getConnect() {
        return connect;
    }

    //重新建立连接
    public static Connect reConnect(){
        connect = new Connect();
        return connect;
    }

    //忽略证书检查
    public static SSLContext createUnverifiedSslContext() {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        try {
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }}, new SecureRandom());
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
        return sslContext;
    }

    public String getToken() {
        return token;
    }

    public String getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public String getAuthorization() {
        return authorization;
    }

    public Boolean getStatus() {
        return status;
    }

}
