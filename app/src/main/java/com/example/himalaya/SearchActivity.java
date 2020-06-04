package com.example.himalaya;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.adepters.AlbumListAdapter;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.ISearchCallback;
import com.example.himalaya.presenters.SearchPresenter;
import com.example.himalaya.views.FlowTextLayout;
import com.example.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity implements ISearchCallback {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initView();
        initEvent();
        initPresenter();
    }

    private void initPresenter() {
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
        mUILoader.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                if (mSearchPresenter != null) {
                    mSearchPresenter.reSearch();
                    mUILoader.updateStatus(UILoader.UIStatus.LAODING);
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
                if (mSearchPresenter != null) {
                    mSearchPresenter.doSearch(keyWord);
                    mUILoader.updateStatus(UILoader.UIStatus.LAODING);
                }
            }
        });

        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e(TAG, "CharSequence is --> " + s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                Toast.makeText(SearchActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        mSearchBackBtn = this.findViewById(R.id.search_back);
        mSearchEdit = this.findViewById(R.id.search_edit);
        mDoSearchBtn = this.findViewById(R.id.do_search_btn);
        mResultContainer = this.findViewById(R.id.search_container);
        mFlowTextLayout = this.findViewById(R.id.flow_text_layout);
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
        mResultListView = resultView.findViewById(R.id.result_list_view);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mResultListView.setLayoutManager(layoutManager);
        mAlbumListAdapter = new AlbumListAdapter();
        mResultListView.setAdapter(mAlbumListAdapter);
        return resultView;
    }

    @Override
    public void onSearchResultLoad(List<Album> result) {
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

    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWordList) {

    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }
}
