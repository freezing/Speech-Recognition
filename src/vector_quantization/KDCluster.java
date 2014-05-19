package vector_quantization;

import java.util.Set;
import java.util.TreeSet;

public class KDCluster extends KDPoint {

	private Set<KDPoint> points;
	
	public KDCluster(float[] coordinates) {
		super(coordinates);
		points = new TreeSet<>();
	}
	
	public KDCluster(KDPoint point) {
		super(point);
		points = new TreeSet<>();
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
