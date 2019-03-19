package src;
import java.util.ArrayList;
import java.util.Arrays;

public class Semantics {
	public Semantics() {
		currentNode = createEmptyNode();
		root = currentNode;
	}

	final boolean EXIT_ON_ERROR = false;

	TreeNode root;
	TreeNode currentNode;
	
	TreeNode createEmptyNode() {
		NodeData data = new NodeData(NodeType.typeNone);
		return new TreeNode(data);
	}
	
	void throwError(String message) {
		System.err.println(message);
		new Exception().printStackTrace();
		
		if (EXIT_ON_ERROR) {
			System.exit(0);
		}
	}

	// sem1: проверка типов
	boolean checkTypeAndThrowErrors(char[] typeId, Types lex) {
		boolean typeOk;
		
		if (lex == Types.Tid) {
			typeOk = checkRefType(typeId);
			
			if (!typeOk) {
				throwError("Неверный тип " + new String(typeId).trim());
			}
		} else {
			typeOk = checkRawType(lex);
			
			if (!typeOk) {
				throwError("Неверный тип " + new String(lex.toString()).trim());
			}
		}
		
		return typeOk;
	}

	// sem1: проверка типов
	boolean checkType(char[] typeId, Types lex) {
		if (lex == Types.Tid) {
			return checkRefType(typeId);
		} else {
			return checkRawType(lex);
		}
	}
	
	// поиск typedef типа	
	RefType findRefType(char[] typeId) {
		TreeNode node = currentNode;
		
		while (node != null) {
			if (node.data.type != NodeType.typeTypedef) {
				node = node.parent;
				continue;
			}
			
			if (Arrays.equals(node.data.id, typeId)) {
				return node.data.refValue.refType;
			}

			node = node.parent;
		}
		
		return null;
	}
	
	RefValue findVar(char[] id) {
		TreeNode node = currentNode;
		
		while (node != null) {
			if (node.data.type != NodeType.typeVar) {
				node = node.parent;
				continue;
			}
			
			if (Arrays.equals(node.data.id, id) && node.data.refValue.arrayIndex.isEmpty()) {
				return node.data.refValue;
			}

			node = node.parent;
		}
		
		return null;
	}
	
	RefValue findArrayCellVar(char[] id, ArrayList<Integer> indices, TreeNode arrayTreeNode) {
		TreeNode node = arrayTreeNode.leftChild;
		
		while (node != null && Arrays.equals(node.data.id, id)) {
			if (node.data.type != NodeType.typeVar) {
				node = node.leftChild;
				continue;
			}
			
			if (Arrays.equals(node.data.id, id) && node.data.refValue.arrayIndex.equals(indices)) {
				return node.data.refValue;
			}

			node = node.leftChild;
		}
		
		return null;
	}
	
	boolean checkVarAndThrowErrors(char[] id) {		
		if (findVar(id) != null) {
			return true;
		} else {
			throwError("Неверная переменная " + new String(id).trim());
			return false;
		}
	}
	
	// добавление typedef типа
	RefType addRefType(char[] typeId, char[] refTypeId, Types refRawType, int textPos) {
		if (findRefType(typeId) != null) {
			throwError("Тип " + new String(typeId).trim() + " уже есть");
			return null;
		}
		
		if (!checkTypeAndThrowErrors(refTypeId, refRawType)) {
			return null;
		}

		RefType newType;
		if (refRawType != Types.Tid) {
			newType = new RefType(typeId, refRawType);
		} else {
			newType = new RefType(typeId, findRefType(refTypeId));
		}
		
		RefValue val = new RefValue(typeId, 0);
		val.refType = newType;
	
		NodeData data = new NodeData(NodeType.typeTypedef, typeId, textPos, val);
		currentNode.data = data;
		
		TreeNode leftChild = createEmptyNode();
		currentNode.leftChild = leftChild;
		leftChild.parent = currentNode;
		
		currentNode = leftChild;
		return newType;
	}
	
	// sem1: проверка простых типов
	boolean checkRawType(Types lex) {
		Types[] allowed = new Types[]{Types.Tint, Types.Tint64, Types.Ttypedef};
		if (Arrays.binarySearch(allowed, lex) >= 0) {
			return true;
		}

		return false;
	}
	
	// sem1: проверка typedef типов
	boolean checkRefType(char[] typeId) {
		if (findRefType(typeId) != null) {
			return true;
		}

		return false;
	}
	
	boolean addVar(char[] typeId, Types type, char[] id, long value, int textPos) {
		checkType(typeId, type);
		
		if (findVar(id) != null) {
			throwError("Переменная уже есть " + new String(id).trim());
			return false;
		}
		
		if (type == Types.Tid) {
			addVar(typeId, id, 0, textPos);
		} else {
			addVar(type, id, 0, textPos);
		}
		
		return true;
	}
	
	void addVar(Types type, char[] id, long value, int textPos) {
		RefValue val = new RefValue(id, value);
		val.rawType = type;
		
		updateCurrentNode(id, textPos, val);
	}
	
	void addVar(char[] typeId, char[] id, long value, int textPos) {
		RefValue val = new RefValue(id, value);
		val.refType = findRefType(typeId);
		
		updateCurrentNode(id, textPos, val);
	}
	
	void addArrayCellVar(RefValue ref, char[] id, ArrayList<Integer> indices, RefValue value, TreeNode arrayTreeNode) {
		RefValue val = new RefValue(id, value.value);
		val.rawType = ref.rawType;
//		val.refType = ref.refType; // no ref type for array cell fix?
		val.arrayIndex = indices;

		TreeNode newNode = createEmptyNode();
		newNode.data = new NodeData(NodeType.typeVar, id, 0, val);
		newNode.parent = arrayTreeNode;
		newNode.leftChild = arrayTreeNode.leftChild;
		arrayTreeNode.leftChild.parent = newNode;
		arrayTreeNode.leftChild = newNode;
	}
	
	void updateCurrentNode(char[] id, int textPos, RefValue val) {
		NodeType type = new String(id).trim().equalsIgnoreCase("main") ? NodeType.typeMain : NodeType.typeVar;
		NodeData data = new NodeData(type, id, textPos, val);
		currentNode.data = data;
		
		TreeNode leftChild = createEmptyNode();
		currentNode.leftChild = leftChild;
		leftChild.parent = currentNode;
		
		currentNode = leftChild;
	}
	
	void updateCurrentNodeToRight() {		
		TreeNode rightChild = createEmptyNode();
		TreeNode leftChild = createEmptyNode();
		TreeNode newCurrent = createEmptyNode();

		currentNode.rightChild = rightChild;
		currentNode.leftChild = leftChild;
		leftChild.parent = currentNode;
		rightChild.parent = currentNode;
		
		rightChild.leftChild = newCurrent;
		newCurrent.parent = rightChild;
		
		currentNode = newCurrent;
	}
	
	void returnFromBlock() {
		TreeNode prevNode = currentNode;
		TreeNode currNode = currentNode.parent;
		if (currNode == null) {
			throwError("Закрывается неоткрытый блок");
			return;
		}
		
		while (currNode.rightChild != prevNode) {
			prevNode = currNode;
			currNode = currNode.parent;
			
			if (currNode == null) {
				throwError("Закрывается неоткрытый блок");
				return;
			}
		}
		
		currentNode = currNode.leftChild;
	}
	
	// добавление размерности к переменной
	void addLengthToVar(int length) {
		if (length < 1) {
			throwError("Размер массива должен быть > 0. Дано: " + length);
			return;
		}
		
		if (currentNode == null || currentNode.parent == null) {
			return;
		}

		NodeData data = currentNode.parent.data;
		if (data.type == NodeType.typeVar) {
			data.refValue.arrayLength.add(length);
		} else if (data.type == NodeType.typeTypedef) {
			data.refValue.refType.length.add(length);
		}
	}
	
	boolean checkVarLength(char[] varId, Integer[] indices) {
		RefValue val = findVar(varId);
		if (val == null) {
			return false;
		}

		int indicesLength = indices.length;
		
		@SuppressWarnings("unchecked")
		ArrayList<Integer> recursiveIndices = (ArrayList<Integer>) val.arrayLength.clone();
		
		RefType type = val.refType;
		int length = val.arrayLength.size();
		while (type != null) {
			length += type.length.size();
			recursiveIndices.addAll(type.length);
			type = type.refType;
		}
		
		if (indicesLength > length) {
			throwError("Неверная размерность переменной " + new String(varId).trim() + ": " + indicesLength + " > " + length);
			return false;
		} else if (indicesLength < length) {
			throwError("Операции над массивами не разрешены для " + new String(varId).trim() + ": " + indicesLength + " < " + length);
			return false;
		}
		
		for (int i = 0; i < indicesLength; i++) {
			int index = indices[i];
			int indexLength = recursiveIndices.get(i);
			
			if (index < 0 || index > indexLength - 1) {
				throwError("out of bounds " + new String(varId).trim() + ": i = " + index + " не соответствует условию 0 <= i <= " + (indexLength - 1));
				return false;
			}
		}
		
		return true;
	}
	
	void setVarValue(char[] varId, RefValue value) {
		RefValue val = findVar(varId);
		if (val == null) {
			throwError("Неверная переменная " + new String(varId).trim());
		}
		
		RefType type = value.refType;
		int length = 0;
		while (type != null) {
			length += type.length.size();
			type = type.refType;
		}
		
		if (length > 0) {
			throwError("Операции над массивами не разрешены для " + new String(varId).trim());
			return;
		}

		val.value = value.value;
	}
	
	TreeNode findArrayVarNode(RefValue val) {
		TreeNode node = currentNode;
		
		while (node != null) {
			if (node.data.type != NodeType.typeVar) {
				node = node.parent;
				continue;
			}
			
			if (node.data.refValue == val) {
				return node;
			}

			node = node.parent;
		}
		
		return null;
	}
	
	void setVarArrayCellValue(char[] varId, Integer[] indices, RefValue value) {
		RefValue val = findVar(varId);
		if (val == null) {
			throwError("Неверная переменная " + new String(varId).trim());
			return;
		}

		if (!checkVarLength(varId, indices)) {
			return;
		}
		
		val.value = value.value;

		ArrayList<Integer> indicesList = new ArrayList<Integer>(Arrays.asList(indices));
		TreeNode arrayTreeNode = findArrayVarNode(val);
		
		RefValue cellVal = findArrayCellVar(varId, indicesList, arrayTreeNode);
		if (cellVal == null) {
			addArrayCellVar(val, varId, indicesList, value, arrayTreeNode);
		} else {
			cellVal.value = value.value;
		}
	}
}
