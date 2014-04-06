package ch.hgdev.toposuite.calculation.activities.axisimpl;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import ch.hgdev.toposuite.App;
import ch.hgdev.toposuite.R;
import ch.hgdev.toposuite.SharedResources;
import ch.hgdev.toposuite.TopoSuiteActivity;
import ch.hgdev.toposuite.calculation.Abriss;
import ch.hgdev.toposuite.calculation.AxisImplantation;
import ch.hgdev.toposuite.calculation.Calculation;
import ch.hgdev.toposuite.calculation.CalculationType;
import ch.hgdev.toposuite.calculation.FreeStation;
import ch.hgdev.toposuite.calculation.activities.orthoimpl.OrthogonalImplantationActivity;
import ch.hgdev.toposuite.history.HistoryActivity;
import ch.hgdev.toposuite.points.Point;
import ch.hgdev.toposuite.utils.DisplayUtils;
import ch.hgdev.toposuite.utils.MathUtils;
import ch.hgdev.toposuite.utils.ViewUtils;

public class AxisImplantationActivity extends TopoSuiteActivity {
    private static final String        AXIS_IMPL_POSITION          = "axis_impl_position";
    private static final String        STATION_SELECTED_POSITION   = "station_selected_position";
    private static final String        ORIGIN_SELECTED_POSITION    = "origin_selected_position";
    private static final String        EXTREMITY_SELECTED_POSITION = "extremity_selected_position";

    private CheckBox                   checkboxZ0;

    private Spinner                    stationSpinner;
    private Spinner                    originSpinner;
    private Spinner                    extremitySpinner;

    private TextView                   calculatedDistanceTextView;
    private TextView                   extremityTextView;
    private TextView                   originTextView;
    private TextView                   stationTextView;

    private EditText                   unknownOrientationEditText;

    private ListView                   measuresListView;

    private AxisImplantation           axisImpl;

    private ArrayListOfMeasuresAdapter adapter;
    private ArrayAdapter<Point>        pointsAdapter;

    private int                        stationSelectedPosition;
    private int                        originSelectedPosition;
    private int                        extremitySelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_axis_implantation);

        this.checkboxZ0 = (CheckBox) this.findViewById(R.id.checkbox_z0);
        this.stationSpinner = (Spinner) this.findViewById(R.id.station_spinner);
        this.originSpinner = (Spinner) this.findViewById(R.id.origin_spinner);
        this.extremitySpinner = (Spinner) this.findViewById(R.id.extremity_spinner);
        this.calculatedDistanceTextView = (TextView) this.findViewById(R.id.calculated_distance);
        this.extremityTextView = (TextView) this.findViewById(R.id.extremity_point);
        this.originTextView = (TextView) this.findViewById(R.id.origin_point);
        this.stationTextView = (TextView) this.findViewById(R.id.station_point);
        this.measuresListView = (ListView) this.findViewById(R.id.measures_list);
        this.unknownOrientationEditText = (EditText) this.findViewById(R.id.unknown_orientation);

        this.unknownOrientationEditText.setInputType(App.getInputTypeCoordinate());

        this.stationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                AxisImplantationActivity.this.stationSelectedPosition = pos;

                Point pt = (Point) AxisImplantationActivity.this.stationSpinner
                        .getItemAtPosition(pos);
                if (!pt.getNumber().isEmpty()) {
                    AxisImplantationActivity.this.stationTextView.setText
                            (DisplayUtils.formatPoint(AxisImplantationActivity.this, pt));
                } else {
                    AxisImplantationActivity.this.stationTextView.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // nothing
            }
        });

        this.originSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                AxisImplantationActivity.this.originSelectedPosition = pos;

                Point pt = (Point) AxisImplantationActivity.this.originSpinner
                        .getItemAtPosition(pos);
                if (!pt.getNumber().isEmpty()) {
                    AxisImplantationActivity.this.originTextView.setText
                            (DisplayUtils.formatPoint(AxisImplantationActivity.this, pt));
                } else {
                    AxisImplantationActivity.this.originTextView.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // nothing
            }
        });

        this.extremitySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                AxisImplantationActivity.this.extremitySelectedPosition = pos;

                Point pt = (Point) AxisImplantationActivity.this.extremitySpinner
                        .getItemAtPosition(pos);
                if (!pt.getNumber().isEmpty()) {
                    AxisImplantationActivity.this.extremityTextView.setText
                            (DisplayUtils.formatPoint(AxisImplantationActivity.this, pt));
                } else {
                    AxisImplantationActivity.this.extremityTextView.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // nothing
            }
        });

        Bundle bundle = this.getIntent().getExtras();
        if ((bundle != null)) {
            int position = bundle.getInt(HistoryActivity.CALCULATION_POSITION);
            this.axisImpl = (AxisImplantation) SharedResources
                    .getCalculationsHistory().get(position);
        } else {
            this.axisImpl = new AxisImplantation(null, 0.0, null, null, true);
        }

        this.drawList();

        this.registerForContextMenu(this.measuresListView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<Point> points = new ArrayList<Point>();
        points.add(new Point("", 0.0, 0.0, 0.0, true));
        points.addAll(SharedResources.getSetOfPoints());

        this.pointsAdapter = new ArrayAdapter<Point>(
                this, R.layout.spinner_list_item, points);
        this.stationSpinner.setAdapter(this.pointsAdapter);
        this.originSpinner.setAdapter(this.pointsAdapter);
        this.extremitySpinner.setAdapter(this.pointsAdapter);

        if (this.stationSelectedPosition > 0) {
            this.stationSpinner.setSelection(
                    this.stationSelectedPosition);
        }

        if (this.originSelectedPosition > 0) {
            this.originSpinner.setSelection(
                    this.originSelectedPosition);
        }

        if (this.extremitySelectedPosition > 0) {
            this.extremitySpinner.setSelection(
                    this.extremitySelectedPosition);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.axis_implementation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected String getActivityTitle() {
        return this.getString(R.string.title_activity_axis_implantation);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(AxisImplantationActivity.ORIGIN_SELECTED_POSITION,
                this.stationSelectedPosition);
        outState.putInt(AxisImplantationActivity.ORIGIN_SELECTED_POSITION,
                this.originSelectedPosition);
        outState.putInt(AxisImplantationActivity.EXTREMITY_SELECTED_POSITION,
                this.extremitySelectedPosition);

        int index = SharedResources.getCalculationsHistory().indexOf(this.axisImpl);
        outState.putInt(AxisImplantationActivity.AXIS_IMPL_POSITION, index);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            int index = savedInstanceState.getInt(
                    OrthogonalImplantationActivity.ORTHO_IMPL_POSITION);
            this.adapter.clear();

            this.axisImpl = (AxisImplantation) SharedResources
                    .getCalculationsHistory().get(index);

            this.drawList();

            this.stationSelectedPosition = savedInstanceState
                    .getInt(AxisImplantationActivity.STATION_SELECTED_POSITION);
            this.originSelectedPosition = savedInstanceState
                    .getInt(AxisImplantationActivity.ORIGIN_SELECTED_POSITION);
            this.extremitySelectedPosition = savedInstanceState
                    .getInt(AxisImplantationActivity.EXTREMITY_SELECTED_POSITION);
        }
    }

    /**
     * Callback for R.id.checkbox_z0
     */
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
        case R.id.checkbox_z0:
            if (checked) {
                this.fetchLastFreeStationOrAbriss();
                if (MathUtils.isIgnorable(this.axisImpl.getUnknownOrientation())) {
                    ViewUtils.showToast(this,
                            this.getString(R.string.error_no_suitable_calculation_found));
                } else {
                    this.unknownOrientationEditText.setText(DisplayUtils
                            .toStringForEditText(this.axisImpl.getUnknownOrientation()));
                    this.unknownOrientationEditText.setEnabled(false);
                    this.stationSpinner.setSelection(
                            this.pointsAdapter.getPosition(this.axisImpl.getStation()));
                    this.stationSpinner.setEnabled(false);
                }
            } else {
                this.unknownOrientationEditText.setText("");
                this.unknownOrientationEditText.setEnabled(true);
                this.stationSpinner.setSelection(0);
                this.stationSpinner.setEnabled(true);
            }
            break;
        }
    }

    private void drawList() {
        this.adapter = new ArrayListOfMeasuresAdapter(
                this, R.layout.axis_implantation_list_item,
                this.axisImpl.getMeasures());
        this.measuresListView.setAdapter(this.adapter);
    }

    /**
     * FIXME put this method in a util class of in the super class!
     */
    private void fetchLastFreeStationOrAbriss() {
        for (Calculation c : SharedResources.getCalculationsHistory()) {
            if ((c != null) && (c.getType() == CalculationType.ABRISS)) {
                Abriss a = (Abriss) c;
                a.compute();
                /*this.axisImpl.setUnknownOrientation(a.getMean());
                this.axisImpl.setStation(a.getStation());
                this.axisImpl.setZ0CalculationId(c.getId());*/
                break;
            }
            if ((c != null) && (c.getType() == CalculationType.FREESTATION)) {
                FreeStation fs = (FreeStation) c;
                fs.compute();
                /*this.axisImpl.setUnknownOrientation(fs.getUnknownOrientation());
                this.axisImpl.setStation(fs.getStationResult());
                this.axisImpl.setZ0CalculationId(c.getId());*/
                break;
            }
        }
    }
}
