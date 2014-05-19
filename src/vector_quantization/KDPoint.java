package vector_quantization;

public class KDPoint {
	protected float[] coordinates;

	public KDPoint(float[] coordinates) {
		coordinates = this.coordinates;
	}

	public KDPoint(KDPoint point) {
		coordinates = new float[point.getDimension()];
		for (int i = 0; i < coordinates.length; i++) {
			coordinates[i] = point.getCoordinate(i);
		}
	}

	public float getCoordinate(int idx) {
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
		float distance = this.getDistance(points[0]);
		for (int i = 0; i < points.length; i++) {
			float tmpDistance = this.getDistance(points[i]);
			if (tmpDistance < distance) {
				distance = tmpDistance;
				idx = i;
			}
		}

		return idx;
	}

	private float getDistance(KDPoint point) throws Exception {
		if (point.getDimension() != this.getDimension()) {
			throw new Exception("Points must have the same dimensions, " + 
				this.getDimension() + ", " + point.getDimension());
		}
		
		float distance = 0.0f;
		for (int i = 0; i < this.getDimension(); i++) {
			float d = this.getCoordinate(i) - point.getCoordinate(i);
			distance += d * d;
		}
		return distance;
	}
}
