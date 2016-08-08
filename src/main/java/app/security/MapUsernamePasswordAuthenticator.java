package app.security;

import com.google.common.base.Strings;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;
import org.pac4j.http.profile.HttpProfile;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

/**
 * @author Chris.Ge
 */
public class MapUsernamePasswordAuthenticator implements UsernamePasswordAuthenticator {
    //We also define the key length to be used.
    public static final int KEY_LENGTH = 192;
    //Here, we define a static value for how many iterations are used when building the key for the password hash (more on this to come).
    private static final int ITERATIONS = 1000;
    //Here, we provide the map of username-to-password-hash so that we can retrieve the appropriate hash by the supplied username.
    Map<String, String> userMap;

    public MapUsernamePasswordAuthenticator(Map<String, String> userMap) {
        this.userMap = userMap;
    }

    //The hashPassword method is the main utility in our authenticator. It takes the provided password and a salt. In this case, we can use the username as the salt for the password, though you may find benefit in using a pre-configured salt provided by your application’s configuration.
    public static String hashPassword(String password, String salt)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes();
        //From the password and username, using the previously defined ITERATIONS and KEY_LENGTH count, we construct the key specification that will be used to generate the password hash (which will be validated against the configured value from earlier).
        PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, ITERATIONS, KEY_LENGTH);
        //You can specify whatever digest algorithm you desire. Here we specify the PBKDF2WithHmacSHA512 algorithm, which uses PBKDF2 with HMAC-SHA-512 as the pseudorandom function. (As a note, this is a good approach for password security, though there are caveats to the level of rigidity of this security mechanism. For example, too low an iteration count could leave your passwords more-easily brute-forced. Play with the ITERATIONS value to ensure you are providing the appropriate level of security. For this, 1000 count is a good starting place).
        SecretKeyFactory key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        //Here, we generate and capture the encoded value, which is a byte array of our password hash.
        byte[] hashedPassword = key.generateSecret(spec).getEncoded();
        //Finally, we format and return the hashed password for validation.
        return String.format("%x", new BigInteger(hashedPassword));
    }

    //The validate method is what will be called by the Pac4j infrastructure, which provides us with a UsernamePasswordCredentials object to get access to the supplied username and password.
    @Override
    public void validate(UsernamePasswordCredentials credentials) {
        //From the provided username-to-password-hash map, we retrieve the password hash for the given user.
        String pasHash = userMap.get(credentials.getUsername());
        try {
            //We can provide a sanity check to ensure the user is known, and then validate that the configured hash matches the hash specified by the configuration.
            if (Strings.isNullOrEmpty(pasHash) || !pasHash
                .equals(hashPassword(credentials.getPassword(), credentials.getUsername()))) {
                //If there is no user or if the hashes do not match, then we throw a CredentialsException, which informs Pac4j that authentication has failed.
                throwsException("Invalid username or password.");

            }
            //Here, we have created a UserProfile object in the form of a simple HttpProfile instance, for which we set the id field to the authenticated user. We set the profile on the UsernamePasswordCredentials object so that it is accessible for subsequent requests.
            UserProfile up = new HttpProfile();
            up.setId(credentials.getUsername());
            credentials.setUserProfile(up);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throwsException(e.toString());
        }

    }

    protected void throwsException(final String message) {
        throw new CredentialsException(message);
    }
}
