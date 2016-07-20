package net.mononz.lastepisode.ui_main;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.mononz.lastepisode.LastEpisode;
import net.mononz.lastepisode.R;
import net.mononz.lastepisode.ui_settings.SettingsActivity;
import net.mononz.lastepisode.database.Database;
import net.mononz.lastepisode.library.DividerItemDecoration;
import net.mononz.lastepisode.adapters.Adapter_Shows;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Fragment_Main extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.recyclerview) protected RecyclerView recList;

    private static final int CURSOR_LOADER_ID = 1;
    private Adapter_Shows mAdapterHeroes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, rootView);

        mAdapterHeroes = new Adapter_Shows(getActivity(), null, new Adapter_Shows.Callback() {
            @Override
            public void openHero(long _id) {
                ((Activity_Main) getActivity()).openDetail(_id);
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setHasFixedSize(true);
        recList.setLayoutManager(llm);
        recList.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.divider));
        recList.setAdapter(mAdapterHeroes);

        setHasOptionsMenu(true);
        return rootView;
    }

    @OnClick(R.id.fab)
    protected void fab() {
        ((Activity_Main) getActivity()).openDetail(-1);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(CURSOR_LOADER_ID, new Bundle(), this);
        LastEpisode.sendScreen("Main");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), Database.shows.CONTENT_URI,
                new String[]{Database.shows.id + " AS " + Database.shows._id, Database.shows.name, Database.shows.season, Database.shows.episode, Database.shows.active },
                null, null, "lower(" + Database.shows.name + ") ASC");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapterHeroes.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapterHeroes.swapCursor(null);
    }

}