package com.example.himalaya.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtils;

public class HimalayaDBHelper extends SQLiteOpenHelper {


    private static final String TAG = "HimalayaDBHelper";

    public HimalayaDBHelper(Context context) {

        super(context, Constants.DB_NAME, null, Constants.DB_VERSION_CODE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtils.d(TAG,"onCreate...");
        //创建数据表
        //图片，标题，播放量，节目数量，作者名称，专辑id
        String subTbSql = "create table " + Constants.SUB_DB_NAME + "(" +
                Constants.SUB_ID + " integer primary key autoincrement, " +
                Constants.SUB_COVER_URL + " varchar, " +
                Constants.SUB_TITLE + " varchar," +
                Constants.SUB_DESCRIPTION + " varchar," +
                Constants.SUB_PLAY_COUNT + " integer," +
                Constants.SUB_TRACKS_COUNT + " integer," +
                Constants.SUB_AUTHOR_NAME + " varchar," +
                Constants.SUB_ALBUM_ID + " integer" +
                ")";
        db.execSQL(subTbSql);
        //测试添加数据
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+ Constants.SUB_DB_NAME);
        onCreate(db);
    }
}
