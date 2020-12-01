package net.kk.chat.utils;

import java.util.Set;

public class MessageUtil {
    public static String getMessNames(Set<String> names) {
        StringBuilder builder = new StringBuilder("{\"userCount\":[");
        for (String name : names) {
            builder.append("{\"name\":\"").append(name + "\",").append("\"url\":\"").append("/upload/" + name + ".jpg\"},");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("],\"type\": \"userCount\"}");
        return builder.toString();
    }
}
