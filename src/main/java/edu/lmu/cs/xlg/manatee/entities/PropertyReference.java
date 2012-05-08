package edu.lmu.cs.xlg.manatee.entities;

import edu.lmu.cs.xlg.util.Log;

public class PropertyReference extends Expression {
    private Expression object;
    private String subscript;
    
    public PropertyReference(Expression e, String subscript) {
        this.object = e;
        this.subscript = subscript;
    }
    
    @Override
    boolean isWritableLValue() {
        return true;
    }
    
    @Override
    public void analyze(Log log, SymbolTable table, Subroutine owner, boolean inLoop) {
        this.object.analyze(log, table, owner, inLoop);
        type = ObjectType.class.cast(this.object.getType());
        
        if (type == null) {
            log.error("Invalid object property.");
        }
        
        for (ObjectType.Property p : ObjectType.class.cast(this.object.getType()).getProperties()) {
            if (p.getName().equals(subscript)) {
                return;
            }
        }
        log.error("Property name does not exist.");
    }
}