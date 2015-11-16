package jenkins.plugins.bot;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHelper {
	public static final String dump(final Throwable throwable)
    {
        if (throwable == null)
        {
            return "null";
        }
        final StringWriter sw = new StringWriter(512);
        throwable.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }
}
