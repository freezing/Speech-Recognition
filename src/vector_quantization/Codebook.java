package vector_quantization;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Codebook implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_SIZE = 1024;
	
	private List<KDCluster> clusters;
	private List<KDPoint> points;
	private int codebookSize = DEFAULT_SIZE;
	
	public Codebook(List<KDPoint> points, int codebookSize) {
		this.points = points;
		System.out.println("Number of points in codebook = " + points.size());
		this.codebookSize = codebookSize;
	}
	
	public void initialize() throws Exception {
		if (points.size() < codebookSize) {
			throw new Exception("Not enough points to generate a codebook of size " + codebookSize);
		}
		
		// TODO: Implement some clustering algorithm LGB (or only k-means) to cluster
		// these points into clusters (default 256)
		// for now just the simplest k-means is implemented
		Collections.shuffle(points, new Random(123123));
		clusters = new LinkedList<>();
		for (KDPoint point : points) {
			clusters.add(new KDCluster(point));
			if (clusters.size() == codebookSize) {
				break;
			}
		}
		
		System.out.println("Codebook size = " + codebookSize);
		
		// iterate 20 times and try to find better means
		for (int it = 0; it < 20; it++) {
			System.out.println("Codebook iteration = " + it);
			// assign each point to closest cluster
			reasignPoints(clusters, points);
			
			// Update cluster coordinates
			updateClusters(clusters);
		}
	}	
	
	public int[] quantize(List<KDPoint> points) throws Exception {
		int quantizedPoints[] = new int[points.size()];
		for (int i = 0; i < points.size(); i++) {
			quantizedPoints[i] = points.get(i).findNearestPoint(clusters);
		}
		return quantizedPoints;
	}
	
	public List<KDCluster> getClusters() {
		return clusters;
	}
	
	public List<KDPoint> getPoints() {
		return points;
	}
	
	public int getSize() {
		return codebookSize;
	}
	
	private void reasignPoints(List<KDCluster> clusters, List<KDPoint> points) throws Exception {
		for (KDCluster cluster : clusters) {
			cluster.removeAllPoints();
		}
		
		int processedPoints = 0;
		for (KDPoint point : points) {
			int idx = point.findNearestPoint(clusters);
			clusters.get(idx).addPoint(point);
			if (processedPoints++ % 1024 == 0) {
				System.out.println("Processed: " + processedPoints);
			}
		}
	}

	private void updateClusters(List<KDCluster> clusters) {
		for (KDCluster cluster : clusters) {
			cluster.update();
		}
	}
}
