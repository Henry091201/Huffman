import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Huffman {

    /*
    When running in terminal, options will be:
    java Huffman encode file
    java Huffman decode file
     */

    /**
     * Takes the file path and calls each method in order, which results in the text huffman encoded
     * @param filePath A String thats the file path of the text file you want to encode
     * @return The huffman encoded string
     */
    public static String encode(String filePath) {
        String finalEncoded = null;
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = bufferedReader.readLine();
            StringBuilder message = new StringBuilder();

            while (line != null) {
                message.append(line + '\n');
                line = bufferedReader.readLine();
            }
            if(message.toString().length() == 0){
                System.out.println("File was empty");
                System.exit(1);
            }
            Map<Character, Integer> ft = frequencyTable(message.toString());
            ArrayList<Node> nodeArrayList = buildListOfNodes(ft);
            sortNodes(nodeArrayList, 0, nodeArrayList.size() - 1);
            Node tree = buildTree(nodeArrayList);
            outputTree(tree);
            Map<Character, String> lookup = buildLookupTable(tree);
            finalEncoded = buildEncodedString(tree, lookup, message.toString());


        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }


        return finalEncoded;
    }

    /**
     * Creates the frequency table of characters
     * @param toBeEncoded The string from the text file to be encoded
     * @return HashMap of Character integers
     */
    public static Map<Character, Integer> frequencyTable(String toBeEncoded){
        Map<Character, Integer> freq = new HashMap<>();
        for(char character:toBeEncoded.toCharArray()){
            try{
                if(freq.containsKey(character)){
                    freq.put(character, freq.get(character) + 1);
                }else {
                    freq.put(character, 1);
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        return freq;
    }

    /**
     * Sorts an array list of nodes in order of their frequency, smallest frequency at the start and highest frequency at the end
     * @param nodeList Array list of all the leaf nodes
     * @param low start index used in quicksort
     * @param high end index used in quicksort
     */
    public static void sortNodes(ArrayList<Node> nodeList, int low, int high){

        //Check for an empty List
        if(nodeList == null || nodeList.size() == 0){
            return;
        }
        if(low >= high){
            return;
        }

        //Get the pivot element from the middle of the list
        int middle = low + (high - low)/2;
        Node pivot = nodeList.get(middle);

        // Make left < pivot and right > pivot

        int i = low, j = high;
        while(i<=j){
            //Check all values on left side of array are lower than pivot
            while(nodeList.get(i).getFrequency() < pivot.getFrequency()){
                i++;
            }
            //Check until all values on left side are greater than pivot
            while(nodeList.get(j).getFrequency() > pivot.getFrequency()){
                j--;
            }
            // Now compare values from both side of lists to see if they need swapping
            // After swapping move the iterator on both lists
            if(i<=j){
                swap(nodeList, i, j);
                    i++;
                    j--;
                }
            }
        if(low < j){
            sortNodes(nodeList, low, j);
        }
        if(high > i){
            sortNodes(nodeList, i, high);
        }
        }

    /**
     * Swaps elements in the Array around
     * @param list ArrayList of Nodes
     * @param firstIndex Index of first element that will be swapped
     * @param secondIndex Index of second element that will be swapped
     */
    public static void swap(ArrayList<Node> list, int firstIndex, int secondIndex){
        Node element = list.get(firstIndex);
        list.set(firstIndex, list.get(secondIndex));
        list.set(secondIndex, element);
    }

    /**
     * Creates all the leaf nodes from the frequeny table and stores them in an ArrayList
     * @param frequencyTable Hashmap of all the frequencies
     * @return returns the ArrayList of leaf nodes
     */
    public static ArrayList<Node> buildListOfNodes(Map<Character, Integer> frequencyTable){

        ArrayList<Node> nodeList = new ArrayList<>();
        for(Map.Entry<Character, Integer> entry: frequencyTable.entrySet()){
            nodeList.add(new Node(entry.getKey(), entry.getValue(), null, null));
        }

        return nodeList;
    }

    /**
     * Starts the recursive function to buiild the lookup table by traversing down the tree and assigning a leaf node
     * a Huffman Encoding
     * @param root The root Node of the tree
     * @return Hashmap of Character String, with the string being the Huffman Encoding
     */
    public static Map<Character, String> buildLookupTable(Node root){

        Map<Character, String> lookupTable = new HashMap<>();

        addToLookupTable(root, "", lookupTable);

        return lookupTable;
    }

    /**
     *The recursive function that traverses down the tree and eventually gives the leaf nodes their Huffman Encoding
     * @param node The node the function will traverse down
     * @param s The string the huffman encoding will be appended to
     * @param lookupTable The hashmap containing the characters and their huffman encodings
     */
    private static void addToLookupTable(Node node, String s, Map<Character, String> lookupTable) {
        if(!node.isLeaf){
            addToLookupTable(node.leftChild, s + '0', lookupTable);
            addToLookupTable(node.rightChild, s + '1', lookupTable);
        }else{
            lookupTable.put(node.letter, s);
        }
    }

    /**
     * Takes a sorted ArrayList of nodes and continually merges the two smallest weighted nodes until the tree is built
     * and the only node left in the array is the root
     * @param sortedNodeList An ArrayList of nodes sorted by their frequencies
     * @return A Node which is the root of the tree
     */
    public static Node buildTree(ArrayList<Node> sortedNodeList){

        while(sortedNodeList.size() > 1){
            //Get lowest and second lowest frequency node
            Node lowest1 = sortedNodeList.get(0);
            Node lowest2 = sortedNodeList.get(1);
            // remove the two lowest
            sortedNodeList.remove(0);
            sortedNodeList.remove(0);

            // New combined Node
            Node parent = new Node('\u0000', lowest1.getFrequency() + lowest2.getFrequency(), lowest1, lowest2);

            // Add parent back to list in the correct position

            for(int i =0; i<sortedNodeList.size(); i++){
                if(parent.getFrequency() <= sortedNodeList.get(i).getFrequency()){
                    sortedNodeList.add(i, parent);
                    break;
                }else if (parent.getFrequency() > sortedNodeList.get(sortedNodeList.size()-1).getFrequency()){
                    sortedNodeList.add(parent);
                    break;
                }
            }
            // If it is the combination of the last two nodes
            if(sortedNodeList.size() == 0){
                sortedNodeList.add(parent);
            }

        }
        return sortedNodeList.get(0);
    }

    /**
     * Saves the hashmap of the encoded characters as a file
     * @param root The Node root of the tree
     */
    public static void outputTree(Node root){
        String filename = "tree_object.ser";

        try{
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(root);

            out.close();
            file.close();
        }catch (IOException ex){
            System.out.println("IOException caught");
        }

    }

    /**
     * Goes through the string of the text file that is to be encoded letter by letter and appends the huffman encoding
     * of that letter to a string builder. Finally fully encoded sting will be returned
     * @param root The root Node of the tree
     * @param lookup The lookup table containing the Characters and their Huffman Encodings
     * @param toBeEncoded The string that will be encoded
     * @return The string encoded as their Huffman counterparts
     */
    public static String buildEncodedString(Node root, Map<Character, String> lookup, String toBeEncoded){
        StringBuilder encoded = new StringBuilder();
        for(char character : toBeEncoded.toCharArray()){
            if(lookup.containsKey(character)) {
                encoded.append(lookup.get(character));
            }else{
                encoded.append(lookup.get(' '));
            }
        }
        return encoded.toString();
    }


    /*
    Decode, takes the compressed file and the tree and decompresses the file
     */

    /**
     * Goes through the binary string and traverses the tree, once it gets to the leaf node it adds the character to
     * the string buider and eventually returns the decompressed string
     * @param compressedFile String of all the Huffman Encoded text file
     * @param root root Node of the tree
     * @return returns decompressed string
     */
    public static String decompress(String compressedFile, Node root){
        StringBuilder sb = new StringBuilder();
        Node c = root;
        for (int i = 0; i < compressedFile.length(); i++) {
            c = compressedFile.charAt(i) == '1' ? c.rightChild : c.leftChild;
            if (c.leftChild == null && c.rightChild == null) {
                sb.append(c.letter);
                c = root;
            }
        }
        return sb.toString();
    }

    /**
     * Reads the file that containes the root node of the tree
     * @param tree the file path of the file that contains the root node
     * @return returns The root Node of the tree
     */
    public static Node readTreeFile(String tree){
        Node test = null;

        try{
            FileInputStream file = new FileInputStream(tree);
            ObjectInputStream in = new ObjectInputStream(file);

            test = (Node)in.readObject();

            in.close();
            file.close();


        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return test;
    }

    public static void main(String[] args){
        String encodeOrDecode = args[0];
        String path = args[1];
        String outputFileName = args[2];
        String treeFile = null;
        if(args.length >3){treeFile = args[3];}
        
        // if first argument is encode
        if(encodeOrDecode.equals("encode")){
            String test = encode(path);
            BitstringJava bitstringJava = new BitstringJava();

            String binary_string_a = test;
            byte[] converted = bitstringJava.GetBinary(binary_string_a);

            // Save bit array to file
            try {
                OutputStream outputStream = new FileOutputStream(outputFileName);
                outputStream.write(converted);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // if first argument is decode
        if(encodeOrDecode.equals("decode")){
            try {
                byte[] allBytes = Files.readAllBytes(Paths.get(path));
                String decompressed = Huffman.decompress(BitstringJava.getString(allBytes), Huffman.readTreeFile(outputFileName));

                File file = new File("decompressed.txt");

                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);

                bw.write(decompressed);
                bw.close();


            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
