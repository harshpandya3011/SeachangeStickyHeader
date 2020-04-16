package com.seachange.healthandsafty.offline.domain;

import com.seachange.healthandsafty.offline.model.ModelConstants;
import com.seachange.healthandsafty.offline.model.ZoneCheck;

import io.reactivex.Completable;

public class AddZoneCheckUseCase {
    private final LocalZoneCheckRepository localZoneCheckRepository;
    private final SyncZoneCheckUseCase syncZoneCheckUseCase;

    public AddZoneCheckUseCase(LocalZoneCheckRepository localCheckRepository, SyncZoneCheckUseCase syncCheckUseCase) {
        this.localZoneCheckRepository = localCheckRepository;
        this.syncZoneCheckUseCase = syncCheckUseCase;
    }

    public Completable addZoneCheck(ZoneCheck check) {
        return localZoneCheckRepository.add(check)
                .flatMapCompletable(syncZoneCheckUseCase::syncZoneCheck);
    }
}
