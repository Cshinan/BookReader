package example.tctctc.com.tybookreader.bookshelf.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import example.tctctc.com.tybookreader.R;
import example.tctctc.com.tybookreader.base.BaseAdapter;
import example.tctctc.com.tybookreader.base.BasePageFragment;
import example.tctctc.com.tybookreader.bean.Chapter;
import example.tctctc.com.tybookreader.bookshelf.common.ContentManager;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import vite.rxbus.RxThread;
import vite.rxbus.Subscribe;
import vite.rxbus.ThreadType;

/**
 * Created by tctctc on 2017/4/7.
 * Function:
 */

public class DirectoryListFragment extends BasePageFragment {

    private List<Chapter> mChapterList;

    private ContentManager mManager;

    @BindView(R.id.directory_recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty)
    TextView mEmpty;

    private BaseAdapter<Chapter> adapter;

    private int mCurrentPosition;
    private int mLastPosition;
    private LinearLayoutManager manager;

    @Override
    protected void initView() {
//        setLazyLoad(false);
        mRxManager.registerBus(this);
        mManager = ContentManager.getInstance();
        mChapterList = new ArrayList<>();
        manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new BaseAdapter<>(getContext(), R.layout.directory_item, mChapterList, new BaseAdapter.OnItem() {
            @Override
            public void onClick(int position) {
                Chapter chapter = mChapterList.get(position);
                mRxManager.post("chapter", chapter);
                mRxManager.post("closeDrawer", 1);
            }

            @Override
            public void onLongClick(int position) {
            }

            @Override
            public void onBind(BaseAdapter.BaseViewHolder mHolder, int position) {
                Chapter chapter = mChapterList.get(position);
                if (position == mCurrentPosition) {
                    mHolder.setText(R.id.chapterName, chapter.getName(), getResources().getColor(R.color.tab_text_color));
                } else {
                    mHolder.setText(R.id.chapterName, chapter.getName(), getResources().getColor(R.color.black));
                }
            }
        });
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(manager);

//        mRxManager.onEvent("Directory", new Consumer<Integer>() {
//
//
//            @Override
//            public void accept(@NonNull Integer result) throws Exception {
//                if (result == 1) {
//                    judgeLoadData(true);
//                    mEmpty.setVisibility(View.INVISIBLE);
//                    mRecyclerView.setVisibility(View.VISIBLE);
//                } else if (result == 2) {
//                    //无法提取目录
//                    mEmpty.setVisibility(View.VISIBLE);
//                    mRecyclerView.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
//        mRxManager.onEvent("updateCurrentChapter", new Consumer<Integer>() {
//            @Override
//            public void accept(@NonNull Integer result) {
//                mLastPosition = mCurrentPosition;
//                mCurrentPosition = result;
//                adapter.notifyItemChanged(mLastPosition);
//                adapter.notifyItemChanged(mCurrentPosition);
//
//                mRecyclerView.scrollToPosition(mCurrentPosition);
//            }
//        });
    }

    @Subscribe("Directory")
    @RxThread(ThreadType.MainThread)
    public void directoryChange(Integer result){
        if (result == 1) {
            judgeLoadData(true);
            mEmpty.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else if (result == 2) {
            //无法提取目录
            mEmpty.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    @Subscribe("updateCurrentChapter")
    @RxThread(ThreadType.MainThread)
    public void updateCurrentChapter(Integer result){
        mLastPosition = mCurrentPosition;
        mCurrentPosition = result;
        adapter.notifyItemChanged(mLastPosition);
        adapter.notifyItemChanged(mCurrentPosition);

        mRecyclerView.scrollToPosition(mCurrentPosition);
    }

    @Override
    public int getLayoutId() {
        return R.layout.directory_list_fragment;
    }

    @Override
    public boolean fetchData() {
        mChapterList.clear();
        mChapterList.addAll(mManager.getDirectory());
        adapter.notifyDataSetChanged();

        if (mChapterList.isEmpty()) {
            mEmpty.setVisibility(View.VISIBLE);
        } else {
            mEmpty.setVisibility(View.GONE);
        }
        return true;
    }
}
