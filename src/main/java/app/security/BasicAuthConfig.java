package app.security;

import java.util.Map;

/**
 * @author Chris.Ge
 */
public class BasicAuthConfig {
    //The userPassMap will hold the mappings of username to password as defined in the security.basic.userPassMap directive of our config file. This Map will be what we work with when validating authentication.
    public Map<String, String> userPassMap;

    public Map<String, String> getUserPassMap() {
        return userPassMap;
    }

    public void setUserPassMap(Map<String, String> userPassMap) {
        this.userPassMap = userPassMap;
    }
}
