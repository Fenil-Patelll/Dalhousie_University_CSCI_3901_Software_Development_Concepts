import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Collections;
import java.util.LinkedHashMap;
public class SubstitutionCipher {
   LinkedHashMap<Character, Character> Key = new LinkedHashMap<>();
   String keyName = "";
   String ciphertext;
   StringBuilder plaintext;
   Queue<String> languageNames = new LinkedList<>();
   LinkedHashMap<String,Double> languageabsDiff = new LinkedHashMap<>();
   LinkedHashMap cipherFrequencyTable;
   Boolean derivedKeyValidation;
   LinkedHashMap<Character,Character> derivedKey = new LinkedHashMap<>();
   LinkedHashMap<String, LinkedHashMap<Character,Double>>  allLanguagesFrequencyTable = new LinkedHashMap<>();
   SubstitutionCipher() {

   }

   //this constructor will be called if the user provides key while creating the object
   SubstitutionCipher(String name, LinkedHashMap<Character, Character> key) {

      // Key validation is done before diving into the main logic
      if(!keyIsValid()){
         System.out.println("Key is not valid");
         Runtime.getRuntime().halt(0);
      }
      //Check Whether the Null Value is provided in the constructor
      if(name==null || key==null){
         System.out.println("null value provided in substitution cipher");
         Runtime.getRuntime().halt(0);
      }
      //check if the name of key is empty
      else if(name.length()==0 || key.size()==0){
         System.out.println("string name or key is empty in constructor");
         Runtime.getRuntime().halt(0);
      }

         this.Key = key;
         this.keyName = name;
   }

   //read the cipher text file and generate the hashtable with character as a key and characterFrequency as Values sorted
   //in descending order of the values
   //Frequency table will be generated in the descending order.
   Boolean  ciphertext(String name) throws FileNotFoundException {
      if(name == null ){
         Runtime.getRuntime().halt(0);
      }
      String cipherTemp = "";
      File text = new File(name);
      Scanner reader = new Scanner(text);
      while(reader.hasNextLine()){
         cipherTemp = cipherTemp  + reader.nextLine() + '\n';
      }
      if (cipherTemp.isEmpty()){
        return false;
      }
      else{
         this.ciphertext = cipherTemp;
         double totalChar = 0;
         LinkedHashMap<Character, Integer> languageTable = new LinkedHashMap<>();
         LinkedHashMap<Character, Double> sortedTable = new LinkedHashMap<>();
         for(char c : cipherTemp.toCharArray()) {
            if(Character.isAlphabetic(c)){
               totalChar++;
               c = Character.toLowerCase(c);
               if(languageTable.containsKey(c)){
                  languageTable.put(c,languageTable.get(c)+1);
               }
               else{
                  languageTable.put(c,1);
               }
            }
         }
         //System.out.println("total number of character "+totalChar);

         while (languageTable.size() > 0)
         {

            double currentmaximum = 0;
            char argumentmaximum = 0;
            for (Map.Entry<Character,
                    Integer> Item : languageTable.entrySet())
            {
               if (Item.getValue() > currentmaximum ||
                       (Item.getValue() == currentmaximum &&
                               Item.getKey() > argumentmaximum))
               {
                  argumentmaximum = Item.getKey();
                  currentmaximum = Item.getValue();
               }
            }

            double op = currentmaximum/totalChar;
            DecimalFormat df = new DecimalFormat("#.###");
            op = Double.valueOf(df.format(op));
            sortedTable.put(argumentmaximum, op);
            // Delete the maximum value
            languageTable.remove(argumentmaximum);
         }
//         System.out.println("Cipher Frequency table");
//         sortedTable.forEach((k, v ) -> {
//            System.out.println(k +" "+ v);
//         });
         this.cipherFrequencyTable = sortedTable;

         return true;
      }
   }

   //orginal langauge file will be read, frequency table will be generated, sorted  in descending order
   //validation will be checked
   //LinkedHashmap will be created with each language as key and its abs total difference as value
   Boolean originalLanguage(String name, String filename) throws FileNotFoundException {
      if(name == null || filename == null){
         System.out.println("Null values provided in original language");
         Runtime.getRuntime().halt(0);
      }
      String language = "";
      File languageFile = new File(filename);
      Scanner languageReader = new Scanner((languageFile));
      while (languageReader.hasNextLine()){
         language = language + languageReader.nextLine() + '\n';
      }
      if(language.isEmpty()){
         return false;
      }
      else{
         double totalChar = 0;
         LinkedHashMap<Character, Integer> languageTable = new LinkedHashMap<>();
         LinkedHashMap<Character, Double> sortedTable = new LinkedHashMap<>();
         for(char c : language.toCharArray()) {
            if(Character.isAlphabetic(c)){
               totalChar++;
               c = Character.toLowerCase(c);
               if(languageTable.containsKey(c)){
                  languageTable.put(c,languageTable.get(c)+1);
               }
               else{
                  languageTable.put(c,1);
               }
            }
         }
         //System.out.println("total number of character "+totalChar);

         while (languageTable.size() > 0)
         {

            double currentmaximum = 0;
            char argumentmaximum = 0;
            for (Map.Entry<Character,
                    Integer> Item : languageTable.entrySet())
            {
               if (Item.getValue() > currentmaximum ||
                       (Item.getValue() == currentmaximum &&
                               Item.getKey() > argumentmaximum))
               {
                  argumentmaximum = Item.getKey();
                  currentmaximum = Item.getValue();
               }
            }
            double op = currentmaximum/totalChar;
            DecimalFormat df = new DecimalFormat("#.###");
            op = Double.valueOf(df.format(op));
            sortedTable.put(argumentmaximum, op);
            languageTable.remove(argumentmaximum);
         }
         //System.out.println("Language Frequency table");
//         sortedTable.forEach((k, v ) -> {
//            System.out.println(k +" "+ v);
//         });
         this.languageNames.add(name);
         this.allLanguagesFrequencyTable.put(name,sortedTable);
      return true;
      }
   }

   //Comparison between  the abs total deference of the all languages will be done,
   //language with minimum value of total deference will be returned
   String matchLanguage(){

     //System.out.println("Language Frequency table values");
      this.allLanguagesFrequencyTable.keySet();
      for(LinkedHashMap<Character,Double> lft : this.allLanguagesFrequencyTable.values()){
         ArrayList<Double> langTableValues
             = new ArrayList<Double>(lft.values());
         ArrayList<Double> cipherTableValues
                 = new ArrayList<Double>(cipherFrequencyTable.values());
         //System.out.println(cipherTableValues);
         //System.out.println(langTableValues);
         int langTableSize = langTableValues.size();
         int cipherTableSize = cipherTableValues.size();
         int count = langTableSize<cipherTableSize?langTableSize:cipherTableSize;
         double absDiffTotal = 0;
         for(int i=0;i<count;i++){
            absDiffTotal = absDiffTotal + Math.abs(langTableValues.get(i) - cipherTableValues.get(i));
         }
         DecimalFormat df = new DecimalFormat("#.####");
         absDiffTotal = Double.valueOf(df.format(absDiffTotal));
         String lang_name = languageNames.remove();
         this.languageabsDiff.put(lang_name,absDiffTotal);
         System.out.println("Language name - " + lang_name +" Abs Difference - " + absDiffTotal);
      }
      return Collections.min(this.languageabsDiff.entrySet(), Map.Entry.comparingByValue()).getKey();
   }

   // decryption of the ciphertext with the help of known key will be done
   //decoded string will be returned
   String decodeText(){
      if(this.keyIsValid()){
         this.plaintext = new StringBuilder(this.ciphertext);
         Map<Character, Character> newKey = new HashMap<>();
         for(Map.Entry<Character, Character> entry : Key.entrySet()){
            newKey.put(entry.getValue(), entry.getKey());
         }

         for(int i = 0, n = this.plaintext.length() ; i < n ; i++) {
            Character c =this.plaintext.charAt(i);
            if(Character.isAlphabetic(c) && newKey.containsKey(Character.toLowerCase(c))){
               if(Character.isUpperCase(c)){
                  this.plaintext.setCharAt(i,Character.toUpperCase(newKey.get(Character.toLowerCase(c))));
               }
               else{
                  this.plaintext.setCharAt(i,newKey.get(c));
               }
            }
         }
         return this.plaintext.toString();
      }
      else{
         return "Key is not valid";
      }

   }

   // create and return an encryption key by mapping the letter frequencies between the matched language,
   // and the ciphertext frequencies
   Boolean guessKeyFromFrequencies(String language){
      if(language == null){
         System.out.println("Null value provided in language");
         Runtime.getRuntime().halt(0);
      }
      ArrayList<Character> langCharTable = new ArrayList<>(this.allLanguagesFrequencyTable.get(language).keySet());
      ArrayList<Character> cipherChrTable =  new ArrayList<>(this.cipherFrequencyTable.keySet());
      int langCharSize = langCharTable.size();
      int cipherCharSize = cipherChrTable.size();
      this.derivedKeyValidation = cipherCharSize <= langCharSize;

      if(derivedKeyValidation){
         for(int i = 0; i < cipherCharSize; i++)
         {
            this.derivedKey.put(langCharTable.get(i),cipherChrTable.get(i));
         }
         this.Key = this.derivedKey;
      }
      return derivedKeyValidation;
   }

   //Key Validation is done
   Boolean keyIsValid(){
      for(Character c :this.Key.keySet()){
         if(!Character.isAlphabetic(c)){
            System.out.println("Key is not valid");
            return false;
         }
      }

      HashMap<Character,Integer> unq = new HashMap<>();
      ArrayList<Character> ch = new ArrayList<>(this.Key.values());
      boolean check = true;
      for(Character c : ch){
         if(unq.containsKey(c)){
            check = false;
            break;
         }
         else{
            unq.put(c,1);
         }
      }
      return check;
   }

   //get the key currently in use
   HashMap<Character, Character> getKey(){

      return this.Key;
   }

   //Set the encryption key with user provided key and value
   Boolean setDecodeLetter(Character plaintextChar, Character ciphertextChar){
      if(plaintextChar == null || ciphertextChar == null || !Character.isAlphabetic(plaintextChar) || !Character.isAlphabetic(ciphertextChar)){
         System.out.println("invalid input for set decode letter");
         Runtime.getRuntime().halt(0);
      }
      if(this.keyIsValid()){
         this.Key.put(plaintextChar,ciphertextChar);
         return true;
      }
      else{
         return false;
      }
   }
}

