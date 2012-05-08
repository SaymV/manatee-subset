package edu.lmu.cs.xlg.manatee.entities;

import edu.lmu.cs.xlg.util.Log;


/**
 * This was completed before it was deprecated.
 */
public class ImportStatement extends Statement {
	
	private String moduleName;
	
	public ImportStatement(String module) {
		this.moduleName = module;
	}
	
	public String getModuleName() {
		return this.moduleName;
	}

	@Override
	public void analyze(Log log, SymbolTable table, Subroutine owner, boolean inLoop) {
	}

}
