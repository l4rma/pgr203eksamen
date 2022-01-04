package no.eksamenPGR203;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHash {
    public static String md5Hash(String s) throws NoSuchAlgorithmException {
        String md5;
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(s.getBytes(), 0, s.length());
        md5 = new BigInteger(1, md.digest()).toString(16);
        return md5;
    }


}
