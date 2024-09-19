package minigames.client.bomberman;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements the union find union join algorithm for finding if two points are joined together
 *
 *  Path compression to make this close to O(1) for findRoot, and union. O(n) for setting up graph
 *
 *  Contributors: Lixang Li lli32@myune.edu.au
 *
 *  Example : UnionFind2D uf = new UnionFind2D();
 *
 * Point p1 = new Point(0, 0);
 * Point p2 = new Point(1, 0);
 * Point p3 = new Point(1, 1);
 *
 * uf.add(p1);
 * uf.add(p2);
 * uf.add(p3);
 *
 * uf.union(p1, p2); // Union (0,0) and (1,0)
 *
 * System.out.println(uf.connected(p1, p2)); true
 * System.out.println(uf.connected(p1, p3));  false
 *
 * uf.union(p2, p3); // Union (1,0) and (1,1)
 *
 * System.out.println(uf.connected(p1, p3));  now be true
 */
public class UnionFind {
    private final Map<Point, Point> parent = new HashMap<>();
    private final Map<Point,Integer> rank = new HashMap<>();

    /**
     * Add a new point to the graph
     */
    public  void add(Point point) {
        if (!parent.containsKey(point)) {
            parent.put(point,point);
            rank.put(point,0);
        }

    }

    /**
     * Find the root of the point
     * Compress the branches so it becomes a fat tree graph
     * @param point
     * @return
     */
    public Point findRoot(Point point) {
        if (!parent.containsKey(point)) {
            return null;
        }
        if (!point.equals(parent.get(point))) {
            parent.put(point, findRoot(parent.get(point))); //Compression point to the root called recursively
        }
        return parent.get(point);
    }

    /**
     *
     * @param point1 Point awt
     * @param point2 Point awt
     */
    public void union(Point point1, Point point2) {
        Point root1 = findRoot(point1);
        Point root2 = findRoot(point2);

        if (root2 == null || root1 == null || root1.equals(root2) ) {
            return; //early return
        }
        if (rank.get(root1) > rank.get(root2)) {
            parent.put(root2,root1);
        }
        else if (rank.get(root1) < rank.get(root2)) {
            parent.put(root1,root2);
        }
        else {
                parent.put(root2,root1);
                rank.put(root1,rank.get(root1) + 1);
        }
    }

    /**
     * Checks if they are connected (transitive)
     * @param point1
     * @param point2
     * @return true or false
     */
    public boolean isConnected(Point point1, Point point2) {
        return findRoot(point1) != null && findRoot(point1).equals(findRoot(point2));
    }
}
