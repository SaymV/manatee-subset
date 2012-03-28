package edu.lmu.cs.xlg.manatee.entities;

import edu.lmu.cs.xlg.util.Log;

public class TryStatement extends Statement {
	private Block tryBlock;
    private Block recoverBlock;
    
    public TryStatement( Block tryBlock, Block recoverBlock) {
        this.tryBlock = tryBlock;
        this.recoverBlock = recoverBlock;
    }
    
    // Add getters
    public Block getTryBlock() {
        return tryBlock;
    }
    
    public Block getRecoverBlock() {
        return recoverBlock;
    }
    
	@Override
	public void analyze(Log log, SymbolTable table, Subroutine owner,
			boolean inLoop) {
		tryBlock.analyze(log, table, owner, inLoop);
        recoverBlock.analyze(log, table, owner, inLoop);
	}
}
