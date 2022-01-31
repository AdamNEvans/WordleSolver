Attempts to solve Wordle puzzles.

See the game at https://www.powerlanguage.co.uk/wordle/

Code Design:
- Main is in Wordle.java.
- Initialize a WordleSolver object to play a game with
- Create Guess objects, populate the guess results, and pass it to the solver which will reduce its possibilities accordingly
- Ask the chosen solver for the next guess, and repeat until solved or the guess limit is reached
