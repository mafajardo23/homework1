import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;


/**
 * Class: CSC-370
 *
 * @author: Maria Fajardo & Jack Bray
 * 
 * Purpose: Implement A* algorithm + Reproduce the
 * experiment of Russell and Norvig in the 8-puzzle domain.
 *
 */

import java.util.Random;
import java.util.Set;

/**
 * PuzzleBoard Generation
 * 
 * Start w/ Goal State
 * ______________ (Loop of depth)
 * Put Current Board into Set of Previous Boards
 * Find the Empty Cell
 * Find the Adjacent Cells (that could move into Empty Cell)
 * Drop any whose move would land on a Board in Previous Boards
 * Randomly Select one of what's left and Move it to Empty Cell
 * If No Valid Boards available, Restart loop
 * Repeat Loop
 * ______________
 * For every tile, count how far it is from home (columns apart plus rows apart)
 * Add all of those distances up 
 * If the total equals d, the puzzle is confirmed to be exactly d moves from solved, so keep it 
 * If not, throw it out and run the whole walk again
 */

public class PuzzleBoard {

    // the solved board; 0 is the empty space, then tiles 1 through 8 in order
    public static int[] goalState() {
        int[] board = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
        return board;
    }

    // where is the empty space? returns its spot in the array (0 through 8)
    public static int findEmptyCell(int[] board) {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) {
                return i;
            }
        }
        return -1;   // error handling
    }

    // swap Adjacent Cell and Empty Cell
    public static int[]swap(int[] board, int emptyCell, int adjacentCell) {
        int[] newBoard = board.clone();

        int temp = newBoard[emptyCell];
        newBoard[emptyCell] = newBoard[adjacentCell];
        newBoard[adjacentCell] = temp;

        return newBoard;
    }

    // find the Adjacent Cells (returns the spots that could slide into the hole)
    public static int[] findAdjacent(int[] board, int emptyTile) {
        List<Integer> possible = new ArrayList<>();

        int row = emptyTile / 3;
        int col = emptyTile % 3;

        if (row > 0) {
            possible.add(emptyTile - 3);
        }
        if (row < 2) {
            possible.add(emptyTile + 3);
        }
        if (col > 0) {
            possible.add(emptyTile - 1);
        }
        if (col < 2) {
            possible.add(emptyTile + 1);
        }

        int[] result = new int[possible.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = possible.get(i);
        }
        return result;
    }

    // randomly select one of the Adjacent Cells
    
    // one Random for the whole program, reused by every call.
    private static Random random = new Random();

    // randomly select from availableIndices passed from findAdjacent
    public static int selectRandomIndex(List<Integer> availableIndices) {
        int pick = random.nextInt(availableIndices.size());
        return availableIndices.get(pick);
    }

    // turn a board into a String so a HashSet can compare it by contents
    public static String key(int[] board) {
        return Arrays.toString(board);
    }

    // h1: how many tiles are sitting in the wrong square (blank doesn't count)
    public static int h1(int[] board) {
        int count = 0;
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) {
                continue;
            }
            if (board[i] != i) {
                count++;
            }
        }
        return count;
    }

    // h2: total Manhattan distance, every tile to its home square (blank doesn't count)
    public static int h2(int[] board) {
        int total = 0;
        for (int i = 0; i < board.length; i++) {
            int tile = board[i];
            if (tile == 0) {
                continue;
            }
            total += Math.abs(i / 3 - tile / 3) + Math.abs(i % 3 - tile % 3);
        }
        return total;
    }

    // h3: constraint relaxation approach. Manhattan distance plus 2 for every pair of tiles that are in the same row or column and are reversed from their goal order.
    public static int h3(int[] board) {
        int total = h2(board);

        for (int index = 0; index < board.length; index++) {
            int tile = board[index];

            if (tile == 0) {
                continue;
            }

            int row = index / 3;
            int col = index % 3;
            int goalRow = tile / 3;
            int goalCol = tile % 3;

            for (int otherIndex = index + 1; otherIndex < board.length; otherIndex++) {
                int otherTile = board[otherIndex];
                if (otherTile == 0) {
                    continue;
                }

                int otherRow = otherIndex / 3;
                int otherCol = otherIndex % 3;
                int otherGoalRow = otherTile / 3;
                int otherGoalCol = otherTile % 3;

                // row conflict
                if (row == otherRow && goalRow == row && otherGoalRow == row) {
                    if (col > otherCol && goalCol < otherGoalCol) {
                        total += 2;
                    }
                }

                // column conflict
                if (col == otherCol && goalCol == col && otherGoalCol == col) {
                    if (row > otherRow && goalRow < otherGoalRow) {
                        total += 2;
                    }
                }
            }
        }

        return total;           
    }

    public static double bFactor(int nBoards, int depth){
        double min = 1.0;
        double expNodes = nBoards + 1.0;
         double max = expNodes;

        for (int itCounter = 0; itCounter < 100; itCounter++) {
            double sum = 0.0;
            double average = (min + max) /  2.0;

                // geosum
            for (int powCounter = 0; powCounter <= depth; powCounter++) {
                sum += Math.pow(average, powCounter);
            }

            if (sum < expNodes) {
                min = average;
            } else {
                max = average;
            }
        }

        return  (min + max) / 2.0;
    }

    // filtering options to prevent loops
    public static List<Integer> filterVisited(int[] board, int emptyIndex, int[] options, Set<String> visited) {
        List<Integer> legal = new ArrayList<>();
        for (int spot : options) {
            int[] result = swap(board, emptyIndex, spot);
            if (!visited.contains(key(result))) {
                legal.add(spot);
            }
        }
        return legal;
    }


    // MAIN RANDOM WALK METHOD
    // scramble the goal board with d random moves, never revisiting a board
    // returns null if the walk dead ends, meaning the caller should try again
    public static int[] randomWalk(int d) {
        int[] board = goalState();

        Set<String> visited = new HashSet<>();
        visited.add(key(board));

        for (int step = 0; step < d; step++) {
            int blank = findEmptyCell(board);
            int[] options = findAdjacent(board, blank);
            List<Integer> legal = filterVisited(board, blank, options, visited);


            if (legal.isEmpty()) {
                return null;              // stuck, caller restarts
            }

            int chosen = selectRandomIndex(legal);
            board = swap(board, blank, chosen);
            visited.add(key(board));
        }

        return board;
    }

    // run it w/ check
    // keep walking until we get a board confirmed to be exactly d moves out
    public static int[] generate(int d) {
        while (true) {
            int[] candidate = randomWalk(d);
            if (candidate != null && Search.bfs(candidate) == d) {
                return candidate;
            }
        }
    }

    public static Map<Integer, List<int[]>> generateAllProblems() {
        Map<Integer, List<int[]>> simulationProblems = new HashMap<>();

        for (int d = 2; d <= 8; d += 2) {
            List<int[]> depthBoards = new ArrayList<>();

            for (int i = 0; i < 100; i++) {
                int[] puzzle = generate(d);
                depthBoards.add(puzzle);
            }

            simulationProblems.put(d, depthBoards);
        }

        return simulationProblems;
    }

    // RUN THE EXPIREMENT
    public static void main(String[] args) {
        Map<Integer, List<int[]>> simulationProblems = generateAllProblems();
    }
}

 

