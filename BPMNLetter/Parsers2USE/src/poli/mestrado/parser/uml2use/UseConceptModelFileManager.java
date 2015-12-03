package poli.mestrado.parser.uml2use;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import poli.mestrado.parser.util.MyConstants;

public class UseConceptModelFileManager {
	private File useFile;
	private BufferedWriter bufferedWriterUseFile;
	private static UseConceptModelFileManager instance = null;

	private UseConceptModelFileManager(){
		try {
			useFile = new File(UmlFileManager.getInstance().getExportXmiFile().getAbsolutePath().substring(0, UmlFileManager.getInstance().getExportXmiFile().getAbsolutePath().lastIndexOf(File.separator)+1)+MyConstants.CLASS_DIAGRAM_USE_FILE_NAME);
			if(useFile.exists()){
				useFile.delete();
			}
			useFile.createNewFile();
			bufferedWriterUseFile =  new BufferedWriter(new FileWriter(useFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static UseConceptModelFileManager getInstance(){
		if(instance == null){
			instance = new UseConceptModelFileManager();
		}
		return instance;
	}

	public void writeLine(String str){
		try {
			bufferedWriterUseFile.write(str+"\n");
			bufferedWriterUseFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeFile(){
		try {
			bufferedWriterUseFile.close();
			instance = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
