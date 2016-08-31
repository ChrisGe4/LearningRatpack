package app.security;

import groovy.sql.GroovyRowResult;
import groovy.sql.Sql;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;
import org.pac4j.http.profile.HttpProfile;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.inject.Inject;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

/**
 * @author Chris.Ge
 */
public class DatabaseUsernamePasswordAuthenticator implements UsernamePasswordAuthenticator {

    private static final int ITERATIONS = 1000;
    private static final int KEY_LENGTH = 192;

    private final Sql sql;

    //As noted, we use the Guice @Inject annotation to get access to the Groovy SQL Sql object for querying the database in the validate method.
    @Inject
    public DatabaseUsernamePasswordAuthenticator(Sql sql) {
        this.sql = sql;
    }

    public static String hashPassword(String password, String salt)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes();
        PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        byte[] hashedPassword = key.generateSecret(spec).getEncoded();
        //for big int, Requires the output to be formatted as an integer in base sixteen. No localization is applied.
        //        If x is negative then the result will be a signed value beginning with '-' ('\u002d'). Signed output is allowed for this type because unlike the primitive types it is not possible to create an unsigned equivalent without assuming an explicit data-type size.
        //
        //            If x is positive or zero and the '+' flag is given then the result will begin with '+' ('\u002b').
        //
        //            If the '#' flag is given then the output will always begin with the radix indicator "0x".
        //
        //            If the '0' flag is given then the output will be padded to the field width with leading zeros after the radix indicator or sign (if present).
        //
        //        If the ',' flag is given then a FormatFlagsConversionMismatchException will be thrown.
        return String.format("%x", new BigInteger(hashedPassword));
    }

    @Override
    public void validate(UsernamePasswordCredentials credentials) {
        try {
            //From the credentials supplied by the user, we query the USER_AUTH database table for the row corresponding to the specified username. We can safely make this call here synchronously, because Ratpack assumes the authentication sequence will block, so the UsernamePasswordAuthenticator is pre-emptively scheduled to a blocking thread.
            GroovyRowResult userRow =
                sql.firstRow("SELECT * FROM USER_AUTH WHERE USER = ${credentials.username}");
            //If there is no row for the given username, then throw an exception, which will short-circuit the authentication sequence within Pac4j.
            if (userRow == null || userRow.isEmpty())
                throw new RuntimeException("Invalid username or password");
            //Provided we have found the user’s authentication row in the database, capture the PASS field, which is the hashed version of the user’s password.
            String passHash = (String) userRow.get("PASS");
            if (passHash == null || !passHash
                .equals(hashPassword(credentials.getPassword(), credentials.getUsername()))) {
                throw new RuntimeException("Invalid username or password");
            }

            HttpProfile profile = new HttpProfile();
            profile.setId(credentials.getUsername());
            credentials.setUserProfile(profile);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }



    }
}
