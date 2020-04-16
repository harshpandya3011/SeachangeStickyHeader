package com.seachange.healthandsafty.offline.domain;

import com.seachange.healthandsafty.offline.model.ModelConstants;
import com.seachange.healthandsafty.offline.model.ZoneCheck;

import java.util.List;

import io.reactivex.Flowable;

public class GetZoneCheckUseCase {
    private final LocalZoneCheckRepository localCommentRepository;

    public GetZoneCheckUseCase(LocalZoneCheckRepository localCheckRepository) {
        this.localCommentRepository = localCheckRepository;
    }

    public Flowable<List<ZoneCheck>> getComments() {
        return localCommentRepository.getChecks();
    }
}
