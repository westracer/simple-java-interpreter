package tfy_lab3;

import java.util.ArrayList;

public class RefValue {
	public RefValue(char[] id, long value) {
		this.id = id;
		this.value = value;
		
		this.arrayIndex = new ArrayList<Integer>();
	}
	
	public char[] id;						// �������������
	public Types rawType;					// ������ �� ������� ���
	public RefType refType;					// ������ �� typedef ���
	public long value;						// ��������
	public ArrayList<Integer> arrayIndex;	// ������� � ������� (��� �������� � �������)
}
