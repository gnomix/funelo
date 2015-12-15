package de.zalando.funelo.util;

import java.util.ArrayList;
import java.util.List;

public class UrlAnalyzer {

    /**
     * @param   url  - example: "/v1/myfeed/:eventtype/:subtype"
     *
     * @return  example: {eventtype, subtype}
     */
    public static List<String> extractParamNames(final String url) {
        final List<String> paramNames = new ArrayList<>();
        if (url == null) {
            return paramNames;
        }

        final String[] urlParts = url.split("/");
        for (String urlPart : urlParts) {
            if (urlPart.startsWith(":")) {
                paramNames.add(urlPart.substring(1));
            }
        }

        return paramNames;
    }

}
