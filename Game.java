
import java.util.LinkedList;
import java.util.Random;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution; //My Java version of Python's random.choices()

public class Game {
    private int NS; // Our game settings
    private int ND;
    private int H;
    private int L;
    private int M;
    private int G;
    private boolean v;
    private boolean Player_A; // Boolean to keep track of the player
    private int[][][] Losses; // Our 3D arrays of Wins/Losses
    private int[][][] Wins;
    private double[] W; // Our Win probability vector
    private Random rand; // A random number generator to simulate rolling the dice 1 through NS
    private int[] Dice; // A vector to hold the dice numbers 1 through ND to be used with my library version of random.choices()
    private EnumeratedIntegerDistribution dist; // The distribution generator to pick the amount of dice based on P[d]

    Game(int NS, int ND, int H, int L, int M, int G, boolean v) {
        this.NS = NS; // Initialize the settings, player, random generator, Wins/Lozzes, and our Dice vector for the EnumeratedIntegerDistribution
        this.ND = ND;
        this.H = H;
        this.L = L;
        this.M = M;
        this.G = G;
        this.v = v;
        this.Player_A = true;
        rand = new Random();
        Losses = new int[L][L][ND + 1];
        Wins = new int[L][L][ND + 1];
        W = new double[ND];
        Dice = new int[ND]; // Fill Dice with 1 through ND so that we can properly get our random dice amount
        for (int i = 0; i < ND; i++) {
            Dice[i] = i + 1;
        }
    }

    // Our Simulation function to play the games
    public void Simulation() {
        for (int G = 0; G < this.G; G++) { // Play the game the specified amount of times

            // For -v, provide new game trace back
            if (v) {
                System.out.println("***Starting new game***");
            }

            int score_A = 0; // For keeping track of player A's score
            int score_B = 0; // For keeping track of player B's score
            boolean Winner = false; // For tracking if someone won
            Player_A = true; // A always goes first
            LinkedList<Entry> A = new LinkedList<Entry>(); // LinkedLists to keep track of each player's history
            LinkedList<Entry> B = new LinkedList<Entry>();

            // While someone has not won, continue playing the game
            while (!Winner) {

                Entry entry = new Entry(); // Create a history entry
                entry.X = (Player_A) ? score_A : score_B; // If it is player A's turn, A's score is X and vice versa
                entry.Y = (Player_A) ? score_B : score_A; // If it is player A's turn, B's score is Y and vice versa
                entry.d = getDice(entry.X, entry.Y);    //Choose the amount of dice to roll based on the current state
                if (Player_A) { // Add the entry to the associated history
                    A.add(entry);
                } else {
                    B.add(entry);
                }

                // Now begin rolling that player's dice
                if (v) {
                    System.out.printf("%s rolls %d dice:", (Player_A) ? "A" : "B", entry.d);
                }
                // For each dice, randomly roll it for a value 1 through NS
                for (int i = 0; i < entry.d; i++) {
                    int roll = rand.nextInt(NS) + 1;
                    if (Player_A) { // Add the roll to the current player's score
                        score_A += roll;
                    } else {
                        score_B += roll;
                    }
                    if (v) {
                        System.out.printf(" %s%d", (i > 0) ? "& " : "", roll);
                    }
                }

                // Now check for wins and losses
                int score = (Player_A) ? score_A : score_B;
                if (v) {
                    System.out.printf(" for a score of %d", score);
                }

                if (score <= H && score >= L) { // Current player won, exit while loop
                    if (v) {
                        System.out.printf(". %s wins\n", (Player_A) ? "A" : "B");
                    }
                    Winner = true;
                    continue;
                } else if (score > H) { // Current player lost, set winner to other player and exit while loop
                    if (v) {
                        System.out.printf(". %s loses\n", (Player_A) ? "A" : "B");
                    }
                    Winner = true;
                    Player_A = !Player_A;
                    continue;
                } else { // No one won, continue game by flipping turns
                    if (v) {
                        System.out.println();
                    }
                    Player_A = !Player_A;
                }
            }

            // Game is over, update the histories based on who won (the current listed player)
            LinkedList<Entry> W = (Player_A) ? A : B; // If it's A's turn, A won and vice versa
            LinkedList<Entry> L = (Player_A) ? B : A; // If it's A's turn, B lost and vice versa
            Entry index;

            // Update the winning states
            while (!W.isEmpty()) {
                index = W.remove();
                if (v) {
                    System.out.printf("\tUpdating Wins[%d,%d,%d] from %d to ", index.X, index.Y, index.d, Wins[index.X][index.Y][index.d]);
                }
                Wins[index.X][index.Y][index.d]++;
                if (v) {
                    System.out.printf("%d\n", Wins[index.X][index.Y][index.d]);
                }
            }

            // Update the losing states
            while (!L.isEmpty()) {
                index = L.remove();
                if (v) {
                    System.out.printf("\tUpdating Losses[%d,%d,%d] from %d to ", index.X, index.Y, index.d, Losses[index.X][index.Y][index.d]);
                }
                Losses[index.X][index.Y][index.d]++;
                if (v) {
                    System.out.printf("%d\n", Losses[index.X][index.Y][index.d]);
                }
            }

            // With -v, each game is space separated
            if (v) {
                System.out.println();
            }
        }

        // When there are no more games left to play, print the final output table
        print_output();
    }

    // Our function to randomly pick the amount of dice to be rolled
    int getDice(int X, int Y) {

        double numerator; // For keeping track of the Win probability numerator
        double denominator; // For keeping track of the Win probability denominator
        int max = 0; // Index of the max Win probability
        double s = 0; // For keeping track of the total Win probability
        int total_games = 0; // Keeps track of total games for later

        if (v) {
            System.out.printf("<X=%d,Y=%d> W[", X, Y);
        }

        // Calculate the Win probability of the given state <X,Y> for each Dice
        for (int d = 1; d < ND + 1; d++) {
            numerator = Wins[X][Y][d]; // Find the total Wins
            denominator = numerator + Losses[X][Y][d]; // Calculate the denominator of that state by adding the Wins and Losses
            total_games += denominator; // Add this amount to the total games counter used in the Probability calculation
            W[d - 1] = (denominator == 0) ? 0 : numerator / denominator; // Sets W[d] to zero if it divides by 0
            s += W[d - 1]; // Add the win probability to s
            if (W[d - 1] > W[max]) { // Checks for max Win probability and randomly picks when they're equal
                max = d - 1;
            } else if (W[d - 1] == W[max]) {
                max = rand.nextBoolean() ? d - 1 : max;
            }
            if (v) {
                System.out.printf("%s%d=%.3f", (d != 1) ? " " : "", d, W[d - 1]);
            }
        }

        if (v) {
            System.out.printf("] max=<%d=%.3f> ", max + 1, W[max]);
        }

        // Now calculate the probability weights
        double[] P = new double[ND];
        int M_val = (M == 0) ? 1 : M; // If M is 0, we use 1
        s -= W[max]; // Subtract the max Win from s and calculate its probability using the formula
        P[max] = ((total_games * W[max]) + M_val) / ((total_games * W[max]) + (ND * M_val));
        for (int d = 0; d < ND; d++) { // Calculate the remaining probabilities
            if (d == max) {
                continue;
            }
            P[d] = ((1 - P[max]) * (total_games * W[d] + M_val)) / ((s * total_games) + ((ND - 1) * M_val));
        }

        // Print the probability array for the v mode
        if (v) {
            print_probabilities(P);
        }

        // Create the dice distribution and sample it to get a randomly choosen
        // Dice[1,..,ND] amount based on their probability weights P
        dist = new EnumeratedIntegerDistribution(Dice, P);
        int dice = dist.sample();
        if (v) {
            System.out.printf("sample %d\n", dice);
        }
        return dice;
    }

    // Function to print our final output tables at the end of the simulation
    void print_output() {

        String[][] output = new String[L + 1][L + 1];
        int L_length = Integer.toString(L).length();    //For formatting the cell length to fit the indexes (0 through L),
        int ND_Length = Integer.toString(ND).length();  //dice (1 through ND), or to have a default length of 8
        int MAX_LENGTH = (8 < L_length || 8 < ND_Length + 7)? Math.max(L_length, ND_Length + 7):8;
        output[0][0] = String.format("%" + MAX_LENGTH + "s", ""); // First cell is a buffer cell of MAX_LENGTH whitespace

        // Now fill in the Row and Column indexes
        for (int i = 1; i < L + 1; i++) {
            output[i][0] = String.format("%" + MAX_LENGTH + "d", i - 1);
            output[0][i] = output[i][0];
        }

        // For each state, find its max W[d] and set the output cell to that value
        for (int i = 0; i < L; i++) {
            for (int j = 0; j < L; j++) {

                double numerator; // For calculating W[d] and tracking the max index
                double denominator;
                int max = 0;
                int total_games = 0; // Track total games in case this state <X=i,Y=j> was never reached

                // For every dice at this state, we skip it if it wasn't played or check if it's
                // the max if it was
                for (int d = 1; d < ND + 1; d++) {
                    numerator = Wins[i][j][d];
                    denominator = numerator + Losses[i][j][d];
                    if (denominator != 0) { // We only want to consider the games we played when printing the table
                        W[d - 1] = numerator / denominator; // Calculate W[d]
                        if (total_games == 0 || W[d - 1] > W[max]) { // Set max to the current dice if no other dice was played yet or if it's the current max
                            max = d - 1;
                        } else if (W[d - 1] == W[max]) { // Randomly pick between equal maximums as usual
                            max = rand.nextBoolean() ? d - 1 : max;
                        }
                        total_games += denominator; // Add the games so we know not to print n/a
                    } else { // We don't do anything when that state was never played
                        continue;
                    }
                }

                // After calculating the max win probability, set the output value to n/a if it
                // was never played or to the max value
                if (total_games == 0) { // State was never reached, print n/a
                    output[i+1][j+1] = String.format("%" + MAX_LENGTH + "s", "n/a");
                } else { // Otherwise, it was reached so print the max W[d]
                    output[i+1][j+1] = String.format(" %" + (MAX_LENGTH - 7) + "d:%.3f", max+1, W[max]);
                }
            }
        }

        // Now print the table cells
        for (int i = 0; i < L + 1; i++) {
            for (int j = 0; j < L + 1; j++) {
                System.out.print(output[i][j]);
            }
            System.out.println();
        }
    }

    // For the -v option, shows the calculated P distribution of that state
    void print_probabilities(double[] P) {
        System.out.print("P[");
        for (int i = 0; i < P.length; i++) {
            System.out.printf("%s%d=%.3f", (i != 0) ? " " : "", i + 1, P[i]);
        }
        System.out.print("] ");
    }

    // Text example from the Lab 4 instructions to double check my getDice() function
    // Prints W[1=0.000 2=0.750 3=0.500] max=<2=0.750> P[1=0.148 2=0.556 3=0.296]
    // as expected with -ND 3 -M 4 -v (other options can be whatever, they won't matter)
    void test() {
        Wins[2][3][1] = 0;
        Wins[2][3][2] = 3;
        Wins[2][3][3] = 1;
        Losses[2][3][1] = 2;
        Losses[2][3][2] = 1;
        Losses[2][3][3] = 1;
        getDice(2, 3);
    }
}
