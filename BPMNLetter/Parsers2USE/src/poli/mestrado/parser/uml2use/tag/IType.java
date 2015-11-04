package poli.mestrado.parser.uml2use.tag;

import java.io.Serializable;

public interface IType extends Serializable{
	public static final String STRUTURAL_FEATURE_TYPE_TAG = "UML:StructuralFeature.type";
	public static final String PARAMETER_TYPE_TAG = "UML:Parameter.type";
	public static final String ENUMERATION_TAG = "UML:Enumeration";
	public static final String DATA_TYPE_TAG = "UML:DataType";
	public static final String ATTRIBUTE_TAG_HREF = "href";
	
	public String getName();
	public void setName(String name);
	public String getIdRef();
}
