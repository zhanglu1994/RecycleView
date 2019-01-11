package com.zhangl.recycleview;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

public class SampleActivity extends AppCompatActivity {


    private RecyclerView mRecyclerView;

    private XXRecycleAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static final int PAGE_SIZE = 10;

    private int pageNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        initRecycleView();


    }

    private void initRecycleView() {
        mRecyclerView = findViewById(R.id.mRecyclerView);
        mSwipeRefreshLayout = findViewById(R.id.mSwipeRefreshLayout);


        mAdapter = new XXRecycleAdapter(R.layout.item_rv, null);                        // 实例化Adapter，传入item的layout文件

        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {     //设置上拉加载更多监听
            @Override
            public void onLoadMoreRequested() {
                loadMore();     //加载更多
            }
        }, mRecyclerView);


        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));  //设置下拉控件颜色
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));              //设置recycleview方向
        mAdapter.setLoadMoreView(new RecycleLoadingView());                                 //设置底部load视图
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);                          //设置recycleview item进入动画
        mRecyclerView.setAdapter(mAdapter);                                                 //recycleview设置adapter

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {        //每条item的点击事件
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {      //item子view的点击事件
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {

                    default:
                        break;
                }


            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {   //设置下拉刷新监听事件
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);                                                //初次进入时设置刷新
        refresh();                                                                              //刷新
    }

    /**
     * 将网络上获取到的数据  显示到recycleView上
     * @param isRefresh         刷新/第一次 传入数据
     * @param data              网络上下载下来的数据 转成 list<bean>
     */
    private void setData(boolean isRefresh, List data) {
        pageNumber++;
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


    /**
     *  刷新recycleView
     */
    private void refresh() {
        pageNumber = 1;
        mAdapter.setEnableLoadMore(false);          //这里的作用是防止下拉刷新的时候还可以上拉加载




        //   P层操作  将pageNumber 传过去请求数据

        new Request(pageNumber, new RequestCallBack() {
            @Override
            public void success(List<UserBean> data) {          //请求成功
                setData(true, data);
                mAdapter.setEnableLoadMore(true);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void fail(Exception e) {                     //请求失败

                mAdapter.setEnableLoadMore(true);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }).start();

    }


    /**
     *  加载更多
     */
    private void loadMore() {

        //   P层操作  将pageNumber 传过去请求数据

        new Request(pageNumber, new RequestCallBack() {
            @Override
            public void success(List<UserBean> data) {          //请求成功

                boolean isRefresh = pageNumber == 1;
                setData(isRefresh, data);
            }

            @Override
            public void fail(Exception e) {                     //请求失败
                mAdapter.loadMoreFail();
            }
        }).start();

    }




}
