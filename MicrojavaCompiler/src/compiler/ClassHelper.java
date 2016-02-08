package compiler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class ClassHelper {
	public static int VTABLE = 0;
	public static final Map<Struct, Obj> classDefs = new LinkedHashMap<Struct, Obj>();
	public Obj dummyCall = Tab.insert(Obj.Meth, "dummy", Tab.noType);
	
	public static void createInstance(Struct type) {
		int num = type.getNumberOfFields();
		num = (num + 1) * 4;					// broj polja + 1 za v-tabelu
		Code.put(Code.new_);
		Code.put2(num);				
												// stek		| adrObjekta		
		
		Code.put(Code.dup);						// stek		| adrObjekta adrObjekta
		Obj cl = classDefs.get(type);
		Code.loadConst(cl.getAdr());			// stek		| adrObjekta adrObjekta adrVTabele
		Code.put(Code.putfield);
		Code.put2(VTABLE);						// stek		| adrObjekta
		
	}
	
	public static int allocVTables(int lp) {
		Iterator<Obj> iter = classDefs.values().iterator();
		// ide po klasama
		while (iter.hasNext()) {
			Obj cl = iter.next();
			Iterator<Obj> membersIter = cl.getType().getMembers().symbols().iterator();
			// postavlja adresu Vtabele u adr polje Obj cvora klase
			cl.setAdr(lp);
			// ide po svim clanovima klase
			while (membersIter.hasNext()) {
				Obj meth = membersIter.next();
				// ako je clan klase metoda
				if (meth.getKind() == Obj.Meth) {
					String methName = meth.getName();
					for (int i = 0; i < methName.length(); i++) {
						Code.loadConst(methName.charAt(i));
						Code.put(Code.putstatic);
						Code.put2(lp++);
					}
					// kraj imena metode
					Code.loadConst(-1);
					Code.put(Code.putstatic);
					Code.put2(lp++);
					// adresa metode
					Code.loadConst(meth.getAdr());
					Code.put(Code.putstatic);
					Code.put2(lp++);
				}
			}
			// kraj vtabele
			Code.loadConst(-2);
			Code.put(Code.putstatic);
			Code.put2(lp++);
		}
		return lp;
	}
	
	public void declareDummyCall() {
		int ADR = 0, PAR = 1;
		Tab.openScope();
		Tab.insert(Obj.Var, "adr", Tab.intType).setFpPos(0);;
		Tab.insert(Obj.Var, "par", Tab.intType).setFpPos(1);
		dummyCall.setLevel(2);
		dummyCall.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(dummyCall.getLevel());
		Code.put(Tab.currentScope().getnVars());
		
		Code.put(Code.load_n + PAR);
		Code.put(Code.load_n + ADR);
		
		Tab.chainLocalSymbols(dummyCall);
		Tab.closeScope();
		
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	public void dummyCall() {
		int dest_adr = dummyCall.getAdr() - Code.pc; // relativna adresa
		Code.put(Code.call);
		Code.put2(dest_adr);	
	}

}
