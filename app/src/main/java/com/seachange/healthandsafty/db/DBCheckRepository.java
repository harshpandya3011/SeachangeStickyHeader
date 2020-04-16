package com.seachange.healthandsafty.db;

import android.app.Application;
import androidx.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class DBCheckRepository {

    private DBCheckDao mCheckDao;
    private LiveData<List<DBCheck>> mAllChecks;

    private DBTourCheckDao mTourCheckDao;
    private LiveData<List<DBTourCheck>> mAllTourChecks;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    DBCheckRepository(Application application) {
        DBCheckDatabase db = DBCheckDatabase.getDatabase(application);
        mCheckDao = db.checkDao();
        mAllChecks = mCheckDao.getAllChecks();

        mTourCheckDao = db.tourCheckDao();
        mAllTourChecks = mTourCheckDao.getAllTourChecks();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<DBCheck>> getAllChecks() {
        return mAllChecks;
    }

    // You must call this on a non-UI thread or your app will crash.
    // Like this, Room ensures that you're not doing any long running operations on the main
    // thread, blocking the UI.
    void insert(DBCheck check) {
        new insertAsyncTask(mCheckDao).execute(check);
    }

    void delete(DBCheck check) {
        new deleteAsyncTask(mCheckDao).execute(check);
    }

    void update(DBCheck check) {
        new updateAsyncTask(mCheckDao).execute(check);
    }

    void updateSync(DBCheck check) {
        new updateSyncAsyncTask(mCheckDao).execute(check);
    }

    void deleteAllChecks() {
        new deleteAllChecksAsyncTask(mCheckDao).execute();
        new deleteAllTourChecksAsyncTask(mTourCheckDao).execute();
    }

    private static class insertAsyncTask extends AsyncTask<DBCheck, Void, Void> {

        private DBCheckDao mAsyncTaskDao;

        insertAsyncTask(DBCheckDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final DBCheck... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAllChecksAsyncTask extends AsyncTask<Void, Void, Void> {

        private DBCheckDao mAsyncTaskDao;

        deleteAllChecksAsyncTask(DBCheckDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    private static class deleteAllTourChecksAsyncTask extends AsyncTask<Void, Void, Void> {

        private DBTourCheckDao mAsyncTaskDao;

        deleteAllTourChecksAsyncTask(DBTourCheckDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<DBCheck, Void, Void> {

        private DBCheckDao mAsyncTaskDao;

        deleteAsyncTask(DBCheckDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final DBCheck... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<DBCheck, Void, Void> {

        private DBCheckDao mAsyncTaskDao;

        updateAsyncTask(DBCheckDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final DBCheck... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }

    private static class updateSyncAsyncTask extends AsyncTask<DBCheck, Void, Void> {

        private DBCheckDao mAsyncTaskDao;

        updateSyncAsyncTask(DBCheckDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final DBCheck... params) {
            mAsyncTaskDao.updateSync(params[0].getZoneCheck());
            return null;
        }
    }


    //tour checks... get all tour checks...
    LiveData<List<DBTourCheck>> getAllTourChecks() {
        return mAllTourChecks;
    }

    //these are insert, delete and update operations...
    void insertTourCheck(DBTourCheck check) {
        new insertTourCheckAsyncTask(mTourCheckDao).execute(check);
    }

    void deleteTourCheck(DBTourCheck check) {
        new deleteTourCheckAsyncTask(mTourCheckDao).execute(check);
    }

    void updateTourCheck(DBTourCheck check) {
        new updateTourCheckAsyncTask(mTourCheckDao).execute(check);
    }

    private static class insertTourCheckAsyncTask extends AsyncTask<DBTourCheck, Void, Void> {

        private DBTourCheckDao mAsyncTaskDao;

        insertTourCheckAsyncTask(DBTourCheckDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final DBTourCheck... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }


    private static class deleteTourCheckAsyncTask extends AsyncTask<DBTourCheck, Void, Void> {

        private DBTourCheckDao mAsyncTaskDao;

        deleteTourCheckAsyncTask(DBTourCheckDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final DBTourCheck... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    private static class updateTourCheckAsyncTask extends AsyncTask<DBTourCheck, Void, Void> {

        private DBTourCheckDao mAsyncTaskDao;

        updateTourCheckAsyncTask(DBTourCheckDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final DBTourCheck... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }
}
