package com.alogic.bundle;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.logicbus.backend.server.LogicBusApp;

/**
 * 引导类
 * 
 * @author duanyy
 *
 */
public class Bootstrap extends LogicBusApp {
	public void onInit(Settings settings){		
		String app = settings.GetValue("app", "${server.app}");
	
		String ip = getHostIp();
		
		long mem = 0;
		{
			mem = PropertiesConstants.getLong(settings, "mem", 0);
			if (mem <= 0){
				mem = Runtime.getRuntime().maxMemory() / 1000 / 1000;
			}
		}
		long cores = 0;
		{
			cores = PropertiesConstants.getLong(settings, "vcores", 0);
			if (cores <= 0){
				cores = Runtime.getRuntime().availableProcessors();
			}
		}	
		
		String port = settings.GetValue("port", "${server.port}");
		
		System.setProperty("server.ip", ip);
		System.setProperty("server.port", port);
		System.setProperty("server.mem", String.valueOf(mem));
		System.setProperty("server.app", app);
		System.setProperty("server.cores", String.valueOf(cores));
		
		super.onInit(settings);
	}
	
	
	public void onDestroy(Settings settings){
		super.onDestroy(settings);
	}
	
	public static String getHostIp(){
		// get ip from env : KETTY_IP
		String ip = System.getenv("KETTY_IP");
		if (ip != null && ip.length() > 0){
			return ip;
		}
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostAddress();
		}catch (Exception ex){
			logger.error("Can not get ip from Local Host");
		}
		
		logger.info("Try to get ip from network interface.");
		Enumeration<NetworkInterface> interfaces = null;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();  
		}catch (Exception ex){
			return "127.0.0.1";
		}
        while (interfaces.hasMoreElements()) {  
            NetworkInterface ni = interfaces.nextElement();  
            Enumeration<InetAddress> addrs = ni.getInetAddresses();
            while (addrs.hasMoreElements()){
            	InetAddress inetAddr = addrs.nextElement();
            	
            	if (!inetAddr.getHostAddress().equalsIgnoreCase("127.0.0.1")){
            		return inetAddr.getHostAddress();
            	}
            }
        }
        return "127.0.0.1";
	}	
}
