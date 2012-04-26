package edu.lmu.cs.xlg.manatee.entities;

import java.util.ArrayList;
import java.util.List;

import org.GNOME.Accessibility.Table;

import edu.lmu.cs.xlg.util.Log;

public class ObjectType extends Type {
    private List<Property> properties;
    
    public static class Property extends Entity {
        String name;
        Type type;
        
        public void analyze(Log log, SymbolTable table, Subroutine owner, boolean inLoop) {
            // This does absolutely nothing since the Type analyze method is empty
            this.type.analyze(log, table, owner, inLoop);
        }
        
        public Property(String name, String type) {
            this.name = name;
            
            /* TODO:
             * We can not create a new Type to be assigned to this property since in the ObjectLiteral
             * analyzer, the == operator will fail to properly compare the Types. this.type needs to
             * be assigned to the already-constructed respective Type in the Type class.
             */
            this.type = new Type(type);
        }
        
        public String getName() {
            return name;
        }
        
        public Type getType() {
            return type;
        }
    }
    
    public List<Property> getProperties() {
        return properties;
    }
    
    public void analyze(Log log, SymbolTable table, Subroutine owner, boolean inLoop) {
        ArrayList<String> history = new ArrayList<String>();
        for (Property p: this.properties) {
            if (history.contains(p.name)) {
                log.error("Duplicate property IDs in ObjectType.");
            } else {
                history.add(p.name);
            }
        }
        table.insert(this, log);
    }
    
    public ObjectType(String name, List<Property> properties) {
        super(name);
        this.properties = properties;
    }

}