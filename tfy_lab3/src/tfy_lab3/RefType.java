package tfy_lab3;

import java.util.ArrayList;

public class RefType {
	public RefType(char[] id, Types rawType) {
		this.id = id;
		this.rawType = rawType;
		
		this.length = new ArrayList<Integer>();
	}

	public RefType(char[] id, RefType refType) {
		this.id = id;
		this.refType = refType;
		
		this.length = new ArrayList<Integer>();
	}
	
	public char[] id;					// �������������
	public Types rawType;				// ������ �� ������� ���
	public RefType refType;				// ������ �� typedef ���
	public ArrayList<Integer> length;	// ����������� (length > 0 ��� �������)
}