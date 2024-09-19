package minigames.client.EightBall;

public enum BallType {
    NEUTRAL, STRIPES, SOLIDS, EIGHT, CUE;

    public static String toString(BallType type) {
        switch (type) {
            case STRIPES:
                return "Stripes";
            case SOLIDS:
                return "Solids";
            case EIGHT:
                return "Eight";
            case CUE:
                return "Cue";
            default:
                return "Neutral";
        }
    }
}
