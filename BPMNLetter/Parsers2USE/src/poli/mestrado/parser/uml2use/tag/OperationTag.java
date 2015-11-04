package poli.mestrado.parser.uml2use.tag;

import java.util.ArrayList;
import java.util.List;

public class OperationTag extends BaseTag{

	private List<ParameterTag> parameterList;
	public static final String TAG_NAME = "UML:Operation";

	
	public OperationTag(String id, String name, String visibility, List<ParameterTag> parameterList) {
		super(id, name);
		this.parameterList = new ArrayList<ParameterTag>(parameterList);
	}
	
	public List<ParameterTag> getParameterList() {
		return parameterList;
	}

	public void addParameter(ParameterTag parameter) {
		this.parameterList.add(parameter);
	}
	
	@Override
	public String toString() {
		
		String operationStr = "\t";
		String returnParameter = "";
		
		operationStr += super.getName() + " (";
		for (ParameterTag parameterTag : parameterList) {
			if(!parameterTag.getKind().equalsIgnoreCase("return") && parameterTag.getType() != null){
				operationStr += " "+parameterTag.getName() +" : "+parameterTag.getType().getName()+",";
			}else{
				if(parameterTag.getType() != null && parameterTag.getType() != null){
					returnParameter = " : "+parameterTag.getType().getName();
				}
			}
		}
		if(operationStr.endsWith(",")){
			operationStr = operationStr.substring(0, operationStr.length()-1);
		}
		operationStr += ")"+returnParameter;
		
		return operationStr;
	}
	
	
	
	
	
	
}
