package vector_quantization;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class KDPoint implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected double[] coordinates;

	public KDPoint(double[] coordinates) {
		this.coordinates = coordinates;
	}

	public KDPoint(KDPoint point) {
		coordinates = new double[point.getDimension()];
		for (int i = 0; i < coordinates.length; i++) {
			coordinates[i] = point.getCoordinate(i);
		}
	}

	public double getCoordinate(int idx) {
		return coordinates[idx];
	}

	public int getDimension() {
		return coordinates.length;
	}

	public int findNearestPoint(KDPoint[] points) throws Exception {
		if (points.length == 0) {
			return -1;
		}

		int idx = 0;
		double distance = this.getDistance(points[0]);
		for (int i = 0; i < points.length; i++) {
			double tmpDistance = this.getDistance(points[i]);
			if (tmpDistance < distance) {
				distance = tmpDistance;
				idx = i;
			}
		}

		return idx;
	}
	
	public int findNearestPoint(List<? extends KDPoint> points) throws Exception {
		if (points.size() == 0) {
			return -1;
		}

		int idx = 0;
		double distance = this.getDistance(points.get(0));
		for (int i = 0; i < points.size(); i++) {
			double tmpDistance = this.getDistance(points.get(i));
			if (tmpDistance < distance) {
				distance = tmpDistance;
				idx = i;
			}
		}

		return idx;
	}

	private double getDistance(KDPoint point) throws Exception {
		if (point.getDimension() != this.getDimension()) {
			throw new Exception("Points must have the same dimensions, " + 
				this.getDimension() + ", " + point.getDimension());
		}
		
		double distance = 0.0f;
		for (int i = 0; i < this.getDimension(); i++) {
			double d = this.getCoordinate(i) - point.getCoordinate(i);
			distance += d * d;
		}
		return distance;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(coordinates);
	}
}
