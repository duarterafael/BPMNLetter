package poli.mestrado.parser.uml2use;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import poli.mestrado.parser.uml2use.tag.ModelTag;

public class GenereteSerializable {
	
	private static GenereteSerializable instance = null;

	private GenereteSerializable(){
	}

	public static GenereteSerializable getInstance(){
		if(instance == null){
			instance = new GenereteSerializable();
		}
		return instance;
	}

	public static void saveModel(String path, Object obj) throws IOException{
		FileOutputStream fos = new FileOutputStream(path);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(obj);	
	}

	public static Object retryveModel(String path) throws IOException, ClassNotFoundException{
		FileInputStream fi = new FileInputStream(path);
		ObjectInputStream x = new ObjectInputStream(fi);
		return x.readObject();
	}


}
