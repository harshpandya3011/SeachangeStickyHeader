package com.seachange.healthandsafty.offline.domain;

import com.seachange.healthandsafty.offline.model.ZoneCheck;

import io.reactivex.Completable;

public class SyncZoneCheckUseCase {
    private final RemoteZoneCheckRepository remoteZoneCheckRepository;

    public SyncZoneCheckUseCase(RemoteZoneCheckRepository remoteCheckRepository) {
        this.remoteZoneCheckRepository = remoteCheckRepository;
    }

    public Completable syncZoneCheck(ZoneCheck zoneCheck) {
        return remoteZoneCheckRepository.sync(zoneCheck);
    }
}
