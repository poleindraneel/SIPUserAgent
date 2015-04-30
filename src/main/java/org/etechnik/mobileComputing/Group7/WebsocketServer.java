package org.etechnik.mobileComputing.Group7;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class WebsocketServer implements Runnable {

	   public void startServer() throws Exception {
	        Server server = new Server(8082);
	        
	        WebSocketHandler wsHandler = new WebSocketHandler() {
	            @Override
	            public void configure(WebSocketServletFactory factory) {
	                factory.register(WebsocketHandler.class);
	            }
	        };
	        

	        ResourceHandler resource_handler = new ResourceHandler();
	        resource_handler.setDirectoriesListed(true);
	        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
	 
	       // resource_handler.setResourceBase("/home/gregor/Schreibtisch/againMOBU/SIP/");
	        // MUST BE CHANGED ! 
	        resource_handler.setResourceBase("/home/pi/MoCoFINAL/");
	        HandlerList handlers = new HandlerList();
	        handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
	        
	        HandlerCollection handlerCollection = new HandlerCollection();
	        handlerCollection.setHandlers(new Handler[] { wsHandler, handlers } );

	        server.setHandler(handlerCollection);
	        server.start();
	        server.join();
	    }
	
	   
	   public static void main(String[] args) {
		   try {
			new WebsocketServer().startServer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }


	@Override
	public void run() {
		try {
			startServer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
