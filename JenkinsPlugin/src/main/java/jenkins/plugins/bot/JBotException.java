package jenkins.plugins.bot;

import java.io.IOException;

public class JBotException extends IOException
{
    private static final long serialVersionUID = 1L;

    public JBotException(final Exception e) {
        super(e.getMessage());
        initCause(e);
    }
    
    public JBotException(String msg) {
    	super(msg);
    }

}