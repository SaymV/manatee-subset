package edu.lmu.cs.xlg.manatee.entities;

import java.util.ArrayList;

import edu.lmu.cs.xlg.util.Log;

public class ObjectLiteral extends Expression {
    private String typeName;
    private ArrayList<Arg> args = new ArrayList<Arg>();

    public ObjectLiteral(String typeName, ArrayList<Arg> args) {
        this.typeName = typeName;
        this.args = args;
    }
    
    public static class Arg {
        private String key;
        private Type type;
        private Expression value; // ERRTHANG extends Expression
        
        public Arg(String key, Expression value) {
            this.key = key;
            this.type = value.getType();
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public Type getType() {
            return type;
        }

        public Expression getValue() {
            return value;
        }
    }
    
    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public ArrayList<Arg> getArgs() {
        return args;
    }
    public void setArgs(ArrayList<Arg> args) {
        this.args = args;
    }
    
    @Override
    public void analyze(Log log, SymbolTable table, Subroutine owner, boolean inLoop) {
        // Intentionally empty.
    }
    
}
