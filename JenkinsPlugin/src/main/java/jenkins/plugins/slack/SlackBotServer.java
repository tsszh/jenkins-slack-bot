package jenkins.plugins.slack;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;

import org.kohsuke.stapler.StaplerRequest;

import hudson.Plugin;
import net.sf.json.JSONObject;

/**
 * 
 * A server inside Jenkins to communicate with Slack Channel.
 * 
 * @author Zehao Song
 * 
 */

public class SlackBotServer extends Plugin implements Serializable {
	/** Serial Number for SlackBotServer */
	private static final long serialVersionUID = 1L;
	/** Logger for SlackBotServer */
	private final static Logger logger = Logger.getLogger(SlackBotServer.class.getName());
	/** The instance of SlackBot */
	private transient SlackBot bot;
	/** Identification of SlackBot, Provided by Slack */
	private String token;
	/** Jenkins User Name. Used for secured Jenkins */
	private String userID;
	
	
	/*------------------------------------------  Jenkins Plugin    -------------------------------------*/
	
	/**
     * {@inheritDoc}
     */
    @Override
    public void start() throws Exception {
        super.start();
        logger.info("starting bot server plugin");
        bot = new SlackBot(); 				// Create the slackbot
		(new Thread(bot)).start(); 			// Thread for slack bot server
		load();								// Loads token & userID of this instance from the persisted storage.
		if ( this.token == null ) this.token = "";
		if ( this.userID == null ) this.userID = "";
		resetSlackBot();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() throws Exception {
    	closeSlackBot();
        super.stop();						
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(StaplerRequest req, JSONObject formData) throws IOException{
    	JSONObject datas = formData.getJSONObject("slackBot");
    	this.token = datas.optString("slackBotToken","");
    	this.userID = datas.optString("slackJenkinsUserID","");
    	resetSlackBot();
		save();								// Saves token & userID of this instance to the persisted storage.
    }
    
    /*------------------------------------------  Slack Connection  -------------------------------------*/
    
    /**
     * Whenever the token or userID is changed, SlackBot must be updated.
     */
    private void resetSlackBot(){
    	bot.setSlackBot(this.token, this.userID);
    }
    /**
     * Clear the connection to Slack Server
     */
    private void closeSlackBot() {
    	bot.disconnectToSlack();
    }
    
    
    /*---------------------------------------------  Jelly  ---------------------------------------------*/
    
    /**
     * The Getter Method Used By Jelly To Retrieve the JenkinsUserID
     * 
     * If you're using a secured Jenkins, you must specify a valid username for the Slack Bot. Otherwise most commands won't work.
     * You can also use this to restrict which commands can be executed via the bot, by removing the respective permissions for this user.
     * 
     * @return String  slackJenkinsUserID
     */
    public String getSlackJenkinsUserID() {
    	return this.userID;
    }
    
    /**
     * The Getter Method Used By Jelly To Retrieve the Slack Bot Token
     * 
     * Each slack bot has a global unique token, which can be used to establish the connection. 
     * 
     * @return String slackBotToken
     */
    public String getSlackBotToken() {
    	return this.token;
    }
}
