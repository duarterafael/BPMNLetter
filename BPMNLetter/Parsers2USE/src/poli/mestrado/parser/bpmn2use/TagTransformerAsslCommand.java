package poli.mestrado.parser.bpmn2use;

import java.util.ArrayList;
import java.util.List;

import poli.mestrado.parser.util.Utility;

public class TagTransformerAsslCommand {
	
	public static List<String> conditionExpression2AsslCommand(String conditionExpression){
		List<String> retunAsslCommandList =  new ArrayList<String>();
		String[] comands = conditionExpression.split(";");
		
		for (String string : comands) {
			retunAsslCommandList.add(string+";");
		}
		
		return retunAsslCommandList;
	}
	

}
