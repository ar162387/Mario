package minigames.server.api.auth;

import org.mindrot.jbcrypt.BCrypt;

public class HashingService {

    public static String hashPassword(String password) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(10));
        return hashed;
    }

    public static boolean comparePassword(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }
}

