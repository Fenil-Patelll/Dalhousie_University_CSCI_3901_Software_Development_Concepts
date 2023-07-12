// Separate Class for TestCases
class TestCases {

    SearchTree treeTest = new SearchTree();

    // Input Validation
    void inputValidation() {

        //Send a null and empty string to add method
        System.out.println(treeTest.add(null));
        System.out.println(treeTest.add(""));

        //Send a null and empty string to find method
        System.out.println(treeTest.find(""));
        System.out.println(treeTest.find(null));

    }

    //Boundary Cases Validatiom
    void boundaryCases() {

        //reset and print an empty string
        treeTest.reset();
        treeTest.printTree();

        //add, reset and print a string of 1 character
        System.out.println(treeTest.add("P"));
        treeTest.reset();
        System.out.println(treeTest.printTree());

        //add, print a node with long string
        System.out.println(treeTest.add("this is a long string added in the tree"));
        treeTest.reset();
        System.out.println(treeTest.printTree());

        //add, reset and print big tree
        System.out.println(treeTest.add("Halifax"));
        System.out.println(treeTest.add("Van"));
        System.out.println(treeTest.add("Toronto"));
        System.out.println(treeTest.add("Calgary"));
        System.out.println(treeTest.add("Windsor"));
        System.out.println(treeTest.add("Sydney"));
        System.out.println(treeTest.add("Victoria"));
        System.out.println(treeTest.add("Montreal"));
        treeTest.reset();
        System.out.println(treeTest.printTree());

    }
}