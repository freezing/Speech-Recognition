package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import database.model.CodebookModel;
import database.model.HmmModel;
import database.model.Model;
import database.model.TrainingSetFiles;

public class Database {
	public static String DEFAULT_PATH = "/home/nikola/SpeechTest/";

	public static String TRAINING_SET_FOLDER_NAME = "TrainingSet";
	public static String CODEBOOK_FOLDER_NAME = "Codebook";
	public static String HMM_FOLDER_NAME = "HiddenMarkovModels";
	
	private File rootFolder;
	private File trainingSetFolder;
	private File codebookFolder;
	private File hmmFolder;
	
	public Database() {
		this(DEFAULT_PATH);
	}
	
	public Database(String rootPath) {
		this.rootFolder = new File(rootPath);
		if (!rootFolder.exists()) {
			rootFolder.mkdir();
		}
	}
	
	public void save(Model model) {
		File folder = getFolder(model.getClass());
		
		try {
			File file = new File(folder.getAbsoluteFile() + "/" + model.getName() + model.getExtension());
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			
			out.writeObject(model);
			
			fileOut.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println(e);
		}
	}

	public Model load(Class clazz, String name, String extension) {
		File folder = getFolder(clazz);
		try {
			File file = new File(folder.getAbsoluteFile() + "/" + name + extension);
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			
			Model loadedModel = (Model) in.readObject();
			
			in.close();
			fileIn.close();
			return loadedModel;
		}
		catch (IOException | ClassNotFoundException e) {
			System.err.println(e);
		}
		return null;
	}
	
	public HmmModel[] loadHmmModels() {
		String[] folderList = hmmFolder.list();
		HmmModel[] hmmModels = new HmmModel[folderList.length];
		
		int k = 0;
		for (String folder : folderList) {
			hmmModels[k++] = (HmmModel) load(HmmModel.class, folder, HmmModel.HMM_EXTENSION);
		}
		return hmmModels;
	}
	
	public TrainingSetFiles getTrainingSet() {
		TrainingSetFiles trainingSetFiles = new TrainingSetFiles();
		String[] folders = trainingSetFolder.list();
		
		for (String folder : folders) {
			trainingSetFiles.addSet(new File(trainingSetFolder.getAbsoluteFile() + "/" + folder));
		}
		return trainingSetFiles;
	}
	
	private File getFolder(Class clazz) {
		if (clazz.equals(HmmModel.class)) {
			return hmmFolder;
		}
		else if (clazz.equals(CodebookModel.class)) { 
			return codebookFolder;
		}
		else {
			return null;
		}
	}
}
