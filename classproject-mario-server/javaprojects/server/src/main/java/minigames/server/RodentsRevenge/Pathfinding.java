package minigames.server.RodentsRevenge;
import java.util.*;
import java.awt.*;
import minigames.server.RodentsRevenge.RodentPlayer;
import java.util.Collection;

/**
 * A* pathfinding algorithm originally from YouTube Code Monkey
 * https://www.youtube.com/watch?v=alU04hvz6L4&list=WL&index=47&t=11s
 * It finds the shortest path to the target.
 */
public class Pathfinding {

    private static final int MOVE_STRAIGHT_COST = 10;
    private static final int MOVE_DIAGONAL_COST = 14;

    private int[][] board;
    private int width;
    private int height;

    public Pathfinding(int[][] board, int width, int height){
        this.board = board;
        this.width = width;
        this.height = height;
    }

    public static Point getNextMove(int catX, int catY, int[][] board, int width, int height, Collection<RodentPlayer> players){

        Pathfinding pathfinding = new Pathfinding(board, width, height);

        //Arrays.sort(players, (a, b) ->  pathfinding.getPointDistance(catX, catY, a.getX(), a.getY()) - pathfinding.getPointDistance(catX, catY, b.getX(), b.getY()) );

        for(var player : players){

            if(!player.isAlive()) continue;

            var path = pathfinding.findPath(catX, catY, player.getX(), player.getY());

            if(path != null){
                return path.size() > 2 ? new Point(path.get(1).getX(), path.get(1).getY()) : null;
            }

        }

        return null;

    }

    public ArrayList<PathNode> findPath(int sX, int sY, int eX, int eY) {

        Grid grid = new Grid(board, width, height);

        PathNode startNode = grid.get(sY, sX);
        PathNode endNode = grid.get(eY, eX);

        ArrayList<PathNode> openList = new ArrayList<PathNode>();
        HashSet<PathNode> closedSet = new HashSet<PathNode>();

        openList.add(startNode);

        for (var x = 0; x < width; ++x)
        {
            for (var y = 0; y < height; ++y)
            {
                PathNode pathNode = grid.get(y,x);
                pathNode.setGCost(Integer.MAX_VALUE);
                pathNode.calculateFCost();
            }
        }

        startNode.setGCost(0);
        startNode.setHCost(calculateDistanceCost(startNode, endNode));
        startNode.calculateFCost();

        while (openList.size() > 0)
        {
            PathNode currentNode = getLowestFCostNode(openList);
            if (currentNode == endNode) {
                return calculatePath(currentNode);
            }

            openList.remove(currentNode);
            closedSet.add(currentNode);

            ArrayList<PathNode> toAdd = new ArrayList<PathNode>();

            for (PathNode node : getNeighbourList(currentNode, grid))
            {
                if (!node.isWalkable()) { closedSet.add(node); }
                if (closedSet.contains(node)) continue;

                int tentativeGCost = currentNode.getGCost() + calculateDistanceCost(currentNode, node);

                if (tentativeGCost < node.getGCost()) {
                    node.setParent(currentNode);
                    node.setGCost(tentativeGCost);
                    node.setHCost(calculateDistanceCost(node, endNode));
                    node.calculateFCost();
                    toAdd.add(node);

                    if (!openList.contains(node))
                    {
                        openList.add(node);
                    }
                }
            }
        }

        return null;

    }

    private ArrayList<PathNode> getNeighbourList(PathNode currNode, Grid grid) {

        ArrayList<PathNode> nList = new ArrayList<PathNode>();

        int[][] combinations = new int[][] {
                { 1, 0 },
                { -1, 0 },
                { 0, 1 },
                { 0, -1 },
                { 1, 1 },
                { -1, -1 },
                { 1, -1 },
                { -1, 1 }
        };

        for (int index = 0;index < combinations.length; ++index) {

            PathNode candidateNode = grid.get(combinations[index][1] + currNode.y, combinations[index][0] + currNode.x);

            if (candidateNode != null) {
                nList.add(candidateNode);
            }

        }

        return nList;

    }

    private ArrayList<PathNode> calculatePath(PathNode endNode) {

        ArrayList<PathNode> path = new ArrayList<PathNode>();

        path.add(endNode);
        PathNode curr = endNode;

        while (curr.getParent() != null) {
            curr = curr.getParent();
            path.add(curr);
        }

        Collections.reverse(path);

        return path;
    }

    private int calculateDistanceCost(PathNode a, PathNode b) {
        int xDistance = Math.abs(a.x - b.x);
        int yDistance = Math.abs(a.y - b.y);
        int remaining = Math.abs(xDistance - yDistance);
        return (MOVE_DIAGONAL_COST * Math.min(xDistance, yDistance)) + (MOVE_STRAIGHT_COST * remaining);
    }

    private int getPointDistance(int x1, int y1, int x2, int y2) {
        int xDistance = Math.abs(x1 - x2);
        int yDistance = Math.abs(y1 - y2);
        return Math.abs(xDistance - yDistance);
    }

    private PathNode getLowestFCostNode(ArrayList<PathNode> pathNodeList) {

        PathNode lowestCostNode = pathNodeList.get(0);
        for (var i=0;i<pathNodeList.size();++i) {
            if (pathNodeList.get(i).getFCost() < lowestCostNode.getFCost()) {
                lowestCostNode = pathNodeList.get(i);
            }
        }

        return lowestCostNode;
    }


    class Grid {

        private PathNode[][] nodeBoard;
        private int[][] board;
        private int width;
        private int height;

        public Grid(int[][] board, int width, int height){
            this.board = board;
            this.nodeBoard = new PathNode[height][width];
            this.width = width;
            this.height = height;
        }

        public PathNode get(int y, int x){

            if(y < 0 || x < 0 || x >= width || y >= height) return null;


            PathNode node = nodeBoard[y][x];

            if(node == null){
                node = new PathNode(x,y, board[y][x]);
                nodeBoard[y][x] = node;
            }

            return node;

        }

    }


    class PathNode
    {

        private int x;
        private int y;
        private int gCost;
        private int hCost;
        private int fCost;
        private int tileValue;
        private boolean isWalkable;
        private float dist;
        private PathNode parent;

        public PathNode(int x, int y, int tileValue){
            this.x = x;
            this.y = y;
            this.tileValue = tileValue;
        }

        public boolean isWalkable(){
            return this.tileValue == 0 || this.tileValue == 3;
        }

        public void setParent(PathNode pathNode){
            this.parent = pathNode;
        }

        public PathNode getParent(){
            return this.parent;
        }

        public void calculateFCost() {
            fCost = gCost + hCost;
        }

        public void setGCost(int gCost){
            this.gCost = gCost;
        }

        public void setHCost(int hCost){
            this.hCost = hCost;
        }

        public void setFCost(int fCost){
            this.fCost = fCost;
        }

        public int getGCost(){
            return this.gCost;
        }

        public int getFCost(){
            return this.fCost;
        }

        public int getX(){
            return this.x;
        }

        public int getY(){
            return this.y;
        }

    }

}


