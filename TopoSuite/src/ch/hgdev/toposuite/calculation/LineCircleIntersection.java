package ch.hgdev.toposuite.calculation;

import java.util.Date;

import org.json.JSONException;

import android.util.Log;
import ch.hgdev.toposuite.App;
import ch.hgdev.toposuite.R;
import ch.hgdev.toposuite.SharedResources;
import ch.hgdev.toposuite.calculation.activities.linecircleintersection.LineCircleIntersectionActivity;
import ch.hgdev.toposuite.points.Point;
import ch.hgdev.toposuite.utils.DisplayUtils;
import ch.hgdev.toposuite.utils.Logger;
import ch.hgdev.toposuite.utils.MathUtils;

import com.google.common.base.Preconditions;

public class LineCircleIntersection extends Calculation {
    private static final String LINE_CIRCLE_INTERSECTION = "Line-Circle intersection: ";

    private Point               p1L;
    private Point               p2L;
    private double              displacementL;

    private Point               centerC;
    private double              radiusC;

    /**
     * Point on the first intersection.
     */
    private Point               firstIntersection;
    /**
     * Point on the second intersection (if relevant).
     */
    private Point               secondIntersection;

    public LineCircleIntersection(long id, Date lastModification) {
        super(id,
                CalculationType.LINECIRCINTERSEC,
                App.getContext().getString(R.string.title_activity_line_circle_intersection),
                lastModification,
                true);
    }

    public LineCircleIntersection(Point _p1L, Point _p2L, double _displacementL, double _gisement,
            Point _centerC, double _radiusC,
            boolean hasDAO) throws IllegalArgumentException {
        super(CalculationType.LINECIRCINTERSEC,
                App.getContext().getString(R.string.title_activity_line_circle_intersection),
                hasDAO);

        this.initAttributes(_p1L, _p2L, _displacementL, _gisement, _centerC, _radiusC);

        if (hasDAO) {
            SharedResources.getCalculationsHistory().add(0, this);
        }
    }

    public LineCircleIntersection(Point _p1L, Point _p2L, double _displacementL,
            Point _centerC, double _radiusC) {
        this(_p1L, _p2L, _displacementL, 0.0, _centerC, _radiusC, false);
    }

    public LineCircleIntersection(Point _p1L, double _displacementL, double _gisement,
            Point _centerC, double _radiusC) {
        this(_p1L, null, _displacementL, _gisement, _centerC, _radiusC, false);
    }

    public LineCircleIntersection() {
        super(CalculationType.LINECIRCINTERSEC,
                App.getContext().getString(R.string.title_activity_line_circle_intersection),
                true);

        this.p1L = new Point(0, 0.0, 0.0, 0.0, false, false);
        this.p2L = new Point(0, 0.0, 0.0, 0.0, false, false);
        this.displacementL = 0.0;

        this.centerC = new Point(0, 0.0, 0.0, 0.0, false, false);
        this.radiusC = 0.0;

        this.firstIntersection = new Point(0, 0.0, 0.0, 0.0, false, false);
        this.secondIntersection = new Point(0, 0.0, 0.0, 0.0, false, false);

        SharedResources.getCalculationsHistory().add(0, this);
    }

    /**
     * Initialize class attributes.
     * 
     * @param _p1L
     *            First point on the line. Must NOT be null.
     * @param _p2L
     *            Second point on the line. May be null.
     * @param _displacementL
     *            Displacement.
     * @param _gisement
     *            Gisement. Not used of _p2L is not null.
     * @param _centerC
     *            Center of the circle.
     * @param _radiusC
     *            Radius of the circle.
     * @throws IllegalArgumentException
     */
    public void initAttributes(Point _p1L, Point _p2L, double _displacementL, double _gisement,
            Point _centerC, double _radiusC) throws IllegalArgumentException {
        Preconditions.checkNotNull(_p1L, "The first point must not be null");

        this.p1L = _p1L;
        if (_p2L == null) {
            this.p2L = new Point(
                    0,
                    MathUtils.pointLanceEast(_p1L.getEast(), _gisement, 100),
                    MathUtils.pointLanceNorth(_p1L.getNorth(), _gisement, 100.0),
                    0.0,
                    false);
        } else {
            this.p2L = _p2L;
        }
        this.displacementL = _displacementL;

        this.centerC = _centerC;
        this.radiusC = _radiusC;

        this.firstIntersection = new Point(0, 0.0, 0.0, 0.0, false, false);
        this.secondIntersection = new Point(0, 0.0, 0.0, 0.0, false, false);
    }

    @Override
    public void compute() {
        Point p1LClone = this.p1L.clone();
        Point p2LClone = this.p2L.clone();

        if (!MathUtils.isZero(this.displacementL)) {
            double displGis = new Gisement(p1LClone, p2LClone, false).getGisement();
            displGis += MathUtils.isNegative(this.displacementL) ? -100 : 100;
            p1LClone.setEast(MathUtils.pointLanceEast(
                    p1LClone.getEast(), displGis, Math.abs(this.displacementL)));
            p1LClone.setNorth(MathUtils.pointLanceNorth(
                    p1LClone.getNorth(), displGis, Math.abs(this.displacementL)));
            p2LClone.setEast(MathUtils.pointLanceEast(
                    p2LClone.getEast(), displGis, Math.abs(this.displacementL)));
            p2LClone.setNorth(MathUtils.pointLanceNorth(
                    p2LClone.getNorth(), displGis, Math.abs(this.displacementL)));
        }

        double alpha = new Gisement(p1LClone, p2LClone, false).getGisement() -
                new Gisement(p1LClone, this.centerC, false).getGisement();

        double minRadius = MathUtils.euclideanDistance(
                p1LClone, this.centerC) * Math.sin(MathUtils.gradToRad(alpha));
        double proj = minRadius / this.radiusC;
        double beta = 0.0;

        // check that the circle crosses the line
        if (MathUtils.isPositive((-proj * proj) + 1)) {
            beta = MathUtils.radToGrad(Math.atan(proj / Math.sqrt((-proj * proj) + 1)));
        } else {
            Log.w(Logger.TOPOSUITE_CALCULATION_IMPOSSIBLE,
                    LINE_CIRCLE_INTERSECTION
                            + "No line-circle crossing. The radius should be longer than "
                            + DisplayUtils.toString(minRadius)
                            + " (" + DisplayUtils.toString(this.radiusC) + " given).");
            this.setZeros();
            return;
        }

        double stPtIntersecGis1 = new Gisement(p1LClone, p2LClone, false).getGisement();
        double stPtIntersecGis2 = stPtIntersecGis1;
        double distAP1, distAP2;

        // center of the circle on first point of the line
        if (MathUtils.equals(this.centerC.getEast(), p1LClone.getEast())
                && MathUtils.equals(this.centerC.getNorth(), p1LClone.getNorth())) {
            distAP1 = this.radiusC;
            distAP2 = this.radiusC;
            stPtIntersecGis2 = stPtIntersecGis2 - 200;

            // center of the circle on the second point of the line
        } else if (MathUtils.equals(this.centerC.getEast(), p2LClone.getEast())
                && MathUtils.equals(this.centerC.getNorth(), p2LClone.getNorth())) {
            distAP1 = MathUtils.euclideanDistance(p1LClone, this.centerC) + this.radiusC;
            distAP2 = MathUtils.euclideanDistance(p2LClone, p1LClone) - this.radiusC;

            // center of the circle aligned with the two points of the line
        } else if (MathUtils.isZero(Math.sin(MathUtils.gradToRad(alpha)))) {
            double dist = MathUtils.euclideanDistance(p1LClone, this.centerC);
            distAP1 = dist + this.radiusC;
            distAP2 = dist - this.radiusC;

            // center of the circle elsewhere
        } else {
            distAP1 = (this.radiusC * Math.sin(MathUtils.gradToRad(200 - alpha - beta)))
                    / Math.sin(MathUtils.gradToRad(alpha));
            distAP2 = distAP1 + ((this.radiusC * Math.sin(MathUtils.gradToRad((2 * beta) - 200)))
                    / Math.sin(MathUtils.gradToRad(200 - beta)));
        }

        this.firstIntersection.setEast(
                MathUtils.pointLanceEast(p1LClone.getEast(), stPtIntersecGis1, distAP1));
        this.firstIntersection.setNorth(
                MathUtils.pointLanceNorth(p1LClone.getNorth(), stPtIntersecGis1, distAP1));
        this.secondIntersection.setEast(
                MathUtils.pointLanceEast(p1LClone.getEast(), stPtIntersecGis2, distAP2));
        this.secondIntersection.setNorth(
                MathUtils.pointLanceNorth(p1LClone.getNorth(), stPtIntersecGis2, distAP2));

        this.updateLastModification();
        this.notifyUpdate(this);
    }

    /**
     * Set resulting points coordinate to 0.0. This usually indicates that there
     * was an error.
     */
    private void setZeros() {
        this.firstIntersection.setEast(0.0);
        this.firstIntersection.setNorth(0.0);
        this.secondIntersection.setEast(0.0);
        this.secondIntersection.setNorth(0.0);
    }

    @Override
    public String exportToJSON() throws JSONException {
        // TODO implement
        return null;
    }

    @Override
    public void importFromJSON(String jsonInputArgs) throws JSONException {
        // TODO implement
    }

    @Override
    public Class<?> getActivityClass() {
        return LineCircleIntersectionActivity.class;
    }

    public Point getFirstIntersection() {
        return this.firstIntersection;
    }

    public Point getSecondIntersection() {
        return this.secondIntersection;
    }
}
