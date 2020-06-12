package com.example.himalaya.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtils;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionDao implements ISubDao {
    private static final String TAG = "SubscriptionDao";

    private static final SubscriptionDao ourInstance = new SubscriptionDao();
    private final HimalayaDBHelper mHimalayaDBHelper;
    private ISubDaoCallback mCallback = null;

    public static SubscriptionDao getInstance() {
        return ourInstance;
    }

    private SubscriptionDao() {
        mHimalayaDBHelper = new HimalayaDBHelper(BaseApplication.getAppContext());
    }

    @Override
    public void setCallback(ISubDaoCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void addAlbum(Album album) {
        SQLiteDatabase db = null;
        boolean isAddSuccess = false;
        try {
            db = mHimalayaDBHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            //封装数据
            contentValues.put(Constants.SUB_COVER_URL, album.getCoverUrlLarge());
            contentValues.put(Constants.SUB_TITLE, album.getAlbumTitle());
            contentValues.put(Constants.SUB_DESCRIPTION, album.getAlbumIntro());
            contentValues.put(Constants.SUB_TRACKS_COUNT, album.getIncludeTrackCount());
            contentValues.put(Constants.SUB_PLAY_COUNT, album.getPlayCount());
            contentValues.put(Constants.SUB_AUTHOR_NAME, album.getAnnouncer().getNickname());
            contentValues.put(Constants.SUB_ALBUM_ID, album.getId());
            //插入数据
            db.insert(Constants.SUB_DB_NAME, null, contentValues);
            db.setTransactionSuccessful();
            LogUtils.d(TAG, "addAlbumSuccess");
            isAddSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            isAddSuccess = false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onAddResult(isAddSuccess);
            }
        }
    }

    @Override
    public void delAlbum(Album album) {
        SQLiteDatabase db = null;
        boolean isDelSuccess = false;
        try {
            db = mHimalayaDBHelper.getWritableDatabase();
            db.beginTransaction();
            int delete = db.delete(Constants.SUB_DB_NAME, Constants.SUB_ALBUM_ID + "=?", new String[]{album.getId() + ""});
            LogUtils.d(TAG, "delete -- >" + delete);
            db.setTransactionSuccessful();
            isDelSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            isDelSuccess = false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onDelResult(isDelSuccess);
            }
        }
    }

    @Override
    public void listAlbums() {
        SQLiteDatabase db = null;
        List<Album> result = new ArrayList<>();
        try {
            db = mHimalayaDBHelper.getReadableDatabase();
            db.beginTransaction();
            Cursor query = db.query(Constants.SUB_DB_NAME, null, null, null, null, null, "_id desc");
            //封装数据
            while (query.moveToNext()) {
                Album album = new Album();
                //封面
                String coverUrl = query.getString(query.getColumnIndex(Constants.SUB_COVER_URL));
                album.setCoverUrlLarge(coverUrl);
                //标题
                String title = query.getString(query.getColumnIndex(Constants.SUB_TITLE));
                album.setAlbumTitle(title);
                //描述
                String description = query.getString(query.getColumnIndex(Constants.SUB_DESCRIPTION));
                album.setAlbumIntro(description);
                //专辑数
                int tracksCount = query.getInt(query.getColumnIndex(Constants.SUB_TRACKS_COUNT));
                album.setIncludeTrackCount(tracksCount);
                //播放量
                int PlayCount = query.getInt(query.getColumnIndex(Constants.SUB_PLAY_COUNT));
                album.setPlayCount(PlayCount);
                //专辑id
                int AlbumId = query.getInt(query.getColumnIndex(Constants.SUB_ALBUM_ID));
                album.setId(AlbumId);

                String authorName = query.getString(query.getColumnIndex(Constants.SUB_AUTHOR_NAME));
                Announcer announcer = new Announcer();
                announcer.setNickname(authorName);
                album.setAnnouncer(announcer);
                result.add(album);
            }
            if (mCallback != null) {
                mCallback.onSubListLoaded(result);
            }
            query.close();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            //把数据通知给ui
            if (mCallback != null) {
                mCallback.onSubListLoaded(result);
            }
        }
    }
}
