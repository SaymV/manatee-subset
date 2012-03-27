package edu.lmu.cs.xlg.manatee.entities;

import edu.lmu.cs.xlg.util.Log;

/**
 * An assignment statement.
 */
public class DecrementStatement extends Statement {

    private Expression target;
    private Expression delta;

    public DecrementStatement(Expression target, Expression delta) {
        this.target = target;
        this.delta = delta;
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
        delta.analyze(log, table, owner, inLoop);
        // Should target be an expression or an ID? Discuss...
        if (!target.isWritableLValue()) {
            log.error("non.writable.in.decrement.statement");
        }
        delta.assertInteger("Decrement delta value is not a whole number.", log);
        target.assertInteger("Target of decrement is not a whole number.", log);
        
        // assertAssignableTo does type checking and therefore is irrelevant?
        // target.assertAssignableTo(target.getType(), log, "increment.type.mismatch");
    }
}
