package org.etechnik.mobileComputing.Group7;

import java.util.ArrayList;
import java.util.Arrays;

public class CodecNegotiation {
	
	public ArrayList<String> codecNegotiation(String payloadTypesOfUser,
			String payloadAttributesOfUser,
			ArrayList<String> wellKnownCodecOfSystem, 
			ArrayList<String> codecOfSystemAttribute,
			ArrayList<String> codecOfSystemPrio){

		
		ArrayList<String> supportedCodecs = new ArrayList<String>();

		String codecs = "";
		int start = payloadTypesOfUser.indexOf(" ");
		int start2 = payloadTypesOfUser.indexOf(" ", start + 1);
		int start3 = payloadTypesOfUser.indexOf(" ", start2 + 1);
		
		payloadTypesOfUser = payloadTypesOfUser.substring(start3+1,(payloadTypesOfUser.length() - 2)) + " ";
		
		ArrayList<String> workOn = new ArrayList<String>(Arrays.asList(payloadTypesOfUser.split(" ")));

		// check for WellKnown Codecs
		for(String s : workOn){
			if(wellKnownCodecOfSystem.contains(s))
				{
					supportedCodecs.add(s);
					//workOn.remove(s);
				} else {

					String x = getCodecnameBasedOnPayloadtype(s, payloadAttributesOfUser);
					// if name != null (not supported wellknown-codec from system)
					if(!(x==null)){
						for(String t : codecOfSystemAttribute)
						{
							if(t.toLowerCase().contains(x.toLowerCase())){
								int y= codecOfSystemAttribute.indexOf(t);
								supportedCodecs.add(codecOfSystemAttribute.get(y-2));
							}
						}		
					} else System.out.println("no codec-name found");
					
				}
			
			
		}
		return supportedCodecs;

	}
	
	private String getCodecnameBasedOnPayloadtype(String payloadType, String payloadAttribute ){
		String y = "rtpmap:" + payloadType+ " ";
		
		int x = payloadAttribute.indexOf("rtpmap:" + payloadType + " ") + y.length();

		// -1 = no entry found
		String codecName = "";
		if(!(x==-1)){
			codecName = payloadAttribute.substring(x, payloadAttribute.indexOf("/", x));
			System.out.println(codecName);
			return codecName + "/";
		}
		
		return null;
	}
}
