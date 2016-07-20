package net.mononz.lastepisode.ui_settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import net.mononz.lastepisode.R;
import net.mononz.lastepisode.database.Database;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        Preference pref_reset = findPreference(getString(R.string.preference_reset));
        pref_reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle(getString(R.string.pref_reset));
                alertDialogBuilder
                        .setMessage(getString(R.string.pref_sum_reset_dialog))
                        .setCancelable(true)
                        .setNeutralButton(getString(R.string.alert_cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(getString(R.string.alert_proceed), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().getContentResolver().delete(Database.shows.CONTENT_URI, null, null);
                                Toast.makeText(getActivity(), getString(R.string.reset_database), Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });

        Preference pref_email = findPreference(getString(R.string.preference_email));
        pref_email.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.pref_sum_email)});
                email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                email.putExtra(Intent.EXTRA_TEXT, "");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, getString(R.string.settings_email_choose)));
                return true;
            }
        });
    }

}