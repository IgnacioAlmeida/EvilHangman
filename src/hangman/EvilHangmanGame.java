package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame{
    private Set<String> dictionary = new HashSet<>();
    private SortedSet<Character> guessedLetters = new TreeSet<>();
    public String globalKey;

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        Scanner scanner = new Scanner(dictionary);
        this.dictionary.clear();
        if(!scanner.hasNext()){
            throw new EmptyDictionaryException();
        }

        while (scanner.hasNext()) {
            EvilHangmanGame game = new EvilHangmanGame();
            String str = scanner.next();
            if (str.length() == wordLength) {
                this.dictionary.add(str);
            }
        }

        if(this.dictionary.size() == 0){
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



        //3a
        for(String string : dictionary){
            StringBuilder key = new StringBuilder();

            //Creates a key with the guessed letter for each letter in the dicctionary. Ex: Word Length 4,
            //Guessed Letter: E, Key for ECHO would be E---
            for(int i = 0; i < string.length(); i++){
                key.append('-');
                if(string.charAt(i) == guess){
                    key.setCharAt(i, guess);
                }
            }


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
        for(String key : keyedWords.keySet()){
            if(biggestListKey.isEmpty()){
                biggestListKey = key;
            }
            else{
                //Chooses the largest of the word groups
                if(keyedWords.get(key).size() > keyedWords.get(biggestListKey).size()){
                    biggestListKey = key;
                }
                //.1 If they are of the same size, it chooses the one that has the guess char the least
                if(keyedWords.get(key.toString()).size() == keyedWords.get(biggestListKey).size()){
                    int keyGuessedCount = 0;
                    int biggestKeyGuessedCount = 0;
                    int keyIndexCount = 0;
                    int biggestKeyIndexCount = 0;

                    for(int i = 0; i < key.length(); i++){
                        if(key.charAt(i) == guess){
                            keyGuessedCount++;
                            keyIndexCount += i;
                        }
                        if(biggestListKey.charAt(i) == guess){
                            biggestKeyGuessedCount++;
                            biggestKeyIndexCount += i;
                        }
                    }
                    if(keyGuessedCount < biggestKeyGuessedCount){
                        biggestListKey = key;
                    }

                    //.2 if .1 is not solved, we choose the one with the rightmost guessed letter. Ex: E--E and -E-E, we
                    //chose the second
                    if(keyGuessedCount == biggestKeyGuessedCount){
                        if(keyIndexCount > biggestKeyIndexCount){
                            biggestListKey = key;
                        }
                    }
                    //if .2 is not solved, we choose the one with the next rightmost letter (repeat until group is chosen)
                }
            }
        }

        globalKey = biggestListKey;
        dictionary = keyedWords.get(biggestListKey);

        return dictionary;
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedLetters;
    }

    public String randomWinWord(){
        String[] randomWord = dictionary.toArray(new String[dictionary.size()]);
        Random rndm = new Random();
        int rndmNumber = rndm.nextInt(dictionary.size());
        String output = randomWord[rndmNumber];
        return output;
    }

    public String getGlobalKey(){
        return globalKey;
    }
}
