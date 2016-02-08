package compiler;

import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class StringHelper {
	// NIVO B
	public static final int STR1 = 0, LEN1 = 1, STR2 = 2, LEN2 = 3, RES = 4, I = 5, J = 6, LEN3 = 7; 
	public static final Struct stringType = new Struct(Struct.Array, Tab.charType);
	public static final Struct boolType = new Struct(Struct.Int);
	public Obj printsCall = Tab.insert(Obj.Meth, "prints", Tab.noType),
			   readsCall = Tab.insert(Obj.Meth, "reads", stringType),
			   cmpStrCall = Tab.insert(Obj.Meth, "cmpStr", Tab.intType),
			   concatCall = Tab.insert(Obj.Meth, "concat", stringType);
	
	
	// NIVO B
	public void declareFunction(String methName) {
		Obj o = Tab.find(methName);
		o.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(o.getLevel());
		Code.put(Tab.currentScope().getnVars());

		if (methName.equals("len")) {
			Code.put(Code.load_n + 0); // | par
			Code.put(Code.arraylength);
		} else if (methName.equals("ord")) {
			Code.put(Code.load_n + 0); // | par
			Code.loadConst(48);
			Code.put(Code.sub);
		} else {
			Code.put(Code.load_n + 0); // | par
			Code.loadConst(48);
			Code.put(Code.add);
		}

		// ... povr. vrednost na stek

		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	// NIVO B
	// inicijalizacija stringova
	public void stringLiteral(String s) {
		Code.loadConst(s.length() - 2); // n
		Code.put(Code.newarray);
		Code.put(0);

		int i = 0;
		for (; i < s.length() - 2; i++) {
			Code.put(Code.dup);
			Code.loadConst(i);
			Code.loadConst(s.charAt(i + 1));
			Code.put(Code.bastore);
		}
	}
	
	// NIVO B
	public void declareCmpStr() {
		int ARR1 = 0, ARR2 = 1, LEN1 = 2, LEN2 = 3, I = 4;
		Tab.openScope();
		Tab.insert(Obj.Var, "arr1", stringType).setFpPos(0);
		Tab.insert(Obj.Var, "arr2", stringType).setFpPos(1);
		Tab.insert(Obj.Var, "len1", Tab.intType).setFpPos(2);
		Tab.insert(Obj.Var, "len2", Tab.intType).setFpPos(3);
		cmpStrCall.setLevel(4);

		Tab.insert(Obj.Var, "i", Tab.intType);
		cmpStrCall.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(cmpStrCall.getLevel());
		Code.put(Tab.currentScope().getnVars());
		
		// IF ZA DUZINE
		Code.put(Code.load_n + LEN1);
		Code.put(Code.load_n + LEN2);
		Code.putFalseJump(Code.ne, 0);
		int p = Code.pc - 2;
		Code.loadConst(1);
		Code.putJump(0);
		int p2 = Code.pc - 2;
		Code.fixup(p);
		Code.loadConst(0);
		Code.fixup(p2);
		
		Code.loadConst(1);							// provera celokupnog if uslova
		Code.putFalseJump(Code.eq, 0);
		p = Code.pc - 2;
		Code.loadConst(0);
		Code.put(Code.exit);
		Code.put(Code.return_);
		Code.fixup(p);
		
		Code.loadConst(0);
		Code.put(Code.store);
		Code.put(I); 
		
		// WHILE PETLJA
		int t = Code.pc; 							// top = Code.pc

		Code.put(Code.load);
		Code.put(I);
		Code.put(Code.load_n + LEN1); 				// stek 	| i len1
		Code.putFalseJump(Code.lt, 0); 				// ako je >= idi na else
		p = Code.pc - 2;
		Code.loadConst(1); 							// ako je < postavi 1(true) na stek
		Code.putJump(0);
		p2 = Code.pc - 2;
		Code.fixup(p);
		Code.loadConst(0); 							// ako je netacan uslov postavi 0(false) na stek
		Code.fixup(p2);

		Code.loadConst(1);							// provera celokupnog while uslova
		Code.putFalseJump(Code.eq, 0);
		p = Code.pc - 2;

		// OPERACIJE UNUTAR WHILE PETLJE
		Code.put(Code.load_n + ARR1);
		Code.put(Code.load);
		Code.put(I); 								// stek 	| arr1 i
		Code.put(Code.baload);						// stek		| arr1[i]
		Code.put(Code.load_n + ARR2);
		Code.put(Code.load);
		Code.put(I); 								// stek 	| arr2 i
		Code.put(Code.baload);						// stek		| arr2[i]
		
		Code.putFalseJump(Code.ne, 0);
		int p3 = Code.pc - 2;
		Code.loadConst(1);
		Code.putJump(0);
		p2 = Code.pc - 2;
		Code.fixup(p3);
		Code.loadConst(0);
		Code.fixup(p2);
		
		Code.loadConst(1);							// provera celokupnog if uslova
		Code.putFalseJump(Code.eq, 0);
		p3 = Code.pc - 2;
		Code.loadConst(0);
		Code.put(Code.exit);
		Code.put(Code.return_);
		Code.fixup(p3);
		
		// INKREMENTIRANJE
		Code.put(Code.load);
		Code.put(I);
		Code.loadConst(1);
		Code.put(Code.add); 						// i + 1
		Code.put(Code.store);
		Code.put(I); 								// i = i + 1

		// KRAJ WHILE PETLJE
		Code.putJump(t);
		Code.fixup(p);
		
		// POVRATNA VREDNOST NA STEK
		Code.loadConst(1);
				
		Tab.chainLocalSymbols(cmpStrCall);
		Tab.closeScope();

		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	// NIVO B
	public void callCmpStr() {
		int dest_adr = cmpStrCall.getAdr() - Code.pc; // relativna adresa
		Code.put(Code.call);
		Code.put2(dest_adr);	
	}

	// NIVO B
	public void declareConcat() {
		//concatCall = Tab.insert(Obj.Meth, "concat", stringType);
		Tab.openScope();
		Tab.insert(Obj.Var, "str1", stringType).setFpPos(0);
		Tab.insert(Obj.Var, "len1", Tab.intType).setFpPos(1);
		Tab.insert(Obj.Var, "str2", stringType).setFpPos(2);
		Tab.insert(Obj.Var, "len2", Tab.intType).setFpPos(3);
		concatCall.setLevel(4);

		Tab.insert(Obj.Var, "res", Tab.intType);
		Tab.insert(Obj.Var, "i", Tab.intType);
		Tab.insert(Obj.Var, "j", Tab.intType);
		Tab.insert(Obj.Var, "len3", Tab.intType);
		concatCall.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(concatCall.getLevel());
		Code.put(Tab.currentScope().getnVars());

		Code.put(Code.load_n + LEN1);
		Code.put(Code.load_n + LEN2);
		Code.put(Code.add); 						// len1 + len2
		Code.put(Code.store);
		Code.put(LEN3); 							// len3 = len1 + len2
		Code.put(Code.load);
		Code.put(LEN3);
		Code.put(Code.newarray);
		Code.put(0); 								// new char[len3]
		Code.put(Code.store);
		Code.put(RES); 								// res = new char[len3]
		Code.loadConst(0);
		Code.put(Code.store);
		Code.put(I); 								// i = 0

		copyStr1();
		copyStr2();
		Code.put(Code.load);
		Code.put(RES); 								// adresa rez stringa na stek

		Tab.chainLocalSymbols(concatCall);
		Tab.closeScope();

		Code.put(Code.exit);
		Code.put(Code.return_);

	}

	// NIVO B
	public void copyStr1() {
		// WHILE PETLJA
		int t = Code.pc; 							// top = Code.pc

		Code.put(Code.load);
		Code.put(I);
		Code.put(Code.load_n + LEN1); 				// stek 	| i len1
		Code.putFalseJump(Code.lt, 0); 				// ako je >= idi na else
		int p = Code.pc - 2;
		Code.loadConst(1); 							// ako je < postavi 1(true) na stek
		Code.putJump(0);
		int p2 = Code.pc - 2;
		Code.fixup(p);
		Code.loadConst(0); 							// ako je netacan uslov postavi 0(false) na stek
		Code.fixup(p2);

		Code.loadConst(1);							// provera celokupnog while uslova
		Code.putFalseJump(Code.eq, 0);
		p = Code.pc - 2;

		// OPERACIJE UNUTAR WHILE PETLJE
		Code.put(Code.load);
		Code.put(RES);
		Code.put(Code.load);
		Code.put(I); 								// stek 	| res i
		Code.put(Code.load_n + STR1);
		Code.put(Code.load);
		Code.put(I); 								// stek 	| res i str1 i
		Code.put(Code.baload); 						// stek 	| res i str1[i]
		Code.put(Code.bastore);						// res[i] = str1[i]
		Code.put(Code.load);
		Code.put(I);
		Code.loadConst(1);
		Code.put(Code.add); 						// i + 1
		Code.put(Code.store);
		Code.put(I); 								// i = i + 1

		// KRAJ WHILE PETLJE
		Code.putJump(t);
		Code.fixup(p);
	}

	// NIVO B
	public void copyStr2() {
		// WHILE PETLJA
		int t = Code.pc; 							// top = Code.pc

		Code.put(Code.load);
		Code.put(J);
		Code.put(Code.load_n + LEN2); 				// stek 	| j len2
		Code.putFalseJump(Code.lt, 0); 				// ako je >= idi na else
		int p = Code.pc - 2;
		Code.loadConst(1); 							// ako je < postavi 1(true) na stek
		Code.putJump(0);
		int p2 = Code.pc - 2;
		Code.fixup(p);
		Code.loadConst(0); 							// ako je netacan uslov postavi 0(false) na stek
		Code.fixup(p2);

		Code.loadConst(1); 							// provera celokupnog while uslova
		Code.putFalseJump(Code.eq, 0);
		p = Code.pc - 2;

		// OPERACIJE UNUTAR WHILE PETLJE
		Code.put(Code.load);
		Code.put(RES);
		Code.put(Code.load);
		Code.put(I); 								// stek 	| res i
		Code.put(Code.load_n + STR2);
		Code.put(Code.load);
		Code.put(J); 								// stek 	| res i str2 j
		Code.put(Code.baload); 						// stek 	| res i str2[j]
		Code.put(Code.bastore); 					// res[i] = str2[j]
		Code.put(Code.load);
		Code.put(I);
		Code.loadConst(1);
		Code.put(Code.add); 						// i + 1
		Code.put(Code.store);
		Code.put(I); 								// i = i + 1
		Code.put(Code.load);
		Code.put(J);
		Code.loadConst(1);
		Code.put(Code.add); 						// j + 1
		Code.put(Code.store);
		Code.put(J); 								// j = j + 1

		// KRAJ WHILE PETLJE
		Code.putJump(t);
		Code.fixup(p);
	}

	// NIVO B
	public void callConcat() {
		int dest_adr = concatCall.getAdr() - Code.pc; // relativna adresa
		Code.put(Code.call);
		Code.put2(dest_adr);

	}
	
	// NIVO B
	public void declarePrints() {
		int STR = 0, LEN = 1, I = 2;
		//printsCall = Tab.insert(Obj.Meth, "prints", stringType);
		Tab.openScope();
		Tab.insert(Obj.Var, "str", stringType).setFpPos(0);
		Tab.insert(Obj.Var, "len", Tab.intType).setFpPos(1);
		printsCall.setLevel(2);

		Tab.insert(Obj.Var, "i", Tab.intType);
		printsCall.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(printsCall.getLevel());
		Code.put(Tab.currentScope().getnVars());
		
		Code.loadConst(0);
		Code.put(Code.store_n + I);
		
		// WHILE PETLJA
		int t = Code.pc; 							// top = Code.pc

		Code.put(Code.load_n + I);
		Code.put(Code.load_n + LEN); 				// stek 	| i len
		Code.putFalseJump(Code.lt, 0); 				// ako je >= idi na else
		int p = Code.pc - 2;
		Code.loadConst(1); 							// ako je < postavi 1(true) na stek
		Code.putJump(0);
		int p2 = Code.pc - 2;
		Code.fixup(p);
		Code.loadConst(0); 							// ako je netacan uslov postavi 0(false) na stek
		Code.fixup(p2);

		Code.loadConst(1);							// provera celokupnog while uslova
		Code.putFalseJump(Code.eq, 0);
		p = Code.pc - 2;

		// OPERACIJE UNUTAR WHILE PETLJE
		Code.put(Code.load_n + STR);				// stek		| str
		Code.put(Code.load_n + I);					// stek		| str i
		Code.put(Code.baload);						// stek		| str[i]
		Code.loadConst(1);							// stek		| str[i] 1
		Code.put(Code.bprint);
		Code.put(Code.load_n + I);
		Code.loadConst(1);
		Code.put(Code.add); 						// i + 1
		Code.put(Code.store_n + I);					// i = i + 1 								

		// KRAJ WHILE PETLJE
		Code.putJump(t);
		Code.fixup(p);
		
		Tab.chainLocalSymbols(printsCall);
		Tab.closeScope();

		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	// NIVO B
	public void callPrints() {
		int dest_adr = printsCall.getAdr() - Code.pc; // relativna adresa
		Code.put(Code.call);
		Code.put2(dest_adr);	
	}
	
	// NIVO B
	public void declareReads() {
		int I = 0, LEN = 1, C = 2, RES = 3;
		Tab.openScope();
		readsCall.setLevel(0);
		Tab.insert(Obj.Var, "i", Tab.intType);
		Tab.insert(Obj.Var, "len", Tab.intType);
		Tab.insert(Obj.Var, "c", Tab.charType);
		Tab.insert(Obj.Var, "res", Tab.intType);
		readsCall.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(readsCall.getLevel());
		Code.put(Tab.currentScope().getnVars());
		
		Code.loadConst(0);
		Code.put(Code.store_n + LEN);				// len = 0
		Code.put(Code.bread);						// stek	| val
		
		// WHILE PETLJA
		int t = Code.pc; 							// top = Code.pc
		Code.put(Code.dup);							// stek	| val val
		Code.loadConst(13);							// stek	| val val 13
		Code.putFalseJump(Code.ne, 0); 				// ako je == idi na else
		int p = Code.pc - 2;
		Code.loadConst(1); 							// ako je != postavi 1(true) na stek
		Code.putJump(0);
		int p2 = Code.pc - 2;
		Code.fixup(p);
		Code.loadConst(0); 							// ako je netacan uslov postavi 0(false) na stek
		Code.fixup(p2);

		Code.loadConst(1);							// provera celokupnog while uslova
		Code.putFalseJump(Code.eq, 0);
		p = Code.pc - 2;

		// OPERACIJE UNUTAR WHILE PETLJE			// stek		| val
		// INKREMENTIRANJE
		Code.loadConst(1);
		Code.put(Code.load_n + LEN);
		Code.put(Code.add);
		Code.put(Code.store_n + LEN);
		Code.put(Code.bread);						// stek		| val val1
		
		// KRAJ WHILE PETLJE
		Code.putJump(t);
		Code.fixup(p);
		
		// SKIDAMO SA STEKA LF + CR
		Code.put(Code.pop);
		//Code.put(Code.bread);
		//Code.put(Code.pop);
		
		// KREIRANJE NOVOG NIZA
		Code.put(Code.load_n + LEN);
		Code.put(Code.newarray);
		Code.put(0);
		Code.put(Code.store_n + RES);				// res = new char[len]
		
		Code.put(Code.load_n + LEN);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.put(Code.store_n + I);					// i = len - 1
		
		// WHILE PETLJA								// stek		| val val1 val2 ... valn
		t = Code.pc; 								// top = Code.pc
		Code.put(Code.load_n + I);
		Code.loadConst(0);
		Code.putFalseJump(Code.ge, 0); 				// ako je < idi na else
		p = Code.pc - 2;
		Code.loadConst(1); 							// ako je >= postavi 1(true) na stek
		Code.putJump(0);
		p2 = Code.pc - 2;
		Code.fixup(p);
		Code.loadConst(0); 							// ako je netacan uslov postavi 0(false) na stek
		Code.fixup(p2);

		Code.loadConst(1);							// provera celokupnog while uslova
		Code.putFalseJump(Code.eq, 0);
		p = Code.pc - 2;

		// OPERACIJE UNUTAR WHILE PETLJE			// stek		| val val1 val2 ... valn
		Code.put(Code.store_n + C);					// c = vrednost sa vrha steka
		Code.put(Code.load_n + RES);
		Code.put(Code.load_n + I);
		Code.put(Code.load_n + C);					// stek		| res i c
		Code.put(Code.bastore);						// res[i] = c
		// DEKREMENTIRANJE
		Code.put(Code.load_n + I);
		Code.loadConst(1);		
		Code.put(Code.sub);
		Code.put(Code.store_n + I);
		
		// KRAJ WHILE PETLJE
		Code.putJump(t);
		Code.fixup(p);
		
		// POVRATNA VREDNOST NA STEK
		Code.put(Code.load_n + RES);

		Tab.chainLocalSymbols(readsCall);
		Tab.closeScope();

		Code.put(Code.exit);
		Code.put(Code.return_);
		
	}
	
	// NIVO B
	public void callReads() {
		int dest_adr = readsCall.getAdr() - Code.pc; // relativna adresa
		Code.put(Code.call);
		Code.put2(dest_adr);	
	}
}
