package edu.lmu.cs.xlg.manatee.entities;

import edu.lmu.cs.xlg.util.Log;

/**
 * An increment statement.
 */
public class IncrementStatement extends Statement {

    private Expression target;
    private Expression delta;

    public IncrementStatement(Expression target, Expression delta) {
        this.target = target;
        
        // If delta is not given, assume increment of 1
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
            log.error("non.writable.in.increment.statement");
        }
        
        // Ensure that target and delta are both integers
        delta.analyze(log, table, owner, inLoop);
        delta.assertInteger("Increment delta value is not a whole number.", log);
        target.assertInteger("Target of increment is not a whole number.", log);
    }
}
