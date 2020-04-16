package com.seachange.healthandsafty.offline.domain;

import com.seachange.healthandsafty.offline.model.ZoneCheck;

import io.reactivex.Completable;

public interface RemoteZoneCheckRepository {
    Completable sync(ZoneCheck zoneCheck);
}
