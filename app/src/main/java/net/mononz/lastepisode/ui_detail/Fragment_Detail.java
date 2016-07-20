package net.mononz.lastepisode.ui_detail;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.mononz.lastepisode.LastEpisode;
import net.mononz.lastepisode.R;
import net.mononz.lastepisode.library.Constants;
import net.mononz.lastepisode.database.Database;
import net.mononz.lastepisode.ui_main.Activity_Main;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Fragment_Detail extends Fragment {

    @Bind(R.id.name) protected EditText name;
    @Bind(R.id.season_text) protected TextView season_text;
    @Bind(R.id.episode_text) protected TextView episode_text;
    @Bind(R.id.end) protected CheckBox end;
    @Bind(R.id.last_updated) protected TextView last_updated;
    @Bind(R.id.last_updated_text) protected TextView last_updated_text;

    public static final String EXTRA_NAME = "show_id";

    private static final String KEY_ID = "KEY_ID";
    private static final String KEY_NAME = "KEY_NAME";
    private static final String KEY_SEASON = "KEY_SEASON";
    private static final String KEY_EPISODE = "KEY_EPISODE";
    private static final String KEY_ACTIVE = "KEY_ACTIVE";
    private static final String KEY_UPDATED = "KEY_UPDATED";

    private long id = -1;
    private int season = 1;
    private int episode = 1;
    private long updated = 1;

    public static Fragment_Detail newInstance(long position) {
        Fragment_Detail fragment = new Fragment_Detail();
        Bundle args = new Bundle();
        args.putLong(EXTRA_NAME, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);
        if (getArguments().containsKey(EXTRA_NAME)) {
            id = getArguments().getLong(EXTRA_NAME);
        }
        setHasOptionsMenu(true);

        if (savedInstanceState != null && id > -1) {
            name.setText(savedInstanceState.getString(KEY_NAME));
            season = savedInstanceState.getInt(KEY_SEASON);
            episode = savedInstanceState.getInt(KEY_EPISODE);
            updated = savedInstanceState.getLong(KEY_UPDATED);
            last_updated_text.setText(Constants.formattedTimestamp(updated));
            end.setChecked(savedInstanceState.getInt(KEY_ACTIVE) == 0);

        } else if (id > -1) {
            Cursor cursor = getActivity().getContentResolver().query(ContentUris.withAppendedId(Database.shows.CONTENT_URI, id), null, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst() && cursor.getCount() > 0) {
                    int idx_id = cursor.getColumnIndex(Database.shows.id);
                    int idx_name = cursor.getColumnIndex(Database.shows.name);
                    int idx_season = cursor.getColumnIndex(Database.shows.season);
                    int idx_episode = cursor.getColumnIndex(Database.shows.episode);
                    int idx_active = cursor.getColumnIndex(Database.shows.active);
                    int idx_timestamp = cursor.getColumnIndex(Database.shows.timestamp);

                    id = cursor.getLong(idx_id);
                    name.setText(cursor.getString(idx_name));
                    season = cursor.getInt(idx_season);
                    episode = cursor.getInt(idx_episode);
                    updated = cursor.getLong(idx_timestamp);
                    last_updated_text.setText(Constants.formattedTimestamp(updated));
                    end.setChecked(cursor.getInt(idx_active) == 0);
                }
                cursor.close();
            }
        } else {
            last_updated.setVisibility(View.GONE);
            last_updated_text.setVisibility(View.GONE);
        }
        setSeason();
        setEpisode();

        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_ID, id);
        outState.putString(KEY_NAME, name.getText().toString());
        outState.putInt(KEY_SEASON, season);
        outState.putInt(KEY_EPISODE, episode);
        outState.putInt(KEY_ACTIVE, (end.isChecked()) ? 0 : 1);
        outState.putLong(KEY_UPDATED, updated);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideKeyboard();
        if (item.getItemId() == R.id.trash && id > Activity_Main.DETAIL_NEW) {
            getActivity().getContentResolver().delete(Database.shows.CONTENT_URI, Database.shows.id + "=?", new String[]{"" + id});
            Toast.makeText(getActivity(), getString(R.string.deleted) + " " + name.getText().toString(), Toast.LENGTH_SHORT).show();
            LastEpisode.sendAction("Deleted");
            returnToMain();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSeason() {
        hideKeyboard();
        season_text.setText(Constants.makeDoubleDigit(season));
    }

    private void setEpisode() {
        hideKeyboard();
        episode_text.setText(Constants.makeDoubleDigit(episode));
    }


    @OnClick(R.id.season_minus)
    protected void season_minus() {
        if (season > 1) {
            season--;
            episode = 1;
        }
        setSeason();
        setEpisode();
    }

    @OnClick(R.id.season_plus)
    protected void season_plus() {
        if (season < Constants.MAX_SEASONS) {
            season++;
            episode = 1;
        }
        setSeason();
        setEpisode();
    }

    @OnClick(R.id.episode_minus)
    protected void episode_minus() {
        if (episode > 1)
            episode--;
        setEpisode();
    }

    @OnClick(R.id.episode_plus)
    protected void episode_plus() {
        if (episode < Constants.MAX_EPISODES)
            episode++;
        setEpisode();;
    }

    @OnClick(R.id.fab)
    protected void fab() {
        save();
    }

    private void save() {
        hideKeyboard();
        if (name.getText().toString().equals("")) {
            Toast.makeText(getActivity(), getString(R.string.toast_show_name), Toast.LENGTH_SHORT).show();
        } else {
            if (id == Activity_Main.DETAIL_NEW) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Database.shows.name, name.getText().toString());
                contentValues.put(Database.shows.season, season);
                contentValues.put(Database.shows.episode, episode);
                contentValues.put(Database.shows.notes, "");
                contentValues.put(Database.shows.rating, 0);
                contentValues.put(Database.shows.active, (end.isChecked()) ? 0 : 1);
                contentValues.put(Database.shows.timestamp, Constants.getSystemTime());
                Uri uri = getActivity().getContentResolver().insert(Database.shows.CONTENT_URI, contentValues);
                id = ContentUris.parseId(uri);
                if (id > 0) {
                    Toast.makeText(getActivity(), getString(R.string.now_tracking) + " " + name.getText().toString(), Toast.LENGTH_SHORT).show();
                    LastEpisode.sendAction("Create");
                    returnToMain();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_insert), Toast.LENGTH_SHORT).show();
                    LastEpisode.sendError("Create");
                }
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Database.shows.name, name.getText().toString());
                contentValues.put(Database.shows.season, season);
                contentValues.put(Database.shows.episode, episode);
                contentValues.put(Database.shows.notes, "");
                contentValues.put(Database.shows.rating, 0);
                contentValues.put(Database.shows.active, (end.isChecked()) ? 0 : 1);
                contentValues.put(Database.shows.timestamp, Constants.getSystemTime());
                long num = getActivity().getContentResolver().update(Database.shows.CONTENT_URI, contentValues, Database.shows.id + "=?", new String[]{"" + id});

                if (num > 0) {
                    Toast.makeText(getActivity(), getString(R.string.updated) + " " + name.getText().toString(), Toast.LENGTH_SHORT).show();
                    LastEpisode.sendAction("Update");
                    returnToMain();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_update), Toast.LENGTH_SHORT).show();
                    LastEpisode.sendError("Update");
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LastEpisode.sendScreen("Detail");
    }

    private void returnToMain() {
        if (((Activity_Main) getActivity()).mTwoPane) {
            ((Activity_Main) getActivity()).openDetail(Activity_Main.DETAIL_EMPTY);
        } else {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void hideKeyboard() {
        name.clearFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(name.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}