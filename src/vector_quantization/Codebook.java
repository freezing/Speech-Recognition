package vector_quantization;

public class Codebook {
	private KDCluster[] clusters;
	private KDPoint[] points;
	private int codebookSize = 256;
	
	public Codebook(KDPoint[] points, int codebookSize) {
		this.points = points;
		this.codebookSize = codebookSize;
	}
	
	public void initialize() throws Exception {
		if (points.length < codebookSize) {
			throw new Exception("Not enough points to generate a codebook of size " + codebookSize);
		}
		
		// TODO: Implement some clustering algorithm LGB (or only k-means) to cluster
		// these points into clusters (default 256)
		// for now just the simplest k-means is impleneted
		clusters = new KDCluster[codebookSize];
		for (int i = 0; i < clusters.length; i++) {
			clusters[i] = new KDCluster(points[i]);
		}
		
		// iterate 20 times and try to find better means
		for (int it = 0; it < 20; it++) {
			// assign each point to closest cluster
			reasignPoints(clusters, points);
			
			// Update cluster coordinates
			updateClusters(clusters);
		}
	}	
	
	public int[] quantize(KDPoint[] points) throws Exception {
		int quantizedPoints[] = new int[points.length];
		for (int i = 0; i < points.length; i++) {
			quantizedPoints[i] = points[i].findNearestPoint(clusters);
		}
		return quantizedPoints;
	}
	
	private void reasignPoints(KDCluster[] clusters, KDPoint[] points) throws Exception {
		for (KDCluster cluster : clusters) {
			cluster.removeAllPoints();
		}
		
		for (int i = 0; i < points.length; i++) {
			int idx = points[i].findNearestPoint(clusters);
			clusters[idx].addPoint(points[i]);
		}
	}

	private void updateClusters(KDCluster clusters[]) {
		for (KDCluster cluster : clusters) {
			cluster.update();
		}
	}
	
	public void saveToFile() {
		
	}
	
	public static Codebook readFromFile() {
		return new Codebook(null, 256);
	}
}
