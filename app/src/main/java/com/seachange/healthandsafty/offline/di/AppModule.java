//package com.seachange.healthandsafty.offline.di;
//
//import android.content.Context;
//
//import javax.inject.Singleton;
//
//import dagger.Module;
//import dagger.Provides;
//
//@Module
//public class AppModule {
//
//    @Provides
//    Context provideContext(App application) {
//        return application.getApplicationContext();
//    }
//
//    @Singleton
//    @Provides
//    SchedulerJobService provideSchedulerJobService() {
//        return new SchedulerJobService();
//    }
//
//    @Singleton
//    @Provides
//    GcmJobService provideGcmJobService() {
//        return new GcmJobService();
//    }
//
//    @Singleton
//    @Provides
//    CommentDao provideCommentDao(Context context) {
//        return CommentDatabase.getInstance(context).commentDao();
//    }
//
//    @Singleton
//    @Provides
//    LocalCommentRepository provideLocalCommentRepository(CommentDao commentDao) {
//        return new LocalCommentDataStore(commentDao);
//    }
//
//    @Singleton
//    @Provides
//    RemoteCommentRepository provideRemoteCommentRepository() {
//        return new RemoteCommentDataStore();
//    }
//}