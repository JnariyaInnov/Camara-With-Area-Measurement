package ch.hgdev.toposuite.calculation.activities.polarimplantation;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import ch.hgdev.toposuite.App;
import ch.hgdev.toposuite.R;
import ch.hgdev.toposuite.SharedResources;
import ch.hgdev.toposuite.TopoSuiteActivity;
import ch.hgdev.toposuite.calculation.Abriss;
import ch.hgdev.toposuite.calculation.Calculation;
import ch.hgdev.toposuite.calculation.CalculationType;
import ch.hgdev.toposuite.calculation.Measure;
import ch.hgdev.toposuite.calculation.PolarImplantation;
import ch.hgdev.toposuite.history.HistoryActivity;
import ch.hgdev.toposuite.points.Point;
import ch.hgdev.toposuite.utils.DisplayUtils;
import ch.hgdev.toposuite.utils.Logger;
import ch.hgdev.toposuite.utils.MathUtils;

public class PolarImplantationActivity extends TopoSuiteActivity implements
        AddPointWithSDialogFragment.AddPointWithSDialogListener,
        EditPointWithSDialogFragment.EditPointWithSDialogListener {

    public static final String    STATION_NUMBER_LABEL       = "station_number";
    public static final String    POINTS_WITH_S_NUMBER_LABEL = "points_with_s_number";
    public static final String    POINTS_WITH_S_LABEL        = "points_with_s";

    private static final String   STATION_SELECTED_POSITION  = "station_selected_position";
    public static final String    S                          = "s";
    private Spinner               stationSpinner;
    private int                   stationSelectedPosition;
    private ArrayAdapter<Point>   stationAdapter;
    private TextView              stationPointTextView;
    private EditText              iEditText;
    private EditText              unknownOrientEditText;
    private ListView              pointsListView;
    private ArrayAdapter<Measure> adapter;

    private Point                 station;
    private PolarImplantation     polarImplantation;

    private double                abrissZ0;
    private Point                 abrissStation;

    /**
     * Position of the calculation in the calculations list. Only used when open
     * from the history.
     */
    private int                   position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_polar_implantation);

        this.position = -1;
        this.abrissZ0 = 0.0;

        this.stationSpinner = (Spinner) this.findViewById(R.id.station_spinner);
        this.stationPointTextView = (TextView) this.findViewById(R.id.station_point);
        this.unknownOrientEditText = (EditText) this.findViewById(R.id.unknown_orientation);
        this.iEditText = (EditText) this.findViewById(R.id.i);
        this.pointsListView = (ListView) this.findViewById(R.id.list_of_points);

        this.iEditText.setInputType(App.INPUTTYPE_TYPE_NUMBER_COORDINATE);
        this.unknownOrientEditText.setInputType(App.INPUTTYPE_TYPE_NUMBER_COORDINATE);

        this.stationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                PolarImplantationActivity.this.stationSelectedPosition = pos;

                PolarImplantationActivity.this.station = (Point) PolarImplantationActivity.this.stationSpinner
                        .getItemAtPosition(pos);
                if (PolarImplantationActivity.this.station.getNumber() > 0) {
                    PolarImplantationActivity.this.stationPointTextView.setText(DisplayUtils
                            .formatPoint(PolarImplantationActivity.this,
                                    PolarImplantationActivity.this.station));
                } else {
                    PolarImplantationActivity.this.stationPointTextView.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // actually nothing
            }
        });

        ArrayList<Measure> list = new ArrayList<Measure>();

        // check if we create a new polar implantation calculation or if we
        // modify an
        // existing one.
        Bundle bundle = this.getIntent().getExtras();
        if ((bundle != null)) {
            this.position = bundle.getInt(HistoryActivity.CALCULATION_POSITION);
            this.polarImplantation = (PolarImplantation) SharedResources.getCalculationsHistory()
                    .get(
                            this.position);
            list = this.polarImplantation.getMeasures();
        }

        this.adapter = new ArrayListOfPointsWithSAdapter(this,
                R.layout.points_with_s_list_item, list);

        this.drawList();

        this.registerForContextMenu(this.pointsListView);
    }

    @Override
    public void onResume() {
        super.onResume();

        List<Point> points = new ArrayList<Point>();
        points.add(new Point(0, 0.0, 0.0, 0.0, false));
        points.addAll(SharedResources.getSetOfPoints());

        this.stationAdapter = new ArrayAdapter<Point>(
                this, R.layout.spinner_list_item, points);
        this.stationSpinner.setAdapter(this.stationAdapter);

        for (Calculation c : SharedResources.getCalculationsHistory()) {
            if ((c != null) && (c.getType() != CalculationType.ABRISS)) {
                continue;
            }
            Abriss a = (Abriss) c;
            a.compute();
            this.abrissZ0 = a.getMean();
            this.abrissStation = a.getStation();
            break;
        }

        if (this.polarImplantation != null) {
            this.stationSpinner.setSelection(
                    this.stationAdapter.getPosition(this.polarImplantation.getStation()));
            Measure m = this.polarImplantation.getMeasures().get(0);

            this.iEditText.setText(DisplayUtils.toString(m.getI()));
            this.unknownOrientEditText.setText(DisplayUtils.toString(m.getUnknownOrientation()));
        } else {
            if (this.stationSelectedPosition > 0) {
                this.stationSpinner.setSelection(
                        this.stationSelectedPosition);
            }
        }
    }

    @Override
    protected String getActivityTitle() {
        return this.getString(R.string.title_activity_polar_implantation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.polar_implantation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(PolarImplantationActivity.STATION_SELECTED_POSITION,
                this.stationSelectedPosition);

        JSONArray json = new JSONArray();
        for (int i = 0; i < this.adapter.getCount(); i++) {
            json.put(this.adapter.getItem(i).toJSONObject());
        }

        outState.putString(PolarImplantationActivity.POINTS_WITH_S_LABEL, json.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            this.adapter.clear();
            this.stationSelectedPosition = savedInstanceState.getInt(
                    PolarImplantationActivity.STATION_SELECTED_POSITION);
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(
                        savedInstanceState
                                .getString(PolarImplantationActivity.POINTS_WITH_S_LABEL));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = (JSONObject) jsonArray.get(i);
                    Measure m = Measure.getMeasureFromJSON(json.toString());
                    this.adapter.add(m);
                }
            } catch (JSONException e) {
                Log.e(Logger.TOPOSUITE_PARSE_ERROR,
                        "PolarImplantationActivity: cannot restore saved instance.");
            }
            this.drawList();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
        case R.id.add_point_button:
            this.showAddPointDialog();
            return true;
        case R.id.run_calculation_button:
            if (this.checkInputs()) {
                this.showPolarImplantationResultActivity();
            } else {
                Toast errorToast = Toast.makeText(this, this.getText(R.string.error_fill_data),
                        Toast.LENGTH_SHORT);
                errorToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                errorToast.show();
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.polar_implant_points_list_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
        case R.id.edit_point_with_s:
            this.showEditPointWithSDialog(info.position);
            return true;
        case R.id.delete_point_with_s:
            this.adapter.remove(this.adapter.getItem(info.position));
            this.adapter.notifyDataSetChanged();
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
        case R.id.checkbox_z0:
            if (checked) {
                if (MathUtils.isZero(this.abrissZ0)) {
                    Toast errorToast = Toast.makeText(this,
                            this.getText(R.string.error_no_abriss_calculation_found),
                            Toast.LENGTH_SHORT);
                    errorToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    errorToast.show();
                } else {
                    this.unknownOrientEditText.setText(DisplayUtils.toString(this.abrissZ0));
                    this.unknownOrientEditText.setEnabled(false);
                    this.stationSpinner.setSelection(
                            this.stationAdapter.getPosition(this.abrissStation));
                    this.stationSpinner.setEnabled(false);
                }
            } else {
                this.unknownOrientEditText.setText("");
                this.unknownOrientEditText.setEnabled(true);
                this.stationSpinner.setSelection(0);
                this.stationSpinner.setEnabled(true);
            }
            break;
        }
    }

    /**
     * Show a dialog to add a new point, with optional S.
     */
    private void showAddPointDialog() {
        AddPointWithSDialogFragment dialog = new AddPointWithSDialogFragment();
        dialog.show(this.getFragmentManager(), "AddPointWithSDialogFragment");
    }

    /**
     * 
     * @param position
     *            Position of the point with S to edit.
     */
    private void showEditPointWithSDialog(int position) {
        EditPointWithSDialogFragment dialog = new EditPointWithSDialogFragment();

        this.position = position;
        Measure m = this.adapter.getItem(position);
        Bundle args = new Bundle();
        args.putInt(PolarImplantationActivity.POINTS_WITH_S_NUMBER_LABEL, m.getPoint().getNumber());
        args.putDouble(PolarImplantationActivity.S, m.getS());

        dialog.setArguments(args);
        dialog.show(this.getFragmentManager(), "EditPointWithSDialogFragment");
    }

    /**
     * Draw the list of points.
     */
    private void drawList() {
        this.pointsListView.setAdapter(this.adapter);
    }

    /**
     * Check that the I field, the unknown orientation field have been filled
     * and that the station has been chosen.
     * 
     * @return True if inputs are OK, false otherwise.
     */
    private boolean checkInputs() {
        if ((this.station == null) || (this.station.getNumber() < 1)) {
            return false;
        }
        if (this.unknownOrientEditText.length() == 0) {
            return false;
        }
        if (this.adapter.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Start the activity that shows the results of the calculation.
     */
    private void showPolarImplantationResultActivity() {
        double i = 0.0;
        double unknownOrient;

        if (this.iEditText.length() > 0) {
            i = Double.parseDouble(this.iEditText.getText().toString());
        }
        if (this.unknownOrientEditText.length() > 0) {
            unknownOrient = Double.parseDouble(this.unknownOrientEditText.getText().toString());
        } else {
            Toast errorToast = Toast.makeText(this,
                    this.getText(R.string.error_choose_unknown_orientation),
                    Toast.LENGTH_SHORT);
            errorToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            errorToast.show();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putInt(PolarImplantationActivity.STATION_NUMBER_LABEL, this.station.getNumber());

        JSONArray json = new JSONArray();
        for (int j = 0; j < this.adapter.getCount(); j++) {
            Measure m = this.adapter.getItem(j);
            m.setI(i);
            m.setUnknownOrientation(unknownOrient);
            json.put(m.toJSONObject());
        }

        bundle.putString(PolarImplantationActivity.POINTS_WITH_S_LABEL, json.toString());

        Intent resultsActivityIntent = new Intent(this, PolarImplantationResultsActivity.class);
        resultsActivityIntent.putExtras(bundle);
        this.startActivity(resultsActivityIntent);
    }

    @Override
    public void onDialogAdd(AddPointWithSDialogFragment dialog) {

        Measure m = new Measure(
                dialog.getPoint(),
                0.0,
                0.0,
                0.0,
                dialog.getS(),
                0.0,
                0.0,
                0.0,
                0.0);

        this.adapter.add(m);
        this.adapter.notifyDataSetChanged();
        this.showAddPointDialog();
    }

    @Override
    public void onDialogCancel(AddPointWithSDialogFragment dialog) {
        // do nothing actually
    }

    @Override
    public void onDialogEdit(EditPointWithSDialogFragment dialog) {

        this.adapter.remove(this.adapter.getItem(this.position));
        Measure m = new Measure(
                dialog.getPoint(),
                0.0,
                0.0,
                0.0,
                dialog.getS(),
                0.0,
                0.0,
                0.0,
                0.0);

        this.position = -1;
        this.adapter.add(m);
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogCancel(EditPointWithSDialogFragment dialog) {
        // do nothing actually
    }
}
