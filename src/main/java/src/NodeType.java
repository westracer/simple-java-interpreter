package src;
enum NodeType {	
	typeVar(200), typeMain(201), typeTypedef(202), 
	typeNone(-1), typePlaceholder(-2);
	
    private final int value;

    private NodeType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
