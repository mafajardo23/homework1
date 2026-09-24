import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.PriorityQueue;


public class Search {

    // what a search found (how deep the answer was and how long it took to get there)
    public static class SearchResult {
        public int depth;
        public int nodesGenerated;

        public SearchResult(int depth, int nodesGenerated) {
            this.depth = depth;
            this.nodesGenerated = nodesGenerated;
        }
    }

    // a board in the search
    public static class Node implements Comparable<Node> {
        public int[] board;
        public int g;       // moves spent getting here
        public int h;       // estimated moves to go
        public Node parent; // where we came from (parent node)
        
        public Node(int[] board, int g, int h, Node parent) {
            this.board = board;
            this.g = g;
            this.h = h;
            this.parent = parent;
        }

        public int f() {
            return g+h;
        }

    // ordering rule for the frontier: smallest f = g + h comes out first
    // if two nodes tie on f, prefer the one that looks closer to the goal
    public int compareTo(Node other) {
        if (this.f() != other.f()) {
            return this.f() - other.f();
        }
        return this.h - other.h;
    }
    }

    // the two heuristic choices, named so call sites read clearly
    public static final int MISPLACED_TILES = 1;
    public static final int MANHATTAN = 2;
    public static final int LINEAR_CONFLICT = 3;
    public static final int ZERO = 4;

    // hands back the right estimate for this board, based on which one was asked for
    public static int heuristic(int[] board, int which) {
        if (which == MISPLACED_TILES) {
            return PuzzleBoard.h1(board);
        } else if (which == MANHATTAN) {
            return PuzzleBoard.h2(board);
        } else if (which == ZERO) {
            return PuzzleBoard.h4(board);
        } else {
            return PuzzleBoard.h3(board);
        }
    }


    public static int bfs(int[] startingBoard) {
        Queue<int[]> frontier = new LinkedList<>();
        Queue<Integer> depthCheck = new LinkedList<>();
        Set<String> exploredSet = new HashSet<>();
        int depth = 0;

        frontier.add(startingBoard);
        depthCheck.add(depth);
        exploredSet.add(PuzzleBoard.key(startingBoard));

        int[] goal = PuzzleBoard.goalState();

        while (!frontier.isEmpty()) {
            int[] currentBoard = frontier.poll();
            depth = depthCheck.poll();

            if (Arrays.equals(currentBoard, goal)) {
                return depth;
            }

            int blank = PuzzleBoard.findEmptyCell(currentBoard);
            int [] options = PuzzleBoard.findAdjacent(currentBoard, blank);

            for (int spot : options) {
                int[] succesors = PuzzleBoard.swap(currentBoard, blank, spot);
                if (!exploredSet.contains(PuzzleBoard.key(succesors))) {
                    frontier.add(succesors);
                    depthCheck.add(depth + 1);
                    exploredSet.add(PuzzleBoard.key(succesors));
                }
            }
        }

        return -1; // Error handling
    }

    public static SearchResult astar(int[] startingBoard, int which) {
        // frontier is ordered by Node.compareTo rule: min{f=g+h} first
        PriorityQueue<Node> frontier = new PriorityQueue<>();
        Set<String> exploredSet = new HashSet<>();
        int generated = 1;

        int[] goal = PuzzleBoard.goalState();
        int startEstimate = heuristic(startingBoard, which);          // how far from done?
        Node startNode = new Node(startingBoard, 0, startEstimate, null);
        frontier.add(startNode);

        while (!frontier.isEmpty()) {
            // best option
            Node current = frontier.poll();

            if (exploredSet.contains(PuzzleBoard.key(current.board))) {
                continue;
            }
            exploredSet.add(PuzzleBoard.key(current.board));

            // only test for the goal after it comes off the frontier, never when
            // it is generated, or the path we report might not be the shortest one
            if (Arrays.equals(current.board, goal)) {
                return new SearchResult(current.g, generated);
            }

            int blank = PuzzleBoard.findEmptyCell(current.board);
            int[] options = PuzzleBoard.findAdjacent(current.board, blank);

            for (int spot : options) {
                int[] successor = PuzzleBoard.swap(current.board, blank, spot);
                if (!exploredSet.contains(PuzzleBoard.key(successor))) {
                    int cost = current.g + 1;                          // one more move spent
                    int estimate = heuristic(successor, which);        // moves still to go
                    frontier.add(new Node(successor, cost, estimate, current));
                    generated++;
                }
            }
        }

        return new SearchResult(-1, generated); // only reachable if the board is unsolvable
    }
}


