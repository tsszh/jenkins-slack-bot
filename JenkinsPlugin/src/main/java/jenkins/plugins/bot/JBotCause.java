package jenkins.plugins.bot;

import hudson.model.Cause;

public class JBotCause extends Cause{
	private final String description;

	public JBotCause(String description) {
		this.description = description;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getShortDescription() {
		return this.description;
	}
}
