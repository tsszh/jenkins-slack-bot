package jenkins.plugins.bot;

import hudson.model.AbstractProject;
import hudson.model.View;
import hudson.ExtensionPoint;

import java.util.List;

import jenkins.model.Jenkins;

/**
 * Default {@link JobProvider} which directly accesses {@link Jenkins#getInstance()}.
 *
 * @author kutzi
 */
public class DefaultJobProvider implements ExtensionPoint, JobProvider {

    public AbstractProject<?, ?> getJobByName(String name) {
        return Jenkins.getInstance().getItemByFullName(name, AbstractProject.class);
    }
    

    @SuppressWarnings("rawtypes")
    public AbstractProject<?, ?> getJobByDisplayName(String displayName) {
        List<AbstractProject> allItems = Jenkins.getInstance().getAllItems(AbstractProject.class);
        for (AbstractProject job : allItems) {
            if (displayName.equals(job.getDisplayName())) {
                return job;
            }
        }
        return null;
    }

    public AbstractProject<?, ?> getJobByNameOrDisplayName(String name) {
        AbstractProject<?,?> jobByName = getJobByName(name);
        return jobByName != null ? jobByName : getJobByDisplayName(name);
    }

    @SuppressWarnings("unchecked")
    public List<AbstractProject<?,?>> getAllJobs() {
        @SuppressWarnings("rawtypes")
        List items = Jenkins.getInstance().getAllItems(AbstractProject.class);
        return items;
    }
    
    @SuppressWarnings("unchecked")
    public List<AbstractProject<?,?>> getTopLevelJobs() {
        @SuppressWarnings("rawtypes")
        List items = Jenkins.getInstance().getItems(AbstractProject.class);
        return items;
    }

    public boolean isTopLevelJob(AbstractProject<?, ?> job) {
        return Jenkins.getInstance().equals(job.getParent());
    }

    public View getView(String viewName) {
        return Jenkins.getInstance().getView(viewName);
    }
}
