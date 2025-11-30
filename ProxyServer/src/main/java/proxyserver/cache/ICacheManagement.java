package proxyserver.cache;

import citizen.dto.InformacionAlCiudadano;

public interface ICacheManagement {

   
    InformacionAlCiudadano get(String key);

    void put(String key, InformacionAlCiudadano value);
}