package com.sitm.mio.datacenter.interfaces;

import com.sitm.mio.datacenter.model.TravelTimeStat;

public interface ITravelTimeStatsRepository {
    void save(TravelTimeStat stat);
}
