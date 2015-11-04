package poli.mestrado.parser.uml2use.tag;

import java.util.HashMap;

public class DataTypeTag implements IType {
	
	private String id;
	private String name;
	private HashMap<String, String> dataTypeHash;
	

	public DataTypeTag(String href) {
		super();
		
		dataTypeHash = new HashMap<String, String>();
        dataTypeHash.put("-84-17--56-5-43645a83:11466542d86:-8000:000000000000087C", "Integer");
        dataTypeHash.put("-84-17--56-5-43645a83:11466542d86:-8000:000000000000087D", "Integer");
        dataTypeHash.put("-84-17--56-5-43645a83:11466542d86:-8000:000000000000087E", "String");
        dataTypeHash.put("-84-17--56-5-43645a83:11466542d86:-8000:0000000000000880", "Boolean");
		
        this.id =  href.split("#")[1];
        if(dataTypeHash.containsKey(this.id)){
        	name = dataTypeHash.get(this.id);
        }
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String href) {
		String key = href.split("#")[1];
        if(dataTypeHash.containsKey(key)){
        	name = dataTypeHash.get(key);
        }else{
        	name = key;
        }
	}

	@Override
	public String getIdRef() {
		return this.id;
	}
	
	
	@Override
	public String toString() {
		return name;
	}

}
