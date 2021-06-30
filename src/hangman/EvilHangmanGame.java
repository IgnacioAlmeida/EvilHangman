package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame{
    private Set<String> englishWords = new HashSet<String>();
    private SortedSet<Character> guessedLetters = new TreeSet<Character>();
    public String globalKey;

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        Scanner scanner = new Scanner(dictionary);
        englishWords.clear();
        if(!scanner.hasNext()){
            throw new EmptyDictionaryException();
        }

        while (scanner.hasNext()) {
            EvilHangmanGame game = new EvilHangmanGame();
            String str = scanner.next();
            if (str.length() == wordLength) {
                englishWords.add(str);
            }
        }

        if(englishWords.size() == 0){
            throw new EmptyDictionaryException();
        }

        scanner.close();

    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        guess = Character.toLowerCase(guess);

        if(guessedLetters.contains(guess)){
            throw new GuessAlreadyMadeException();
        }
        else {
            guessedLetters.add(guess);
        }

        Map<String, Set<String>> keyedWords = new HashMap<>();
        //System.out.println("Inside makeGuess function\n");

        //Checks if guessed letter has been used already
        if(guessedLetters.contains(guess)){

        }

        //3a
        for(String string : englishWords){
            StringBuilder key = new StringBuilder();

            //Creates a key with the guessed letter for each letter in the dicctionary. Ex: Word Length 4,
            //Guessed Letter: E, Key for ECHO would be E---
            for(int i = 0; i < string.length(); i++){
                key.append('-');
                if(string.charAt(i) == guess){
                    key.setCharAt(i, guess);
                }
            }
            //System.out.printf("Key: %s \n", key);

            //If that key isn't in the map, we create a set that has all the possible words that will match that key and
            //assign it as the value. Ex: Key: E--- Values: ECHO, ESSO, etc
            if(!(keyedWords.containsKey(key.toString()))){
                Set<String> possibleWords = new HashSet<>();
                possibleWords.add(string);
                keyedWords.put(key.toString(), possibleWords);
            }
            //If the key already exists, we get the value related to that key which is a set of string and add the new
            //word to the set
            else{
                keyedWords.get(key.toString()).add(string);
            }
        }

        //3b Get the largest set of possibleWords from the map
        String biggestListKey = "";
        //Contains the set with the most words to make it harder to guess
        Set<String> biggestList = new HashSet<>();
        for(String key : keyedWords.keySet()){
            if(biggestListKey.isEmpty()){
                biggestListKey = key.toString();
            }
            else{
                //Chooses the largest of the word groups
                if(keyedWords.get(key.toString()).size() > keyedWords.get(biggestListKey).size()){
                    biggestListKey = key.toString();
                }
                //.1 If they are of the same size, it chooses the one that has the guess char the least
                if(keyedWords.get(key.toString()).size() == keyedWords.get(biggestListKey).size()){
                    int keyGuessedLetterCount = 0;
                    int biggestKeyGuessedLetterCount = 0;
                    int keyIndexCount = 0;
                    int biggestKeyIndexCount = 0;

                    for(int i = 0; i < key.length(); i++){
                        if(key.charAt(i) == guess){
                            keyGuessedLetterCount++;
                            keyIndexCount += i;
                        }
                        if(biggestListKey.charAt(i) == guess){
                            biggestKeyGuessedLetterCount++;
                            biggestKeyIndexCount += i;
                        }
                    }
                    if(keyGuessedLetterCount < biggestKeyGuessedLetterCount){
                        biggestListKey = key.toString();
                    }

                    //.2 if .1 is not solved, we choose the one with the rightmost guessed letter. Ex: E--E and -E-E, we
                    //chose the second
                    if(keyGuessedLetterCount == biggestKeyGuessedLetterCount){
                        if(keyIndexCount > biggestKeyIndexCount){
                            biggestListKey = key.toString();
                        }
                    }
                    //if .2 is not solved, we choose the one with the next rightmost letter (repeat until group is chosen)
                }
            }
        }

        //it searches for the guess character in the key
        biggestList = keyedWords.get(biggestListKey);
        globalKey = biggestListKey;

        if(biggestListKey.indexOf(guess) == -1){
            System.out.printf("Sorry, there are no %s's\n\n", guess);
        }

        if(biggestListKey.indexOf(guess) != -1){
            int counter = 0;
            for(int i = 0; i < biggestListKey.length(); i++){
                if(biggestListKey.charAt(i) == guess){
                    counter++;
                }
            }
            System.out.printf("Yes, there is %s %s's\n\n",counter, guess);
        }

//        String lettersUsed = "Used letters: ";
//        for(Character c : guessedLetters){
//            lettersUsed += c + " ";
//        }
//        System.out.println(lettersUsed);
//
//        System.out.printf("Word: %s\n", biggestListKey);

        englishWords = biggestList;

        return biggestList;
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedLetters;
    }

    public String randomWinWord(){
        String[] randomWord = englishWords.toArray(new String[englishWords.size()]);
        Random rndm = new Random();
        int rndmNumber = rndm.nextInt(englishWords.size());
        String output = randomWord[rndmNumber];
        return output;
    }

    public String getGlobalKey(){
        return globalKey;
    }
}
