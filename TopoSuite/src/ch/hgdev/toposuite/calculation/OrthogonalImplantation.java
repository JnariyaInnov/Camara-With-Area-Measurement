package ch.hgdev.toposuite.calculation;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.hgdev.toposuite.SharedResources;
import ch.hgdev.toposuite.calculation.activities.orthoimpl.OrthogonalImplantationActivity;
import ch.hgdev.toposuite.points.Point;
import ch.hgdev.toposuite.utils.MathUtils;

public class OrthogonalImplantation extends Calculation {
    public static final String                       CALCULATION_NAME = "Implant. Ortho.";
    public static final String                       ORTHOGONAL_BASE  = "orthogonal_base";
    public static final String                       MEASURES         = "measures";
    public static final String                       POINT_NUMBER     = "point_number";

    private OrthogonalBase                           orthogonalBase;
    private ArrayList<Point>                         measures;
    private ArrayList<OrthogonalImplantation.Result> results;

    public OrthogonalImplantation(Point origin, Point extremity, boolean hasDAO) {
        super(CalculationType.ORTHOIMPL, OrthogonalImplantation.CALCULATION_NAME, hasDAO);

        this.orthogonalBase = new OrthogonalBase(origin, extremity);
        this.measures = new ArrayList<Point>();
        this.results = new ArrayList<OrthogonalImplantation.Result>();

        if (hasDAO) {
            SharedResources.getCalculationsHistory().add(0, this);
        }
    }

    public OrthogonalImplantation(boolean hasDAO) {
        super(CalculationType.ORTHOIMPL, OrthogonalImplantation.CALCULATION_NAME, hasDAO);

        this.orthogonalBase = new OrthogonalBase();
        this.measures = new ArrayList<Point>();
        this.results = new ArrayList<OrthogonalImplantation.Result>();

        if (hasDAO) {
            SharedResources.getCalculationsHistory().add(0, this);
        }
    }

    public OrthogonalImplantation(long id, Date lastModification) {
        super(id, CalculationType.ORTHOIMPL,
                OrthogonalImplantation.CALCULATION_NAME, lastModification, true);

        this.orthogonalBase = new OrthogonalBase();
        this.measures = new ArrayList<Point>();
        this.results = new ArrayList<OrthogonalImplantation.Result>();
    }

    public void compute() {
        if (this.measures.size() == 0) {
            return;
        }

        this.results.clear();

        double gisBase = new Gisement(this.orthogonalBase.getOrigin(),
                this.orthogonalBase.getExtemity(), false).getGisement();

        for (Point p : this.measures) {
            PointProjectionOnALine ppoal = new PointProjectionOnALine(
                    4242,
                    this.orthogonalBase.getOrigin(),
                    this.orthogonalBase.getExtemity(),
                    p,
                    0.0,
                    false);
            ppoal.compute();

            Point projPt = ppoal.getProjPt();
            double abscissa = MathUtils.euclideanDistance(this.orthogonalBase.getOrigin(),
                    projPt);
            double ordinate = MathUtils.euclideanDistance(p, projPt);

            double angle = MathUtils.angle3Pts(this.orthogonalBase.getExtemity(),
                    this.orthogonalBase.getOrigin(), p);

            abscissa = ((angle > 100) && (angle < 300)) ? -abscissa : abscissa;
            ordinate = (angle > 200) ? -ordinate : ordinate;

            this.results.add(new Result(p, abscissa, ordinate));
        }

        this.updateLastModification();
        this.notifyUpdate(this);
    }

    @Override
    public String exportToJSON() throws JSONException {
        JSONObject json = new JSONObject();

        if (this.orthogonalBase != null) {
            json.put(OrthogonalImplantation.ORTHOGONAL_BASE,
                    this.orthogonalBase.toJSONObject());
        }

        if (this.measures.size() > 0) {
            JSONArray measuresArray = new JSONArray();
            for (Point p : this.measures) {
                measuresArray.put(p.getNumber());
            }

            json.put(OrthogonalImplantation.MEASURES, measuresArray);
        }

        return json.toString();
    }

    @Override
    public void importFromJSON(String jsonInputArgs) throws JSONException {
        JSONObject json = new JSONObject(jsonInputArgs);

        OrthogonalBase ob = OrthogonalBase.getOrthogonalBaseFromJSON(
                ((JSONObject) json.get(OrthogonalImplantation.ORTHOGONAL_BASE)).toString());
        this.orthogonalBase = ob;

        JSONArray measuresArray = json.getJSONArray(OrthogonalImplantation.MEASURES);

        for (int i = 0; i < measuresArray.length(); i++) {
            Integer pos = (Integer) measuresArray.get(i);
            Point p = SharedResources.getSetOfPoints().find((int) pos);
            this.measures.add(p);
        }
    }

    @Override
    public Class<?> getActivityClass() {
        return OrthogonalImplantationActivity.class;
    }

    public OrthogonalBase getOrthogonalBase() {
        return this.orthogonalBase;
    }

    public void setOrthogonalBase(OrthogonalBase orthogonalBase) {
        this.orthogonalBase = orthogonalBase;
    }

    public ArrayList<Point> getMeasures() {
        return this.measures;
    }

    public void setMeasures(ArrayList<Point> _measures) {
        this.measures = _measures;
    }

    public ArrayList<OrthogonalImplantation.Result> getResults() {
        return this.results;
    }

    public void setResults(ArrayList<OrthogonalImplantation.Result> _results) {
        this.results = _results;
    }

    public static class Result {
        private Point  point;
        private double abscissa;
        private double ordinate;

        public Result(Point _point, double _abscissa, double _ordinate) {
            this.point = _point;
            this.abscissa = _abscissa;
            this.ordinate = _ordinate;
        }

        public Point getPoint() {
            return this.point;
        }

        public void setPoint(Point _point) {
            this.point = _point;
        }

        public double getAbscissa() {
            return this.abscissa;
        }

        public void setAbscissa(double abscissa) {
            this.abscissa = abscissa;
        }

        public double getOrdinate() {
            return this.ordinate;
        }

        public void setOrdinate(double ordinate) {
            this.ordinate = ordinate;
        }
    }
}