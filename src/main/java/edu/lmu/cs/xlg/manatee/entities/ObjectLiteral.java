package edu.lmu.cs.xlg.manatee.entities;

import java.util.ArrayList;
import java.util.HashMap;

import edu.lmu.cs.xlg.util.Log;

public class ObjectLiteral extends Expression {
    private String typeName;
    private ArrayList<Arg> args = new ArrayList<Arg>();

    public ObjectLiteral(String typeName, ArrayList<Arg> args) {
        this.typeName = typeName;
        this.args = args;
    }
    
    public static class Arg extends Expression {
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
        
        @Override
        public void analyze(Log log, SymbolTable table, Subroutine owner, boolean inLoop) {
            // TODO: Type needs to be set and checked for in the symbol table
            value.analyze(log, table, owner, inLoop);
            Type t = table.lookupType(value.getType().getName(), log);
            if (t == null) {
                log.error("Invalid object property type.");
            } else {
                type = t;
            }
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
    
    /*
    public Expression getPropertyValue(String id) {
        for (Arg a : this.args) {
            if (a.getKey().equals(id)) {
                return a;
            }
        }
        
        log.error("Nonexistent property.");
        return null;
    }
    */
    
    @Override
    public void analyze(Log log, SymbolTable table, Subroutine owner, boolean inLoop) {
        Type t = table.lookupType(typeName, log);
        ObjectType o = ObjectType.class.cast(t);
        
        // Used to represent all properties not found yet in args
        ArrayList<String> unfoundProperties = new ArrayList<String>();
        HashMap<String, Type> properties = new HashMap<String, Type>();
        
        if (t == null) {
            log.error("Undefined type.");
        }
        
        if (o.getProperties().size() != args.size()) {
            log.error("Size conflict with expected object properties.");
        }
        
        // Populate unfound properties list
        for (ObjectType.Property p: o.getProperties()) {
            unfoundProperties.add(p.getName());
            properties.put(p.getName(), p.getType());
        }
        
        // Search args for unfound properties
        for (Arg a: args) {
            if (unfoundProperties.contains(a.getKey())) {
                a.analyze(log, table, owner, inLoop);
                // If property found, remove it from unfound properties list
                unfoundProperties.remove(a.getKey());
                Type argType = a.getType();
                Type propType = properties.get(a.getKey());
                
                System.out.println("Type test from " + argType.getName()
                    + " to " + propType.getName()
                    + " is " + argType.canBeAssignedTo(propType));
                
                if (!argType.canBeAssignedTo(propType)) {
                    log.error("Object literal type mismatch.");
                }
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
