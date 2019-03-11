package tfy_lab3;

import java.util.HashMap;
import java.util.LinkedList;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Interpreter {
    static HashMap<Types, Integer> precedence = new HashMap<Types, Integer>();
    
	LinkedList expression;
	LinkedList<RefValue> operands = new LinkedList();
	LinkedList<Types> operators = new LinkedList();
	
	public RefValue result;
	
	Interpreter(LinkedList ex) {
		this.expression = new LinkedList(ex);

		precedence.put(Types.Tinc, 1);
		precedence.put(Types.Tdec, 1);
		precedence.put(Types.TlPar, 1);
		precedence.put(Types.TrPar, 1);
		precedence.put(Types.Tmul, 3);
		precedence.put(Types.Tdiv, 3);
		precedence.put(Types.Tmod, 3);
		precedence.put(Types.Tplus, 4);
		precedence.put(Types.Tminus, 4);
		precedence.put(Types.Tless, 6);
		precedence.put(Types.TlessEq, 6);
		precedence.put(Types.Tmore, 6);
		precedence.put(Types.TmoreEq, 6);
		precedence.put(Types.Teq, 7);
		precedence.put(Types.TnotEq, 7);
	}
	
	void throwError(String message) {
		System.err.println(message);
		new Exception().printStackTrace();
		System.exit(0);
	}
	
	private void _evaluateUnaryOperation() {
		RefValue op1 = operands.pollLast();
		Types operator = operators.pollLast();

		RefValue refValue = new RefValue(null, 0);
		
		switch (operator) {
		case Tinc:
			refValue.value = op1.value + 1;
			op1.value = refValue.value;
			break;
		case Tdec:
			refValue.value = op1.value - 1;
			op1.value = refValue.value;
			break;
		default:
			throwError("�������� ��� ���������: " + operator);
			return;
		}
		
		operands.addLast(refValue);
//		System.out.println(op1 + " ["+operator+"] => " + refValue.value);
	}
	
	private void _evaluateOperation() {
		Types lastType = operators.pollLast();
		operators.addLast(lastType);
		
		if (lastType == Types.Tdec || lastType == Types.Tinc) {
			_evaluateUnaryOperation();
		} else {
			_evaluateBinaryOperation();									
		}
	}
	
	private void _evaluateBinaryOperation() {
		RefValue op2 = operands.pollLast();
		RefValue op1 = operands.pollLast();
		Types operator = operators.pollLast();

		RefValue refValue = new RefValue(null, 0);
		
		switch (operator) {
		// arithmetic
		case Tplus:
			refValue.value = op1.value + op2.value;
			break;
		case Tminus:
			refValue.value = op1.value - op2.value;
			break;
		case Tdiv:
			refValue.value = op1.value / op2.value;
			break;
		case Tmul:
			refValue.value = op1.value * op2.value;
			break;
		case Tmod:
			refValue.value = op1.value % op2.value;
			break;
			
		// logical
		case Tmore:
			refValue.value = op1.value > op2.value ? 1 : 0;
			break;
		case TmoreEq:
			refValue.value = op1.value >= op2.value ? 1 : 0;
			break;
		case Tless:
			refValue.value = op1.value < op2.value ? 1 : 0;
			break;
		case TlessEq:
			refValue.value = op1.value <= op2.value ? 1 : 0;
			break;
		case Teq:
			refValue.value = op1.value == op2.value ? 1 : 0;
			break;
		case TnotEq:
			refValue.value = op1.value != op2.value ? 1 : 0;
			break;
		default:
			throwError("�������� ��� ���������: " + operator);
			return;
		}
		
		operands.addLast(refValue);
//		System.out.println(op1 + " ["+operator+"] "+op2+" => " + refValue.value);
	}
	
	// shunting yard
	void evaluate() {
		for (Object obj : this.expression) {
			if (obj.getClass() == RefValue.class) {
				operands.addLast((RefValue) obj);
			} else if (obj.getClass() == Types.class) {
				Types type = (Types) obj;
				
				if (type != Types.TrPar && type != Types.TlPar) {
					if (operators.size() > 0) {
						Types lastType = operators.pollLast();
						operators.addLast(lastType);
						
						// ���� ����. �������� ����� ������������ ���������, �� ������� ���������
						if (lastType != Types.TrPar && lastType != Types.TlPar) {
							if (!precedence.containsKey(type)) throwError("�������� ��� ���������: " + type);
							if (!precedence.containsKey(type)) throwError("�������� ��� ���������: " + lastType);

							if (precedence.get(type) > precedence.get(lastType)) {
								_evaluateOperation();
							}
						}
					}
				} else if (type == Types.TrPar) {
					if (!operators.contains(Types.TlPar)) {
						throwError("����������� ���������� ������ ���������");
						return;
					}

					// ���� �� �������� ����������� ������ ������� ���������
					Types last = operators.pollLast();
					while (last != Types.TlPar) {
						operators.addLast(last);
						_evaluateOperation();
						last = operators.pollLast();
					}
				}
				
				// ���, ����� ����������� ������ ��������� � ���������
				if (type != Types.TrPar) {
					operators.addLast(type);
				}
			}
		}
		
		while (!operators.isEmpty()) {
			_evaluateOperation();
		}

		if (operands.size() != 1) {
			throwError("������ ��� ������� ���������");
		}
		
		result = operands.pop();
	}
}
