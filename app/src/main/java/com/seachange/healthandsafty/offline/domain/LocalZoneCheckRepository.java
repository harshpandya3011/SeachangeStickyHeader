package com.seachange.healthandsafty.offline.domain;

import com.seachange.healthandsafty.offline.model.ZoneCheck;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface LocalZoneCheckRepository {
    Single<ZoneCheck> add(ZoneCheck check);
    Completable update(ZoneCheck check);
    Completable delete(ZoneCheck check);
    Flowable<List<ZoneCheck>> getChecks();
}
