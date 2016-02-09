package compiler;

import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.visitors.DumpSymbolTableVisitor;

public class Visitor extends DumpSymbolTableVisitor {
	public static Struct boolType = null, stringType = null;
	
	@Override
	public void visitObjNode(Obj objToVisit) {
		//output.append("[");
		switch (objToVisit.getKind()) {
		case Obj.Con:  output.append("Con "); break;
		case Obj.Var:  output.append("Var "); break;
		case Obj.Type: output.append("Type "); break;
		case Obj.Meth: output.append("Meth "); break;
		case Obj.Fld:  output.append("Fld "); break;
		case Obj.Prog: output.append("Prog "); break;
		}
		
		output.append(objToVisit.getName());
		output.append(": ");
		
		if ((Obj.Var == objToVisit.getKind()) && "this".equalsIgnoreCase(objToVisit.getName()))
			output.append("this pointer");
		else {
			if (objToVisit.getType().getKind() != Struct.Class) {
				objToVisit.getType().accept(this);
				
				output.append(", ");
				output.append(objToVisit.getAdr());
				output.append(", ");
				output.append(objToVisit.getLevel() + " ");
			} else {
				output.append("Class");
				output.append(", ");
				output.append(objToVisit.getAdr());
				output.append(", ");
				output.append(objToVisit.getLevel() + "\n");
				nextIndentationLevel();
				objToVisit.getType().accept(this);
				previousIndentationLevel();
			}
		}

		
		

				
		if (objToVisit.getKind() == Obj.Prog || objToVisit.getKind() == Obj.Meth) {
			output.append("\n");
			nextIndentationLevel();
		}
		

		for (Obj o : objToVisit.getLocalSymbols()) {
			output.append(currentIndent.toString());
			o.accept(this);
			output.append("\n");
		}
		
		if (objToVisit.getKind() == Obj.Prog || objToVisit.getKind() == Obj.Meth) 
			previousIndentationLevel();

		//output.append("]");
		
	}
	
	@Override
	public void visitStructNode(Struct structToVisit) {
		if (structToVisit == stringType) {
			output.append("string");
			return;
		} else if (structToVisit == boolType) {
			output.append("bool");
			return;
		}
		
		switch (structToVisit.getKind()) {
		case Struct.None:
			output.append("notype");
			break;
		case Struct.Int:
			output.append("int");
			break;
		case Struct.Char:
			output.append("char");
			break;
		case Struct.Array:
			output.append("Arr of ");
			
			switch (structToVisit.getElemType().getKind()) {
			case Struct.None:
				output.append("notype");
				break;
			case Struct.Int:
				output.append("int");
				break;
			case Struct.Char:
				output.append("char");
				break;
			case Struct.Class:
				output.append("Class");
				break;
			}
			break;
		case Struct.Class:
			output.append(currentIndent.toString());
			output.append("[\n");
			for (Obj obj : structToVisit.getMembers().symbols()) {
				output.append(currentIndent.toString()).append("  ");
				obj.accept(this);
				if (obj.getKind() != Obj.Meth) 
					output.append("\n");				
			}
			output.append(currentIndent.toString());
			output.append("]");
			break;
		}

	}
}
