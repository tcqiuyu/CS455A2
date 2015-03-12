package cs455.util;

import cs455.harvester.Node;
import cs455.transport.ConnectionFactory;
import cs455.transport.TCPConnection;

import java.io.*;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Qiu on 3/9/2015.
 */
public class ConfigUtil {

    //key is root url, value is host:port
    private static final Map<String, String> crawlerMap = new HashMap<String, String>();

    private Node node;

    public ConfigUtil(String confPath, Node node) {
        File conf = new File(confPath);
        this.node = node;
        readConfFile(conf);
    }

    public static Map<String, String> getCrawlerMap() {
        return crawlerMap;
    }

    public static Set<String> getTargetDomain() {
        return crawlerMap.keySet();
    }

    public static int getCrawlerCount() {
        return crawlerMap.size();
    }

    private void readConfFile(File conf) {
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(conf));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] info = line.split(",");
                String crawlerInfo = info[0];
                String rootInfo = info[1];
                String rootDomain = URLUtil.getDomain(rootInfo);
//                System.out.println(rootInfo + "------->" + rootDomain);
                crawlerMap.put(rootDomain, crawlerInfo);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public TCPConnection getConnectionToCrawler(String rootURL) throws IOException {
        String domain = URLUtil.getDomain(rootURL);
        String info = crawlerMap.get(domain);
        String hostname = info.split(":")[0];
        int port = Integer.parseInt(info.split(":")[1]);
        return ConnectionFactory.getInstance().getConnection(hostname, port, InetAddress.getLocalHost(), node);
    }

}
