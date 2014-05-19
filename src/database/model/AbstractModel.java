package database.model;

import java.io.Serializable;


public abstract class AbstractModel implements Model, Serializable {
	private static final long serialVersionUID = 1L;
	
	protected String name;
	
	public AbstractModel(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
