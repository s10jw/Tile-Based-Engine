package byow.Core;

import byow.TileEngine.Tileset;

import java.util.*;
public class PathSearch {
    private TileMap worldData;
    private final int height;
    private final int width;


    public PathSearch(TileMap tileMap) {
        this.worldData = tileMap;
        this.height = worldData.height;
        this.width = worldData.width;
    }

    public List<Integer> findPath(int start, int target) {
        if (start == target) {
            throw new IllegalArgumentException("Start cannot equal target.");
        }
        PriorityQueue<Node> openSites = new PriorityQueue<>();
        Map<Node, Integer> gScores = new HashMap<>();
        List<Integer> path = new ArrayList<>();
        boolean[][] visited = new boolean[width][height];
//        System.out.println("Start:" + start);
//        System.out.println("Target:" + target);
        path.add(start);
        worldData.put(start, Tileset.LOCKED_DOOR);
        worldData.put(target, Tileset.LOCKED_DOOR);
        int i = start / width;
        int j = start % width;
        int n = target / width;
        int m = target % width;

        Node startNode = new Node(i, j, 0, eucDistance(i, j, n, m), null);

        openSites.add(startNode);
        gScores.put(startNode, 0);

        while (!openSites.isEmpty()) {
            Node current = openSites.poll();
            if (isTarget(current, target)) {
                path.addAll(drawPath(current));
                path.add(target);
                return path;
            }
            visited[current.i][current.j] = true;
            for (int di = -1; di < 2; di++) {
                for (int dj = -1; dj < 2; dj++) {
                    if (di * di == dj * dj) {
                        continue;
                    }
                    int nextI = current.i + di;
                    int nextJ = current.j + dj;
                    if (validate(nextI, nextJ) && !visited[nextI][nextJ]) {
                        int nextGScore = gScores.get(current) + 1;
                        Node nextNode = new Node(nextI, nextJ, nextGScore, eucDistance(nextI, nextJ, n, m),
                                current);
                        if (!openSites.contains(nextNode)) {
                            openSites.add(nextNode);
                        } else if (nextGScore >= gScores.get(nextNode)) {
                            continue;
                        }
                        gScores.put(nextNode, nextGScore);
                    }
                }
            }
        }
        return null;
    }

    private int eucDistance(int i, int j, int n, int m) {
        int x = i - n;
        int y = j - m;
        return (int) Math.sqrt((x * x) + (y * y));
    }

    private boolean isTarget(Node node, int end) {
        return eucDistance(node.i, node.j, end / width, end % width) == 0;
    }

    private List<Integer> drawPath(Node node) {
        Node tempNode = node;
        List<Integer> path = new ArrayList<>();
        while (tempNode != null) {
            path.add(tempNode.pos);
            tempNode = tempNode.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private boolean validate(int x, int y) {
        int pos = y + (x * width);
        // Check if in bounds of world.
        if (x < 2 || x > width - 2 || y < 2 || y > height - 2) {
            return false;
        }
        // Check to make sure there's enough room to add walls around the hallways.
        return worldData.get(pos) != Tileset.WALL && worldData.get(pos) != Tileset.FLOOR;
    }

    private class Node implements Comparable<Node> {
        int i;
        int j;
        int pos;
        int gScore;
        int hScore;
        Node parent;

        public Node(int x, int y, int gScore, int hScore, Node parent) {
            this.i = x;
            this.j = y;
            this.pos = y + (x * width);
            this.gScore = gScore;
            this.hScore = hScore;
            this.parent = parent;
        }

        public int fScore() {
            return gScore + hScore;
        }

        public int compareTo(Node target) {
            return Integer.compare(fScore(), target.fScore());
        }
    }
}
