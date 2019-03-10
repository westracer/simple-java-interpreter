package tfy_lab3;

class TreeNode {
	public TreeNode() {}
	
	public TreeNode(NodeData data) {
		this.data = data;
	}
	
	public TreeNode(NodeData data, TreeNode parent, TreeNode leftChild, TreeNode rightChild) {
		this.data = data;
		this.parent = parent;
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}
	
	public NodeData data;
	public TreeNode parent;
	public TreeNode leftChild;
	public TreeNode rightChild;
	
	public boolean isNotEmpty() {
		return data != null && data.id != null && data.type != NodeType.typeNone;
	}
}
