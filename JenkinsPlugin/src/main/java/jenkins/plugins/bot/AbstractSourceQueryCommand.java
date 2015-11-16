package jenkins.plugins.bot;

import java.util.ArrayList;
import java.util.Collection;

import hudson.model.AbstractProject;

/**
 * Abstract command which returns a result message for one or several jobs.
 *
 * @author kutzi
 */
abstract class AbstractSourceQueryCommand extends AbstractTextSendingCommand {
	
	static final String UNKNOWN_JOB_STR = "unknown job";
	static final String UNKNOWN_VIEW_STR = "unknown view";

	/**
	 * Returns the message to return for this job.
	 * Note that {@link AbstractMultipleJobCommand} already inserts one newline after each job's
	 * message so you don't have to do it yourself.
	 * 
	 * @param job The job
	 * @return the result message for this job
	 */
    protected abstract CharSequence getMessageForJob(Collection<AbstractProject<?, ?>> projects, String[] args);

    /**
     * Returns a short name of the command needed for the help message
     * and as a leading descriptor in the result message.
     * 
     * @return short command name
     */
    protected abstract String getCommandShortName();
    

    @Override
	protected String getReply(Bot bot, JBotSender sender, String[] args) {
    	
//    	if (!authorizationCheck()) {
//    		return "Sorry, can't do that!";
//    	}

        Collection<AbstractProject<?, ?>> projects = new ArrayList<AbstractProject<?, ?>>();
        try {
            getProjects(sender, args, projects);
        } catch (CommandException e) {
            return getErrorReply(sender, e);
        }

        if (!projects.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            msg.append(getMessageForJob(projects,args));
            return msg.toString();
        } else {
            return sender + ": no job found";
        }
	}
    
    /**
     * Returns a list of projects for the given arguments.
     * 
     * @param projects the list to which the projects are added
     * @return a pair of Mode (single job, jobs from view or all) and view name -
     * where view name will be null if mode != VIEW
     */
    void getProjects(JBotSender sender, String[] args, Collection<AbstractProject<?, ?>> projects) throws CommandException {
        projects.addAll(getJobProvider().getAllJobs());
        return;
    }

    @Override
	public String getHelp() {
        return " user <user> | date < < | = | > > <YYYY-MM-DD-HH-mm> | project <project> | build <build number> | jobs < < | = | > > <job number> - show the "
                + getCommandShortName()
                + " of jobs specified";
    }
}
