package ch.hgdev.toposuite.calculation;

import java.util.Date;

import org.json.JSONException;

import ch.hgdev.toposuite.utils.MathUtils;
import ch.hgdev.toposuite.utils.Pair;

import com.google.common.base.Preconditions;

public class TriangleSolver extends Calculation {

    private Pair<Double, Double> a;
    private Pair<Double, Double> b;
    private Pair<Double, Double> c;
    private Pair<Double, Double> alpha;
    private Pair<Double, Double> beta;
    private Pair<Double, Double> gamma;

    private Pair<Double, Double> perimeter;
    private Pair<Double, Double> height;
    private Pair<Double, Double> surface;
    private Pair<Double, Double> incircleRadius;
    private Pair<Double, Double> excircleRadius;

    private boolean              twoSolutions;

    public TriangleSolver(long id, Date lastModification) {
        super(id, null, "Triangle solver", lastModification, true);
    }

    public TriangleSolver(
            double _a, double _b, double _c,
            double _alpha, double _beta, double _gamma,
            boolean hasDAO) throws IllegalArgumentException {
        super(CalculationType.TRIANGLESOLVER, "Triangle solver", hasDAO);

        Preconditions.checkArgument(_a >= 0.0, "Argument was %s but expected nonnegative", _a);
        Preconditions.checkArgument(_b >= 0.0, "Argument was %s but expected nonnegative", _b);
        Preconditions.checkArgument(_c >= 0.0, "Argument was %s but expected nonnegative", _c);
        Preconditions.checkArgument(
                _alpha >= 0.0, "Argument was %s but expected nonnegative", _alpha);
        Preconditions
                .checkArgument(_beta >= 0.0, "Argument was %s but expected nonnegative", _beta);
        Preconditions.checkArgument(
                _gamma >= 0.0, "Argument was %s but expected nonnegative", _gamma);

        this.a = new Pair<Double, Double>(_a, 0.0);
        this.b = new Pair<Double, Double>(_b, 0.0);
        this.c = new Pair<Double, Double>(_c, 0.0);
        this.alpha = new Pair<Double, Double>(MathUtils.modulo400(_alpha), 0.0);
        this.beta = new Pair<Double, Double>(MathUtils.modulo400(_beta), 0.0);
        this.gamma = new Pair<Double, Double>(MathUtils.modulo400(_gamma), 0.0);

        this.twoSolutions = false;

        if (!this.checkInputs()) {
            throw new IllegalArgumentException(
                    "TriangleSolver: At least 3 of the arguments should be greater than 0 "
                            + "and the sum of the 3 angles must be less than or equal to 200");
        }

        this.findMissingValues();

        this.perimeter = new Pair<Double, Double>(0.0, 0.0);
        this.height = new Pair<Double, Double>(0.0, 0.0);
        this.surface = new Pair<Double, Double>(0.0, 0.0);
        this.incircleRadius = new Pair<Double, Double>(0.0, 0.0);
        this.excircleRadius = new Pair<Double, Double>(0.0, 0.0);
    }

    /**
     * Check that at least three arguments are greater than zero and that the
     * sum of all angles is no greater than 200 (remember we use gradian).
     * 
     * @return True if OK, false otherwise.
     */
    private boolean checkInputs() {
        // sum of the angles > 200
        if ((this.alpha.first + this.beta.first + this.gamma.first) > 200.0) {
            return false;
        }
        // three angles given and sum of the angles < 200
        if (MathUtils.isPositive(this.alpha.first) && MathUtils.isPositive(this.beta.first)
                && MathUtils.isPositive(this.gamma.first)
                && ((this.alpha.first + this.beta.first + this.gamma.first) < 200.0)) {
            return false;
        }

        int count = 0;

        if (this.a.first == 0.0) {
            count++;
        }
        if (this.b.first == 0.0) {
            count++;
        }
        if (this.c.first == 0.0) {
            count++;
        }
        if (this.alpha.first == 0.0) {
            count++;
        }
        if (this.beta.first == 0.0) {
            count++;
        }
        if (this.gamma.first == 0.0) {
            count++;
        }

        return count < 4;
    }

    /**
     * Attempt to find missing values; ie: find values of the angles if a, b and
     * c are greater than 0, etc.
     */
    private void findMissingValues() {
        // three sides given
        if (this.areAllPositive(this.a.first, this.b.first, this.c.first)) {
            this.alpha.first = this.determineAngleHavingThreeSides(
                    this.a.first, this.b.first, this.c.first);
            this.beta.first = this.determineAngleHavingThreeSides(
                    this.b.first, this.a.first, this.c.first);
            this.gamma.first = this.determineAngleHavingThreeSides(
                    this.c.first, this.a.first, this.b.first);
            return;
        }
        // a, b and gamma given
        if (this.areAllPositive(this.a.first, this.b.first, this.gamma.first)) {
            this.c.first = this.determineSideHavingTwoSidesAndOneAngle(
                    this.a.first, this.b.first, this.gamma.first);
            this.alpha.first = this.determineAngleHavingThreeSides(
                    this.a.first, this.b.first, this.c.first);
            this.beta.first = this.determineAngleHavingThreeSides(
                    this.b.first, this.a.first, this.c.first);
            return;
        }
        // b, c and alpha given
        if (this.areAllPositive(this.b.first, this.c.first, this.alpha.first)) {
            this.a.first = this.determineSideHavingTwoSidesAndOneAngle(
                    this.b.first, this.c.first, this.alpha.first);
            this.beta.first = this.determineAngleHavingThreeSides(
                    this.b.first, this.a.first, this.c.first);
            this.gamma.first = this.determineAngleHavingThreeSides(
                    this.c.first, this.a.first, this.b.first);
            return;
        }
        // a, c and beta given
        if (this.areAllPositive(this.a.first, this.c.first, this.beta.first)) {
            this.b.first = this.determineSideHavingTwoSidesAndOneAngle(
                    this.a.first, this.c.first, this.beta.first);
            this.alpha.first = this.determineAngleHavingThreeSides(
                    this.a.first, this.b.first, this.c.first);
            this.gamma.first = this.determineAngleHavingThreeSides(
                    this.c.first, this.a.first, this.b.first);
            return;
        }
        // a, b and alpha given (2 solutions case)
        if (this.areAllPositive(this.a.first, this.b.first, this.alpha.first)) {
            this.twoSolutions = true;
            this.beta.first = this.determineAngleHavingTwoSidesAndOneAngle(
                    this.a.first, this.b.first, this.alpha.first);
            this.beta.second = 200.0 - this.beta.first;
            this.gamma.first = this.determineAngleHavingTheTwoOthers(
                    this.alpha.first, this.beta.first);
            this.gamma.second = this.determineAngleHavingTheTwoOthers(
                    this.alpha.first, this.beta.second);
            this.c.first = this.determineSideHavingTwoSidesAndOneAngle(
                    this.a.first, this.b.first, this.gamma.first);
            this.c.second = this.determineSideHavingTwoSidesAndOneAngle(
                    this.a.second, this.b.second, this.gamma.second);
            return;
        }
        // a, b and beta given (2 solutions case)
        if (this.areAllPositive(this.a.first, this.b.first, this.beta.first)) {
            this.twoSolutions = true;
            this.alpha.first = this.determineAngleHavingTwoSidesAndOneAngle(
                    this.b.first, this.a.first, this.beta.first);
            this.alpha.second = 200.0 - this.alpha.first;
            this.gamma.first = this.beta.first - this.alpha.first;
            this.gamma.second = this.determineAngleHavingTheTwoOthers(
                    this.beta.first, this.alpha.second);
            this.c.first = this.determineSideHavingTwoSidesAndOneAngle(
                    this.a.first, this.b.first, this.gamma.first);
            this.c.second = this.determineSideHavingTwoSidesAndOneAngle(
                    this.a.second, this.b.second, this.gamma.second);
            return;
        }
        // b, c and beta given (2 solutions case)
        if (this.areAllPositive(this.b.first, this.c.first, this.beta.first)) {
            this.twoSolutions = true;
            this.gamma.first = this.determineAngleHavingTwoSidesAndOneAngle(
                    this.b.first, this.c.first, this.beta.first);
            this.gamma.second = 200.0 - this.gamma.first;
            this.alpha.first = this.determineAngleHavingTheTwoOthers(
                    this.beta.first, this.gamma.first);
            this.alpha.second = this.determineAngleHavingTheTwoOthers(
                    this.beta.first, this.gamma.second);
            this.a.first = this.determineSideHavingTwoSidesAndOneAngle(
                    this.b.first, this.c.first, this.alpha.first);
            this.a.second = this.determineSideHavingTwoSidesAndOneAngle(
                    this.b.first, this.c.first, this.alpha.second);
            return;
        }
        // b, c and gamma given (2 solutions case)
        if (this.areAllPositive(this.b.first, this.c.first, this.gamma.first)) {
            this.twoSolutions = true;
            this.beta.first = this.determineAngleHavingTwoSidesAndOneAngle(
                    this.c.first, this.b.first, this.gamma.first);
            this.beta.second = 200.0 - this.beta.first;
            this.alpha.first = this.determineAngleHavingTheTwoOthers(
                    this.beta.first, this.gamma.first);
            this.alpha.second = this.determineAngleHavingTheTwoOthers(
                    this.beta.second, this.gamma.first);
            this.a.first = this.determineSideHavingTwoSidesAndOneAngle(
                    this.b.first, this.c.first, this.alpha.first);
            this.a.second = this.determineSideHavingTwoSidesAndOneAngle(
                    this.b.first, this.c.first, this.alpha.second);
            return;
        }
        // a, c and alpha given (2 solutions case)
        if (this.areAllPositive(this.a.first, this.c.first, this.alpha.first)) {
            this.twoSolutions = true;
            this.gamma.first = this.determineAngleHavingTwoSidesAndOneAngle(
                    this.a.first, this.c.first, this.alpha.first);
            this.gamma.second = 200.0 - this.gamma.first;
            this.beta.first = this.determineAngleHavingTheTwoOthers(
                    this.alpha.first, this.gamma.first);
            this.beta.second = this.determineAngleHavingTheTwoOthers(
                    this.alpha.first, this.gamma.second);
            this.a.first = this.determineSideHavingTwoSidesAndOneAngle(
                    this.b.first, this.c.first, this.beta.first);
            this.a.second = this.determineSideHavingTwoSidesAndOneAngle(
                    this.b.first, this.c.first, this.beta.second);
            return;
        }
        // a, c and gamma given (2 solutions case)
        if (this.areAllPositive(this.a.first, this.c.first, this.gamma.first)) {
            this.twoSolutions = true;
            this.alpha.first = this.determineAngleHavingTwoSidesAndOneAngle(
                    this.c.first, this.a.first, this.gamma.first);
            this.alpha.second = 200.0 - this.alpha.first;
            this.beta.first = this.determineAngleHavingTheTwoOthers(
                    this.gamma.first, this.alpha.first);
            this.beta.second = this.determineAngleHavingTheTwoOthers(
                    this.gamma.first, this.alpha.second);
            this.b.first = this.determineSideHavingTwoSidesAndOneAngle(
                    this.a.first, this.c.first, this.beta.first);
            this.b.second = this.determineSideHavingTwoSidesAndOneAngle(
                    this.a.first, this.c.first, this.beta.second);
            return;
        }
        // a, beta and gamma given
        if (this.areAllPositive(this.a.first, this.beta.first, this.gamma.first)) {
            this.alpha.first = this.determineAngleHavingTheTwoOthers(
                    this.beta.first, this.gamma.first);
            this.b.first = this.determineSideHavingOneSideAndTwoAngles(
                    this.a.first, this.beta.first, this.alpha.first);
            this.c.first = this.determineSideHavingOneSideAndTwoAngles(
                    this.a.first, this.gamma.first, this.alpha.first);
            return;
        }
        // b, alpha and gamma given
        if (this.areAllPositive(this.b.first, this.alpha.first, this.gamma.first)) {
            this.beta.first = this.determineAngleHavingTheTwoOthers(
                    this.alpha.first, this.gamma.first);
            this.a.first = this.determineSideHavingOneSideAndTwoAngles(
                    this.b.first, this.alpha.first, this.beta.first);
            this.c.first = this.determineSideHavingOneSideAndTwoAngles(
                    this.b.first, this.gamma.first, this.beta.first);
            return;
        }
        // c, alpha and beta given
        if (this.areAllPositive(this.c.first, this.alpha.first, this.beta.first)) {
            this.gamma.first = this.determineAngleHavingTheTwoOthers(
                    this.alpha.first, this.beta.first);
            this.a.first = this.determineSideHavingOneSideAndTwoAngles(
                    this.c.first, this.alpha.first, this.gamma.first);
            this.b.first = this.determineSideHavingOneSideAndTwoAngles(
                    this.c.first, this.beta.first, this.gamma.first);
            return;
        }
        // a, alpha and beta given
        if (this.areAllPositive(this.a.first, this.alpha.first, this.beta.first)) {
            this.gamma.first = this.determineAngleHavingTheTwoOthers(
                    this.alpha.first, this.beta.first);
            this.b.first = this.determineSideHavingOneSideAndTwoAngles(
                    this.a.first, this.beta.first, this.alpha.first);
            this.c.first = this.determineSideHavingOneSideAndTwoAngles(
                    this.a.first, this.gamma.first, this.alpha.first);
            return;
        }
        // a, alpha and gamma given
        if (this.areAllPositive(this.a.first, this.alpha.first, this.gamma.first)) {
            this.beta.first = this.determineAngleHavingTheTwoOthers(
                    this.alpha.first, this.gamma.first);
            this.b.first = this.determineSideHavingOneSideAndTwoAngles(
                    this.a.first, this.beta.first, this.alpha.first);
            this.c.first = this.determineSideHavingOneSideAndTwoAngles(
                    this.a.first, this.gamma.first, this.alpha.first);
            return;
        }
        // b, alpha and beta given
        if (this.areAllPositive(this.b.first, this.alpha.first, this.beta.first)) {
            this.gamma.first = this.determineAngleHavingTheTwoOthers(
                    this.alpha.first, this.beta.first);
            this.a.first = this.determineSideHavingOneSideAndTwoAngles(
                    this.b.first, this.alpha.first, this.beta.first);
            this.c.first = this.determineSideHavingOneSideAndTwoAngles(
                    this.b.first, this.gamma.first, this.beta.first);
            return;
        }
        // b, gamma and beta given
        if (this.areAllPositive(this.b.first, this.gamma.first, this.beta.first)) {
            this.alpha.first = this.determineAngleHavingTheTwoOthers(
                    this.beta.first, this.gamma.first);
            this.a.first = this.determineSideHavingOneSideAndTwoAngles(
                    this.b.first, this.alpha.first, this.beta.first);
            this.c.first = this.determineSideHavingOneSideAndTwoAngles(
                    this.b.first, this.gamma.first, this.beta.first);
            return;
        }
        // c, gamma and alpha given
        if (this.areAllPositive(this.c.first, this.gamma.first, this.alpha.first)) {
            this.beta.first = this.determineAngleHavingTheTwoOthers(
                    this.alpha.first, this.gamma.first);
            this.a.first = this.determineSideHavingOneSideAndTwoAngles(
                    this.c.first, this.alpha.first, this.gamma.first);
            this.b.first = this.determineSideHavingOneSideAndTwoAngles(
                    this.c.first, this.beta.first, this.gamma.first);
            return;
        }
        // c, gamma and beta given
        if (this.areAllPositive(this.c.first, this.gamma.first, this.beta.first)) {
            this.alpha.first = this.determineAngleHavingTheTwoOthers(
                    this.beta.first, this.gamma.first);
            this.a.first = this.determineSideHavingOneSideAndTwoAngles(
                    this.c.first, this.alpha.first, this.gamma.first);
            this.b.first = this.determineSideHavingOneSideAndTwoAngles(
                    this.c.first, this.beta.first, this.gamma.first);
            return;
        }

    }

    /**
     * Compute perimeter, height, surface, incircle radius and excircle radius.
     */
    public void compute() {
        this.perimeter.first = this.computePerimeter(this.a.first, this.b.first, this.c.first);
        this.height.first = this.computeHeight(this.beta.first, this.c.first);
        this.incircleRadius.first = this.computeIncircleRadius(
                this.perimeter.first, this.a.first, this.b.first, this.c.first);
        this.excircleRadius.first = this.computeExcircleRadius(this.a.first, this.alpha.first);
        this.surface.first = this.computeSurface(
                this.a.first, this.b.first, this.c.first, this.excircleRadius.first);

        if (this.twoSolutions) {
            this.perimeter.second = this.computePerimeter(
                    this.a.second, this.b.second, this.c.second);
            this.height.second = this.computeHeight(this.beta.second, this.c.second);
            this.incircleRadius.second = this.computeIncircleRadius(
                    this.perimeter.second, this.a.second, this.b.second, this.c.second);
            this.excircleRadius.second = this.computeExcircleRadius(
                    this.a.second, this.alpha.second);
            this.surface.second = this.computeSurface(
                    this.a.second, this.b.second, this.c.second, this.excircleRadius.second);
        }
    }

    /**
     * Compute the triangle perimeter.
     */
    private double computePerimeter(double a, double b, double c) {
        return a + b + c;
    }

    /**
     * Compute the triangle height.
     * 
     * @return
     */
    private double computeHeight(double beta, double c) {
        return Math.sin(MathUtils.gradToRad(beta)) * c;
    }

    /**
     * Compute the triangle surface. Warning: requires excircleRadius to be
     * computed before.
     */
    private double computeSurface(double a, double b, double c, double excircleRadius) {
        return (a * b * c) / (4 * excircleRadius);
    }

    /**
     * Compute the triangle's incircle radius. Warning: requires the perimeter
     * to be computed before.
     */
    private double computeIncircleRadius(double perimeter, double a, double b, double c) {
        return Math.sqrt((((perimeter / 2) - a) * ((perimeter / 2) - b) * ((perimeter / 2) - c))
                / (perimeter / 2));
    }

    /**
     * Compute the triangle's excircle radius.
     */
    private double computeExcircleRadius(double a, double alpha) {
        return (a / Math.sin(MathUtils.gradToRad(alpha))) / 2;
    }

    /**
     * Return true if all provided parameters are positive.
     * 
     * @param a
     * @param b
     * @param c
     * @return
     */
    private boolean areAllPositive(double a, double b, double c) {
        return MathUtils.isPositive(a) && MathUtils.isPositive(b)
                && MathUtils.isPositive(c);
    }

    /**
     * Determine an angle when having three sides.
     * 
     * @param a
     * @param b
     * @param c
     * @return The angle.
     */
    private double determineAngleHavingThreeSides(double a, double b, double c) {
        return MathUtils.radToGrad(Math.acos(
                ((Math.pow(b, 2) + Math.pow(c, 2)) - Math.pow(a, 2))
                        / (2 * b * c)));
    }

    private double determineAngleHavingTwoSidesAndOneAngle(double a, double b, double alpha) {
        return MathUtils.radToGrad(Math.asin((b * Math.sin(MathUtils.gradToRad(alpha))) / a));
    }

    /**
     * Determine alpha angle when having beta and gamma.
     */
    private double determineAngleHavingTheTwoOthers(double beta, double gamma) {
        return 200.0 - gamma - beta;
    }

    /**
     * Determine c when having a, b and gamma.
     * 
     * @param a
     * @param b
     * @param gamma
     * @return
     */
    private double determineSideHavingTwoSidesAndOneAngle(double a, double b, double gamma) {
        return Math.sqrt((Math.pow(a, 2) + Math.pow(b, 2))
                - (2 * a * b * Math.cos(MathUtils.gradToRad(gamma))));
    }

    private double determineSideHavingOneSideAndTwoAngles(double a, double beta, double alpha) {
        return (a * Math.sin(MathUtils.gradToRad(beta))) / Math.sin(MathUtils.gradToRad(alpha));
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
        // TODO implement
        return null;
    }

    public Pair<Double, Double> getPerimeter() {
        return this.perimeter;
    }

    public Pair<Double, Double> getHeight() {
        return this.height;
    }

    public Pair<Double, Double> getSurface() {
        return this.surface;
    }

    public Pair<Double, Double> getIncircleRadius() {
        return this.incircleRadius;
    }

    public Pair<Double, Double> getExcircleRadius() {
        return this.excircleRadius;
    }

}