package com.example.himalaya;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.adepters.AlbumListAdapter;
import com.example.himalaya.adepters.SearchRecommendAdepter;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.ISearchCallback;
import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.example.himalaya.presenters.SearchPresenter;
import com.example.himalaya.utils.LogUtils;
import com.example.himalaya.views.FlowTextLayout;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity implements ISearchCallback, AlbumListAdapter.OnAlbumItemClickListener {

    private ImageView mSearchBackBtn;
    private EditText mSearchEdit;
    private String TAG = "SearchActivity";
    private SearchPresenter mSearchPresenter;
    private FlowTextLayout mFlowTextLayout;
    private TextView mDoSearchBtn;
    private FrameLayout mResultContainer;
    private UILoader mUILoader;
    private RecyclerView mResultListView;
    private AlbumListAdapter mAlbumListAdapter;
    private InputMethodManager mInputMethodManager;
    private ImageView mSearchEditClear;
    private static final int TIME_SHOW_IMM = 200;
    private RecyclerView mSearchRecommendList;
    private SearchRecommendAdepter mRecommendAdepter;
    private TwinklingRefreshLayout mRefreshLayout;
    private boolean mNeedSuggestWord = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initView();
        initEvent();
        initPresenter();
    }

    private void initPresenter() {
        mInputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        //注册UI更新的接口
        mSearchPresenter = SearchPresenter.getSearchPresenter();
        mSearchPresenter.registerViewCallback(this);
        //拿热词
        mSearchPresenter.getHotWords();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消UI更新的接口
        if (mSearchPresenter != null) {
            mSearchPresenter.unRegisterViewCallback(this);
            mSearchPresenter = null;
        }
    }

    private void initEvent() {

        mAlbumListAdapter.setOnAlbumItemClickListener(this);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                LogUtils.d(TAG, "LoadMore");
                super.onLoadMore(refreshLayout);
                if (mSearchPresenter != null) {
                    mSearchPresenter.loadMore();
                }
            }
        });
        if (mRecommendAdepter != null) {
            mRecommendAdepter.setItemClickListener(new SearchRecommendAdepter.ItemClickListener() {
                @Override
                public void onItemClick(String keyWord) {
                    //推荐热词的点击
                    //不需要进行联想
                    mNeedSuggestWord = false;
                    //执行搜索动作
                    switch2Search(keyWord);

                }
            });
        }

        mSearchEditClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchEdit.setText(null);
            }
        });
        mUILoader.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                if (mSearchPresenter != null) {
                    mSearchPresenter.reSearch();
                    mUILoader.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        });
        mSearchBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDoSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行搜索
                String keyWord = mSearchEdit.getText().toString().trim();
                if (TextUtils.isEmpty(keyWord)) {
                    //搜索内容为空
                    Toast.makeText(SearchActivity.this,"请输入搜索内容",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mSearchPresenter != null) {
                    mSearchPresenter.doSearch(keyWord);
                    mUILoader.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        });

        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.e(TAG, "CharSequence is --> " + s);
                if (TextUtils.isEmpty(s)) {
                    mSearchPresenter.getHotWords();
                    mSearchEditClear.setVisibility(View.GONE);
                } else {
                    mSearchEditClear.setVisibility(View.VISIBLE);
                    if (mNeedSuggestWord) {
                        //触发联想查询
                        getSuggestWord(s.toString());
                    }else {
                        mNeedSuggestWord = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                //不需要进行联想
                mNeedSuggestWord = false;
                switch2Search(text);
            }
        });
    }

    private void switch2Search(String text) {
        if (TextUtils.isEmpty(text)) {
            //搜索内容为空
            Toast.makeText(SearchActivity.this,"请输入搜索内容",Toast.LENGTH_SHORT).show();
            return;
        }
        mSearchEdit.setText(text);
        mSearchEdit.setSelection(text.length());
        //用热词搜索
        if (mSearchPresenter != null) {
            mSearchPresenter.doSearch(text);
        }
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.LOADING);
        }
    }

    /**
     * 获取联想的关键词
     *
     * @param keyWord
     */
    private void getSuggestWord(String keyWord) {
        if (mSearchPresenter != null) {
            mSearchPresenter.getRecommendWord(keyWord);
        }

    }

    private void initView() {
        mSearchBackBtn = this.findViewById(R.id.search_back);
        mSearchEdit = this.findViewById(R.id.search_edit);
        mSearchEditClear = this.findViewById(R.id.search_edit_clear);
        mSearchEditClear.setVisibility(View.GONE);
        mSearchEdit.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSearchEdit.requestFocus();
                mInputMethodManager.showSoftInput(mSearchEdit, InputMethodManager.SHOW_IMPLICIT);
            }
        }, TIME_SHOW_IMM);
        mDoSearchBtn = this.findViewById(R.id.do_search_btn);
        mResultContainer = this.findViewById(R.id.search_container);
        if (mUILoader == null) {
            mUILoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }
            };
            if (mUILoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
            }
            mResultContainer.addView(mUILoader);
        }

    }

    /**
     * c创建数据请求成功的view
     *
     * @return
     */
    private View createSuccessView() {
        View resultView = LayoutInflater.from(this).inflate(R.layout.search_result_layout, null);
        //刷新列表
        mRefreshLayout = resultView.findViewById(R.id.search_result_refresh_Layout);
        mRefreshLayout.setEnableRefresh(false);

        mResultListView = resultView.findViewById(R.id.result_list_view);
        //显示热词的View
        mFlowTextLayout = resultView.findViewById(R.id.hot_word_view);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mResultListView.setLayoutManager(layoutManager);
        mAlbumListAdapter = new AlbumListAdapter();
        mResultListView.setAdapter(mAlbumListAdapter);
        mResultListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 4);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 4);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        //搜索推荐
        mSearchRecommendList = resultView.findViewById(R.id.search_recommend_list);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mSearchRecommendList.setLayoutManager(linearLayoutManager);
//        mSearchRecommendList.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                outRect.top = UIUtil.dip2px(view.getContext(), 2);
//                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
//                outRect.left = UIUtil.dip2px(view.getContext(), 5);
//                outRect.right = UIUtil.dip2px(view.getContext(), 5);
//            }
//        });
        //设置适配器
        mRecommendAdepter = new SearchRecommendAdepter();
        mSearchRecommendList.setAdapter(mRecommendAdepter);

        return resultView;
    }

    @Override
    public void onSearchResultLoad(List<Album> result) {
        handeSearchResult(result);
        //搜索有数据回来隐藏键盘
        mInputMethodManager.hideSoftInputFromWindow(mSearchEdit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void handeSearchResult(List<Album> result) {
        hideSuccessView();
        mResultListView.setVisibility(View.VISIBLE);
        if (result != null) {
            if (result.size() == 0) {
                if (mUILoader != null) {
                    mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
                }
            } else {
                //有数据就设置显示
                mAlbumListAdapter.setDeta(result);
                mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }
    }

    @Override
    public void onHotWordLoad(List<HotWord> hotWordList) {
        hideSuccessView();
        mFlowTextLayout.setVisibility(View.VISIBLE);
        mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        List<String> hotWords = new ArrayList<>();
        hotWords.clear();
        for (HotWord hotWord : hotWordList) {
            String searchWord = hotWord.getSearchword();
            hotWords.add(searchWord);
        }
        //更新UI
        mFlowTextLayout.setTextContents(hotWords);
    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {
        //处理加载更多的结果
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
        }
        if (isOkay) {
            handeSearchResult(result);
        } else {
            Toast.makeText(SearchActivity.this, "没有更多内容", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWordList) {
        //关键字的联想词
        if (mRecommendAdepter != null) {
            mRecommendAdepter.setData(keyWordList);
        }
        //TODO：控制UI的状态隐藏和显示
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        hideSuccessView();
        mSearchRecommendList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    private void hideSuccessView() {
        mSearchRecommendList.setVisibility(View.GONE);
        mResultListView.setVisibility(View.GONE);
        mFlowTextLayout.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        //item被点击了
        Intent intent = new Intent();
        intent.setClass(this, DetailActivity.class);
        startActivity(intent);
    }
}
