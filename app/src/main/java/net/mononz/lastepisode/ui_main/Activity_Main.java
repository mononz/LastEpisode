package net.mononz.lastepisode.ui_main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.mononz.lastepisode.ui_misc.Fragment_Empty;
import net.mononz.lastepisode.R;
import net.mononz.lastepisode.ui_detail.Fragment_Detail;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Activity_Main extends AppCompatActivity {

    @Bind(R.id.toolbar) protected Toolbar toolbar;

    public static final long DETAIL_EMPTY = -2;
    public static final long DETAIL_NEW = -1;

    public boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_item_list);
        ButterKnife.bind(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_list_container, new Fragment_Main())
                    .commit();
            if (mTwoPane) {
                openDetail(DETAIL_EMPTY);
            }
        }
    }

    public void openDetail(long _id) {
        if (mTwoPane && _id == DETAIL_EMPTY) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, new Fragment_Empty())
                    .commit();
        } else if (mTwoPane) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, Fragment_Detail.newInstance(_id))
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_list_container, Fragment_Detail.newInstance(_id))
                    .addToBackStack("detail")
                    .commit();
        }
    }
}
