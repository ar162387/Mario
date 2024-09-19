package minigames.smallworld;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
public enum Item {

    DEFAULT("box.png",'0',-1),
    CPU("miniprocessor.png",'#',0),
    COIN("coin.png",'2',1);

    char ascii;
    String spritePath;
    int specialItemID;
    BufferedImage sprite;
    
    private Item(String spritePath, char ascii, int specialItemID){
        this.sprite = loadSprite(spritePath);
        this.ascii = ascii;
        this.specialItemID = specialItemID;
    }

    private BufferedImage loadSprite(String path) {
        String filePath = "smallworld/images/" + path;
        
        try {
            URL imageURL = getClass().getClassLoader().getResource(filePath);

            if(imageURL != null) {
                return ImageIO.read(imageURL);
            } else {
                throw new IOException("Image not found: " + filePath);
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public int getSpecialItemID() {
        return this.specialItemID;
    }

    public char getAscii() {
        return this.ascii;
    }

    public BufferedImage getSprite() {
        return this.sprite;
    }

    public static Item fromChar(char ascii) {
        for (Item item : Item.values()) {
            if (item.getAscii() == ascii) {
                return item;
            }
        }
        throw new IllegalArgumentException("No item with ascii char: " + ascii);
    }
}
