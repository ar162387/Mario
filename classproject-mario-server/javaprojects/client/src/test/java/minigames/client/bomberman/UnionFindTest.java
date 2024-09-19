package minigames.client.bomberman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;

public class UnionFindTest {
    private UnionFind unionFind;

    @BeforeEach
    public void setUp() {
        unionFind = new UnionFind();
    }

    @Test
    @DisplayName("Union and find operations work correctly")
    public void testUnionFindOperations() {
        Point p1 = new Point(1, 1);
        Point p2 = new Point(2, 2);
        Point p3 = new Point(3, 3);
        Point p4 = new Point(4, 4);

        unionFind.add(p1);
        unionFind.add(p2);
        unionFind.add(p3);
        unionFind.add(p4);

        assertFalse(unionFind.isConnected(p1, p2));
        assertFalse(unionFind.isConnected(p1, p3));
        assertFalse(unionFind.isConnected(p2, p3));


        unionFind.union(p1, p2);
        unionFind.union(p2, p3);

        assertTrue(unionFind.isConnected(p1, p2));
        assertTrue(unionFind.isConnected(p1, p3));
        assertTrue(unionFind.isConnected(p2, p3));
        assertFalse(unionFind.isConnected(p1, p4));

        unionFind.union(p3, p4);
        assertTrue(unionFind.isConnected(p1, p4));
        assertTrue(unionFind.isConnected(p2, p4));
    }

    @Test
    @DisplayName("Path compression optimises find operation")
    public void testFindPathCompression() {
        Point p1 = new Point(1, 1);
        Point p2 = new Point(2, 2);
        Point p3 = new Point(3, 3);

        unionFind.add(p1);
        unionFind.add(p2);
        unionFind.add(p3);

        unionFind.union(p1, p2);
        unionFind.union(p2, p3);

        assertEquals(p1, unionFind.findRoot(p3));
        assertEquals(p1, unionFind.findRoot(p2));
    }
}
