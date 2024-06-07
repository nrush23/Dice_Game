
public class dice {

    public static void main(String[] args) {
        if (args.length < 12) {
            System.out.printf("Error: given %d arguments, need at least 6\n", args.length / 2);
            System.exit(-1);
        }

        int ND = -1;
        int NS = -1;
        int H = -1;
        int L = -1;
        int M = -1;
        int G = -1;
        boolean v = false;

        int i = 0;
        while (i < args.length) {   //Parse the options similar to getopt in C++
            switch (args[i]) {
                case "-ND":
                    try {
                        ND = Integer.parseInt(args[i + 1]);
                        if (ND <= 1) {
                            System.out.printf("Error: Invalid -ND value given %d, must be greater than 1\n", ND);
                            System.exit(-2);
                        }
                        i++;
                    } catch (Exception e) {
                        System.out.println((i != args.length) ? "Invalid -ND arg given: " + args[i + 1] : "No -ND value given");
                        System.exit(-2);
                    }
                    break;
                case "-NS":
                    try {
                        NS = Integer.parseInt(args[i + 1]);
                        if (NS <= 1) {
                            System.out.printf("Error: Invalid -NS value given %d, must be greater than 1\n", NS);
                            System.exit(-2);
                        }
                        i++;
                    } catch (Exception e) {
                        System.out.println((i != args.length) ? "Invalid -NS arg given: " + args[i + 1] : "No -NS value given");
                        System.exit(-2);
                    }
                    break;
                case "-H":
                    try {
                        H = Integer.parseInt(args[i + 1]);
                        if (H <= 0) {
                            System.out.printf("Error: -H value %d must be greater than 0\n", H);
                            System.exit(-2);
                        }
                        i++;
                    } catch (Exception e) {
                        System.out.println((i != args.length) ? "Error: Invalid -H value given " + args[i + 1]: "No -H value given");
                        System.exit(-2);
                    }
                    break;
                case "-L":
                    try {
                        L = Integer.parseInt(args[i + 1]);
                        if (L <= 0) {
                            System.out.printf("Error: Invalid -L given %d, must be greater than 0\n", L);
                            System.exit(-2);
                        }
                        i++;
                    } catch (Exception e) {
                        System.out.println((i != args.length) ? "Error: Invalid -L value given " + args[i + 1]: "No -L value given");
                        System.exit(-2);
                    }
                    break;
                case "-M":
                    try {
                        M = Integer.parseInt(args[i + 1]);
                        if (M < 0) {
                            System.out.printf("Error: Invalid -M value given %d, must be greater than 0\n", M);
                            System.exit(-2);
                        }
                        i++;
                    } catch (Exception e) {
                        System.out.println((i != args.length) ? "Error: Invalid -M value given " + args[i + 1]: "No -M value given");
                        System.exit(-2);
                    }
                    break;
                case "-G":
                    try {
                        G = Integer.parseInt(args[i + 1]);
                        if(G < 1){
                            System.out.printf("Error: Invalid -G value given %d, must be greater than 1\n", G);
                            System.exit(-2);
                        }
                        i++;
                    } catch (Exception e) {
                        System.out.println((i != args.length) ? "Error: Invalid -G value given " + args[i + 1]: "No -G value given");
                        System.exit(-2);
                    }
                    break;
                case "-v":
                    v = true;
                    break;
                default:
                    System.out.printf("Error: Invalid argument given %s\n", args[i]);
                    System.exit(-1);
                    break;
            }
            i++;
        }
        if(H  < L){ //Check that H is greater than L
            System.out.printf("Error: -H %d is less than -L %d\n", H, L);
            System.exit(-2);
        }
        if (v) {    //For -v, print the options
            System.out.printf("ND=%d NS=%d H=%d L=%d M=%d G=%d v=%s\n", ND, NS, H, L, M, G, v);
        }
        //Start the simulation with the given settings
        Game game = new Game(NS, ND, H, L, M, G, v);
        game.Simulation();
    }
}