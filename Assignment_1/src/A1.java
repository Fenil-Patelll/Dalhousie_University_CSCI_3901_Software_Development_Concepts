import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;
public class A1 {
    public static void main(String[] args) throws FileNotFoundException {

        //taking the User Input if the user has Key or not
         Scanner sc = new Scanner(System.in);
         System.out.println("Do you have an encryption key? y or n");
         String s1 = sc.nextLine();
         if(s1.equals("n"))
         {
             SubstitutionCipher sc2 = new SubstitutionCipher();
             sc2.ciphertext("ciphertext.txt");
             sc2.originalLanguage("English","Language_A.txt");
             sc2.originalLanguage("Dutch","Language_B.txt");
             sc2.originalLanguage("Spanish","Language_C.txt");
             String matchedLanguage = sc2.matchLanguage();
             System.out.println("Matched language is "+ matchedLanguage);
             System.out.println("Is Guessed key is correct? " + sc2.guessKeyFromFrequencies(matchedLanguage));
             System.out.println("Is Key Valid? "+ sc2.keyIsValid());
             System.out.println("Encryption Key is...");
             for(Character k : sc2.getKey().keySet()){
                 System.out.println("Key -> " + k+ " Value -> "+ sc2.getKey().get(k));
             }
             // uncomment the section for below setDecodeLetter Method and run it.
//           sc2.setDecodeLetter(' ',' ');
         }

         else if(s1.equals("y")){
             LinkedHashMap<Character,Character> key = new LinkedHashMap<>();
             System.out.println("Give the encryption key...");
             System.out.println("Input the total number of character in Key ");
             Integer number = sc.nextInt();
             while(number!=0) {
                 System.out.println("Enter the plaintext character of key");
                 Character plaintext = sc.next().charAt(0);
                 if(!Character.isAlphabetic(plaintext)){
                     System.out.println("not a valid key");
                     return;
                 }
                 System.out.println("Enter the ciphertext character of key");
                 Character ct = sc.next().charAt(0);
                 if(!Character.isAlphabetic(ct)){
                     System.out.println("not a valid key");
                     return;
                 }
                 key.put(Character.toLowerCase(plaintext), Character.toLowerCase(ct));
                 number--;
             }
             SubstitutionCipher sc1 = new SubstitutionCipher("mykey",key);
             if(!sc1.keyIsValid()){
                 System.out.println("Provided Key is not valid");
                 return;
             }
             System.out.println("Is Key Valid? "+ sc1.keyIsValid());
             sc1.ciphertext("ciphertext.txt");
             System.out.println("Encryption Key is...");
             for(Character k : sc1.getKey().keySet()){
                 System.out.println("Key -> " + k+ " Value -> "+ sc1.getKey().get(k));
             }
             System.out.println("Decoded Text is..\n" + sc1.decodeText());
         }
         else{
             System.out.println("Wrong input");
         }
    }
}
