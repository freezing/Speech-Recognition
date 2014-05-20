package database.model;

import vector_quantization.Codebook;

public class CodebookModel extends AbstractModel {
	private static final long serialVersionUID = 1L;
	public static final String CODEBOOK_MODEL_EXTENSION = ".cbm";
	
	private Codebook codebook;

	public CodebookModel(Codebook codebook, String name) {
		super(name);
		this.codebook = codebook;
	}

	@Override
	public String getExtension() {
		return CODEBOOK_MODEL_EXTENSION;
	}
	
	public void setCodebook(Codebook codebook) {
		this.codebook = codebook;
	}
	
	public Codebook getCodebook() {
		return codebook;
	}
	
}
