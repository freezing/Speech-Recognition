package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import database.model.CodebookModel;
import database.model.HmmModel;
import database.model.Model;
import database.model.TrainingSetFiles;
import database.model.VadModel;

public class Database {
	public static String DEFAULT_PATH = "/home/nikola/SpeechTest/";

	public static String TRAINING_SET_FOLDER_NAME = "TrainingSet";
	public static String CODEBOOK_FOLDER_NAME = "Codebook";
	public static String HMM_FOLDER_NAME = "HiddenMarkovModels";
	public static String VAD_FOLDER_NAME = "Vad";
	
	private File rootFolder;
	private File trainingSetFolder;
	private File codebookFolder;
	private File hmmFolder;
	private File vadFolder;
	
	public Database() {
		this(DEFAULT_PATH);
	}
	
	public Database(String rootPath) {
		rootFolder = new File(rootPath);
		if (!rootFolder.exists()) {
			rootFolder.mkdir();
		}
		
		rootPath = rootFolder.getAbsolutePath() + "/";
		
		hmmFolder = new File(rootPath + HMM_FOLDER_NAME);
		if (!hmmFolder.exists()) {
			hmmFolder.mkdir();
		}
		
		codebookFolder = new File(rootPath + CODEBOOK_FOLDER_NAME);
		if (!codebookFolder.exists()) {
			codebookFolder.mkdir();
		}
		
		trainingSetFolder = new File(rootPath + TRAINING_SET_FOLDER_NAME);
		if (!trainingSetFolder.exists()) {
			trainingSetFolder.mkdir();
		}
		
		vadFolder = new File(rootPath + VAD_FOLDER_NAME);
		if (!vadFolder.exists()) {
			vadFolder.mkdir();
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
	
	public List<HmmModel> loadHmmModels() {
		String[] modelList = hmmFolder.list();
		List<HmmModel> hmmModels = new LinkedList<>();
		
		for (String folder : modelList) {
			hmmModels.add( (HmmModel) load(HmmModel.class, folder, "") );
		}
		return hmmModels;
	}
	
	public TrainingSetFiles loadTrainingSet() {
		TrainingSetFiles trainingSetFiles = new TrainingSetFiles();
		String[] folders = trainingSetFolder.list();
		
		for (String folder : folders) {
			trainingSetFiles.addSet(new File(trainingSetFolder.getAbsoluteFile() + "/" + folder));
		}
		return trainingSetFiles;
	}
	
	public TrainingSetFiles loadVadTrainingSet() {
		TrainingSetFiles trainingSetFiles = new TrainingSetFiles();
		String[] folders = vadFolder.list();

		for (String folder : folders) {
			trainingSetFiles.addSet(new File(vadFolder.getAbsoluteFile() + "/" + folder));
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
		else if (clazz.equals(VadModel.class)) {
			return vadFolder;
		}
		else {
			return null;
		}
	}
}
