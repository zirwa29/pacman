import java.util.Scanner;
import java.util.Random;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public boolean equals(Position other) {
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}

abstract class GameEntity {
    protected Position position;
    protected char symbol;
    protected String name;

    public GameEntity(String name, char symbol, int startX, int startY) {
        this.name = name;
        this.symbol = symbol;
        this.position = new Position(startX, startY);
    }

    public Position getPosition() { return position; }
    public char getSymbol() { return symbol; }
    public String getName() { return name; }

    public void setPosition(int x, int y) {
        position.setX(x);
        position.setY(y);
    }

    public abstract void move(char[][] maze);
}

class Pacman extends GameEntity {
    private int score;
    private int lives;
    private Scanner scanner;

    public Pacman(int startX, int startY) {
        super("Pacman", '<', startX, startY);
        this.score = 0;
        this.lives = 3;
        this.scanner = new Scanner(System.in);
    }

    public int getScore() { return score; }
    public int getLives() { return lives; }

    public void addScore(int points) {
        this.score += points;
        System.out.println("+" + points + " points! Total: " + score);
    }

    public void loseLife() {
        this.lives--;
    }

    public boolean isAlive() {
        return lives > 0;
    }

    @Override
    public void move(char[][] maze) {
        System.out.print("Enter move (W/A/S/D): ");
        char input = scanner.next().toUpperCase().charAt(0);

        int newX = position.getX();
        int newY = position.getY();

        switch(input) {
            case 'W': newX--; break;
            case 'S': newX++; break;
            case 'A': newY--; break;
            case 'D': newY++; break;
            default: return;
        }

        if (newX >= 0 && newX < maze.length &&
                newY >= 0 && newY < maze[0].length &&
                maze[newX][newY] != '#') {
            position.setX(newX);
            position.setY(newY);
        } else {
            System.out.println("Can't move there - wall!");
        }
    }
}

class Ghost extends GameEntity {
    private Random random;

    public Ghost(String name, char symbol, int startX, int startY) {
        super(name, symbol, startX, startY);
        this.random = new Random();
    }

    @Override
    public void move(char[][] maze) {
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int attempt = 0; attempt < 100; attempt++) {
            int direction = random.nextInt(4);
            int newX = position.getX() + dx[direction];
            int newY = position.getY() + dy[direction];

            if (newX >= 0 && newX < maze.length &&
                    newY >= 0 && newY < maze[0].length &&
                    maze[newX][newY] != '#') {
                position.setX(newX);
                position.setY(newY);
                break;
            }
        }
    }
}

class World {
    private char[][] bound;
    private int[][] specialFood;
    private int m, n;
    private int foodCount;
    private int specialFoodCount;
    private int initialFoodCount;      // added
    private int initialSpecialFoodCount; // added

    public World() {
        this.m = 20;   // fixed maze size
        this.n = 30;
        this.bound = new char[m][n];
        this.specialFood = new int[m][n];
        this.foodCount = 0;
        this.specialFoodCount = 0;
        setMaze();
    }

    // removed parameterized constructor – maze size is fixed

    private void setMaze() {
        // Initialize empty
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                bound[i][j] = ' ';
                specialFood[i][j] = 0;
            }
        }

        // Outer walls
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0 || i == m - 1 || j == 0 || j == n - 1) {
                    bound[i][j] = '#';
                }
            }
        }

        // Inner walls – hardcoded for 20×30
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 3 && j > 5 && j < 25) bound[i][j] = '#';
                else if (i == 16 && j > 5 && j < 25) bound[i][j] = '#';
                else if (i == 2 && j == 6) bound[i][j] = '#';
                else if (i == 2 && j == 24) bound[i][j] = '#';
                else if (i == 17 && j == 6) bound[i][j] = '#';
                else if (i == 17 && j == 24) bound[i][j] = '#';
                else if (i > 3 && i < 16 && j == 2) bound[i][j] = '#';
                else if (i > 3 && i < 16 && j == 27) bound[i][j] = '#';
                else if (i > 5 && i < 14 && j == 5) bound[i][j] = '#';
                else if (i > 5 && i < 14 && j == 25) bound[i][j] = '#';
                else if (i == 17 && j > 2 && j < 4) bound[i][j] = '#';
                else if (i == 7 && j > 6 && j < 11) bound[i][j] = '#';
                else if (i > 7 && i < 12 && j == 10) bound[i][j] = '#';
                else if (i == 11 && j > 10 && j < 16) bound[i][j] = '#';
                else if (i == 14 && j > 6 && j < 10) bound[i][j] = '#';
                else if (i == 13 && j == 9) bound[i][j] = '#';
                else if (j == 7 && i < 13 && i > 8) bound[i][j] = '#';
                else if (j == 13 && i > 5 && i < 9) bound[i][j] = '#';
                else if (i == 8 && j > 13 && j < 18) bound[i][j] = '#';
                else if (j == 17 && i > 8 && i < 15) bound[i][j] = '#';
                else if (i == 14 && j > 17 && j < 23) bound[i][j] = '#';
                else if (j == 19 && i > 8 && i < 12) bound[i][j] = '#';
                else if (j > 19 && j < 25 && i == 6) bound[i][j] = '#';
                else if (j > 14 && j < 19 && i == 5) bound[i][j] = '#';
            }
        }

        // Place food (.) on all remaining non-wall cells
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (bound[i][j] == ' ') {
                    bound[i][j] = '.';
                    foodCount++;
                }
            }
        }

        // Place special food (*) and mark in specialFood array
        int[][] specialPositions = {
                {5,10}, {5,20}, {10,15}, {15,8},
                {15,22}, {12,12}, {8,20}, {18,15}
        };
        for (int[] pos : specialPositions) {
            int x = pos[0], y = pos[1];
            if (x >= 0 && x < m && y >= 0 && y < n && bound[x][y] != '#') {
                specialFood[x][y] = 5;
                bound[x][y] = '*';
                specialFoodCount++;
            }
        }

        // Save initial counts
        initialFoodCount = foodCount;
        initialSpecialFoodCount = specialFoodCount;
    }

    public void draw(Pacman pacman, Ghost[] ghosts) {
        char[][] display = new char[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                display[i][j] = bound[i][j];
            }
        }

        for (Ghost ghost : ghosts) {
            Position pos = ghost.getPosition();
            if (pos.getX() >= 0 && pos.getX() < m &&
                    pos.getY() >= 0 && pos.getY() < n &&
                    bound[pos.getX()][pos.getY()] != '#') {
                display[pos.getX()][pos.getY()] = ghost.getSymbol();
            }
        }

        Position pacPos = pacman.getPosition();
        if (pacPos.getX() >= 0 && pacPos.getX() < m &&
                pacPos.getY() >= 0 && pacPos.getY() < n &&
                bound[pacPos.getX()][pacPos.getY()] != '#') {
            display[pacPos.getX()][pacPos.getY()] = pacman.getSymbol();
        }

        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.printf("║  SCORE: %-6d  |  LIVES: %-6d  |  FOOD LEFT: %-6d  ║%n",
                pacman.getScore(), pacman.getLives(), foodCount + specialFoodCount);
        System.out.println("╠════════════════════════════════════════════════════════════════════╣");

        for (int i = 0; i < m; i++) {
            System.out.print("║ ");
            for (int j = 0; j < n; j++) {
                System.out.print(display[i][j] + " ");
            }
            System.out.println("║");
        }
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");
        System.out.println("Legend: P=Pacman | G,H,I,T=Ghosts | .=Food(1pt) | *=Special Food(5pt) | #=Wall\n");
    }

    public int eatFood(int x, int y) {
        if (x < 0 || x >= m || y < 0 || y >= n) return 0;

        int points = 0;
        if (specialFood[x][y] == 5) {
            points = 5;
            specialFood[x][y] = 0;
            specialFoodCount--;
            bound[x][y] = ' ';    // FIX: remove from display
            System.out.print("⭐ SPECIAL FOOD! ");
        } else if (bound[x][y] == '.') {
            points = 1;
            bound[x][y] = ' ';
            foodCount--;
        }
        return points;
    }

    public boolean isWall(int x, int y) {
        if (x < 0 || x >= m || y < 0 || y >= n) return true;
        return bound[x][y] == '#';
    }

    public int getTotalFoodCount() {
        return foodCount + specialFoodCount;
    }

    public int getFoodCount() { return foodCount; }
    public int getSpecialFoodCount() { return specialFoodCount; }
    public int getInitialFoodCount() { return initialFoodCount; }
    public int getInitialSpecialFoodCount() { return initialSpecialFoodCount; }

    public char[][] getMaze() { return bound; }
    public int getRows() { return m; }
    public int getCols() { return n; }
}

class GameStats {
    private static final String FILE_NAME = "pacman_scores.txt";

    public static void saveGameResult(String playerName, int score, boolean won, int steps) {
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String result = won ? "WIN" : "LOSS";

            out.printf("%s | %s | Score: %d | Steps: %d | %s%n",
                    dtf.format(now), playerName, score, steps, result);

            System.out.println("\n Game result saved to " + FILE_NAME);

        } catch (IOException e) {
            System.out.println("Error saving game result: " + e.getMessage());
        }
    }

    public static void displayAllScores() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    GAME HISTORY                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            boolean hasEntries = false;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                hasEntries = true;
            }
            if (!hasEntries) {
                System.out.println("No game history found.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("No game history found. Play a game first!");
        } catch (IOException e) {
            System.out.println("Error reading game history: " + e.getMessage());
        }
        System.out.println();
    }
}

public class Game {
    private World world;
    private Pacman pacman;
    private Ghost[] ghosts;
    private int steps;
    private Scanner inputScanner;

    public Game() {
        this.world = new World();                 // fixed size 20×30
        this.pacman = new Pacman(12, 20);
        this.ghosts = new Ghost[] {
                new Ghost("Blinky", 'G', 5, 5),
                new Ghost("Pinky", 'H', 5, 25),   // adjusted to fit 30 columns
                new Ghost("Inky", 'I', 15, 15),
                new Ghost("Tim", 'T', 10, 20)     // adjusted
        };
        this.steps = 0;
        this.inputScanner = new Scanner(System.in);
    }

    public void playGame() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                       PACMAN GAME                              ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.println("║ Controls: W=Up | S=Down | A=Left | D=Right                  ║");
        System.out.println("║                                                              ║");
        System.out.println("║ Game Elements:                                               ║");
        System.out.println("║   • Regular Food (.) = 1 point                              ║");
        System.out.println("║   • Special Food (*) = 5 points                             ║");
        System.out.println("║   • Ghosts (G, H, I, T) = Avoid them!                       ║");
        System.out.println("║   • Lives = 3 (you lose a life when caught)                 ║");
        System.out.println("║                                                              ║");
        System.out.println("║ Goal: Eat ALL food (regular + special) to win!              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        System.out.print("\nEnter your name: ");
        String playerName = inputScanner.nextLine();

        System.out.println("\nPress Enter to start the game...");
        try { System.in.read(); } catch(Exception e) {}

        while (pacman.isAlive() && world.getTotalFoodCount() > 0) {
            world.draw(pacman, ghosts);

            System.out.printf(" Regular Food Left: %d | Special Food Left: %d%n",
                    world.getFoodCount(), world.getSpecialFoodCount());

            pacman.move(world.getMaze());

            Position pacPos = pacman.getPosition();
            int points = world.eatFood(pacPos.getX(), pacPos.getY());
            if (points > 0) {
                pacman.addScore(points);
            }

            for (Ghost ghost : ghosts) {
                ghost.move(world.getMaze());
            }

            for (Ghost ghost : ghosts) {
                if (pacman.getPosition().equals(ghost.getPosition())) {
                    System.out.println("\n OH NO! " + ghost.getName() + " caught Pacman! ");
                    pacman.loseLife();

                    if (pacman.isAlive()) {
                        System.out.println("❤️ Lives remaining: " + pacman.getLives());
                        System.out.println("Resetting positions...");
                        pacman.setPosition(12, 20);
                        ghosts[0].setPosition(5, 5);
                        ghosts[1].setPosition(5, 25);
                        ghosts[2].setPosition(15, 15);
                        ghosts[3].setPosition(10, 20);
                        System.out.println("Press Enter to continue...");
                        try { System.in.read(); } catch(Exception e) {}
                    }
                    break;
                }
            }

            steps++;
        }

        boolean won = world.getTotalFoodCount() == 0;

        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                        GAME OVER!                             ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");

        if (won) {
            System.out.println("║                    🎉 YOU WIN! 🎉                           ║");
            System.out.printf("║              Final Score: %-6d                              ║%n", pacman.getScore());
            System.out.println("║              Congratulations! You ate everything!          ║");
        } else if (!pacman.isAlive()) {
            System.out.println("║                   💀 GAME OVER! 💀                           ║");
            System.out.printf("║              Final Score: %-6d                              ║%n", pacman.getScore());
            System.out.println("║              Better luck next time!                        ║");
        }

        System.out.printf("║              Total Steps Taken: %-6d                          ║%n", steps);

        // Use initial counts to compute eaten amounts
        int regularEaten = world.getInitialFoodCount() - world.getFoodCount();
        int specialEaten = world.getInitialSpecialFoodCount() - world.getSpecialFoodCount();
        System.out.printf("║              Regular Food Eaten: %-6d                         ║%n", regularEaten);
        System.out.printf("║              Special Food Eaten: %-6d/%-6d                    ║%n",
                specialEaten, world.getInitialSpecialFoodCount());

        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        GameStats.saveGameResult(playerName, pacman.getScore(), won, steps);

        System.out.print("\nDo you want to see all game history? (Y/N): ");
        String response = inputScanner.nextLine().toUpperCase();
        if (response.equals("Y")) {
            GameStats.displayAllScores();
        }
    }

    public static void main(String[] args) {
        Scanner mainScanner = new Scanner(System.in);

        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    WELCOME TO PACMAN GAME!                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        boolean keepPlaying = true;
        while (keepPlaying) {
            Game game = new Game();
            game.playGame();

            System.out.print("\nDo you want to play again? (Y/N): ");
            String playAgain = mainScanner.nextLine().toUpperCase();
            if (!playAgain.equals("Y")) {
                keepPlaying = false;
                System.out.println("\nThanks for playing! Goodbye! 👋");
            }
        }
        mainScanner.close();
    }
}