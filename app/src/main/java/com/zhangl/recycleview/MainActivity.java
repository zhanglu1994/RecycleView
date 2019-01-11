package com.zhangl.recycleview;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;


interface RequestCallBack {
    void success(List<UserBean> data);

    void fail(Exception e);
}

class Request extends Thread {
    private static final int PAGE_SIZE = 6;
    private int mPage;
    private RequestCallBack mCallBack;
    private Handler mHandler;

    private static boolean mFirstPageNoMore;
    private static boolean mFirstError = true;

    public Request(int page, RequestCallBack callBack) {
        mPage = page;
        mCallBack = callBack;
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void run() {
        try {Thread.sleep(500);} catch (InterruptedException e) {}

        if (mPage == 2 && mFirstError) {
            mFirstError = false;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallBack.fail(new RuntimeException("fail"));
                }
            });
        } else {
            int size = PAGE_SIZE;
            if (mPage == 1) {

                if (!mFirstError) {
                    mFirstError = true;
                }
            } else if (mPage == 4) {
                size = 1;
            }

            final int dataSize = size;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallBack.success(MainActivity.getData(dataSize));
                }
            });
        }
    }
}
public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private XXRecycleAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static final int PAGE_SIZE = 6;

    private int PAGE_NUMBER = 1;



    public static List<UserBean> getData(int dataSize) {
        List<UserBean> datas = new ArrayList<>();
        UserBean userBean;
        for (int i = 0; i < dataSize; i++) {
            userBean = new UserBean();
            userBean.setName("我是第" + i + "条标题");
            datas.add(userBean);
        }
        return datas;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.mian_rv);
        mSwipeRefreshLayout = findViewById(R.id.mSwipeRefreshLayout);

        mAdapter = new XXRecycleAdapter(R.layout.item_rv, null);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        }, mRecyclerView);

        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setLoadMoreView(new RecycleLoadingView());
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        mRecyclerView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(MainActivity.this, position + "被点击了", Toast.LENGTH_SHORT).show();
            }
        });

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {


                switch (view.getId()) {
                    case R.id.main_iv:

                        Toast.makeText(MainActivity.this, "iv position:" + position, Toast.LENGTH_SHORT).show();

                        break;

                    case R.id.name_tv:

                        Toast.makeText(MainActivity.this, "tv position:" + position, Toast.LENGTH_SHORT).show();

                        break;

                    default:

                        break;
                }


            }
        });

        initRefreshLayout();
        refresh();



    }


    private void initRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);
    }

    private void refresh() {
        PAGE_NUMBER = 1;
        mAdapter.setEnableLoadMore(false);          //这里的作用是防止下拉刷新的时候还可以上拉加载
        new Request(PAGE_NUMBER, new RequestCallBack() {
            @Override
            public void success(List<UserBean> data) {
                setData(true, data);
                mAdapter.setEnableLoadMore(true);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void fail(Exception e) {
                Toast.makeText(MainActivity.this, "cuowuxinxi", Toast.LENGTH_LONG).show();
                mAdapter.setEnableLoadMore(true);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }).start();
    }


    private void loadMore() {
        new Request(PAGE_NUMBER, new RequestCallBack() {
            @Override
            public void success(List<UserBean> data) {

                boolean isRefresh = PAGE_NUMBER == 1;
                setData(isRefresh, data);
            }

            @Override
            public void fail(Exception e) {
                mAdapter.loadMoreFail();
                Toast.makeText(MainActivity.this, "错误信息", Toast.LENGTH_LONG).show();
            }
        }).start();
    }

    private void setData(boolean isRefresh, List data) {
        PAGE_NUMBER++;
        final int size = data == null ? 0 : data.size();
        if (isRefresh) {
            mAdapter.setNewData(data);
        } else {
            if (size > 0) {
                mAdapter.addData(data);
            }
        }
        if (size < PAGE_SIZE) {
            //第一页如果不够一页就不显示没有更多数据布局
            mAdapter.loadMoreEnd(isRefresh);
            Toast.makeText(this, "no more data", Toast.LENGTH_SHORT).show();
        } else {
            mAdapter.loadMoreComplete();
        }
    }







}
