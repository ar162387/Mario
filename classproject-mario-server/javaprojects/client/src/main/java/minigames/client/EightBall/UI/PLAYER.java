package minigames.client.EightBall.UI;

public enum PLAYER {
    OFFLINE("/EightBall/offline.png"),
    ONLINE("/EightBall/online.png");

    private String urlPlayer;

    private PLAYER(String urlPlayer) {
        this.urlPlayer = urlPlayer;
    }

    public String getUrl(){
        return this.urlPlayer;
    }
}
