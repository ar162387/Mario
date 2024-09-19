package minigames.server.smallworld;

import java.util.Random;

import minigames.smallworld.WorldMap;

public class MapGenerator {
    private int width;
    private int height;
    private WorldMap wmap;

    public MapGenerator(WorldMap worldmap) {
        this.wmap = worldmap;
        this.width = wmap.getWidth();
        this.height = wmap.getHeight();
    }

    public WorldMap generateMap() {
        char[][] tileMap = new char[height][width];
        char[][] itemMap = new char[height][width];
        generateTerrain(tileMap); // Generate base terrain
        placePonds(tileMap);
        placeTrees(tileMap);      
        placeClouds(tileMap);
        generateItems(itemMap, tileMap);    
        wmap.setTiles(tileMap);
        wmap.setItems(itemMap);
        return wmap;
    }

    private void generateItems(char[][] map, char[][] tilemap){
        for(int i = 1; i < height; i++){
            for(int j = 1; j < width; j++){
                map[i][j] = '0';
                if(tilemap[i][j] == 'm' && tilemap[i-1][j] == 'm' && tilemap[i][j-1] == 'm' && tilemap[i-1][j-1] == 'm'){
                    if(Math.random() < 0.03){
                        map[i][j] = '#';
                    }else{
                        map[i][j] = '0';
                    }
                }
                else if(tilemap[i][j] == 's' && tilemap[i-1][j] == 's' && tilemap[i][j-1] == 's' && tilemap[i-1][j-1] == 's'){
                    if(i < height - 2){
                    if(Math.random() < 0.8 && (tilemap[i+1][j] == 'g' || tilemap[i+1][j] == 'g')){
                            map[i][j] = '2';
                        }else{
                            map[i][j] = '0';
                        }
                    }
                }
            }
        }
        for(int i = 0; i < height; i++){
            map[i][0] = '0';
        }
        for(int i = 0; i < width; i++){
            map[0][i] = '0';
        }
    }

    private void generateTerrain(char[][] map) {
        double[] noise = generatePerlinNoise();
        double[][] caveNoise = generateCaveNoise();
    
        int flatSectionLength = 30; // Number of blocks to keep flat at the start for player spawn
        int maxTerrainHeight = (int) (height * 0.6); // Maximum height for the terrain
        int minTerrainHeight = (int) (height * 0.4); // Minimum terrain height to avoid excessive valleys
    
        int previousGroundHeight = (int) (height * 0.5); // Start with a base ground height
    
        for (int x = 0; x < width; x++) {
            int groundHeight;
    
            // First flatSectionLength blocks will be flat ground
            if (x < flatSectionLength) {
                groundHeight = previousGroundHeight;
            } else {
                // Using the Perlin noise to determine the height at each x-position after the flat section
                groundHeight = (int) (height * 0.5 + noise[x] * 10); // Adjust for hills/valleys
    
                // Smooth transitions and cap heights
                if (Math.abs(groundHeight - previousGroundHeight) > 1) {
                    if (groundHeight > previousGroundHeight) {
                        groundHeight = previousGroundHeight + 1;
                    } else {
                        groundHeight = previousGroundHeight - 1;
                    }
                }
    
                // Apply the maximum and minimum height caps
                if (groundHeight > maxTerrainHeight) {
                    groundHeight = maxTerrainHeight;
                }
                if (groundHeight < minTerrainHeight) {
                    groundHeight = minTerrainHeight;
                }
            }
    
            // Update the previous ground height for the next iteration
            previousGroundHeight = groundHeight;
    
            // Fill the map based on the calculated ground height
            for (int y = 0; y < height; y++) {
                if (y < groundHeight) {
                    map[y][x] = 's'; // Sky
                } else if (y == groundHeight) {
                    map[y][x] = 'g'; // Grass
                } else if (y >= height - 2) {
                    // Permanent base level at the very bottom of the map
                    map[y][x] = 'b'; // Bedrock layer
                } else {
                    // Introduce cave and lava logic:
                    if (y > groundHeight + 3 && caveNoise[x][y] > 0.8) { // Make caves slightly rarer
                        map[y][x] = 'u'; // Carve out a cave
    
                        // Lava generation deeper underground and limited
                        if (y > height * 0.85 && Math.random() < 0.05) { // 5% chance of lava source
                            if (isPositionValidForLava(map, x, y)) {
                                map[y][x] = 'l'; // Lava block
                                makeLavaFlow(map, x, y); // Make lava flow downwards and horizontally
                            }
                        }
                    } else {
                        map[y][x] = 'm'; // Dirt
                    }
                }
            }
        }
    }
    
    private void makeLavaFlow(char[][] map, int startX, int startY) {
        int maxFlowDistance = 3; // Max horizontal flow distance for lava
    
        // Make lava flow downwards first
        for (int y = startY + 1; y < height - 2 && map[y][startX] != 'b'; y++) {
            if (map[y][startX] == 'm' || map[y][startX] == 'u') {
                map[y][startX] = 'l';
            } else {
                break; // Stop flowing if blocked by something solid
            }
        }
    
        // Make lava flow horizontally, but only when it hits bedrock or can't flow further down
        for (int x = -maxFlowDistance; x <= maxFlowDistance; x++) {
            int flowX = startX + x;
            if (flowX >= 0 && flowX < width) {
                for (int y = startY; y < height - 2; y++) {
                    if (map[y][flowX] == 'm' || map[y][flowX] == 'u') {
                        map[y][flowX] = 'l';
                    } else {
                        break; // Stop horizontal flow if blocked
                    }
                }
            }
        }
    }
    
    private boolean isPositionValidForLava(char[][] map, int x, int y) {
        // Ensure lava only spawns in open cave areas, not on solid ground
        return map[y][x] == 'u' && map[y + 1][x] != 's' && map[y - 1][x] != 's';
    }
    

    
    
    private double[][] generateCaveNoise() {
        double[][] caveNoise = new double[width][height];
        double scale = 0.12; // increase scale for smaller caves
        int octaves = 3; // More octaves for detailed cave shapes
        double persistence = 0.4; // persistence to reduce the cave size
    
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double amplitude = 1;
                double frequency = scale;
                double noiseValue = 0;
    
                // Sum multiple layers of noise (octaves) for more complex cave structure
                for (int i = 0; i < octaves; i++) {
                    double sampleX = x * frequency;
                    double sampleY = y * frequency;
                    double perlinValue = Math.sin(sampleX + sampleY * 1.5) * 0.5 + 0.5; // Apply more weight to the y-axis to avoid zigzag
                    noiseValue += perlinValue * amplitude;
    
                    amplitude *= persistence;
                    frequency *= 2;
                }
    
                caveNoise[x][y] = noiseValue;
            }
        }
    
        return caveNoise;
    }
    
    private double[] generatePerlinNoise() {
        double[] noise = new double[width];
        double scale = 0.1; // Controls frequency of hills and valleys
        double persistence = 0.5; // Controls amplitude of the terrain
        int octaves = 4; // Controls level of detail
    
        for (int x = 0; x < width; x++) {
            double amplitude = 1;
            double frequency = scale;
            double noiseHeight = 0;
    
            // Sum multiple layers of noise (octaves) for more complex terrain
            for (int i = 0; i < octaves; i++) {
                double sampleX = x * frequency;
                double perlinValue = Math.sin(sampleX); // Placeholder for actual Perlin noise
                noiseHeight += perlinValue * amplitude;
    
                amplitude *= persistence;
                frequency *= 2;
            }
    
            noise[x] = noiseHeight;
        }
    
        return noise;
    }



    void placeTrees(char[][] map) {
        Random random = new Random();
        for (int x = 0; x < map[0].length; x++) {
            if (Math.random() < 0.1) { // 10% chance to place a tree
                int groundHeight = getGroundHeight(map, x);
    
                // Ensure that the tree is placed at the grass level (one block above dirt) and not on water
                if (groundHeight > 0 && groundHeight < map.length - 5 && map[groundHeight][x] == 'g') { // Check if it's grass
                    int grassLevel = groundHeight - 1; // Grass level is one block above the dirt
    
                    // Fixed minimum trunk height of 3
                    int trunkHeight = 3 + random.nextInt(3); // Tree trunk height between 3 and 5 blocks
    
                    if (grassLevel >= trunkHeight) { // Ensure there is enough space for the tree trunk
                        // Place the trunk starting from the grass level
                        for (int i = 0; i < trunkHeight; i++) {
                            int trunkY = grassLevel - i;
                            if (trunkY >= 0) { // Ensure we are within bounds
                                map[trunkY][x] = 'T';
                            }
                        }
                    }
    
                    // The height where the leaves should start
                    int leafStartY = grassLevel - trunkHeight;
    
                    // Create bushier leaves with varying patterns
                    for (int i = -2; i <= 2; i++) {
                        for (int j = -2; j <= 2; j++) {
                            if (Math.abs(i) + Math.abs(j) < 3 + random.nextInt(2)) { // A simple condition to give a roundish look with randomness
                                if (x + i >= 0 && x + i < map[0].length && leafStartY + j >= 0) {
                                    map[leafStartY + j][x + i] = 'L';
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void placeClouds(char[][] map) {
        Random random = new Random();
        for (int x = 0; x < width; x++) {
            if (Math.random() < 0.07) { // Slightly increase chance to start a cloud
                int startX = x;
                int startY = (int) (Math.random() * height * 0.1); // Clouds appear in the top 10% of the screen This is starting hieght
    
                // Generate cloud dimensions (larger clouds)
                int cloudWidth = 5 + random.nextInt(3); // Cloud width between 5 and 7 blocks
                int cloudHeight = 3 + random.nextInt(2); // Cloud height between 3 and 4 blocks
    
                // Generate the cloud shape
                for (int i = 0; i < cloudWidth; i++) {
                    for (int j = 0; j < cloudHeight; j++) {
                        int cloudX = startX + i;
                        int cloudY = startY + j;
    
                        // Ensure we stay within bounds
                        if (cloudX < width && cloudY < height) {
                            // Determine if this block is an edge block
                            boolean isEdge = (i == 0 || i == cloudWidth - 1 || j == 0 || j == cloudHeight - 1);
    
                            // If it's an edge block, 80% chance to place it; otherwise, always place it
                            if (!isEdge || random.nextDouble() < 0.8) {
                                map[cloudY][cloudX] = 'C'; // Place the cloud block
                            }
                        }
                    }
                }
    
                // Skip ahead by cloud width to avoid overlapping clouds
                x += cloudWidth;
            }
        }
    }


    private void placePonds(char[][] map) {
        Random random = new Random();
        int maxPondSize = 5; // Max width or height of a pond
    
        for (int x = 2; x < width - 2; x++) { // Start from x=2 and stop at width-2 to avoid edge placement
            if (Math.random() < 0.04) { // 4% chance to generate a pond at this x position
                int groundHeight = getGroundHeight(map, x);
    
                // Avoid placing water at steep drops
                int nextGroundHeight = getGroundHeight(map, x + 1);
                if (Math.abs(groundHeight - nextGroundHeight) <= 1 && groundHeight > 0 && groundHeight < height - 4) {
                    int pondWidth = 3 + random.nextInt(maxPondSize - 3); // Pond width between 3 and maxPondSize
                    int pondDepth = 1 + random.nextInt(2); // Pond depth between 1 and 2 blocks
    
                    boolean canPlacePond = true;
    
                    // Check that there's no dirt block directly above where the pond would be
                    for (int i = 0; i < pondWidth; i++) {
                        int pondX = x + i;
                        int checkY = groundHeight - 1;
                        if (pondX < width && map[checkY][pondX] != 's') { // Ensure the block above is sky
                            canPlacePond = false;
                            break;
                        }
                    }
    
                    if (canPlacePond) {
                        for (int i = 0; i < pondWidth; i++) {
                            int pondX = x + i;
                            if (pondX < width) { // Ensure we are within bounds
                                for (int j = 0; j < pondDepth; j++) {
                                    int pondY = groundHeight + j;
                                    if (pondY < height && (map[pondY][pondX] == 'g' || map[pondY][pondX] == 'm')) { // Replace grass and dirt with water
                                        map[pondY][pondX] = 'w'; // Water block
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private int getGroundHeight(char[][] map, int x) {
        for (int y = height - 1; y >= 0; y--) {
            if (map[y][x] == 'g') { // 'g' represents the grass in your map
                return y;
            }
        }
        return -1; // Fallback in case no ground is found
    }

    public WorldMap getMap(){
        return this.wmap;
    }

}
