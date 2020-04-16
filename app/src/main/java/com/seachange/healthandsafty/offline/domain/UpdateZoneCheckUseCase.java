package com.seachange.healthandsafty.offline.domain;

import com.seachange.healthandsafty.offline.model.ZoneCheck;

import io.reactivex.Completable;

public class UpdateZoneCheckUseCase {
    private final LocalZoneCheckRepository localZoneCheckRepository;

    public UpdateZoneCheckUseCase(LocalZoneCheckRepository localCheckRepository) {
        this.localZoneCheckRepository = localCheckRepository;
    }

    public Completable updateZone(ZoneCheck zoneCheck) {
        return localZoneCheckRepository.update(zoneCheck);
    }
}
