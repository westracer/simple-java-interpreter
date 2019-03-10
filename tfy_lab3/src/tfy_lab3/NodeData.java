package tfy_lab3;

public class NodeData {
	public NodeData(NodeType type) {
		this.type = type;
	}
	
	public NodeData(NodeType type, char[] id, int pos, RefValue refValue) {
		this.type = type;
		this.id = id;
		this.pos = pos;
		this.refValue = refValue;
	}
	
	public NodeType type;		// тип узла
	public char[] id;			// идентификатор
	public int pos;				// позиция в строке
	
	public RefValue refValue;	// значение переменной (если null, то узел - константа)
	public long constValue;		// знчение константы
}
