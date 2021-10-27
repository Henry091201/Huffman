import java.io.Serializable;

/**
 * Creates the nodes that will be used in the tree
 */
public class Node implements Serializable {

    public char letter; // letter used
    public int frequency; // frequency of characters
    public Node leftChild;
    public Node rightChild;
    public boolean isLeaf;

    public Node(char letter, int frequency, Node leftChild, Node rightChild){
        this.letter = letter;
        this.frequency = frequency;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.isLeaf = isLeaf();
    }

    /**
     * Returns if the node is a leaf node
     * @return Boolean, True if leaf node
     */
    public boolean isLeaf(){
        return this.leftChild == null && this.rightChild == null;
    }

    /**
     * Gets the frequency/weight of the node
     * @return Int - frequency/weightof the node
     */
    public int getFrequency() {
        return frequency;
    }
}
