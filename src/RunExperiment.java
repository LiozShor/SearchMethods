import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class RunExperiment {

    // ---------- CONFIG ----------
    private static final int RUNS = 10;                   // runs per size (accepted runs)
    private static final int[] SIZES = {10, 20, 50, 100}; // maze sizes
    private static final int WARMUP = 2;                  // per-size warmup runs to ignore in aggregates
    private static final String CSV_FILE = "bfs_vs_iddfs_results.csv";

    // Optional: focus on depth regime
    private enum DepthMode { ANY, SHALLOW, DEEP }
    private static final DepthMode DEPTH_MODE = DepthMode.ANY;
    private static final double DEEP_MIN_RATIO = 0.75;    // for DEEP mode: Manhattan / maxManhattan >= this
    private static final double SHALLOW_MAX_RATIO = 0.30; // for SHALLOW mode: Manhattan / maxManhattan <= this

    // ---------- DATA MODELS ----------
    private static class RunRow {
        int size;
        int runIndex;            // accepted run index (excludes warmups)
        String algorithm;        // "BFS" / "IDDFS"
        long timeNs;
        int nodes;
        long memBytesDelta;
        int rows, cols;
        int startR, startC, goalR, goalC;
        int manhattan;
        int maxManhattan;
        double depthRatio;       // manhattan / maxManhattan
        double avgDegree;        // avg free-cell degree (0..4)
        double deadEndRatio;     // fraction of free cells with degree=1

        RunRow cloneWithAlgo(String algo, long tNs, int nodesCount, long memDelta) {
            RunRow rr = new RunRow();
            rr.size = this.size;
            rr.runIndex = this.runIndex;
            rr.algorithm = algo;
            rr.timeNs = tNs;
            rr.nodes = nodesCount;
            rr.memBytesDelta = memDelta;
            rr.rows = this.rows;
            rr.cols = this.cols;
            rr.startR = this.startR;
            rr.startC = this.startC;
            rr.goalR = this.goalR;
            rr.goalC = this.goalC;
            rr.manhattan = this.manhattan;
            rr.maxManhattan = this.maxManhattan;
            rr.depthRatio = this.depthRatio;
            rr.avgDegree = this.avgDegree;
            rr.deadEndRatio = this.deadEndRatio;
            return rr;
        }
    }

    private static class Stats {
        double mean, std, median, p90, min, max;
    }

    // ---------- MAIN ----------
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        System.out.println("Starting experiment: BFS vs IDDFS");
        List<RunRow> rows = new ArrayList<>();
        MyMazeGenerator gen = new MyMazeGenerator();

        for (int size : SIZES) {
            System.out.printf("%n=== Size %dx%d ===%n", size, size);

            int accepted = 0;
            int attempts = 0;
            int warmupsLeft = WARMUP;

            while (accepted < RUNS) {
                attempts++;
                Maze maze = gen.generate(size, size); // same maze for both algorithms

                // Compute maze features (no solver changes)
                RunRow features = extractFeatures(maze, size);

                if (!acceptByDepthMode(features.depthRatio)) {
                    // Not in the requested depth bucket (if any) -> regenerate
                    if (attempts > RUNS * 50) {
                        // Safety to avoid infinite loops on unlucky generators
                        System.out.println("Too many attempts to satisfy depth mode; proceeding anyway.");
                        break;
                    }
                    continue;
                }

                // Optionally treat first runs as warmups (for JIT/GC stabilization)
                boolean isWarmup = warmupsLeft > 0;
                if (isWarmup) warmupsLeft--;

                // --- BFS ---
                gcHint();
                long memBeforeB = usedMemory();
                long t0 = System.nanoTime();
                BFS_maze_slover.solveMazeBFS(maze);
                long t1 = System.nanoTime();
                long memAfterB = usedMemory();
                long bfsTime = t1 - t0;
                int bfsNodes = BFS_maze_slover.getNodesOpened();
                long bfsMemDelta = Math.max(0L, memAfterB - memBeforeB);

                // --- IDDFS ---
                gcHint();
                long memBeforeI = usedMemory();
                long t2 = System.nanoTime();
                IDDFS_maze_solver.solveMazeIDDFS(maze);
                long t3 = System.nanoTime();
                long memAfterI = usedMemory();
                long iddfsTime = t3 - t2;
                int iddfsNodes = IDDFS_maze_solver.getNodesDeveloped();
                long iddfsMemDelta = Math.max(0L, memAfterI - memBeforeI);

                // Record
                if (!isWarmup) {
                    int runIndex = accepted + 1;
                    RunRow base = new RunRow();
                    base.size = size;
                    base.runIndex = runIndex;
                    base.rows = features.rows;
                    base.cols = features.cols;
                    base.startR = features.startR;
                    base.startC = features.startC;
                    base.goalR = features.goalR;
                    base.goalC = features.goalC;
                    base.manhattan = features.manhattan;
                    base.maxManhattan = features.maxManhattan;
                    base.depthRatio = features.depthRatio;
                    base.avgDegree = features.avgDegree;
                    base.deadEndRatio = features.deadEndRatio;

                    rows.add(base.cloneWithAlgo("BFS", bfsTime, bfsNodes, bfsMemDelta));
                    rows.add(base.cloneWithAlgo("IDDFS", iddfsTime, iddfsNodes, iddfsMemDelta));

                    // Console quick compare
                    double timeMsB = bfsTime / 1_000_000.0;
                    double timeMsI = iddfsTime / 1_000_000.0;
                    double ratioTime = timeMsI / Math.max(1e-9, timeMsB);
                    double ratioNodes = (double) iddfsNodes / Math.max(1, bfsNodes);
                    System.out.printf("Run #%d: BFS %.3f ms, %d nodes | IDDFS %.3f ms, %d nodes | ratios (I/B): time=%.2fx, nodes=%.2fx | depthRatio=%.2f%n",
                            runIndex, timeMsB, bfsNodes, timeMsI, iddfsNodes, ratioTime, ratioNodes, features.depthRatio);

                    accepted++;
                } else {
                    System.out.println("Warmup run completed (not recorded).");
                }
            }

            // Aggregate per size
            printAggregates(rows, size, "BFS");
            printAggregates(rows, size, "IDDFS");
            printWinner(rows, size);
        }

        // Write CSV
        writeCsv(rows, CSV_FILE);
        System.out.println("\nExperiment completed. Results written to: " + CSV_FILE);
    }

    // ---------- FEATURE EXTRACTION ----------
    private static RunRow extractFeatures(Maze maze, int size) {
        RunRow f = new RunRow();
        int[][] grid = maze.getMaze();
        Position s = maze.getStartPosition();
        Position g = maze.getGoalPosition();
        int rows = grid.length, cols = grid[0].length;

        f.rows = rows;
        f.cols = cols;
        f.size = rows; // assuming square sizes from config
        f.startR = s.getRow();
        f.startC = s.getColumn();
        f.goalR = g.getRow();
        f.goalC = g.getColumn();

        int md = Math.abs(s.getRow() - g.getRow()) + Math.abs(s.getColumn() - g.getColumn());
        int maxMd = (rows - 1) + (cols - 1);
        f.manhattan = md;
        f.maxManhattan = maxMd;
        f.depthRatio = maxMd > 0 ? (double) md / (double) maxMd : 0.0;

        // Degree stats over free cells (0=free, 1=wall)
        int free = 0, degSum = 0, deadEnds = 0;
        final int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 0) {
                    free++;
                    int d = 0;
                    for (int[] dxy : DIRS) {
                        int nr = r + dxy[0], nc = c + dxy[1];
                        if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 0) d++;
                    }
                    degSum += d;
                    if (d == 1) deadEnds++;
                }
            }
        }
        f.avgDegree = free > 0 ? (double) degSum / free : 0.0;
        f.deadEndRatio = free > 0 ? (double) deadEnds / free : 0.0;
        return f;
    }

    private static boolean acceptByDepthMode(double depthRatio) {
        switch (DEPTH_MODE) {
            case DEEP: return depthRatio >= DEEP_MIN_RATIO;
            case SHALLOW: return depthRatio <= SHALLOW_MAX_RATIO;
            default: return true;
        }
    }

    // ---------- AGGREGATION & REPORT ----------
    private static void printAggregates(List<RunRow> all, int size, String algo) {
        List<Double> times = new ArrayList<>();
        List<Double> nodes = new ArrayList<>();
        List<Double> memKb = new ArrayList<>();

        for (RunRow r : all) {
            if (r.size == size && algo.equals(r.algorithm)) {
                times.add(r.timeNs / 1_000_000.0);
                nodes.add((double) r.nodes);
                memKb.add(r.memBytesDelta / 1024.0);
            }
        }
        if (times.isEmpty()) return;

        Stats stTime = stats(times);
        Stats stNodes = stats(nodes);
        Stats stMem = stats(memKb);

        System.out.printf("Aggregate [%s] size %d: time(ms) mean=%.3f p50=%.3f p90=%.3f std=%.3f | nodes mean=%.1f p50=%.0f p90=%.0f | mem(KB) mean=%.1f%n",
                algo, size, stTime.mean, stTime.median, stTime.p90, stTime.std, stNodes.mean, stNodes.median, stNodes.p90, stMem.mean);
    }

    private static void printWinner(List<RunRow> all, int size) {
        double bfsMean = meanFor(all, size, "BFS", true);
        double iddMean = meanFor(all, size, "IDDFS", true);
        String winnerTime = bfsMean < iddMean ? "BFS" : "IDDFS";

        double bfsNodes = meanFor(all, size, "BFS", false);
        double iddNodes = meanFor(all, size, "IDDFS", false);
        String winnerNodes = bfsNodes < iddNodes ? "BFS" : "IDDFS";

        System.out.printf("Winners size %d -> time: %s | nodes: %s%n", size, winnerTime, winnerNodes);
    }

    private static double meanFor(List<RunRow> all, int size, String algo, boolean time) {
        double sum = 0; int cnt = 0;
        for (RunRow r : all) {
            if (r.size == size && algo.equals(r.algorithm)) {
                sum += time ? (r.timeNs / 1_000_000.0) : r.nodes;
                cnt++;
            }
        }
        return cnt == 0 ? Double.NaN : sum / cnt;
    }

    private static Stats stats(List<Double> v) {
        Stats s = new Stats();
        int n = v.size();
        if (n == 0) return s;
        double sum = 0, sum2 = 0;
        for (double x : v) { sum += x; sum2 += x * x; }
        s.mean = sum / n;
        s.std = Math.sqrt(Math.max(0.0, sum2 / n - s.mean * s.mean));

        List<Double> sorted = new ArrayList<>(v);
        Collections.sort(sorted);
        s.min = sorted.get(0);
        s.max = sorted.get(sorted.size() - 1);
        s.median = percentile(sorted, 50);
        s.p90 = percentile(sorted, 90);
        return s;
    }

    private static double percentile(List<Double> sortedAsc, int p) {
        if (sortedAsc.isEmpty()) return Double.NaN;
        double pos = (p / 100.0) * (sortedAsc.size() - 1);
        int i = (int) Math.floor(pos);
        int j = Math.min(sortedAsc.size() - 1, i + 1);
        double frac = pos - i;
        return sortedAsc.get(i) * (1 - frac) + sortedAsc.get(j) * frac;
    }

    // ---------- MEMORY / GC ----------
    private static long usedMemory() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    private static void gcHint() {
        // Best-effort GC hint to reduce noise in memory deltas
        System.gc();
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}
    }

    // ---------- CSV ----------
    private static void writeCsv(List<RunRow> rows, String path) {
        try (PrintWriter pw = new PrintWriter(new File(path))) {
            pw.println("size,run,algorithm,time_ms,nodes,mem_kb,rows,cols,start_r,start_c,goal_r,goal_c,manhattan,max_manhattan,depth_ratio,avg_degree,dead_end_ratio");
            rows.sort(Comparator
                    .comparingInt((RunRow r) -> r.size)
                    .thenComparingInt(r -> r.runIndex)
                    .thenComparing(r -> r.algorithm));
            for (RunRow r : rows) {
                pw.printf(Locale.US,
                        "%d,%d,%s,%.3f,%d,%.1f,%d,%d,%d,%d,%d,%d,%d,%d,%.4f,%.4f,%.4f%n",
                        r.size, r.runIndex, r.algorithm,
                        r.timeNs / 1_000_000.0,
                        r.nodes,
                        r.memBytesDelta / 1024.0,
                        r.rows, r.cols,
                        r.startR, r.startC, r.goalR, r.goalC,
                        r.manhattan, r.maxManhattan,
                        r.depthRatio, r.avgDegree, r.deadEndRatio
                );
            }
        } catch (Exception e) {
            System.err.println("Failed writing CSV: " + e.getMessage());
        }
    }
}
