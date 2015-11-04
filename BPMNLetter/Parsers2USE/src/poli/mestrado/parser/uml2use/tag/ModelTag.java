package poli.mestrado.parser.uml2use.tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.rmi.CORBA.Util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import poli.mestrado.parser.util.Utility;

public class ModelTag implements Serializable{
	public static final String TAG_NAME = "UML:Model";
	private String name;
	private List<ClassTag> classList;
	private List<EnumerationTag> enumList;
	private List<AssociationTag> associationList;
	
	public ModelTag(String name){
		this.name = name;
		classList = new ArrayList<ClassTag>();
	}
	
	public ModelTag(String name, List<EnumerationTag> enumerationList, List<ClassTag> classList, List<AssociationTag> associationList){
		this.name = name;
		this.classList = new ArrayList<ClassTag>(classList);
		this.enumList = new ArrayList<EnumerationTag>(enumerationList);
		this.associationList =  associationList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

	public List<ClassTag> getClassList() {
		return classList;
	}

	public void setClassList(List<ClassTag> classList) {
		this.classList = classList;
	}
	
	
	public List<EnumerationTag> getEnumList() {
		return enumList;
	}

	public void setEnumList(List<EnumerationTag> enumList) {
		this.enumList = enumList;
	}
	

	public List<AssociationTag> getAssociationList() {
		return associationList;
	}

	public void setAssociationList(List<AssociationTag> associationList) {
		this.associationList = associationList;
	}

	@Override
	public String toString() {
		String modelStr = "";

		modelStr += "model "+ name+ "\n";
		
		if(!enumList.isEmpty()){
			for (EnumerationTag enumTag : enumList) {
				modelStr += "\n"+enumTag.toString();
			}
		}
		modelStr += "\n";
		if(!classList.isEmpty()){
			for (ClassTag classTag : classList) {
				modelStr += "\n"+classTag.toString();
			}
		}
		modelStr += "\n";
		if(!associationList.isEmpty()){
			for (AssociationTag associationTag : associationList) {
				modelStr += "\n"+associationTag.toString();
			}
		}

		return Utility.removerAcentos(modelStr);
	}

}
