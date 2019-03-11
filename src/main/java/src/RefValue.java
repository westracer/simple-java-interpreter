package src;
import java.util.ArrayList;

public class RefValue {
	public RefValue(char[] id, long value) {
		this.id = id;
		this.value = value;
		this.rawType = Types.Tint;

		this.arrayIndex = new ArrayList<Integer>();
		this.arrayLength = new ArrayList<Integer>();
	}
	
	public char[] id;						// �������������
	public Types rawType;					// ������ �� ������� ���
	public RefType refType;					// ������ �� typedef ���
	public long value;						// ��������
	public ArrayList<Integer> arrayIndex;	// ������� � ������� (��� �������� � �������)
	public ArrayList<Integer> arrayLength;	// ������� ������� (��� �������)
	
	public String getTypeName() {
		return refType != null ? new String(refType.id).trim() : rawType.toString();
	}
	
	public String getIndexString() {
		String str = "";
		
		for (Integer i : arrayIndex) {
			str += "[" + i + "]";
		}
		
		return str;
	}
	
	public String getLengthString() {
		String str = "";
		
		for (Integer i : arrayLength) {
			str += "[" + i + "]";
		}
		
		return str;
	}
}
