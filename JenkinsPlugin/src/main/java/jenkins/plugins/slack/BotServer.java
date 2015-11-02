package jenkins.plugins.slack;

import java.util.logging.Logger;

import hudson.Plugin;

public class BotServer extends Plugin {
	private final static Logger logger = Logger.getLogger(BotServer.class.getName());
	/**
     * {@inheritDoc}
     */
    @Override
    public void start() throws Exception {
        super.start();
        logger.info("starting bot server plugin");
        SlackBot bot = new SlackBot("xoxb-13203396052-6U3vsnm89DThgtCw4NYsYYpp");
		(new Thread(bot)).start();
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
