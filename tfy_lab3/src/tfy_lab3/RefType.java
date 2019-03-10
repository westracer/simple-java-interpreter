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
	
	public char[] id;					// идентификатор
	public Types rawType;				// ссылка на простой тип
	public RefType refType;				// ссылка на typedef тип
	public ArrayList<Integer> length;	// размерности (length > 0 для массива)
	
	public String getTypeName() {
		return refType != null ? new String(refType.id).trim() : rawType.toString();
	}
	
	public String getLengthString() {
		String str = "";
		
		for (Integer i : length) {
			str += "[" + i + "]";
		}
		
		return str;
	}
}