# 8-Puzzle A* Search

CSC 370 project: implementing A* on the 8-puzzle and reproducing Russell & Norvig's heuristic comparison. Plus a third heuristic of our own.

**Authors:** Jack Bray & Maria Fajardo

## What's in here

- `PuzzleBoard.java` - board representation and mechanics (goal state, legal moves, swapping tiles), the three heuristics (h1, h2, h3), random puzzle generation, and the effective branching factor calculation
- `Search.java` — BFS (used to verify puzzle depth) and A* search
- `Experiment.java` — runs the full experiment: generates 1200 puzzles (100 per even depth, 2–24), solves each with all three heuristics, and prints average nodes generated and effective branching factor per depth

## How it compiles and runs

```
javac PuzzleBoard.java Search.java Experiment.java
java Experiment
```

This prints one summary per depth: average nodes generated and average effective branching factor (b*) for h1, h2, and h3.

## Heuristics

- **h1** — misplaced tiles
- **h2** — Manhattan distance
- **h3** — Manhattan distance + Linear Conflict (Hansson, Mayer & Yung, 1985)
