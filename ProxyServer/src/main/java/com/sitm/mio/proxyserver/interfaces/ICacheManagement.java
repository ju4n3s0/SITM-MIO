package com.sitm.mio.proxyserver.interfaces;

import com.sitm.mio.proxyserver.cache.CacheType;

import SITM.CitizenInformation;

public interface ICacheManagement {

   
    CitizenInformation get(String key);

    void put(String key, CitizenInformation value, CacheType cacheType);
}