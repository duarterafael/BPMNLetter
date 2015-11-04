package poli.mestrado.parser.uml2use.tag;

import java.io.Serializable;
import java.util.List;

public class EnumerationTag extends BaseTag  implements IType{
	
	public static final String TAG_NAME = "UML:Enumeration";
	private static final String STEREOTYPE = "UML:Stereotype";
	
	private String stereotype;
	private List<EnumerationLiteralTag> enumerationLiteralList;
	
	public EnumerationTag(String id){
		super(id, null);
	}
	
	public EnumerationTag(String id, String name, String stereotype, List<EnumerationLiteralTag> enumerationLiteralList) {
		super(id, name);
		this.stereotype = stereotype;
		this.enumerationLiteralList = enumerationLiteralList;
	}


	public String getStereotype() {
		return stereotype;
	}

	public void setStereotype(String stereotype) {
		this.stereotype = stereotype;
	}

	public List<EnumerationLiteralTag> getEnumerationLiteralList() {
		return enumerationLiteralList;
	}

	public void setEnumerationLiteralList(
			List<EnumerationLiteralTag> enumerationLiteralList) {
		this.enumerationLiteralList = enumerationLiteralList;
	}
	
	@Override
	public String getIdRef() {
		return super.getId();
	}
	
	@Override
	public String toString() {
		String enumStr = "";
		String enumLiteralStr ="";
		enumStr += "enum "+super.getName()+" {";
		
		if(!enumerationLiteralList.isEmpty()){
			for (EnumerationLiteralTag enumerationLiteralTag : enumerationLiteralList) {
				enumLiteralStr += enumerationLiteralTag.toString()+", ";
			}
		}
		if(enumLiteralStr.endsWith(", ")){
			enumLiteralStr = enumLiteralStr.substring(0, enumLiteralStr.length()-2);
		}
		
		enumStr += enumLiteralStr+"}";
		return enumStr;
	}



	
	
	

}
