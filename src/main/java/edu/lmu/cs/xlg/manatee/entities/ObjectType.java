package edu.lmu.cs.xlg.manatee.entities;

import java.util.List;

/*import src.main.java.edu.lmu.cs.xlg.manatee.entities.Subroutine;
import src.main.java.edu.lmu.cs.xlg.manatee.entities.SymbolTable;*/
import edu.lmu.cs.xlg.util.Log;

public class ObjectType extends Type {
	List<Property> properties;
	
	public static class Property extends Entity {
		String name;
		String p;
		
		public void analyze(Log log, SymbolTable table, Subroutine owner, boolean inLoop) {
			//p.analyze(log, table, owner, inLoop);
	    }
		
		public Property(String name, String p) {
			this.name = name;
			this.p = p;
		}
		
		/*
		 * 
		 * new ObjectType.Property(propertyName.image,
            propertyType)
		 */
	}
	
	public ObjectType(String name, List<Property> properties) {
		super(name);
		this.properties = properties;
	}

}