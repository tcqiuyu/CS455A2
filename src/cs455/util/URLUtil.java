package cs455.util;

import cs455.graph.Graph;
import cs455.harvester.Crawler;
import net.htmlparser.jericho.*;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Created by Qiu on 3/9/2015.
 */
public class URLUtil {

    private static final URLUtil instance = new URLUtil();
    private static final Map<String, String> processedURLs = new HashMap<String, String>();
    private static final Map<String, String> badURLs = new HashMap<String, String>();

    private URLUtil() {
    }

    public static URLUtil getInstance() {
        return instance;
    }

    public static boolean withinDomain(String pageUrl) {
        try {
            return new URL(pageUrl).getHost().equals(new URL(Crawler.rootURL).getHost());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isTargetDomain(String url) {
        String urlDomain = null;
        try {
            urlDomain = getDomain(url);
//            System.out.println(url + "----->" + urlDomain);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return ConfigUtil.getCrawlerMap().containsKey(urlDomain);
    }

    public static String getDomain(String targetUrl) throws MalformedURLException {
        return new URL(targetUrl).getHost();
    }

    /**
     * Licensed under http://www.apache.org/licenses/LICENSE-2.0
     */
    public static String normalize(String normalized) {

        if (normalized == null) {
            return null;
        }

        // If the buffer begins with "./" or "../", the "." or ".." is removed.
        if (normalized.startsWith("./")) {
            normalized = normalized.substring(1);
        } else if (normalized.startsWith("../")) {
            normalized = normalized.substring(2);
        } else if (normalized.startsWith("..")) {
            normalized = normalized.substring(2);
        }

        // All occurrences of "/./" in the buffer are replaced with "/"
        int index = -1;
        while ((index = normalized.indexOf("/./")) != -1) {
            normalized = normalized.substring(0, index) + normalized.substring(index + 2);
        }

        // If the buffer ends with "/.", the "." is removed.
        if (normalized.endsWith("/.")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        int startIndex = 0;

        // All occurrences of "/<segment>/../" in the buffer, where ".."
        // and <segment> are complete path segments, are iteratively replaced
        // with "/" in order from left to right until no matching pattern remains.
        // If the buffer ends with "/<segment>/..", that is also replaced
        // with "/".  Note that <segment> may be empty.
        while ((index = normalized.indexOf("/../", startIndex)) != -1) {
            int slashIndex = normalized.lastIndexOf('/', index - 1);
            if (slashIndex >= 0) {
                normalized = normalized.substring(0, slashIndex) + normalized.substring(index + 3);
            } else {
                startIndex = index + 3;
            }
        }
        if (normalized.endsWith("/..")) {
            int slashIndex = normalized.lastIndexOf('/', normalized.length() - 4);
            if (slashIndex >= 0) {
                normalized = normalized.substring(0, slashIndex + 1);
            }
        }

        // All prefixes of "<segment>/../" in the buffer, where ".."
        // and <segment> are complete path segments, are iteratively replaced
        // with "/" in order from left to right until no matching pattern remains.
        // If the buffer ends with "<segment>/..", that is also replaced
        // with "/".  Note that <segment> may be empty.
        while ((index = normalized.indexOf("/../")) != -1) {
            int slashIndex = normalized.lastIndexOf('/', index - 1);
            if (slashIndex >= 0) {
                break;
            } else {
                normalized = normalized.substring(index + 3);
            }
        }
        if (normalized.endsWith("/..")) {
            int slashIndex = normalized.lastIndexOf('/', normalized.length() - 4);
            if (slashIndex < 0) {
                normalized = "/";
            }
        }

        return normalized;
    }

    public String resolveRedirects(String url) {
        HttpURLConnection con;
        try {
            con = (HttpURLConnection) (new URL(url).openConnection());
//            con.connect();
//            InputStream inputStream = con.getInputStream();
//            url = con.getURL().toString();

            con.setInstanceFollowRedirects(false);
            con.connect();
            int responseCode = con.getResponseCode();
            if (responseCode == 301) {
                return con.getHeaderField("Location");
            }
            con.disconnect();
        } catch (IOException e) {
            addToBadUrls(url);
        }
        return url;
    }

    public void addProcessedUrl(String url) {
        synchronized (processedURLs) {
            processedURLs.put(url, null);
        }
    }

    public Set<String> extractUrl(String pageUrl) {
//        System.out.println("------------" + resolveRedirects("http://www.cs.colostate.edu"));
        Set<String> extractedUrls = new HashSet<String>();

        Config.LoggerProvider = LoggerProvider.DISABLED;

        try {
//            String redirectedUrl = resolveRedirects(pageUrl);
            Source source = new Source(new URL(pageUrl));

            List<Element> aTags = source.getAllElements(HTMLElementName.A);

            for (Element aTag : aTags) {
                long start = System.currentTimeMillis();

                String href = aTag.getAttributeValue("href");

                if (!isValidHREF(href)) {
                    continue;
                }

                if (!new URI(href).isAbsolute()) {
                    href = new URI(pageUrl).resolve(href).toString();
                }
                href = normalize(href);
                href = "http://" + (new URL(href)).getAuthority() + (new URL(href)).getPath();
//                System.out.println(href);
                if (isProcessed(href)) {
                    Graph.getInstance().addLink(pageUrl, href);
                    continue;
                } else if (!isTargetDomain(href) || !validPage(href)) {
                    continue;
                }

                addProcessedUrl(href);
                href = resolveRedirects(href);
                extractedUrls.add(href);
                long end = System.currentTimeMillis();
                long dur = end - start;
                System.out.println("Time: " + dur + "-----> added: ----->" + href);
            }

        } catch (MalformedURLException e) {
//            e.printStackTrace();
        } catch (IOException e) {
//            System.out.println(e.getMessage());
            addToBadUrls(pageUrl);
        } catch (URISyntaxException e) {
//            System.out.println(e.getMessage());
            addProcessedUrl(pageUrl);
        }
//        System.out.println(extractedUrls.size());
        System.out.println("LOOP OUT!");
        return extractedUrls;
    }

    private boolean validPage(String href) {
        String[] dotDelim = href.split("\\.");
        String end = dotDelim[dotDelim.length - 1];
        return end.contains("html") || end.contains("htm") || end.contains("php") ||
                end.contains("shtml") || end.contains("/") || end.contains("asp") ||
                end.contains("jsp") || end.contains("cfm");
    }

    public boolean isProcessed(String url) {
        synchronized (processedURLs) {
            return processedURLs.containsKey(url);
        }
    }

    private boolean isValidHREF(String href) {
        return !(href == null || href.length() == 0)
                && !(href.startsWith("#") || href.startsWith("mailto")
                || href.startsWith("https") || href.startsWith("ftp"));
    }

    private void addToBadUrls(String url) {
        synchronized (badURLs) {
            badURLs.put(url, null);
        }
    }


}
