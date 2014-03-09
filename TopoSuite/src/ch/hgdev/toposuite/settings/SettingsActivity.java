package ch.hgdev.toposuite.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import ch.hgdev.toposuite.R;
import ch.hgdev.toposuite.TopoSuiteActivity;

/**
 * Activity that provides access to the application settings as well as
 * information about the application.
 * 
 * @author HGdev
 * 
 */
public class SettingsActivity extends TopoSuiteActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_settings);

        // Display the settings fragment as the main content.
        this.getFragmentManager().beginTransaction()
                .add(R.id.layout, new SettingsFragment())
                .commit();
    }

    @Override
    protected String getActivityTitle() {
        return this.getString(R.string.title_activity_settings);
    }

    /**
     * Main settings fragment that is shown when accessing the settings
     * activity. It provides links to about and general settings fragment.
     * 
     * @author HGdev
     * 
     */
    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.addPreferencesFromResource(R.xml.preferences);

            Preference aboutPref = this.findPreference("screen_about_toposuite");
            aboutPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SettingsFragment.this.startAboutActivity();
                    return true;
                }
            });
        }

        /**
         * Start the {@link AboutActivity}.
         */
        private void startAboutActivity() {
            Intent aboutActivityIntent = new Intent(
                    this.getActivity(), AboutActivity.class);
            this.getActivity().startActivity(aboutActivityIntent);
        }
    }
}
