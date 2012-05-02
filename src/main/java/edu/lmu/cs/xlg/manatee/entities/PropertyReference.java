package edu.lmu.cs.xlg.manatee.entities;

import edu.lmu.cs.xlg.util.Log;

public class PropertyReference extends Expression {
    private Expression object;
    private String subscript;
    
    public PropertyReference(Expression e, String subscript) {
        // e is an ObjectLiteral
        // subscript is the property being accessed
        // need to check to see that the property being accessed exists in e
        this.object = e;
        this.subscript = subscript;
    }
    
    @Override
    boolean isWritableLValue() {
        return true;
    }
    
    @Override
    public void analyze(Log log, SymbolTable table, Subroutine owner, boolean inLoop) {
        // TODO Auto-generated method stub
        //object.analyze(log, table, owner, inLoop);
        /*this.object = ObjectLiteral.class.cast(object.getType());
        ObjectLiteral.class.cast(object.getType()).analyze(log, table, owner, inLoop);
        System.out.println("mouallem: " + object.getType().getName());
        for (ObjectType.Property p : ObjectType.class.cast(object.getType()).getProperties()) {
            if (p.getName().equals(this.subscript)) {
                super.type = p.getType();
            }
            
        }*/
    }

}
