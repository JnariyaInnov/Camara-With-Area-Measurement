package ch.hgdev.toposuite.calculation.activities.abriss;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import ch.hgdev.toposuite.R;
import ch.hgdev.toposuite.SharedResources;
import ch.hgdev.toposuite.TopoSuiteActivity;
import ch.hgdev.toposuite.calculation.Abriss;
import ch.hgdev.toposuite.calculation.Measure;
import ch.hgdev.toposuite.history.HistoryActivity;
import ch.hgdev.toposuite.points.Point;
import ch.hgdev.toposuite.utils.DisplayUtils;

public class AbrissActivity extends TopoSuiteActivity implements
        AddOrientationDialogFragment.AddOrientationDialogListener {
    private static final String   STATION_SELECTED_POSITION = "station_selected_position";

    private TextView              stationPoint;

    private Spinner               stationSpinner;

    private ListView              orientationsListView;

    private int                   stationSelectedPosition;

    private Abriss                abriss;

    private ArrayAdapter<Measure> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_abriss);

        this.stationSpinner = (Spinner) this.findViewById(R.id.station_spinner);
        this.orientationsListView = (ListView) this.findViewById(R.id.orientations_list);
        this.stationPoint = (TextView) this.findViewById(R.id.station_point);

        this.stationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                AbrissActivity.this.stationSelectedPosition = pos;

                Point pt = (Point) AbrissActivity.this.stationSpinner.getItemAtPosition(pos);
                if (pt.getNumber() > 0) {
                    AbrissActivity.this.stationPoint.setText(DisplayUtils.formatPoint(
                            AbrissActivity.this, pt));
                } else {
                    AbrissActivity.this.stationPoint.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // actually nothing
            }
        });

        this.adapter = new ArrayListOfOrientationsAdapter(
                this, R.layout.orientations_list_item, new ArrayList<Measure>());
        this.drawList();

        // check if we create a new abriss calculation or if we modify an
        // existing one.
        Bundle bundle = this.getIntent().getExtras();
        if ((bundle != null)) {
            int position = bundle.getInt(HistoryActivity.CALCULATION_POSITION);
            this.abriss = (Abriss) SharedResources.getCalculationsHistory().get(position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        List<Point> points = new ArrayList<Point>();
        points.add(new Point(0, 0.0, 0.0, 0.0, true));
        points.addAll(SharedResources.getSetOfPoints());

        ArrayAdapter<Point> a = new ArrayAdapter<Point>(
                this, R.layout.spinner_list_item, points);
        this.stationSpinner.setAdapter(a);

        if (this.abriss != null) {
            this.stationSpinner.setSelection(
                    a.getPosition(this.abriss.getStation()));
        } else {
            if (this.stationSelectedPosition > 0) {
                this.stationSpinner.setSelection(
                        this.stationSelectedPosition);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.abriss, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(AbrissActivity.STATION_SELECTED_POSITION,
                this.stationSelectedPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            this.stationSelectedPosition = savedInstanceState.getInt(
                    AbrissActivity.STATION_SELECTED_POSITION);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
        case R.id.add_orientation_button:
            this.showAddOrientationDialog();
            return true;
        case R.id.run_calculation_button:
            Point station = (Point) this.stationSpinner.getSelectedItem();

            if (station.getNumber() == 0) {
                Toast.makeText(this, R.string.error_no_station_selected, Toast.LENGTH_LONG)
                        .show();
                return true;
            }

            if (this.orientationsListView.getChildCount() == 0) {
                Toast.makeText(this, R.string.error_at_least_one_orientation, Toast.LENGTH_LONG)
                        .show();
                return true;
            }

            this.abriss = new Abriss(station);

            for (int i = 0; i < this.adapter.getCount(); i++) {
                this.abriss.getMeasures().clear();
                this.abriss.getMeasures().add(this.adapter.getItem(i));
            }

            this.abriss.compute();

            Intent resultsActivityIntent = new Intent(this, AbrissResultsActivity.class);
            this.startActivity(resultsActivityIntent);

            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Display a dialog to allow the user to insert a new orientation.
     */
    private void showAddOrientationDialog() {
        AddOrientationDialogFragment dialog = new AddOrientationDialogFragment();
        dialog.show(this.getFragmentManager(), "AddOrientationDialogFragment");
    }

    /**
     * Draw the main table containing all the orientations.
     */
    private void drawList() {
        this.orientationsListView.setAdapter(this.adapter);
    }

    @Override
    public void onDialogAdd(AddOrientationDialogFragment dialog) {
        this.adapter.add(new Measure(
                dialog.getOrientation(),
                dialog.getHorizontalDirection(),
                100.0,
                dialog.getHorizontalDistance(),
                dialog.getAltitude()));
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogCancel(AddOrientationDialogFragment dialog) {
        // DO NOTHING
    }
}
