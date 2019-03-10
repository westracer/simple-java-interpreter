package tfy_lab3;

import java.util.ArrayList;

public class RefValue {
	public RefValue(char[] id, long value) {
		this.id = id;
		this.value = value;
		
		this.arrayIndex = new ArrayList<Integer>();
	}
	
	public char[] id;						// идентификатор
	public Types rawType;					// ссылка на простой тип
	public RefType refType;					// ссылка на typedef тип
	public long value;						// значение
	public ArrayList<Integer> arrayIndex;	// индексы в массиве (для элемента в массиве)
}
