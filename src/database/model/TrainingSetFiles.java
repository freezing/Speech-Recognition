package database.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TrainingSetFiles {
	private Map<String, File[]> trainingSets;	
	
	public TrainingSetFiles() {
		trainingSets = new HashMap<>();
	}
	
	public void addSet(File folder) {
		trainingSets.put(folder.getName(), folder.listFiles());
	}
	
	public Map<String, File[]> getTrainingSets() {
		return trainingSets;
	}
	
}
