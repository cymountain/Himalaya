package com.example.himalaya.utils;

public class Constants {
    //获取推荐列表的专辑数量

    public static int COUNT_RECOMMEND = 50;

    //默认请求数量

    public static int COUNT_DEFAULT = 50;

    //热词数量
    public static int COUNT_HOT_WORD = 15;

    //数据库相关的常量
    public static final String DB_NAME = "himalaya.db";
    //数据库版本
    public static final int DB_VERSION_CODE = 2;

    public static final String SUB_DB_NAME = "subDb";

    public static final String SUB_ID = "_id";
    public static final String SUB_COVER_URL = "coverUrl";
    public static final String SUB_TITLE = "title";
    public static final String SUB_DESCRIPTION = "description";
    public static final String SUB_TRACKS_COUNT = "tracksCount";
    public static final String SUB_PLAY_COUNT = "playCount";
    public static final String SUB_AUTHOR_NAME = "authorName";
    public static final String SUB_ALBUM_ID = "albumId";

    //最大订阅数量
    public static final int MAX_SUB_COUNT = 50;

    //历史记录的表名
    public static final String HISTORY_DB_NAME = "tb_history";
    public static final String HISTORY_ID = "_id";
    public static final String HISTORY_TRACK_ID = "historyTrackId";
    public static final String HISTORY_TITLE = "historyTitle";
    public static final String HISTORY_PLAY_COUNT = "historyPlayCount";
    public static final String HISTORY_DURATION = "historyDuration";
    public static final String HISTORY_UPDATE_TIME = "historyUpdateTime";
    public static final String HISTORY_COVER = "historyCover";
    public static final String HISTORY_AUTHOR = "history_author";
    //最大的历史记录数
    public static final int MAX_HISTORY_COUNT = 100;


}
