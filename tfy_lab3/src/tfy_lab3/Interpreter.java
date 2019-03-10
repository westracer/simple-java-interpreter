package tfy_lab3;

import java.util.HashMap;
import java.util.LinkedList;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Interpreter {
    static HashMap<Types, Integer> precedence = new HashMap<Types, Integer>();
    
	LinkedList expression;
	LinkedList<Long> operands = new LinkedList();
	LinkedList<Types> operators = new LinkedList();
	
	public long result;
	
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
		System.exit(0);
	}
	
	private void _evaluateUnaryOperation() {
		long op1 = operands.pollLast();
		Types operator = operators.pollLast();

		RefValue refValue = new RefValue(null, 0);
		
		switch (operator) {
		case Tinc:
			refValue.value = op1 + 1;
			break;
		case Tdec:
			refValue.value = op1 - 1;
			break;
		default:
			throwError("Неверный тип оператора: " + operator);
			return;
		}
		
		operands.addLast(refValue.value);
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
		long op2 = operands.pollLast();
		long op1 = operands.pollLast();
		Types operator = operators.pollLast();

		RefValue refValue = new RefValue(null, 0);
		
		switch (operator) {
		// arithmetic
		case Tplus:
			refValue.value = op1 + op2;
			break;
		case Tminus:
			refValue.value = op1 - op2;
			break;
		case Tdiv:
			refValue.value = op1 / op2;
			break;
		case Tmul:
			refValue.value = op1 * op2;
			break;
		case Tmod:
			refValue.value = op1 % op2;
			break;
			
		// logical
		case Tmore:
			refValue.value = op1 > op2 ? 1 : 0;
			break;
		case TmoreEq:
			refValue.value = op1 >= op2 ? 1 : 0;
			break;
		case Tless:
			refValue.value = op1 < op2 ? 1 : 0;
			break;
		case TlessEq:
			refValue.value = op1 <= op2 ? 1 : 0;
			break;
		case Teq:
			refValue.value = op1 == op2 ? 1 : 0;
			break;
		case TnotEq:
			refValue.value = op1 != op2 ? 1 : 0;
			break;
		default:
			throwError("Неверный тип оператора: " + operator);
			return;
		}
		
		operands.addLast(refValue.value);
//		System.out.println(op1 + " ["+operator+"] "+op2+" => " + refValue.value);
	}
	
	// shunting yard
	void evaluate() {
		for (Object obj : this.expression) {
			if (obj.getClass() == Long.class) {
				operands.addLast((Long) obj);
			} else if (obj.getClass() == Types.class) {
				Types type = (Types) obj;
				
				if (type != Types.TrPar && type != Types.TlPar) {
					if (operators.size() > 0) {
						Types lastType = operators.pollLast();
						operators.addLast(lastType);
						
						// если след. операция менее приоритетнее последней, то считаем последнюю
						if (lastType != Types.TrPar && lastType != Types.TlPar) {
							if (!precedence.containsKey(type)) throwError("Неверный тип оператора: " + type);
							if (!precedence.containsKey(type)) throwError("Неверный тип оператора: " + lastType);

							if (precedence.get(type) > precedence.get(lastType)) {
								_evaluateOperation();
							}
						}
					}
				} else if (type == Types.TrPar) {
					if (!operators.contains(Types.TlPar)) {
						throwError("Закрываются неоткрытые скобки выражения");
						return;
					}

					// пока не встретим открывающую скобку считаем выражение
					Types last = operators.pollLast();
					while (last != Types.TlPar) {
						operators.addLast(last);
						_evaluateOperation();
						last = operators.pollLast();
					}
				}
				
				// все, кроме закрывающей скобки добавляем в операторы
				if (type != Types.TrPar) {
					operators.addLast(type);
				}
			}
		}
		
		while (!operators.isEmpty()) {
			_evaluateOperation();
		}

		if (operands.size() != 1) {
			throwError("Ошибка при расчете выражения");
		}
		
		result = operands.pop();
	}
}
