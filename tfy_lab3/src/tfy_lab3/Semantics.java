package tfy_lab3;

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

	// sem1: �������� �����
	boolean checkTypeAndThrowErrors(char[] typeId, Types lex) {
		boolean typeOk;
		
		if (lex == Types.Tid) {
			typeOk = checkRefType(typeId);
			
			if (!typeOk) {
				throwError("�������� ��� " + new String(typeId).trim());
			}
		} else {
			typeOk = checkRawType(lex);
			
			if (!typeOk) {
				throwError("�������� ��� " + new String(lex.toString()).trim());
			}
		}
		
		return typeOk;
	}

	// sem1: �������� �����
	boolean checkType(char[] typeId, Types lex) {
		if (lex == Types.Tid) {
			return checkRefType(typeId);
		} else {
			return checkRawType(lex);
		}
	}
	
	// ����� typedef ����	
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
	
	RefValue findArrayCellVar(char[] id, ArrayList<Integer> indices) {
		TreeNode node = currentNode;
		
		while (node != null) {
			if (node.data.type != NodeType.typeVar) {
				node = node.parent;
				continue;
			}
			
			if (Arrays.equals(node.data.id, id) && node.data.refValue.arrayIndex.equals(indices)) {
				return node.data.refValue;
			}

			node = node.parent;
		}
		
		return null;
	}
	
	boolean checkVarAndThrowErrors(char[] id) {		
		if (findVar(id) != null) {
			return true;
		} else {
			throwError("�������� ���������� " + new String(id).trim());
			return false;
		}
	}
	
	// ���������� typedef ����
	RefType addRefType(char[] typeId, char[] refTypeId, Types refRawType, int textPos) {
		if (findRefType(typeId) != null) {
			throwError("��� " + new String(typeId).trim() + " ��� ����");
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
	
	// sem1: �������� ������� �����
	boolean checkRawType(Types lex) {
		Types[] allowed = new Types[]{Types.Tint, Types.Tint64, Types.Ttypedef};
		if (Arrays.binarySearch(allowed, lex) >= 0) {
			return true;
		}

		return false;
	}
	
	// sem1: �������� typedef �����
	boolean checkRefType(char[] typeId) {
		if (findRefType(typeId) != null) {
			return true;
		}

		return false;
	}
	
	void addVar(char[] typeId, Types type, char[] id, long value, int textPos) {
		checkType(typeId, type);
		
		if (findVar(id) != null) {
			throwError("���������� ��� ���� " + new String(id).trim());
			return;
		}
		
		if (type == Types.Tid) {
			addVar(typeId, id, 0, textPos);
		} else {
			addVar(type, id, 0, textPos);
		}
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
	
	void addArrayCellVar(RefValue ref, char[] id, ArrayList<Integer> indices, long value) {
		RefValue val = new RefValue(id, value);
		val.rawType = ref.rawType;
		val.refType = ref.refType;
		val.arrayIndex = indices;
		
		updateCurrentNode(id, 0, val);
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
			throwError("����������� ���������� ����");
			return;
		}
		
		while (currNode.rightChild != prevNode) {
			prevNode = currNode;
			currNode = currNode.parent;
			
			if (currNode == null) {
				throwError("����������� ���������� ����");
				return;
			}
		}
		
		currentNode = currNode.leftChild;
	}
	
	// ���������� ����������� � ����������
	void addLengthToVar(int length) {
		if (length < 1) {
			throwError("������ ������� ������ ���� > 0. ����: " + length);
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
		int varArrayLength = val.arrayLength.size(); // TODO: recursive typedef lengths
		
		if (indicesLength > varArrayLength) {
			throwError("�������� ����������� ���������� " + new String(varId).trim() + ": " + indicesLength + " > " + varArrayLength);
			return false;
		} else if (indicesLength < varArrayLength) {
			throwError("�������� ��� ��������� �� ��������� ��� " + new String(varId).trim() + ": " + indicesLength + " < " + varArrayLength);
			return false;
		}
		
		for (int i = 0; i < indicesLength; i++) {
			int index = indices[i];
			int length = val.arrayLength.get(i);
			
			if (index < 0 || index > length - 1) {
				throwError("out of bounds " + new String(varId).trim() + ": i = " + index + " �� ������������� ������� 0 <= i <= " + (length - 1));
				return false;
			}
		}
		
		return true;
	}
	
	void setVarValue(char[] varId, long value) {
		RefValue val = findVar(varId);
		if (val == null) {
			throwError("�������� ���������� " + new String(varId).trim());
		}
		
		val.value = value;
	}
	
	void setVarArrayCellValue(char[] varId, Integer[] indices, long value) {
		RefValue val = findVar(varId);
		if (val == null) {
			throwError("�������� ���������� " + new String(varId).trim());
			return;
		}

		if (!checkVarLength(varId, indices)) {
			return;
		}

		ArrayList<Integer> indicesList = new ArrayList<Integer>(Arrays.asList(indices));
		
		RefValue cellVal = findArrayCellVar(varId, indicesList);
		if (cellVal == null) {
			addArrayCellVar(val, varId, indicesList, value);
		} else {
			cellVal.value = value;
		}
	}
}