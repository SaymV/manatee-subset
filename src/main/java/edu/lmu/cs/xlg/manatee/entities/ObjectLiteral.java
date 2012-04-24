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
        private Type type;        // This needs to be set in the analyzer
        private Expression value; // ERRTHANG extends Expression
        
        public Arg(String key, Expression value) {
            this.key = key;
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
        
        public void analyze(Log log, SymbolTable table, Subroutine owner, boolean inLoop) {
            // TODO: Type needs to be set and checked for in the symbol table
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
        Type t = table.lookupType(typeName, log);
        ObjectType o = ObjectType.class.cast(t);
        // Used to represent all properties not found yet in args
        ArrayList<String> unfoundProperties = new ArrayList<String>();
        if (t == null) {
            log.error("Undefined type.");
        }
        if (o.getProperties().size() != args.size()) {
            log.error("Size conflict with expected object properties.");
        }
        // Populate unfound properties list
        for (ObjectType.Property p: o.getProperties()) {
            unfoundProperties.add(p.getName());
        }
        // Search args for unfound properties
        for (Arg a: args) {
            if (unfoundProperties.contains(a.getKey())) {
                // TODO: must verify that property and arg types match
                // If property found, remove it from unfound properties list
                unfoundProperties.remove(a.getKey());
            } else {
                log.error("Duplicate property or not in properties.");
            }
        }
        // If any unfound properties in args, log error
        if (unfoundProperties.size() > 0) {
            log.error("Unassigned properties.");
        }
    }
    
}
