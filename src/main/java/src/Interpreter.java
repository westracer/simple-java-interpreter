package src;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Interpreter {
    static HashMap<Types, Integer> precedence = new HashMap<Types, Integer>();
    static final public long TRUE = 1;
    static final public long FALSE = 0;
    static final public Types[] NUMBER_TYPES = { Types.Tint, Types.Tint64, Types.Tc10int };
    
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
		precedence.put(Types.Tas, 14);
	}
	
	void throwError(String message) {
		System.err.println(message);
		new Exception().printStackTrace();
		System.exit(0);
	}
	
	private boolean _checkBinaryTypes(RefValue op1, RefValue op2, Types operator) {
		if (op1.refType != null || op2.refType != null) {
			throwError("Разрешены только операции над простыми типами");
			return false;
		}
		
		if (
			(op1.rawType != op2.rawType && op1.rawType != Types.Tc10int && op2.rawType != Types.Tc10int) || 
			Arrays.binarySearch(NUMBER_TYPES, op1.rawType) < 0 ||
			Arrays.binarySearch(NUMBER_TYPES, op2.rawType) < 0
		) {			
			throwError("Несовместимые типы операндов: [" + op1.rawType + "], [" + op2.rawType + "] => " + operator);
			return false;
		}
		
		return true;
	}
	
	private boolean _checkUnaryType(RefValue op1, Types operator) {
		if (op1.refType != null) {
			throwError("Разрешены только операции над простыми типами");
			return false;
		}
		
		switch (operator) {
		case Tinc:
		case Tdec:
			if (Arrays.binarySearch(NUMBER_TYPES, op1.rawType) < 0) {				
				throwError("Несовместимый тип операнда: [" + op1.rawType + "] => " + operator);
				return false;
			}
			break;
			default:
		}
		
		return true;
	}
	
	private void _evaluateUnaryOperation() {
		RefValue op1 = operands.pollLast();
		Types operator = operators.pollLast();

		RefValue refValue = new RefValue(null, 0);
		if (!_checkUnaryType(op1, operator)) {
			return;
		}
		
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
			throwError("Неверный тип оператора: " + operator);
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
		if (!_checkBinaryTypes(op1, op2, operator)) {
			return;
		}
		
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
			refValue.value = op1.value > op2.value ? Interpreter.TRUE : Interpreter.FALSE;
			break;
		case TmoreEq:
			refValue.value = op1.value >= op2.value ? Interpreter.TRUE : Interpreter.FALSE;
			break;
		case Tless:
			refValue.value = op1.value < op2.value ? Interpreter.TRUE : Interpreter.FALSE;
			break;
		case TlessEq:
			refValue.value = op1.value <= op2.value ? Interpreter.TRUE : Interpreter.FALSE;
			break;
		case Teq:
			refValue.value = op1.value == op2.value ? Interpreter.TRUE : Interpreter.FALSE;
			break;
		case TnotEq:
			refValue.value = op1.value != op2.value ? Interpreter.TRUE : Interpreter.FALSE;
			break;
		case Tas:
			op1.value = op2.value;
			refValue.value = op1.value;
			break;
		default:
			throwError("Неверный тип оператора: " + operator);
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
