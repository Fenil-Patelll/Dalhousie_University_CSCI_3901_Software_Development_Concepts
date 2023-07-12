public class SearchTree {

    //declared global variables
    Node root;
    Boolean flag = false;
    Integer depth;


    SearchTree() {
        root = null;
    }

    //add method to add node in the tree
    boolean add(String key) {

        // check the key if it is null or empty, it will return null if true
        if(key==null || key == ""){
            return false;
        }

        //flag to check the duplicate value
        if(!flag){
            root =  Insert(root,key);
            return flag?false:true;
        }
        else{
            flag = false;
            return false;
        }
    }
    int find(String key) {

        //input validation of the key
        if(key==null || key==""){
            return 0;
        }
        Node temp = root;
        depth = 1;

        //helper function wil be called to get the foundNode
        Node foundNode = searchKey(temp, key);
        if(foundNode==null){
            return 0;
        }

        //if found the node then it will return the depth of it.
        else{
            foundNode.searchCount++;
            foundNode.nodeDepth = depth;
            treeReshape(foundNode);
            depth = 0;
            return foundNode.nodeDepth;
        }
    }

    //Search count reset function
    void reset() {
        nodeCountReset(root);
    }

    //print function definition for print the key with its depth
    String printTree() {
        depth = 1;
        return printFunction(root);
    }

    //Insert function will take 2 parameters root node and key for node and this key(node) will be
    //added into appropriate location in the tree
    private Node Insert(Node root,String key){

        //create a node if there is no duplicate node present in the tree
        if(root == null){
            Node n1 = new Node(key);
            root = n1;
        }

        //comparing the key value with other node's value
        else if (root.value.compareToIgnoreCase(key) >0 ){
            Node temp = Insert(root.left,key);
            temp.parent = root;
            root.left = temp;

        }

        // if duplicate value found then turn the flag to true
        else if(root.value.compareToIgnoreCase(key)==0){
            flag = true;
        }

        //setting the left and right of the node
        else{
            Node temp = Insert(root.right,key);
            temp.parent = root;
            root.right = temp;
        }
        return root;
    }
    private Node searchKey(Node temp, String key){

       //return null if the node is null
       if(temp == null){
           return null;
       }

       // if the node is found then return node value
       if(temp.value.compareToIgnoreCase(key)==0){
           return temp;
       }

       //if not then call the function in recursive manner and return it
       else{
           depth++;
           return temp.value.compareToIgnoreCase(key)<0? searchKey(temp.right,key): searchKey(temp.left,key);
       }
    }
    private void treeReshape(Node foundNode){

      //condition to check if the foundNode's search count is Higher than the parent's searchCount
      //It will check if the foundNode's parent is null or not
      if(foundNode.parent!=null && foundNode.searchCount > foundNode.parent.searchCount){
          //it will check if the found node is in the left of the parent!
          if(foundNode.parent.left == foundNode){
              //check if the parent is not root node
              if(foundNode.parent.parent!=null){

                       //Interchanges foundNode with parent Node
                       if(foundNode.parent.parent.left == foundNode.parent){
                           foundNode.parent.parent.left = foundNode;
                       }
                       else{
                           foundNode.parent.parent.right = foundNode;
                       }

                      //if found node has the right child then this child will be reassigned to
                      //the parent node of the found node

                      if(foundNode.right!=null){
                          Node temp = foundNode.right;
                          foundNode.parent.left = temp;
                          temp.parent = foundNode.parent;
                          foundNode.right = foundNode.parent;
                      }

                      // if it does not have the right child then it will restructure the othe nodes
                      else{
                          foundNode.right = foundNode.parent;
                          foundNode.parent.left = null;
                      }
                      foundNode.parent = foundNode.parent.parent;
                      foundNode.right.parent = foundNode;
              }

              //if found node has parent node as root
              // Restructuring has been done accordingly
              else{
                  if(foundNode.right!=null){
                      Node temp = foundNode.right;
                      foundNode.parent.left = temp;
                      temp.parent = foundNode.parent;
                      foundNode.right = foundNode.parent;
                  }
                  else{
                      foundNode.right = foundNode.parent;
                      foundNode.right.left =null;

                  }
                  foundNode.parent = null;
                  foundNode.right.parent = foundNode;
                  root = foundNode;
              }
          }

          //same code as above but it will check the condition for the right node
          else{
              if(foundNode.parent.parent!=null){
                  if(foundNode.parent.parent.left == foundNode.parent){
                      foundNode.parent.parent.left = foundNode;
                  }
                  else{
                      foundNode.parent.parent.right = foundNode;
                  }
                  if(foundNode.left!=null){
                      Node temp = foundNode.left;
                      foundNode.parent.right = temp;
                      temp.parent = foundNode.parent;
                      foundNode.left = foundNode.parent;
                  }
                  else{
                      foundNode.left = foundNode.parent;
                      foundNode.parent.right = null;
                  }
                  foundNode.parent = foundNode.parent.parent;
                  foundNode.left.parent = foundNode;
              }
              else{
                  if(foundNode.left!=null){
                      Node temp = foundNode.left;
                      foundNode.parent.right = temp;
                      temp.parent = foundNode.parent;
                      foundNode.left = foundNode.parent;
                  }
                  else{
                      foundNode.left = foundNode.parent;
                      foundNode.left.right = null;
                  }
                  foundNode.parent = null;
                  foundNode.left.parent = foundNode;
                  root = foundNode;
              }
          }
          depth--;
      }
    }

    //Recursion  function to reset the search count of ech  node in tree
    private void nodeCountReset(Node root){

        //null condition checked
        if(root==null){
            return;
        }

        nodeCountReset(root.left);
        //changing the search count of each node
        root.searchCount = 0 ;
        nodeCountReset(root.right);
    }

    //helper method for the print function
    private String printFunction(Node n){
        String s="";
        if(n==null){
            return "";
        }
        s += printFunction(n.left);
        depth = 1;
        Node temp = n;
        while(temp.parent!=null){
            depth++;
            temp = temp.parent;
        }
        s += n.value + " " + depth + "\n";
        s += printFunction(n.right);
        return s;
    }

    // object creation, and program execution starts from here
    public static void main(String[] args) {
        SearchTree st1 = new SearchTree();
        System.out.println(st1.add("Egg"));
        System.out.println(st1.add("Carrot"));
        System.out.println(st1.add("Lentil"));
        System.out.println(st1.add("Apple"));
        System.out.println(st1.add("Date"));
        System.out.println(st1.add("Fig"));
        System.out.println(st1.add("Yam"));
        System.out.println(st1.printTree());
        System.out.println("\n<----Test Cases---->\n");
        TestCases t1 = new TestCases();
        t1.inputValidation();
        t1.boundaryCases();

    }
}




