package org.etechnik.mobileComputing.Group7;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class IP {

	private static NetworkInterface ni;
	public static String getIP(String system) {
		if(system.equals("linux")){
		
		try {
			ni = NetworkInterface.getByName("eth0");
			Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
			while (inetAddresses.hasMoreElements()) {
				InetAddress ia = inetAddresses.nextElement();
				if (!ia.isLinkLocalAddress()) {
					return ia.getHostAddress().toString();
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		return null;
		}else if(system.equals("windows")){
			try {
				
				return Inet4Address.getLocalHost().getHostAddress().toString();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;

	}
}