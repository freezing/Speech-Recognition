package database.model;

public class CodebookModel extends AbstractModel {
	private static final long serialVersionUID = 1L;
	private static final String CODEBOOK_MODEL_EXTENSION = ".cbm";

	public CodebookModel(String name) {
		super(name);
	}

	@Override
	public String getExtension() {
		return CODEBOOK_MODEL_EXTENSION;
	}
	
}
