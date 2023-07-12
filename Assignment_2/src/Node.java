// Separate Node class defined
class Node{
    Node left;
    Node right;
    String value;
    Node parent;
    Integer searchCount = 0;
    Integer nodeDepth;
    Node(String n){
        this.value = n;
    }
}