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
	
	public NodeType type;		// ��� ����
	public char[] id;			// �������������
	public int pos;				// ������� � ������
	
	public RefValue refValue;	// �������� ���������� (���� null, �� ���� - ���������)
	public long constValue;		// ������� ���������
}
