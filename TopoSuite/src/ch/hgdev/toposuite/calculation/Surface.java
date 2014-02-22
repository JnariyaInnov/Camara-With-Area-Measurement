package ch.hgdev.toposuite.calculation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import ch.hgdev.toposuite.App;
import ch.hgdev.toposuite.R;
import ch.hgdev.toposuite.points.Point;
import ch.hgdev.toposuite.utils.Logger;
import ch.hgdev.toposuite.utils.MathUtils;

public class Surface extends Calculation {
    private static final String                 POINTS_LIST = "points_list";
    private String                              name;
    private String                              description;
    private double                              surface;
    private double                              perimeter;
    private final List<Surface.PointWithRadius> points;

    public Surface(long id, Date lastModification) {
        super(id,
                CalculationType.SURFACE,
                App.getContext().getString(R.string.title_activity_surface),
                lastModification,
                true);
        this.points = new ArrayList<Surface.PointWithRadius>();
    }

    public Surface(String _name, String _description, boolean hasDAO) {
        super(CalculationType.SURFACE,
                App.getContext().getString(R.string.title_activity_surface),
                hasDAO);
        this.name = _name;
        this.description = _description;
        this.points = new ArrayList<Surface.PointWithRadius>();
        this.surface = 0.0;
        this.perimeter = 0.0;
    }

    /**
     * Check input.
     * 
     * @return True if the input is OK and the calculation can be run, false
     *         otherwise.
     */
    private boolean checkInput() {
        // we need at least three points to define a surface
        if (this.points.size() < 3) {
            return false;
        }
        return true;
    }

    @Override
    public void compute() {
        if (!this.checkInput()) {
            return;
        }

        int j;
        int nbVertex = this.points.size();
        // compute polygon
        for (int i = 0; i < nbVertex; i++) {
            // last vertex is also the first to close the surface
            if (i == (nbVertex - 1)) {
                j = 0;
            } else {
                j = i + 1;
            }

            PointWithRadius p1 = this.points.get(i);
            PointWithRadius p2 = this.points.get(j);

            this.surface += (((p2.getEast() - p1.getEast()) * (p2.getNorth() + p1.getNorth()))) / 2;

            // compute circular segment
            double radius = Math.abs(p1.getRadius());
            if (MathUtils.isPositive(radius)) {
                // compute angle at the center
                double alpha = Math.asin(MathUtils.euclideanDistance(p1, p2) / (2 * radius)) * 2;
                // compute circular segment
                double segment = ((Math.pow(radius, 2)) * (alpha - Math.sin(alpha))) / 2;
                if (MathUtils.isPositive(p1.getRadius())) {
                    this.surface += segment;
                } else {
                    this.surface -= segment;
                }
                this.perimeter += alpha * radius;
            } else {
                this.perimeter += MathUtils.euclideanDistance(p1, p2);
            }
        }
        this.surface = Math.abs(this.surface);
    }

    @Override
    public String exportToJSON() throws JSONException {
        JSONObject json = new JSONObject();
        if (this.points.size() > 0) {
            JSONArray pointsArray = new JSONArray();
            for (Surface.PointWithRadius p : this.points) {
                pointsArray.put(p.toJSONObject());
            }
            json.put(Surface.POINTS_LIST, pointsArray);
        }
        return json.toString();
    }

    @Override
    public void importFromJSON(String jsonInputArgs) throws JSONException {
        JSONObject json = new JSONObject(jsonInputArgs);
        JSONArray pointsArray = json.getJSONArray(Surface.POINTS_LIST);

        for (int i = 0; i < pointsArray.length(); i++) {
            JSONObject jo = (JSONObject) pointsArray.get(i);
            Surface.PointWithRadius p = Surface.PointWithRadius.getPointFromJSON(jo.toString());
            this.points.add(p);
        }
    }

    @Override
    public Class<?> getActivityClass() {
        // TODO complete when the activity is created
        return null;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public double getSurface() {
        return this.surface;
    }

    public double getPerimeter() {
        return this.perimeter;
    }

    public List<Surface.PointWithRadius> getPoints() {
        return this.points;
    }

    /**
     * Point with a radius.
     * 
     * @author HGdev
     * 
     */
    public static class PointWithRadius extends Point {
        private static final String NUMBER = "number";
        private static final String EAST   = "east";
        private static final String NORTH  = "north";
        private static final String RADIUS = "radius";
        /**
         * Radius wrt to the point of origin. Altitude is ignored.
         */
        private final double        radius;

        public PointWithRadius(int number, double east, double north, double _radius) {
            super(number, east, north, 0.0, false);
            this.radius = _radius;
        }

        public JSONObject toJSONObject() {
            JSONObject json = new JSONObject();
            try {
                json.put(Surface.PointWithRadius.NUMBER, this.getNumber());
                json.put(Surface.PointWithRadius.EAST, this.getEast());
                json.put(Surface.PointWithRadius.NORTH, this.getNorth());
                json.put(Surface.PointWithRadius.RADIUS, this.radius);
            } catch (JSONException e) {
                Log.e(Logger.TOPOSUITE_PARSE_ERROR, e.getMessage());
            }
            return json;
        }

        public static PointWithRadius getPointFromJSON(String json) {
            PointWithRadius p = null;
            try {
                JSONObject jo = new JSONObject(json);
                int number = jo.getInt(Surface.PointWithRadius.NUMBER);
                double east = jo.getDouble(Surface.PointWithRadius.EAST);
                double north = jo.getDouble(Surface.PointWithRadius.NORTH);
                double radius = jo.getDouble(Surface.PointWithRadius.RADIUS);
                p = new PointWithRadius(number, east, north, radius);
            } catch (JSONException e) {
                Log.e(Logger.TOPOSUITE_PARSE_ERROR, e.getMessage());
            }
            return p;
        }

        public PointWithRadius(int number, double east, double north) {
            super(number, east, north, 0.0, false);
            this.radius = 0.0;
        }

        public double getRadius() {
            return this.radius;
        }
    }
}