package poli.mestrado.parser.util;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.LinkedList;
import java.util.Random;

import poli.mestrado.parser.uml2use.GenereteSerializable;
import poli.mestrado.parser.uml2use.UmlFileManager;
import poli.mestrado.parser.uml2use.tag.AttributeTag;
import poli.mestrado.parser.uml2use.tag.ClassTag;
import poli.mestrado.parser.uml2use.tag.EnumerationLiteralTag;
import poli.mestrado.parser.uml2use.tag.EnumerationTag;
import poli.mestrado.parser.uml2use.tag.ModelTag;

public class Utility {
	
	public static String removerAcentos(String acentuada) {  
	    CharSequence cs = new StringBuilder(acentuada);  
	    return Normalizer.normalize(cs, Normalizer.Form.NFKD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");  
	} 
	
	public static String convertUserCMD2ShellUseCMD(String userCMD){
//			case Constants.USER_CMD_RANDOM_SET:
//				return getShellUSECMDRandonSet(userCMD);
			if(userCMD.contains(MyConstants.USER_CMD_CREATE)){
				return getShellUSECMDCreate(userCMD);
			}else if(userCMD.contains(MyConstants.USER_CMD_DELETE)){
				return getShellUSECMDDestroy(userCMD);
			}else if(userCMD.contains(MyConstants.USER_CMD_SET)){
				return getShellUSECMDSET(userCMD);
			}else if(userCMD.contains(MyConstants.USER_CMD_ASSOC)){
				return getShellUSECMDAssoc(userCMD);
			}else if(userCMD.contains(MyConstants.USER_CMD_DESASSOC)){
				return getShellUSECMDDesassoc(userCMD);
			}
			return null;
	}
	
	
	private static String getShellUSECMDCreate(String userCMD){
		String returnedCMD = "!create ";
		String className = userCMD.substring(userCMD.indexOf("(")+1,userCMD.indexOf(")"));
		String idObj = userCMD.substring(0, userCMD.indexOf(":"));
		returnedCMD += idObj;
		returnedCMD += ":"+className;
		
		if(userCMD.contains("entre")){
			returnedCMD += " between "+userCMD.substring(userCMD.lastIndexOf("("),userCMD.lastIndexOf(")")+1);
		}
		
		if(idObj.contains(",")){
			String[] idObjLst  = idObj.split(",");
			for (String string : idObjLst) {
				returnedCMD += returnedCMD+";"+getShellUSECMDRandonSet(className, string);
			}
		}else{
			returnedCMD += ";"+getShellUSECMDRandonSet(className, idObj);
		}
		
		
		return returnedCMD;
	}
	
	private static String getShellUSECMDDestroy(String userCMD){
		String returnedCMD = "!destroy ";
		returnedCMD += userCMD.substring(0, userCMD.indexOf(":"));
		
		return returnedCMD;
	}
	
	private static String getShellUSECMDSET(String userCMD){
		String returnedCMD = "!set ";
		returnedCMD += userCMD.substring(0, userCMD.indexOf(":"));
		returnedCMD += "."+userCMD.substring(userCMD.indexOf("(")+1, userCMD.indexOf(","));
		returnedCMD += ":="+userCMD.substring(userCMD.indexOf(",")+1, userCMD.indexOf(")"));
		
		
		return returnedCMD;
	}
	
	private static String getShellUSECMDRandonSet(String className, String idObj){
		String returnedCMD = "";
		String path =  	UmlFileManager.getInstance().getExportXmiFile().getAbsolutePath()
				.substring(0, UmlFileManager.getInstance().getExportXmiFile().
				getAbsolutePath().lastIndexOf(File.separator)+1)+MyConstants.CLASS_DIAGRAM_SERIALIZABEL_FILE_NAME;

		ModelTag model = null;
		try {
			model = (ModelTag) GenereteSerializable.retryveModel(path);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(model != null){
			for (ClassTag classTag : model.getClassList()) {
					if(classTag.getName().equals(className)){
						for(AttributeTag attTag : classTag.getAttributesList()){
							returnedCMD += "!set "+idObj+"."+attTag.getName()+" := ";
							if(attTag.getType().getName().equalsIgnoreCase("integer")){
								returnedCMD += getRandomInteger(attTag.getValuesInterval());
							}else if(attTag.getType().getName().equalsIgnoreCase("string")){
								returnedCMD += "'"+getRandomSting(attTag.getValuesInterval())+"'";
							}else{
								returnedCMD += getRandomSting(attTag.getValuesInterval());
							}
							returnedCMD+=";";
						}
					}
			}
			
		}
		return returnedCMD;
	}
	
	private static String getShellUSECMDAssoc(String userCMD){
		String returnedCMD = "!insert ";
		returnedCMD += "("+userCMD.substring(0, userCMD.indexOf(":"))+")";
		returnedCMD += " into "+userCMD.substring(userCMD.indexOf("(")+1,userCMD.indexOf(")"));
		return returnedCMD;
	}
	
	private static String getShellUSECMDDesassoc(String userCMD){
		String returnedCMD = "!delete ";
		returnedCMD += "("+userCMD.substring(0, userCMD.indexOf(":"))+")";
		returnedCMD += " from "+userCMD.substring(userCMD.indexOf("(")+1,userCMD.indexOf(")"));
		return returnedCMD;
	}
	
	private static String getRandomSting(String str){
		String[] strList = str.split(",");
		int randonIndex = getRandomInteger(0, strList.length-1);
		return strList[randonIndex];
	}
	
	private static int getRandomInteger(String str) {
		String[] strList = str.split(":");
		if(strList.length == 2){
			return getRandomInteger(new Integer(strList[0]), new Integer(strList[1]));
		}
		return -1;
	}
	
	private static int getRandomInteger(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}  
}
