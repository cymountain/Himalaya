package com.example.himalaya.presenters;

import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.data.ISubDaoCallback;
import com.example.himalaya.data.SubscriptionDao;
import com.example.himalaya.interfaces.ISubscriptionCallback;
import com.example.himalaya.interfaces.ISubscriptionPresenter;
import com.example.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

public class SubscriptionPresenter implements ISubscriptionPresenter, ISubDaoCallback {

    private final SubscriptionDao mSubscriptionDao;
    private Map<Long, Album> mData = new HashMap<>();
    private List<ISubscriptionCallback> mCallbacks = new ArrayList<>();

    private SubscriptionPresenter() {
        mSubscriptionDao = SubscriptionDao.getInstance();
        mSubscriptionDao.setCallback(this);

    }

    private void listSubscription() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) {
                //只调用不处理结果
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.listAlbums();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private static SubscriptionPresenter sInstance = null;

    public static SubscriptionPresenter getInstance() {
        if (sInstance == null) {
            synchronized (SubscriptionPresenter.class) {
                sInstance = new SubscriptionPresenter();
            }
        }
        return sInstance;
    }


    @Override
    public void addSubscription(final Album album) {
        //判断的订阅数量不能超过100
        if (mData.size() >= Constants.MAX_SUB_COUNT) {
            //给出提示
            for (ISubscriptionCallback callback : mCallbacks) {
                callback.onSubFull();
            }
            return;
        }
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) {
                //只调用不处理结果
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.addAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void deleteSubscription(final Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) {
                //只调用不处理结果
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.delAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void getSubscriptionList() {
        listSubscription();
    }

    @Override
    public boolean isSubscription(Album album) {
        Album result = mData.get(album.getId());
        //不为空表示已经订阅
        return result != null;
    }

    @Override
    public void registerViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        if (!mCallbacks.contains(iSubscriptionCallback)) {
            mCallbacks.add(iSubscriptionCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        mCallbacks.remove(iSubscriptionCallback);
    }

    @Override
    public void onAddResult(final boolean isSuccess) {
        listSubscription();
        //添加结果的回调
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onAddResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onDelResult(final boolean isSuccess) {
        listSubscription();
        //删除订阅的回调
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onDeleteResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onSubListLoaded(final List<Album> result) {
        //加载列表的回调
        mData.clear();
        for (Album album : result) {
            mData.put(album.getId(), album);
        }
        //通知ui
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onSubscriptionsLoaded(result);
                }
            }
        });
    }
}
