package app.security;

/**
 * @author Chris.Ge
 */
public class SecurityConfig {
//We embed the structure of the security.basic directive in a nested object type, BasicAuthConfig.
    public BasicAuthConfig basic;

    public BasicAuthConfig getBasic() {
        return basic;
    }

    public void setBasic(BasicAuthConfig basic) {
        this.basic = basic;
    }
}
