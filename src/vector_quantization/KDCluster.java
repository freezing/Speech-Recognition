package vector_quantization;

import java.util.HashSet;
import java.util.Set;

public class KDCluster extends KDPoint {
	private static final long serialVersionUID = 1L;
	
	private Set<KDPoint> points;
	
	public KDCluster(double[] coordinates) {
		super(coordinates);
		points = new HashSet<>();
	}
	
	public KDCluster(KDPoint point) {
		super(point);
		points = new HashSet<>();
	}
	
	public void addPoint(KDPoint point) {
		points.add(point);
	}
	
	public void removePoint(KDPoint point) {
		points.remove(point);
	}
	
	public void update() {
		reset();
		for (KDPoint p : points) {
			for (int i = 0; i < coordinates.length; i++) {
				coordinates[i] += p.getCoordinate(i);
			}
		}
		
		for (int i = 0; i < coordinates.length; i++) {
			coordinates[i] /= points.size();
		}
	}
	
	private void reset() {
		for (int i = 0; i < coordinates.length; i++) {
			coordinates[i] = 0.0f;
		}
	}
	
	public void removeAllPoints() {
		points.clear();
	}

}
