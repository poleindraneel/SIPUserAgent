package org.etechnik.mobileComputing.Group7;

import java.io.IOException;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

public class RestServer {
	
		public void start() {
			try {
				HttpServer server = HttpServerFactory
						.create("http://"+IP.getIP("linux")+":8081/moco");
				server.start();
			} catch (IllegalArgumentException | IOException e) {
				e.printStackTrace();
			}

		}
}
