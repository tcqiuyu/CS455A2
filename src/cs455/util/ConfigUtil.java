package cs455.util;

import cs455.harvester.Node;
import cs455.transport.ConnectionFactory;
import cs455.transport.TCPConnection;

import java.io.*;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

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

    private void readConfFile(File conf) {
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(conf));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] info = line.split(",");
                crawlerMap.put(info[1], info[0]);
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
        String info = crawlerMap.get(rootURL);
        String hostname = info.split(":")[0];
        int port = Integer.parseInt(info.split(":")[1]);
        return ConnectionFactory.getInstance().getConnection(hostname, port, InetAddress.getLocalHost(), node);
    }

    public Map<String, String> getCrawlerMap() {
        return crawlerMap;
    }


}
