package com.seachange.healthandsafty.db;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class DBCheckViewModel extends AndroidViewModel {

    private DBCheckRepository mRepository;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    private LiveData<List<DBCheck>> mAllChecks;
    private LiveData<List<DBTourCheck>> mAllTourChecks;

    public DBCheckViewModel(Application application) {
        super(application);
        mRepository = new DBCheckRepository(application);
        mAllChecks = mRepository.getAllChecks();
        mAllTourChecks = mRepository.getAllTourChecks();
    }

    public LiveData<List<DBCheck>> getAllChecks() {
        return mAllChecks;
    }

    public void insert(DBCheck check) {
        mRepository.insert(check);
    }

    public void delete(DBCheck check) {
        mRepository.delete(check);
    }

    public void update(DBCheck check) {
        mRepository.update(check);
    }

    public void updateSync(DBCheck check) {
        mRepository.updateSync(check);
    }

    //tour checks
    public LiveData<List<DBTourCheck>> getAllTourChecks() {
        return mAllTourChecks;
    }

    public void insertTourCheck(DBTourCheck check) {
        mRepository.insertTourCheck(check);
    }

    public void deleteTourCheck(DBTourCheck check) {
        mRepository.deleteTourCheck(check);
    }

    public void updateTourCheck(DBTourCheck check) {
        mRepository.updateTourCheck(check);
    }

    public void deleteAllChecks() {
        mRepository.deleteAllChecks();
    }
}