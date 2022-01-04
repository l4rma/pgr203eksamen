package no.eksamenPGR203;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

public class QueryString {
    public static HashMap<String, String> queryString(String queryString) throws UnsupportedEncodingException {
        HashMap<String, String> queryMap = new HashMap<>();
        String[] parametersRaw = queryString.split("&");
        for (String s : parametersRaw) {
            String parameterName = s.split("=")[0].trim();
            String parameterValue = s.split("=")[1].trim();
            parameterValue = URLDecoder.decode(parameterValue, "UTF-8");
            queryMap.put(parameterName, parameterValue);
        }
        return queryMap;
    }
}
