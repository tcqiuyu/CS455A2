package cs455.harvester;

/**
 * Created by Qiu on 3/7/15.
 */
public class Crawler {


    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: java cs455.harvester.Crawler portnum thread-pool-size root-url path-to-config-file");
        }

        int port = Integer.parseInt(args[0]);
        int poolSize = Integer.parseInt(args[1]);
        String root = args[2];
        String confPath = args[3];


    }

}
