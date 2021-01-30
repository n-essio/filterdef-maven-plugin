package it.ness.codebuilder.util;

import com.google.common.base.CaseFormat;
import org.apache.maven.plugin.logging.Log;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StringUtil {

    protected static Log log;
    private static Map<String, String> pluralMap = null;

    public StringUtil(Log log) {
        this.log = log;
    }

    public static String removeQuotes(final String str) {
        return str.replaceAll("\"", "");
    }

    public static String getRsPathFromConstantName(final String constantName) {
        String pathName = constantName.toLowerCase(Locale.ROOT);
        if (pathName.contains("_")) {
            int i = pathName.lastIndexOf("_");
            String lastWord = pathName.substring(i+1);
            String plural = getPlural(lastWord);
            pathName = pathName.substring(0, i) + "_" + plural;
            pathName = pathName.toUpperCase(Locale.ROOT) + "_PATH";
            return pathName;
        }
        return "NOT_SET";
    }

    public static String getClassNameFromFileName(final String modelFileName) {
        return modelFileName.substring(0, modelFileName.indexOf(".java"));
    }

    public static String getConstantNameFromFileName(final String modelFileName) {
        String constantName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, modelFileName);
        if (constantName.indexOf(".java") > 0) {
            constantName = constantName.substring(0, constantName.indexOf(".java"));
        }
        return constantName;
    }

    public static String getPlural(String str) {
        if (null == pluralMap) {
            JSONObject jsonObject = null;

            JSONParser jsonParser = new JSONParser();
            InputStream is = StringUtil.class.getClassLoader().getResourceAsStream("plurals.json");
            try {
                jsonObject = (JSONObject) jsonParser.parse(
                        new InputStreamReader(is, "UTF-8"));
            } catch (Exception e) {
                log.error(e);
            }

            pluralMap = new HashMap<>();
            if (jsonObject != null) {
                for (Object key : jsonObject.keySet()) {
                    if (key instanceof String) {
                        pluralMap.put((String)key, (String)jsonObject.get(key));
                    }
                }
            }
        }

        if (!pluralMap.containsKey(str)) {
            log.warn("plural for " + str + " not found. Adding s to make the plural.");
            return str + "s";
        }

        return pluralMap.get(str);
    }
}
