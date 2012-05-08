package edu.lmu.cs.xlg.manatee.entities;

import edu.lmu.cs.xlg.util.Log;

/**
 * A decrement statement.
 */
public class DecrementStatement extends Statement {

    private Expression target;
    private Expression delta;

    public DecrementStatement(Expression target, Expression delta) {
        this.target = target;
        
        // If delta is not provided, assume decrement by 1
        if (delta == null) {
            this.delta = new WholeNumberLiteral("1");
        } else {
            this.delta = delta;
        }
    }

    public Expression getTarget() {
        return target;
    }

    public Expression getDelta() {
        return delta;
    }

    @Override
    public void analyze(Log log, SymbolTable table, Subroutine owner, boolean inLoop) {
        target.analyze(log, table, owner, inLoop);
        
        if (!target.isWritableLValue()) {
            log.error("non.writable.in.decrement.statement");
        }
        
        // Check that delta and target are both integers
        delta.analyze(log, table, owner, inLoop);
        delta.assertInteger("Decrement delta value is not a whole number.", log);
        target.assertInteger("Target of decrement is not a whole number.", log);
    }
}
