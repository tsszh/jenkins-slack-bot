package jenkins.plugins.slack;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;

import org.kohsuke.stapler.StaplerRequest;

import hudson.Plugin;
import net.sf.json.JSONObject;

public class SlackBotServer extends Plugin implements Serializable {
	private final static Logger logger = Logger.getLogger(SlackBotServer.class.getName());
	private transient SlackBot bot;
	private String token;
	private String userID;
	/**
     * {@inheritDoc}
     */
    @Override
    public void start() throws Exception {
        super.start();
        logger.info("starting bot server plugin");
        bot = new SlackBot();
//        bot = new SlackBot("xoxb-13203396052-6U3vsnm89DThgtCw4NYsYYpp","zsong12");
		(new Thread(bot)).start();
		load();
		resetSlackBot();
    }

    @Override
    public void configure(StaplerRequest req, JSONObject formData) throws IOException{
    	JSONObject datas = formData.getJSONObject("slackBot");
    	this.token = datas.optString("slackBotToken","");
    	this.userID = datas.optString("slackJenkinsUserID","");
    	resetSlackBot();
		save();
		
    }
    
    private void resetSlackBot(){
    	bot.setSlackBot(this.token, this.userID);
    }
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void stop() throws Exception {
//    	session.disconnect();
//        super.stop();
//    }
}
