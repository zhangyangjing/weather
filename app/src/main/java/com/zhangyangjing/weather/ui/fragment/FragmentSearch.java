package com.zhangyangjing.weather.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.zhangyangjing.weather.R;
import com.zhangyangjing.weather.provider.weather.WeatherContract;
import com.zhangyangjing.weather.ui.adapter.AdapterQuerySuggest;
import com.zhangyangjing.weather.util.AnimUtils;
import com.zhangyangjing.weather.util.ImeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhangyangjing on 04/11/16.
 */

public class FragmentSearch extends Fragment {
    private static final String TAG = FragmentSearch.class.getSimpleName();
    private static final boolean DEBUG = true;

    private static final int LOADER_ID = 0;
    private static final String KEY_FILTER = "filter";

    private SearchStatus mSearchStatus;
    private MyLoaderManagerCallback mLoaderManagerCallback;

    private AnimatedVectorDrawable mAnimateAddToBack;
    private AnimatedVectorDrawable mAnimateBackToAdd;

    private int mBtnSearchBackOffsetRight;
    private SearchListener mListener;

    @BindView(R.id.btnSearchback)
    ImageButton mBtnSearchback;

    @BindView(R.id.search_view)
    SearchView mSearchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAnimateAddToBack = (AnimatedVectorDrawable) getContext()
                .getDrawable(R.drawable.animate_add_to_back);
        mAnimateBackToAdd = (AnimatedVectorDrawable) getContext()
                .getDrawable(R.drawable.animate_back_to_add);

        int autoCompleteTextViewID = getResources()
                .getIdentifier("search_src_text", "id", getContext().getPackageName());
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)
                mSearchView.findViewById(autoCompleteTextViewID);
        searchAutoComplete.setThreshold(1);

        mSearchView.setSuggestionsAdapter(new AdapterQuerySuggest(getContext()));
        mSearchView.setOnQueryTextListener(new MyQueryTextListener());

        mSearchStatus = SearchStatus.NORMAL;
        mLoaderManagerCallback = new MyLoaderManagerCallback();

        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mBtnSearchBackOffsetRight = v.getWidth() - mBtnSearchback.getWidth();
                mBtnSearchback.setTranslationX(mBtnSearchBackOffsetRight);
                v.removeOnLayoutChangeListener(this);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchListener) {
            mListener = (SearchListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick(R.id.btnSearchback)
    public void onClick(View v) {
        if (SearchStatus.NORMAL == mSearchStatus) {
            enterSearchMode();
        } else {
            exitSearchMode();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void enterSearchMode() {
        mBtnSearchback.setImageDrawable(mAnimateAddToBack);
        mAnimateAddToBack.reset();
        mAnimateAddToBack.start();

        mBtnSearchback.animate()
                .translationX(0)
                .setDuration(AnimUtils.ANIM_DURATION_LONG)
                .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(getContext()))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ImeUtils.showIme(mSearchView);
                        mSearchView.setQuery("", false);
                        mSearchView.setVisibility(View.VISIBLE);
                        mSearchView.requestFocus();
                    }
                })
                .start();

        getLoaderManager().initLoader(LOADER_ID, null, mLoaderManagerCallback);
        mSearchStatus = SearchStatus.SEARCH;
        mListener.onEnterSearch(mBtnSearchBackOffsetRight, mBtnSearchback.getBottom());
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void exitSearchMode() {
        mBtnSearchback.setImageDrawable(mAnimateBackToAdd);
        mAnimateBackToAdd.reset();
        mAnimateBackToAdd.start();

        mBtnSearchback.animate()
                .translationX(mBtnSearchBackOffsetRight)
                .setDuration(AnimUtils.ANIM_DURATION_MEDIUM)
                .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(getContext()))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        ImeUtils.hideIme(mSearchView);
                        mSearchView.setVisibility(View.GONE);
                    }
                })
                .start();

        getLoaderManager().destroyLoader(LOADER_ID);
        mSearchStatus = SearchStatus.NORMAL;
        mListener.onExitSearch();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode && SearchStatus.SEARCH == mSearchStatus) {
            exitSearchMode();
            return true;
        }
        return false;
    }

    class MyQueryTextListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (DEBUG) Log.d(TAG, "onQueryTextChange() called with: newText = [" + newText + "]");
            Bundle data = new Bundle();
            data.putString(KEY_FILTER, newText);
            getLoaderManager().restartLoader(0, data, mLoaderManagerCallback);
            return false;
        }
    }

    class MyLoaderManagerCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String filter = null == args ? "" : args.getString(KEY_FILTER, "");
            Uri uri = TextUtils.isEmpty(filter) ?
                    WeatherContract.City.CONTENT_URI : WeatherContract.City.buildSearchUri(filter);
            return new CursorLoader(getContext(), uri, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (DEBUG) Log.d(TAG, "onLoadFinished() called with: loader = ["
                    + loader + "], cursor = [" + cursor + "]");
            mSearchView.getSuggestionsAdapter().swapCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader loader) {
            if (DEBUG) Log.d(TAG, "onLoaderReset() called with: loader = [" + loader + "]");
            mSearchView.getSuggestionsAdapter().swapCursor(null);
        }
    }

    public interface SearchListener {
        void onEnterSearch(int x, int y);

        void onExitSearch();

        void onSearchResult();
    }

    enum SearchStatus {
        NORMAL, SEARCH
    }
}
