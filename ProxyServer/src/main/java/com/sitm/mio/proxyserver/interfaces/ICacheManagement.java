package com.sitm.mio.proxyserver.interfaces;

import SITM.CitizenInformation;

public interface ICacheManagement {

   
    CitizenInformation get(String key);

    void put(String key, CitizenInformation value);
}