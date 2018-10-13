package utils.java.cssinliner;
import java.io.IOException;
import java.util.StringTokenizer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Css inliner for email,from
 * http://stackoverflow.com/questions/4521557/automatically-convert-style-sheets-to-inline-style
 * 
 * @author dennis<xzhuang@avos.com>
 * @date 2013-1-9
 */
// Modified to exclude Google Guava Dependency

public class CSSInliner {
    
    public static boolean isNullOrEmpty(String param) { 
      return param == null || param.trim().length() == 0;
    }
    public static String inlineStyles(String html) throws IOException {
        // Document doc = Jsoup.connect("http://mypage.com/inlineme.php").get();
        Document doc = Jsoup.parse(html);
        String style = "style";
        Elements els = doc.select(style);// to get all the style elements
        for (Element e : els) {
            String styleRules = e.getAllElements().get(0).data().replaceAll("\n", "").trim(), delims =
                    "{}";
            StringTokenizer st = new StringTokenizer(styleRules, delims);
            while (st.countTokens() > 1) {
                String selector = st.nextToken(), properties = st.nextToken();
                // Process selectors such as "a:hover"
                if (selector.indexOf(":") > 0) {
                    selector = selector.substring(0, selector.indexOf(":"));
                }
                if (isNullOrEmpty(selector)) {
                    continue;
                }
                Elements selectedElements = doc.select(selector);
                for (Element selElem : selectedElements) {
                    String oldProperties = selElem.attr(style);
                    selElem.attr(
                        style,
                        oldProperties.length() > 0 ? concatenateProperties(oldProperties,
                            properties) : properties);
                }
            }
            e.remove();
        }
        return doc.toString();
    }

    private static String concatenateProperties(String oldProp, String newProp) {
        oldProp = oldProp.trim();
        if (!newProp.endsWith(";")) {
            newProp += ";";
        }
        return newProp + oldProp; // The existing (old) properties should take precedence.
    }
}
