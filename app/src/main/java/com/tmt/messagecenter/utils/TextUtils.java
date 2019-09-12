package com.tmt.messagecenter.utils;



import com.tmt.messagecenter.model.UserMessage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TextUtils {

    /**
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, String> getQueries(String uri, String prefix) throws UnsupportedEncodingException {
        if (uri == null) throw new UnsupportedEncodingException("");
        if (uri.contains(prefix)) {
            uri = uri.replaceAll(prefix, "");
            if (uri.contains("?"))
                uri = uri.replaceAll("\\?", "");
            return splitQuery(uri);
        }
        return new HashMap<>();
    }

    /**
     *
     * @param url
     * @return HashMap split for a url
     * e.g to a keyValue pair
     *
     * @throws UnsupportedEncodingException
     */
    public static Map<String, String> splitQuery(String url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        String query = url;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }


    public static String getLocationUrlMessageIfExists(UserMessage mMessage) {
        return mMessage.isLocation() ? "https://www.google.com/maps/search/?api=1&query=" +  mMessage.getLocation().getLat() + "," + mMessage.getLocation().getLng()
                : mMessage.getMessage().getBody();
    }

}