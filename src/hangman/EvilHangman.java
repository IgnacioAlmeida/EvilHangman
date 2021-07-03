package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangman{

    public static void main(String[] args) throws EmptyDictionaryException, IOException, GuessAlreadyMadeException {
        File dictionary = new File(args[0]);
        int wordLength = Integer.parseInt(args[1]);
        int limitGuess = Integer.parseInt(args[2]);
        int guessCounter = 0;
        StringBuilder key = new StringBuilder();
        EvilHangmanGame newGame = new EvilHangmanGame();
        newGame.startGame(dictionary, wordLength);
        Scanner input = new Scanner(System.in);
        char guess = ' ';

        do{
            if(key.toString().length() == 0){
                for(int i = 0; i < wordLength; i++){
                    key.append("-");
                }
            }
            System.out.printf("You have %s guesses left\n", limitGuess - guessCounter);

            String output = "Guessed letters:";
            for(Character c : newGame.getGuessedLetters()){
                output += c + " ";
            }
            System.out.println(output);

            System.out.println("Word: " + key);

            boolean success = false;
            while(!success){
                System.out.println("Enter guess: ");
                guess = input.next().charAt(0);
                try{
                    if(newGame.getGuessedLetters().contains(guess)){
                        throw new GuessAlreadyMadeException();
                    }
                    if(guess < 'a' || guess > 'z'){
                        throw new IOException();
                    }
                } catch (GuessAlreadyMadeException ex){
                    System.out.println("You already used char");
                } catch (IOException ex){
                    System.out.println("Enter valid letter");
                }
                newGame.makeGuess(guess);
                success = true;
            }
            int counter = 0;
            for(int i = 0; i < wordLength; i++){
                if(newGame.getGlobalKey().charAt(i) == guess){
                    key.setCharAt(i, guess);
                    counter++;
                }
            }

            if(counter > 0){
                System.out.printf("Yes, there is %s %s\n", counter, guess);
            }else{
                System.out.printf("Sorry, there are no %s's\n", guess);
                guessCounter++;
            }

            if(key.indexOf("-") == -1){
                System.out.println("You won!\nThe word was: " + key);
            }
            if(limitGuess - guessCounter == 0){
                System.out.println("You lose!\nThe ward was: " + newGame.randomWinWord());
            }

        }while(limitGuess - guessCounter != 0);


    }
}
