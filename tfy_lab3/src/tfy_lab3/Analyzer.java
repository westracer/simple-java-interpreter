package tfy_lab3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Analyzer {
	Scanner sc;
	Semantics sem;
	
	LinkedList stack = new LinkedList();
	Integer[] indices;
	
	Analyzer(Scanner s) {
		sc = s;
		sem = new Semantics();
	}
	
	void S() throws IOException
	{
		Types type;
		
		sc.SavePos();
		type = sc.Scan();
		while(type==Types.Tint || type==Types.Tint64 || type==Types.Ttypedef || type == Types.Tid)
		{
			Types oldType = type;
			sc.LoadPos();
			if (oldType != Types.Ttypedef)
				T();
			
			// ������ ��������?
			sem.checkTypeAndThrowErrors(sc.getLex(), oldType);

			type = sc.Scan();
			if(type==Types.Tmain)
			{
				sem.addVar(Types.Tint, sc.getLex(), 0, sc.pos_text);
				if (oldType==Types.Tint) {
					type = sc.Scan();
					if(type!=Types.TlPar)
						printError(Types.TlPar);
					type = sc.Scan();
					if(type!=Types.TrPar)
						printError(Types.TrPar);
					
					//���� �������
					D();
				}
				else
					printError(Types.Tint64);
			}
			else
			{
				if (oldType == Types.Ttypedef) {
					W();
				}
				else if(type==Types.Tsem)
					;
				else if(type!=Types.Tid)
					printError(Types.Tid);
				else
				{
					type = sc.Scan();
					if(type==Types.TlPar)
					{
						sc.LoadPos();
//						F();
					}
					else 
					{
						sc.LoadPos();
						B();
					}
					
				}
			}
			sc.SavePos();
			type = sc.Scan();
		}
		sc.LoadPos();
		if(type!=Types.Tend)
			{
				printError(Types.Terr);
		}
	}
	void T() throws IOException
	{
		Types type;

		type = sc.Scan();
		if(type==Types.Tint)
		{
//			type = sc.Scan();
//			if(type!=Types.Tint)
//				printError(Types.Tint);

		}
		else if(type==Types.Tint64)
		{
//			type = sc.Scan();
//			if(type!=Types.Tint)
//				printError(Types.Tint);

		} 
		else if (type == Types.Tid) {
			
		}
		if(type!=Types.Tint && type!=Types.Tint64 && type != Types.Tid)
		{
			printError(Types.Tint);
		}
	}
	//�������� ������
	void B() throws IOException
	{
		Types type;
		
		sc.SavePos();
		T();
		sc.LoadPos();
		Types dataType = sc.Scan();
		char[] dataTypeLex = sc.getLex();
		boolean isTypeOk = sem.checkTypeAndThrowErrors(dataTypeLex, dataType);
		
		//������ ���
		sc.SavePos();
		type = sc.Scan();
		if(type!=Types.Tid)
			printError(Types.Tid);
		
		if (isTypeOk) {
			sem.addVar(dataTypeLex, dataType, sc.getLex(), 0, sc.pos_text);
		}
		
		type = sc.Scan();
		if(type==Types.TlBrackets) {
			sc.LoadPos();
			Z1();
			type = sc.Scan();
			if (type != Types.Tsem)
				printError(Types.Tsem);
		}
		if(type==Types.Tas)
		{
			sc.LoadPos();
			H();
			sc.SavePos();
			type=sc.Scan();
		}
		//��������
		while(type==Types.Tcomma)
		{
			sc.SavePos();
			type = sc.Scan();
			if(type!=Types.Tid)
				printError(Types.Tid);
			
			if (isTypeOk) {
				sem.addVar(dataTypeLex, dataType, sc.getLex(), 0, sc.pos_text);
			}
			
			type = sc.Scan();
			if(type==Types.Tas)
				{
					sc.LoadPos();
					H();
					type=sc.Scan();
				}
			
		}
		if(type!=Types.Tsem)
			printError(Types.Tsem);
		// var end
	}
	
	// typedef?
	void W() throws IOException
	{
		Types type;

		sc.SavePos();
		T();
		
		sc.LoadPos();
		Types dataType = sc.Scan();
		char[] refDataId = sc.getLex();
		
		sc.SavePos();
		type = sc.Scan();
		
		if(type==Types.Tid) {
			sem.addRefType(sc.getLex(), refDataId, dataType, sc.pos_text);
			type = sc.Scan();
			if(type==Types.TlBrackets) {
				sc.LoadPos();
				Z1();
				type = sc.Scan();
				if (type != Types.Tsem)
					printError(Types.Tsem);
			} else
				if (type != Types.Tsem)
					printError(Types.Tsem);
		}
	}
	
	void D() throws IOException
	{
		Types type;
		
		type = sc.Scan();
		//{
		if(type!=Types.TlBraces)
			printError(Types.TlBraces);

		sem.updateCurrentNodeToRight();
		sc.SavePos();
		type = sc.Scan();

		while(type==Types.Tint||type==Types.Tfor||type==Types.Ttypedef||type==Types.Tint64||type==Types.Tid||type==Types.TlBraces||type==Types.Tsem)
		{
			if (type==Types.Ttypedef) {
				W();
			} else
			if(type==Types.Tint||type==Types.Tint64)//B
			{
				sc.LoadPos();
				B();
			}
			else //G
			{
				// ��� � ���� ��������������
				if (type == Types.Tid) {
					Types t = sc.Scan();
					if (t == Types.Tid) {
						sc.LoadPos();
						B();
					} else {
						sc.LoadPos(); 
						G();
					}
				}
				else {
					sc.LoadPos(); 
					G();
				}
			}
			sc.SavePos();
			type = sc.Scan();
		}
		sc.LoadPos();
		type = sc.Scan();

		//}
		if(type!=Types.TrBraces)
			printError(Types.TrBraces);
		
		sem.returnFromBlock();
	}
	void G() throws IOException
	{
		Types type;
		
		sc.SavePos();
		type = sc.Scan();
		 if(type==Types.TlBraces)//����
			{
				sc.LoadPos();
				D();
			}
			else
			{
				if(type==Types.Tid)//H
				{
					sem.checkVarAndThrowErrors(sc.getLex());
					type=sc.Scan();
					sc.LoadPos();
					
					if(type == Types.Tas || type == Types.TlBrackets) {
						H();
					} else {
						V();
					}
					
					type = sc.Scan();
				}
				//for
				else if(type==Types.Tfor) {
//					sc.LoadPos();
//					D();
//					type = sc.Scan();
//					if(type != TWhile)
//						printError(TWhile);
					type = sc.Scan();
					if(type!=Types.TlPar)
						printError(Types.TlPar);
					
					sc.SavePos();
					T();
					sc.LoadPos();
					Types varType = sc.Scan();
					char[] varTypeLex = sc.getLex();
					

					sc.SavePos();
					sc.Scan();
					char[] varId = sc.getLex();
					sem.addVar(varTypeLex, varType, varId, 0, sc.pos_text);
					sc.LoadPos();
					
					H();
					type = sc.Scan();
					if(type!=Types.Tsem)
						printError(Types.Tsem);
					V(); // TODO: for
					type = sc.Scan();
					if(type!=Types.Tsem)
						printError(Types.Tsem);
					V(); // TODO: for
					type = sc.Scan();
					if(type!=Types.TrPar)
						printError(Types.TrPar);
					G();
//					type = sc.Scan();
				}
				
				//return
//				else if(type==TReturn)
//				{
//					V();
//					type = sc.Scan();
//				}
//				else if(type==Types.Tinc||type==Types.Tdec||type==Types.Tminus)
//				{
//					sc.LoadPos();
//					V();
//					type=sc.Scan();
//				}
//				if(type!=Types.Tsem)
//					printError(Types.Tsem);
			}
	}
	void H() throws IOException
	{
		Types type;
		
		sc.SavePos();
		type = sc.Scan();
		char[] idLex = sc.getLex();
		
		if(type!=Types.Tid)
		{
			printError(Types.Tid);
		}
		type = sc.Scan();
		
		indices = null;
		if(type==Types.TlBrackets)
		{
			sc.LoadPos();
			Z();
			sc.SavePos();
			type = sc.Scan();
		}
		
		Integer[] varIndices = null;
		if (indices != null) {
			varIndices = indices.clone();
		}
		
		if(type==Types.Tas)
		{
			stack.clear();
			V();
			Interpreter itpr = new Interpreter(stack);
			itpr.evaluate();
			
			if (varIndices != null && varIndices.length > 0) {
				sem.setVarArrayCellValue(idLex, varIndices, itpr.result);
			} else {
				sem.setVarValue(idLex, itpr.result);
			}
			
//			printError(Types.Tas);
		}
		if (type == Types.Tsem)
			sc.LoadPos();
	}
	
	// ��������� ����������� �������
	void Z1() throws IOException
	{
		Types type;
		sc.SavePos();
		
		type = sc.Scan();
		if(type!=Types.Tid)
		{
			printError(Types.Tid);
		}
		type = sc.Scan();
		while(type==Types.TlBrackets)
		{
			stack.clear();
			V();
			Interpreter itpr = new Interpreter(stack);
			itpr.evaluate();
			
			sem.addLengthToVar(Math.toIntExact(itpr.result));
			
			type = sc.Scan();
			if (type != Types.TrBrackets)
				printError(Types.TrBrackets);
			else {
				sc.SavePos();
				type = sc.Scan();
			}
		}
		sc.LoadPos();
	}
	void Z() throws IOException
	{
		Types type;
		sc.SavePos();
		
		type = sc.Scan();
		if(type!=Types.Tid)
		{
			printError(Types.Tid);
		}
		
		ArrayList<Integer> arrIndices = new ArrayList<Integer>();
		
		type = sc.Scan();
		
		while(type==Types.TlBrackets)
		{
			stack.clear();
			V();
			type = sc.Scan();
			if (type != Types.TrBrackets)
				printError(Types.TrBrackets);
			else {
				sc.SavePos();
				type = sc.Scan();

				Interpreter itpr = new Interpreter(stack);
				itpr.evaluate();
				arrIndices.add(Math.toIntExact(itpr.result));
			}
		}
		
		indices = ((List<Integer>) arrIndices).toArray(new Integer[arrIndices.size()]);
		
		sc.LoadPos();
	}
	void V() throws IOException
	{
		Q1();
		Types type;
		
		sc.SavePos();
		type = sc.Scan();
		while(type==Types.Teq||type==Types.TnotEq)
		{
			stack.addLast(type);
			Q1();
			sc.SavePos();
			type = sc.Scan();
		}
		sc.LoadPos();
	}
	void Q1() throws IOException
	{
		Q2();
		Types type;
		
		sc.SavePos();
		type = sc.Scan();
		while(type==Types.Tmore||type==Types.Tless||type==Types.TmoreEq||type==Types.TlessEq)
		{
			stack.addLast(type);
			Q2();
			sc.SavePos();
			type = sc.Scan();
		}
		sc.LoadPos();
	}
	void Q2() throws IOException
	{
		Q3();
		sc.SavePos();
		sc.Scan();
		sc.LoadPos();
	}
	void Q3() throws IOException
	{
		Q4();
		Types type;
		
		sc.SavePos();
		type = sc.Scan();
		while(type==Types.Tplus||type==Types.Tminus)
		{
			stack.addLast(type);
			Q4();
			sc.SavePos();
			type = sc.Scan();
		}
		sc.LoadPos();
	}
	void Q4() throws IOException
	{
		Q5();
		Types type;
		
		sc.SavePos();
		type = sc.Scan();
		while(type==Types.Tmod||type==Types.Tdiv||type==Types.Tmul)
		{
			stack.addLast(type);
			Q5();
			sc.SavePos();
			type = sc.Scan();
		}
		sc.LoadPos();
	}
	void Q5() throws IOException
	{
		Types type;
		
		sc.SavePos();
		type = sc.Scan();
		if(type!=Types.Tinc&&type!=Types.Tdec&&type!=Types.Tminus)
		{
			sc.LoadPos();
		} else stack.addLast(type);
		Q6();
	}
	void Q6() throws IOException
	{
		Types type;
		
		Q7();
		sc.SavePos();
		type = sc.Scan();
		if(type!=Types.Tinc&&type!=Types.Tdec)
		{
			sc.LoadPos();
		} else stack.addLast(type);
	}
	void Q7() throws IOException
	{
		Types type;
		
		sc.SavePos();

		type = sc.Scan();
		char[] lex = sc.getLex();
		
		int[] pos2 = new int[] {sc.num_str, sc.pos_str, sc.pos_text};
		Types newt = sc.Scan();
		sc.num_str = pos2[0];
		sc.pos_str = pos2[1];
		sc.pos_text = pos2[2];
		if(type==Types.TlPar)
		{
			stack.addLast(Types.TlPar);
			V();
			type = sc.Scan();
			if(type!=Types.TrPar)
				printError(Types.TrPar);
			stack.addLast(Types.TrPar);
		}
		else
			if (newt == Types.TlBrackets) {
				sc.LoadPos();
				
				indices = null;
				
				// � Z ����� ����������� ��������� � V. ��������� ���� ������
				LinkedList savedStack = (LinkedList) stack.clone();
				Z();
				stack = savedStack;
				
				ArrayList<Integer> indList = new ArrayList<Integer>(Arrays.asList(indices));
				RefValue val = sem.findArrayCellVar(lex, indList);
				
				if (val != null) {
					stack.addLast(val.value);
				} else {
					String str = "";
					
					for (Integer i : indList) {
						str += "[" + i + "]";
					}
					
					System.err.println("������� ������� " + new String(lex).trim() + str + " �� ��������");
				}
			} else {
				if (type == Types.Tid) {
					boolean existingVar = sem.checkVarAndThrowErrors(lex);
					
					if (existingVar) {
						stack.addLast(sem.findVar(lex).value);
					}
				}
				
				if (type==Types.Tc16int) {
					stack.addLast(Long.decode(new String(lex).trim()));
				} else if (type==Types.Tc10int) {
					stack.addLast(Long.parseLong(new String(lex).trim(), 10));
				}
				
				if(type!=Types.Tc16int&&type!=Types.Tc10int&&type!=Types.Tid)
				printError(Types.Tc16int); 
			}
	}


	void printError(Types type) {
		int row = sc.num_str, pos = sc.pos_str;
		System.err.printf("������ � ������ %d:%d\n",row,pos);
		switch(type) {
			case Terr:System.err.println(new String(sc.TLex).trim() + " - ����������� �������");break;
			case Tid:System.err.println("�������� �������������");break;
			case TlPar:System.err.println("�������� ������ '('");break;
			case TrPar:System.err.println("�������� ������ ')'");break;
			case TlBraces:System.err.println("�������� ������ '{'");break;
			case TrBraces:System.err.println("�������� ������ '}'");break;
			case TlBrackets:System.err.println("�������� ������ '['");break;
			case TrBrackets:System.err.println("�������� ������ ']'");break;
			case Tmain:System.err.println("�������� 'main'");break;
			case Tfor:System.err.println("�������� 'for'");break;
			case Tint:System.err.println("�������� ��� ������");break;
			case Tpoint:System.err.println("�������� ������ '.'");break;
			case Tcomma:System.err.println("�������� ������ ','");break;
			case Tsem:System.err.println("�������� ������ ';'");break;
			case Tc16int:System.err.println("��������� ���������");break;
			case Tc10int:System.err.println("��������� ���������");break;
			case Tas:System.err.println("�������� ������ '='");break;
			case Tint64:System.err.println("��� main ������ ���� int");break;
			default:System.err.print(type);
		}
		System.exit(0);
	}
}
