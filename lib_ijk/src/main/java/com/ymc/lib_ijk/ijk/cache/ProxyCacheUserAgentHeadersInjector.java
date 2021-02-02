package com.ymc.lib_ijk.ijk.cache;


import com.ymc.lib_ijk.videocache.headers.HeaderInjector;

import java.util.HashMap;
import java.util.Map;

/**
 for android video cache header
 */
public class ProxyCacheUserAgentHeadersInjector implements HeaderInjector {

    public final static Map<String, String> mMapHeadData = new HashMap<>();

    @Override
    public Map<String, String> addHeaders(String url) {
        return mMapHeadData;
    }
}