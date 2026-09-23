import java.util.List;
import java.util.Map;


/**
 * Class: CSC-370
 *
 * @author: Maria Fajardo & Jack Bray
 * 
 * Purpose: Run Experiment to replicate 
 *
 */

public class Experiment {

    public static void main(String[] args) {

        Map<Integer, List<int[]>> simulationProblems = PuzzleBoard.generateAllProblems();

       
        for (int d :simulationProblems.keySet()) {
            List<int[]> boards = simulationProblems.get(d);

            double h1TotalNodes = 0;
            double h2TotalNodes = 0;
            double h3TotalNodes = 0;
            double bFactorh1 = 0;
            double bFactorh2 = 0;
            double bFactorh3 = 0;   
            int n = boards.size();

            for (int[] board : boards) {

                Search.SearchResult result1 = Search.astar(board, Search.MISPLACED_TILES);
                Search.SearchResult result2 = Search.astar(board, Search.MANHATTAN);
                Search.SearchResult result3 = Search.astar(board, Search.LINEAR_CONFLICT);

                h1TotalNodes += result1.nodesGenerated;
                h2TotalNodes += result2.nodesGenerated;
                h3TotalNodes += result3.nodesGenerated;

                bFactorh1 += PuzzleBoard.bFactor(result1.nodesGenerated, result1.depth);
                bFactorh2 += PuzzleBoard.bFactor(result2.nodesGenerated, result2.depth);
                bFactorh3 += PuzzleBoard.bFactor(result3.nodesGenerated, result3.depth);

            }

            System.out.println("d = " + d);
            System.out.println("h1 nodes = " + (h1TotalNodes / n));
            System.out.println("h2 nodes = " + (h2TotalNodes / n));
            System.out.println("h3 nodes = " + (h3TotalNodes / n));
            System.out.println("h1 b* = " + (bFactorh1 / n));
            System.out.println("h2 b* = " + (bFactorh2 / n));
            System.out.println("h3 b* = " + (bFactorh3 / n));
        }
    }
}
