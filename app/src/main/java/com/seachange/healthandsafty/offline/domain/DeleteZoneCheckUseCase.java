package com.seachange.healthandsafty.offline.domain;

import com.seachange.healthandsafty.offline.model.ZoneCheck;

import io.reactivex.Completable;

public class DeleteZoneCheckUseCase {

    private final LocalZoneCheckRepository localCommentRepository;

    public DeleteZoneCheckUseCase(LocalZoneCheckRepository localCommentRepository) {
        this.localCommentRepository = localCommentRepository;
    }

    public Completable deleteZoneCheck(ZoneCheck check) {
        return localCommentRepository.delete(check);
    }
}
