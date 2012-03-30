package edu.lmu.cs.xlg.manatee.entities;

import edu.lmu.cs.xlg.util.Log;
import java.util.ArrayList;

/**
 * An assignment statement.
 */
public class AssignmentStatement extends Statement {

    private ArrayList<Expression> target;
    private ArrayList<Expression> source;

    public AssignmentStatement(ArrayList<Expression> target, ArrayList<Expression> source) {
        this.target = target;
        this.source = source;
    }

    public int getTargetLength() {
    	return this.target.size();
    }
    
    public Expression getTarget(int i) {
        return target.get(i);
    }

    public Expression getSource(int i) {
        return source.get(i);
    }

    @Override
    public void analyze(Log log, SymbolTable table, Subroutine owner, boolean inLoop) {
    	for (int x = 0; x < this.target.size(); x++) {
    		target.get(x).analyze(log, table, owner, inLoop);
            source.get(x).analyze(log, table, owner, inLoop);
            if (!target.get(x).isWritableLValue()) {
                log.error("non.writable.in.assignment.statement");
            }
            source.get(x).assertAssignableTo(target.get(x).getType(), log, "assignment.type.mismatch");
    	}
    }
}
