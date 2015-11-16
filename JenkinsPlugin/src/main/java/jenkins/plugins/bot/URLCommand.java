package jenkins.plugins.bot;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import hudson.Extension;
import jenkins.model.Jenkins;

@Extension
public class URLCommand extends AbstractTextSendingCommand {

	private static final String SYNTAX = "<page>";
	private static final String HELP = SYNTAX + " - retrieve the spesific URL for Jenkins";

	HashMap<String, Integer> cmd_list;

	@Override
	public Collection<String> getCommandNames() {
		return Collections.singleton("geturl");
	}

	@Override
	protected String getReply(Bot bot, JBotSender sender, String[] args) {
		if (cmd_list == null) {
			cmd_list = new HashMap<String, Integer>();

			cmd_list.put("base", 1);
			cmd_list.put("root", 1);

			cmd_list.put("configure", 2);
			cmd_list.put("conf", 2);

			cmd_list.put("log", 3);

			cmd_list.put("plugin", 4);
			cmd_list.put("plugins", 4);
            
            cmd_list.put("security", 5);
            cmd_list.put("sec", 5);
            
            cmd_list.put("statistic", 6);
            cmd_list.put("stat", 6);
            
            cmd_list.put("script", 7);
            cmd_list.put("scripts", 7);
            
            cmd_list.put("node", 8);
            cmd_list.put("nodes", 8);
            
            cmd_list.put("user", 9);
            cmd_list.put("users", 9);
            
		}

		if (args.length == 1) {
			return getBaseURL();
		} else if (args.length == 2) {
			int cmd_idx = cmd_list.get(args[1]) == null ? 0 : cmd_list.get(args[1]);
			switch (cmd_idx) {
			case 1:
				return getBaseURL();
			case 2:
				return getGlobalConfigureURL();
			case 3:
				return getGlobalSystemLogURL();
			case 4:
				return getPluginManager();
            case 5:
                return getSecurity();
            case 6:
                return getLoadstatistics();
            case 7:
                return getScriptConsole();
            case 8:
                return getNodes();
            case 9:
                return getUsers();
			default:
				return getAvailableCommand();
			}
		} else {
			return giveSyntax(sender.getNickname(), args[0]);
		}
	}

	@Override
	public String getHelp() {
		return HELP;
	}

	private String giveSyntax(String sender, String cmd) {
		return sender + ": syntax is: '" + cmd + SYNTAX + "'";
	}

	String getBaseURL() {
		return Jenkins.getInstance().getRootUrl();
	}

	private String getGlobalConfigureURL() {
		return getBaseURL() + "configure/";
	}

	private String getGlobalSystemLogURL() {
		return getBaseURL() + "log/";
	}

	private String getPluginManager() {
		return getBaseURL() + "pluginManager/";
	}
    
    
    
    //extend
    private String getSecurity() {
        return getBaseURL() + "configureSecurity/";
    }
    
    private String getLoadstatistics() {
        return getBaseURL() + "load-statistics/";
    }
    
    private String getScriptConsole() {
        return getBaseURL() + "script/";
    }
    
    private String getNodes() {
        return getBaseURL() + "computer/";
    }
    
    private String getUsers() {
        return getBaseURL() + "securityRealm/";
    }
    
    //
	private String getAvailableCommand() {
		StringBuilder buf = new StringBuilder();
		buf.append("Available Command: ");
		for (String key : cmd_list.keySet()) {
			buf.append(key);
			buf.append(", ");
		}
		return buf.substring(0, buf.length() - 2);
	}
}
