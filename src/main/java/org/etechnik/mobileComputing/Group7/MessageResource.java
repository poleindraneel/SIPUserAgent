package org.etechnik.mobileComputing.Group7;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/sip")
public class MessageResource {

	@POST
	@Path("/startCall/{name}/{realm}")
	public Response startCall (@PathParam("name") String name, @PathParam("realm") String realm){
	{
		System.out.println("received something:   " + name + "   " + realm);
		UAClient.getInstance().sendINVITE(name, realm);
		return Response.status(200).build();
	}
		
	
	
	}
	
	
	
}
