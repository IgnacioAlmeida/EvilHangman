package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangman{

    public static void main(String[] args) throws EmptyDictionaryException, IOException, GuessAlreadyMadeException {
        String dictionary = args[0];
        int wordLength = Integer.parseInt(args[1]);
        int guesses = 0;
        char guess = ' ';
        StringBuilder key = new StringBuilder();
        int limitGuesses = Integer.parseInt(args[2]);
        Set<Character> usedGuesses = new TreeSet<>();

        EvilHangmanGame newGame = new EvilHangmanGame();

        Scanner input = new Scanner(System.in);
        newGame.startGame(new File(dictionary), wordLength);

        do{
            if(limitGuesses > 1) {
                System.out.printf("You have %s guesses left\n", limitGuesses - guesses);
            }
            else{
                System.out.println("You have 1 guess left\n");
            }

            String lettersUsed = "Used letters: ";
            for(Character c : usedGuesses){
                lettersUsed += c + " ";
            }
            System.out.println(lettersUsed);

            if(guesses == 0){
                for(int i = 0; i < wordLength; i++){
                    key.append('-');
                }
            }
            System.out.println("Word: " + key);

            boolean success = false;
            while(!success) {
                try {

                    System.out.println("Enter guess: ");
                    guess = input.next().charAt(0);
                    guess = Character.toLowerCase(guess);

                    if(guess < 'a' || guess > 'z'){
                        throw new IOException();
                    }
                    if (usedGuesses.contains(guess)) {
                        throw new GuessAlreadyMadeException();
                    }
                    usedGuesses.add(guess);
                    success = true;
                } catch (GuessAlreadyMadeException ex) {
                    System.out.println("Letter already entered, please try a new one: ");
                } catch (IOException ex){
                    System.out.println("Invalid input, please try again");
                }
            }
            usedGuesses.add(guess);
            newGame.makeGuess(guess);
            guesses++;

            //If the new key has a character, it adds it to the key in main
            if(guesses > 0){
                String temp;
                temp = newGame.getGlobalKey().toString();
                for(int i = 0; i < temp.length(); i++){
                    if(temp.charAt(i) >= 'a' && temp.charAt(i) <= 'z'){
                        key.setCharAt(i, temp.charAt(i));
                    }
                }
            }

            if(key.toString().indexOf('-') == - 1){
                System.out.println("You won! the word is: " + key.toString());
                break;
            }
            if((limitGuesses - guesses) == 0){
                System.out.println("You lose!");
                System.out.println("The word was: " + newGame.randomWinWord());
            }

        }while(guesses < limitGuesses);


    }
}
