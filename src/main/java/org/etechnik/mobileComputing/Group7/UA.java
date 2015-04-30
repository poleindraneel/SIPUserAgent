package org.etechnik.mobileComputing.Group7;

import java.io.Console;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sip.*;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.sip.address.Address;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


public class UA implements SipListener{
	
	//Created by Indraneel Pole
	
	
	public String protocol = "udp";
	public String ipAddress;
	public int port = 5060;
	
	public int audioPort = 39000;
	public int videoPort = 49000;
	
	public String fromName = "Group7";
	private String fromDisplay = "Group7MoBu";
	
	private UAClient uaClient = null;
	private UAServer uaServer = null;
	
	public Dialog callDialog;
	public SipProvider callProvider;
	public ClientTransaction callClientTransaction;
	public ServerTransaction callServerTransaction;
	
	private SipStack sipStack;
	private ListeningPoint listeningPoint;
	public HeaderFactory headerFactory;
	public AddressFactory addressFactory;
	public MessageFactory messageFactory;
	public SipProvider sipProvider;



	
	
	public void createSipStack()
	{
		this.ipAddress = "192.168.50.10";//IP.getIP("windows"); 
		System.out.println("***********************************");
		System.out.println("Trying to set up SIP Stack!");
		System.out.println("IP:       " + ipAddress);
		System.out.println("Port:     " + port);
		System.out.println("Protocol: " + protocol);
		System.out.println("************************************");
		
		
		 ConsoleAppender console = new ConsoleAppender(); //create appender
         //configure the appender
         String PATTERN = "%d [%p|%c|%C{1}] %m%n";
         console.setLayout(new PatternLayout(PATTERN));
         console.setThreshold(Level.DEBUG);
         console.activateOptions();
         //add appender to any Logger (here is root)
        // Logger.getRootLogger().addAppender(console);


		SipFactory sipFactory = SipFactory.getInstance();
        sipStack = null;
        sipFactory.setPathName("gov.nist");
        Properties properties = new Properties();
        
        //properties.setProperty("javax.sip.OUTBOUND_PROXY", ipAddress+":"+port + "/"
         //       + protocol);

        properties.setProperty("javax.sip.STACK_NAME", "mySipStack");

        //properties.setProperty("gov.nist.javax.sip.CACHE_CLIENT_CONNECTIONS",
        //        "false");
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "DEBUG");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
                "shootmedebug.txt");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG",
                "shootmelog.txt");



        	try{
        		
        			//Create Sip Stack and add listening point
        			
        			sipStack = sipFactory.createSipStack(properties);
        
        			
        			headerFactory = sipFactory.createHeaderFactory();
        			messageFactory = sipFactory.createMessageFactory();
        			addressFactory = sipFactory.createAddressFactory();
        			
        			listeningPoint = sipStack.createListeningPoint(ipAddress, port, protocol);
            		sipProvider = sipStack.createSipProvider(listeningPoint);
            		UA sipListener = this;
            		sipProvider.addSipListener(sipListener);
            		System.out.println("SIP Stack, ListeningPoint, SpiProvider created!");
            	
            		uaServer = new UAServer(this);
            		uaClient = new UAClient(this);

        	}
        	catch(Exception ex)
        	{
        		System.out.println("Problems setting up the SIP Stack!");
        		ex.printStackTrace();
        		System.err.println(ex);
        	}        	
	}

	@Override
	public void processRequest(RequestEvent requestEvent) {

		Request recievedRequest = requestEvent.getRequest();
		ServerTransaction serverTransactionID = requestEvent.getServerTransaction();
	//	SipProvider sipProvider = (SipProvider) requestEvent.getSource();
		
		System.out.println("Received Request:   " + recievedRequest.getMethod() + "  ... processing!");
		System.out.println(recievedRequest);
		
		
		// List off all Requests - answer with NOT SUPPORTED HERE at moste requests
		switch(recievedRequest.getMethod())
		{
			case Request.INVITE:
				uaServer.processInvite(requestEvent, serverTransactionID);
				break;
			case Request.ACK:
				uaServer.processAck(requestEvent, serverTransactionID);
				break;
			case Request.BYE:
				uaServer.processBye(requestEvent, serverTransactionID);
				break;
			case Request.CANCEL:
				uaServer.processCancel(requestEvent, serverTransactionID);
				break;
			case Request.MESSAGE:
				uaServer.processMessage(requestEvent, serverTransactionID);
				break;
			case Request.INFO:
				uaServer.processInfo(requestEvent, serverTransactionID);
				break;
			case Request.PUBLISH:
				// process PUBLish
				break;
			case Request.NOTIFY:
				// process NOTIFY
				break;
			case Request.SUBSCRIBE:
				// process SUBSCRIBE
				break;
			case Request.OPTIONS:
				uaServer.processOptions(requestEvent, serverTransactionID);
				break;
			case Request.PRACK:
				// process PRACK
				break;
			case Request.REFER:
				// process REFER
				break;
			case Request.UPDATE:
				// process UPDATE
				break;
			case Request.REGISTER:
				// process REGISTER
				break;
			default:
				sendTwoZeroTwo();
				break;
		}
		
	}
	
	@Override
	public void processResponse(ResponseEvent responseEvent) {
		
		Response recievedResponse = responseEvent.getResponse();
		ClientTransaction clientTransaction = responseEvent.getClientTransaction();
		CSeqHeader cseq = (CSeqHeader) recievedResponse.getHeader(CSeqHeader.NAME);
		
		
		System.out.println("Received Response:   " + recievedResponse.getStatusCode());
		
		switch(recievedResponse.getStatusCode())
		{
			case Response.OK:
				processOk(recievedResponse, clientTransaction, cseq);
				break;
			case Response.TRYING:
				// process OK
				break;
			case Response.RINGING:
				// process RINING
				break;
			case Response.UNAUTHORIZED:
				// process UNAUTHORIZED if nessecarry
				break;
			case Response.UNSUPPORTED_MEDIA_TYPE:
				// process UNSPPORTED - maybe new call?
				break;
			case Response.MOVED_TEMPORARILY:
				// process MOVED - new call?
				break;
		}
	}

	

	private void sendTwoZeroTwo() {
		// TODO Auto-generated method stub
		
	}

		
	public MaxForwardsHeader createMaxForwardsHeader(int expires){
		try {
			MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(expires);
			return maxForwards;
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<ViaHeader> createViaHeader(){
		try {
			ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
			ViaHeader viaHeader = headerFactory.createViaHeader(this.ipAddress, this.port, this.protocol, null);
			viaHeaders.add(viaHeader);
			return viaHeaders;
		} catch (ParseException | InvalidArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ContentTypeHeader createContentTypeHeader(String application, String property){
		try {
			ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader(application, property);
			return contentTypeHeader;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ContactHeader createContactHeader(){
		try {
			SipURI contactURI = addressFactory.createSipURI(fromName, ipAddress);
			contactURI.setPort(port);
			Address contactAddress = addressFactory.createAddress(contactURI);
			contactAddress.setDisplayName(fromDisplay);
			ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
			return contactHeader;
		} catch (ParseException e) {
			e.printStackTrace();
		}	
		return null;
	}

	public ToHeader createToHeader(String toName, String toIP, String toPort){
		try{
			String x = "";
			if(!(toPort == null)) x = toIP+":"+toPort;  
			else x = toIP;
			SipURI toAddress = addressFactory.createSipURI(toName, x);
			Address toNameAddress = addressFactory.createAddress(toAddress);
			ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);
			return toHeader;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public FromHeader createFromHeader(){
		try {
			SipURI from;
			if(UserProperties.realm.isEmpty()){
				from = addressFactory.createSipURI(this.fromName, this.ipAddress );//+ ":" + this.port);
			} else from = addressFactory.createSipURI(UserProperties.name, UserProperties.realm);
			
			Address fromNameAddress = addressFactory.createAddress(from);
			fromNameAddress.setDisplayName(UserProperties.displayName);
			FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress, "ssssa");	
			
			
			return fromHeader;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	// WHICH CLASS DOES THIS HAVE TO GO?!?!??
	public void processOk(Response response, ClientTransaction tid, CSeqHeader cseq){
		if(cseq.getMethod().equals(Request.INVITE)){	
			try {
				
				
				Dialog dialog = tid.getDialog();
				Request ack = dialog.createAck(cseq.getSeqNumber());
				dialog.sendAck(ack);
				callDialog = dialog;
				//callProvider = dialog
				
				System.out.println("received ok");
				// Read out SDP from received OK - maybe do this after
				SDP sdp = new SDP();
				SdpFactory sdpFactory = SdpFactory.getInstance();
				sdp.inspectSDPAnswer(sdpFactory.createSessionDescription(new String(response.getRawContent())));
				HashMap<String,String> map = sdp.getCallInformations();
				
				RestClient.startGStreamer(map);
				
				map.put("type", "GStreamer");
				map.put("status", "running");
				new WebsocketMessageHandler().sendMessage(map);
			
			} catch (InvalidArgumentException | SipException e) {
				e.printStackTrace();
			} catch (SdpParseException e) {
				e.printStackTrace();
			}
			
		} else if(cseq.getMethod().equals(Request.REGISTER)){
			
			HashMap<String,String> map = new HashMap<String,String>();
			
			if(!(UserProperties.cSeqRegister == 0)){
				map.put("type", "Register");
				map.put("status", "success");
				map.put("realm", UserProperties.realm);
				map.put("cseq", String.valueOf(UserProperties.cSeqRegister));
			}else{
				map.put("type", "Register");
				map.put("status", "stopped");
			}
			new WebsocketMessageHandler().sendMessage(map);
		}
	}

	@Override
	public void processTimeout(TimeoutEvent timeoutEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processIOException(IOExceptionEvent exceptionEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processTransactionTerminated(
			TransactionTerminatedEvent transactionTerminatedEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processDialogTerminated(
			DialogTerminatedEvent dialogTerminatedEvent) {
		// TODO Auto-generated method stub
		
	}
	

}
