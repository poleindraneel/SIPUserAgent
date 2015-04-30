package org.etechnik.mobileComputing.Group7;


public class Main {

	/**
	 * @param args
	 * @throws UnknownHostException 
	 */
	public static  UA ua = null;
	
	public static void main(String[] args) {
		RestServer restServer = new RestServer();
		restServer.start();
		
		new Thread(new WebsocketServer()).start();
		
		
		
		ua = new UA();
		ua.createSipStack();
		System.out.println(UserProperties.name);

		//UAClient.getInstance().sendINVITE("PhonerLite", "192.168.2.109");

		
		
	}

}
