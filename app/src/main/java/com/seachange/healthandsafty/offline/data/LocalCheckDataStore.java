package com.seachange.healthandsafty.offline.data;

import com.seachange.healthandsafty.offline.domain.LocalZoneCheckRepository;
import com.seachange.healthandsafty.offline.model.CheckUtils;
import com.seachange.healthandsafty.offline.model.ZoneCheck;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import timber.log.Timber;

public class LocalCheckDataStore implements LocalZoneCheckRepository {

    private final ZoneCheckDao checkDao;

    public LocalCheckDataStore(ZoneCheckDao checkDao) {
        this.checkDao = checkDao;
    }

    /**
     * Adds a zone check
     */
    public Single<ZoneCheck> add(ZoneCheck checkd) {
        Timber.d("creating check %s, ");
        ZoneCheck check = new ZoneCheck();

        return Single.fromCallable(() -> {
            long rowId = checkDao.add(check);
            Timber.d("zone check stored " + rowId);
            return CheckUtils.clone(check, rowId);
        });
    }

    /**
     * Updates a zone check
     */
    public Completable update(ZoneCheck check) {
        Timber.d("updating comment sync status for comment id %s", check.getId());

        return Completable.fromAction(() -> checkDao.update(check));
    }

    /**
     * Deletes a zone check
     */
    public Completable delete(ZoneCheck check) {
        Timber.d("deleting comment with id %s", check.getId());

        return Completable.fromAction(() -> checkDao.delete(check));
    }

    /**
     * Returns Flowable stream of zone check
     */
    public Flowable<List<ZoneCheck>> getChecks() {
        Timber.d("getting checks for id");
        return checkDao.getZoneChecks();
    }
}