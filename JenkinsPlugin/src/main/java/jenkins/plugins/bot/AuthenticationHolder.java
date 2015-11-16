package jenkins.plugins.bot;

import org.acegisecurity.Authentication;


public interface AuthenticationHolder {
    /**
     * Return the {@link Authentication}.
     */
    Authentication getAuthentication();
}