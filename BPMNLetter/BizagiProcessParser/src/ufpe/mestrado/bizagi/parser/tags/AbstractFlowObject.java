package ufpe.mestrado.bizagi.parser.tags;

public class AbstractFlowObject extends AbstractBaseObjetct{
	
	private String name;

	public AbstractFlowObject(String id, String name) {
		super(id);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return super.toString()+ " name= " + name;
	}
	
	

}
