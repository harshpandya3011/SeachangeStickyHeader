package com.seachange.healthandsafty.helper;


import org.mindrot.jbcrypt.BCrypt;

/**
 * Created by kevinsong on 08/03/2018.
 */

public class PassCodeHelper {

    public PassCodeHelper() {

    }

    public boolean isValidPassCode (final String hashedCode, final String passCode) {
        if (hashedCode == null) return false;
        return BCrypt.checkpw(passCode, hashedCode);
    }

    public String hashedPassword(String passcode) {
        return BCrypt.hashpw(passcode, BCrypt.gensalt());
    }
}
