package com.wingedvampires.videoplay.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewTreeObserver;

import com.example.playerlibrary.assist.AssistPlayer;
import com.example.playerlibrary.entity.DataSource;
import com.example.playerlibrary.log.PLog;
import com.example.playerlibrary.provider.VideoBean;
import com.wingedvampires.videoplay.extension.PUtil;

/**
 * Created by Taurus on 2018/4/15.
 */

public class ListPlayLogic {

    private Context mContext;
    private RecyclerView mRecycler;
    private VideoListAdapter mAdapter;

    private int mScreenH;

    private int mPlayPosition = -1;
    private int mVerticalRecyclerStart;

    public ListPlayLogic(Context context, RecyclerView recycler, VideoListAdapter adapter) {
        this.mContext = context;
        this.mRecycler = recycler;
        this.mAdapter = adapter;
        init();
    }

    private void init() {
        mScreenH = PUtil.getScreenH(mRecycler.getContext());
        mRecycler.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int[] location = new int[2];
                mRecycler.getLocationOnScreen(location);
                mVerticalRecyclerStart = location[1];
                mRecycler.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int itemVisibleRectHeight = getItemVisibleRectHeight(mPlayPosition);
                    if (itemVisibleRectHeight <= 0) {
                        PLog.d("ListPlayLogic", "onScrollStateChanged stop");
                        AssistPlayer.get().stop();
                        mAdapter.notifyItemChanged(mPlayPosition);
                        mPlayPosition = -1;
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
    }

    public void updatePlayPosition(int position) {
        notifyPrePosition();
        this.mPlayPosition = position;
    }

    private void notifyPrePosition() {
        if (mPlayPosition >= 0)
            mAdapter.notifyItemChanged(mPlayPosition);
    }

    public void attachPlay() {
        mRecycler.post(new Runnable() {
            @Override
            public void run() {
                VideoListAdapter.VideoItemHolder itemHolder = getItemHolder(mPlayPosition);
                if (itemHolder != null) {
                    PLog.d("ListPlayLogic", "attachPlay...");
                    AssistPlayer.get().play(itemHolder.layoutContainer, null);
                }
            }
        });
    }

    public void playPosition(int position) {
        VideoBean item = getItem(position);
        DataSource dataSource = new DataSource(item.getPath());
        dataSource.setTitle(item.getDisplayName());
        VideoListAdapter.VideoItemHolder holder = getItemHolder(position);
        if (holder != null) {
            PLog.d("ListPlayLogic", "playPosition : position = " + position);
            AssistPlayer.get().play(holder.layoutContainer, dataSource);
        }
    }

    private VideoBean getItem(int position) {
        return mAdapter.getItem(position);
    }

    private VideoListAdapter.VideoItemHolder getItemHolder(int position) {
        RecyclerView.ViewHolder viewHolder = mRecycler.findViewHolderForLayoutPosition(position);
        if (viewHolder != null && viewHolder instanceof VideoListAdapter.VideoItemHolder) {
            return ((VideoListAdapter.VideoItemHolder) viewHolder);
        }
        return null;
    }

    /**
     * 获取Item中渲染视图的可见高度
     *
     * @param position
     * @return
     */
    private int getItemVisibleRectHeight(int position) {
        VideoListAdapter.VideoItemHolder itemHolder = getItemHolder(position);
        if (itemHolder == null)
            return 0;
        int[] location = new int[2];
        itemHolder.layoutBox.getLocationOnScreen(location);
        int height = itemHolder.layoutBox.getHeight();

        int visibleRect;
        if (location[1] <= mVerticalRecyclerStart) {
            visibleRect = location[1] - mVerticalRecyclerStart + height;
        } else {
            if (location[1] + height >= mScreenH) {
                visibleRect = mScreenH - location[1];
            } else {
                visibleRect = height;
            }
        }
        return visibleRect;
    }

    /**
     * 获取两个索引条目中渲染视图可见高度最大的条目
     *
     * @param position1
     * @param position2
     * @return
     */
    private int getVisibleRectMaxPosition(int position1, int position2) {
        VideoListAdapter.VideoItemHolder itemHolder1 = getItemHolder(position1);
        VideoListAdapter.VideoItemHolder itemHolder2 = getItemHolder(position2);
        if (itemHolder1 == null && itemHolder2 == null) {
            return RecyclerView.NO_POSITION;
        }
        if (itemHolder1 == null) {
            return position2;
        }
        if (itemHolder2 == null) {
            return position1;
        }
        int visibleRect1 = getItemVisibleRectHeight(position1);
        int visibleRect2 = getItemVisibleRectHeight(position2);
        return visibleRect1 >= visibleRect2 ? position1 : position2;
    }

    /**
     * 判断给定的索引条目，渲染视图的可见高度是否满足播放条件.
     *
     * @param position
     * @return
     */
    private boolean isVisibleRectAvailablePlay(int position) {
        VideoListAdapter.VideoItemHolder itemHolder = getItemHolder(position);
        if (itemHolder == null)
            return false;
        int height = itemHolder.layoutBox.getHeight();
        return getItemVisibleRectHeight(position) > (height / 2);
    }

    private boolean isCompleteVisibleRect(int position) {
        VideoListAdapter.VideoItemHolder itemHolder = getItemHolder(position);
        if (itemHolder == null)
            return false;
        int height = itemHolder.layoutBox.getHeight();
        return getItemVisibleRectHeight(position) == height;
    }


}
