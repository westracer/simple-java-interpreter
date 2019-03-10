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
	
	public String toString() {
    	if (data == null) return "no data";
    	
    	if (data.type == NodeType.typePlaceholder || data.type == NodeType.typeNone) {
    		return "";
    	}
    	
    	if (data.id != null) {
    		String str = new String(data.id).trim();
    		
    		if (data.type == NodeType.typeVar) {
    			str += " = " + data.refValue.value + " [" + data.refValue.getTypeName() + "]";
    		} else if (data.type == NodeType.typeMain) {
    			str += " [" + data.type + "]";
    		} else if (data.type == NodeType.typeTypedef) {
    			RefType type = data.refValue.refType;
    			
    			str = "typedef " + str + " (" + type.getTypeName() + ")" + type.getLengthString();
    		}
    		
    		return str;
    	} else {
    		return "";
    	}
	}
}
