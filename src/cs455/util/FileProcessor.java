package cs455.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Created by Qiu on 3/12/2015.
 */
public class FileProcessor {

    public static String usrPath = "D:/tmp/cs455-yqiu";

    public static void output(String filepath, Set<String> output) {
        try {
            File file = new File(filepath);
            file.getParentFile().mkdir();
            file.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            for (String string : output) {
                bufferedWriter.write(string + "\n");
            }
            bufferedWriter.close();
        } catch (IOException e) {

            System.out.println(e.getCause());

        }

    }

    public static String getValidPathname(String path) {
        path = path.replace("http://", "");
        path = path.replaceAll("/", "_");
        return path;
    }

    public static void createDir() {
        File dir = new File(usrPath);
        System.out.println("Creating user directory at: " + usrPath);
        boolean success = dir.mkdir();
        if (success) {
            System.out.println("Successful create directory at: " + usrPath);
        } else {
            System.out.println("Failed create directory at: " + usrPath);
        }
    }
}
