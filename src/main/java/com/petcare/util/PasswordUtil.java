package com.petcare.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean verify(String password, String hash) {
        if (hash == null || hash.isEmpty()) return false;
        return BCrypt.checkpw(password, hash);
    }
}
