package poli.mestrado.parser.bpmn2use;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import poli.mestrado.parser.uml2use.UmlFileManager;
import poli.mestrado.parser.util.MyConstants;

public class UseAsslFileHelper {
	private File dir;
	private File asslFile;
	private BufferedWriter bufferedWriterFile;
	private static UseAsslFileHelper instance = null;
	private static final String  FILE_NAME = "caminho";
	private static int pathCount = 1;

	private UseAsslFileHelper(){
		String path = BpmnFileManager.getInstance().getExportFile().getAbsolutePath().substring(0, BpmnFileManager.getInstance().getExportFile().getAbsolutePath().lastIndexOf(File.separator))+File.separator+MyConstants.ASSL_SCRIPT_FOLDER;
		dir = new File(path);
		if(!dir.exists()){
			dir.mkdirs();
		}

	}

	public static void deleteFiles() {
		String path = BpmnFileManager.getInstance().getExportFile().getAbsolutePath().substring(0, BpmnFileManager.getInstance().getExportFile().getAbsolutePath().lastIndexOf(File.separator))+File.separator+MyConstants.ASSL_SCRIPT_FOLDER;
		File auxDir = new File(path);
		if(auxDir.exists() && auxDir.isDirectory()){
			for (File oldAsslFile : auxDir.listFiles()) {
				oldAsslFile.delete();
			}
		}
	}


	public static UseAsslFileHelper getInstance(){
		if(instance == null){
			instance = new UseAsslFileHelper();
		}
		return instance;
	}

	public void writeLine(String str){
		try {
			if(asslFile == null){
				String fileName = File.separator+FILE_NAME+pathCount+".assl";
				asslFile = new File(dir.getAbsoluteFile()+fileName);
				asslFile.createNewFile();
				bufferedWriterFile =  new BufferedWriter(new FileWriter(asslFile));
				System.out.println("The assl file ("+fileName+") was created successfully!");
			}
			if(str != null){
				bufferedWriterFile.write(str+"\n");
				bufferedWriterFile.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeFile(){
		try {
			bufferedWriterFile.close();
			instance = null;
			asslFile = null;
			bufferedWriterFile = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void resetPahtCount(){
		pathCount = 1;
	}
	
	public void incremetPathCount(){
		pathCount++;
	}
	
	public String[] getAsslNames(){
		String[] nameFiles = null;
		String path = BpmnFileManager.getInstance().getExportFile().getAbsolutePath().substring(0, BpmnFileManager.getInstance().getExportFile().getAbsolutePath().lastIndexOf(File.separator))+File.separator+MyConstants.ASSL_SCRIPT_FOLDER;
		dir = new File(path);
		if(dir.exists()){
			nameFiles = new String[dir.listFiles().length];
			for (int i = 0; i<dir.listFiles().length; i++) {
				nameFiles[i] = dir.listFiles()[i].getAbsolutePath().substring(dir.listFiles()[i].getAbsolutePath().lastIndexOf(File.separator)+1, dir.listFiles()[i].getAbsolutePath().lastIndexOf("."));
			}
		}
		return nameFiles;

	}

}
