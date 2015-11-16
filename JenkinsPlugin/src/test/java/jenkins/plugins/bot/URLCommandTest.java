package jenkins.plugins.bot;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;


public class URLCommandTest {

	private Pattern pattern = Pattern.compile("https?://[^/]*?/(([^/]*/)*)");
	private String getPath ( String url ) {
		Matcher m = pattern.matcher(url); 
		if (m.find()) {
            return m.group(1);
        } else {
            return "Not A valid URL!!";
        }
	}
	
	private JBotSender sender;
	private URLCommand cmd;
	private URLCommand spy;
	
	@Before
	public void initialize() {
		sender = new JBotSender("tester");
		cmd = new URLCommand();
		spy = spy(cmd);
		
		doReturn("http://domain:port/").when(spy).getBaseURL();
	}
	@Test
	public void testGetGlobalLog() {
		String[] args = { "geturl", "log" };
		
		String reply = spy.getReply(null, sender, args);
		
		assertEquals(getPath(reply),"log/");
	}
	
	@Test
	public void testGetGlobalConfigure() {
		String[] args0 = { "geturl", "conf" };
		String reply = spy.getReply(null, sender, args0);
		assertEquals(getPath(reply),"configure/");
		
		String[] args1 = { "geturl", "configure" };
		reply = spy.getReply(null, sender, args1);
		assertEquals(getPath(reply),"configure/");
	}
	
	@Test
	public void testGetPluginManager() {
		String[] args0 = { "geturl", "plugin" };
		String reply = spy.getReply(null, sender, args0);
		assertEquals(getPath(reply),"pluginManager/");
		
		String[] args1 = { "geturl", "plugins" };
		reply = spy.getReply(null, sender, args1);
		assertEquals(getPath(reply),"pluginManager/");
	}
    
    //-----------extend
    @Test
    public void testGetSecurity() {
        String[] args0 = { "geturl", "sec" };
        String reply = spy.getReply(null, sender, args0);
        assertEquals(getPath(reply),"configureSecurity/");
        
        String[] args1 = { "geturl", "security" };
        reply = spy.getReply(null, sender, args1);
        assertEquals(getPath(reply),"configureSecurity/");
    }
    
    @Test
    public void testGetLoadstatistics() {
        String[] args0 = { "geturl", "stat" };
        String reply = spy.getReply(null, sender, args0);
        assertEquals(getPath(reply),"load-statistics/");
        
        String[] args1 = { "geturl", "statistic" };
        reply = spy.getReply(null, sender, args1);
        assertEquals(getPath(reply),"load-statistics/");
    }
    
    @Test
    public void testGetScriptConsole() {
        String[] args0 = { "geturl", "script" };
        String reply = spy.getReply(null, sender, args0);
        assertEquals(getPath(reply),"script/");
        
        String[] args1 = { "geturl", "scripts" };
        reply = spy.getReply(null, sender, args1);
        assertEquals(getPath(reply),"script/");
    }
    
    @Test
    public void testGetNodes() {
        String[] args0 = { "geturl", "node" };
        String reply = spy.getReply(null, sender, args0);
        assertEquals(getPath(reply),"computer/");
        
        String[] args1 = { "geturl", "nodes" };
        reply = spy.getReply(null, sender, args1);
        assertEquals(getPath(reply),"computer/");
    }
    
    @Test
    public void testGetUsers() {
        String[] args0 = { "geturl", "user" };
        String reply = spy.getReply(null, sender, args0);
        assertEquals(getPath(reply),"securityRealm/");
        
        String[] args1 = { "geturl", "users" };
        reply = spy.getReply(null, sender, args1);
        assertEquals(getPath(reply),"securityRealm/");
    }
    //------------
	
	@Test
	public void testGetRootURL() {
		String[] args0 = { "geturl", "base" };
		String reply = spy.getReply(null, sender, args0);
		assertEquals(getPath(reply),"");
		
		String[] args1 = { "geturl", "root" };
		reply = spy.getReply(null, sender, args1);
		assertEquals(getPath(reply),"");
	}
	
	@Test
	public void testGetDefaultURL() {
		String[] args0 = { "geturl"};
		String reply = spy.getReply(null, sender, args0);
		assertEquals(getPath(reply),"");
	}
	
	@Test
	public void testGetURLNotFound() {
		String[] args0 = { "geturl","This is some thing weird!"};
		String reply = spy.getReply(null, sender, args0);
		assertTrue(reply.startsWith("Available Command:"));
	}
}
