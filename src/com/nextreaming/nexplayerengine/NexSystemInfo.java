package com.nextreaming.nexplayerengine;

import java.io.IOException;
import java.io.InputStream;
import android.util.Log;

public class NexSystemInfo {
	private static String TAG = "NexSystemInfo";

	private static String getCPUInfoField( String cpuInfo, String field_name ) {	
		String findStr = "\n"+field_name+"\t: ";
		int stringStart = cpuInfo.indexOf(findStr);
		if( stringStart < 0 ) {
			findStr = "\n"+field_name+": ";
			stringStart = cpuInfo.indexOf(findStr);
			if( stringStart < 0 )
				return null;
		}
		int start = stringStart+findStr.length();
		int end = cpuInfo.indexOf("\n", start);
		return cpuInfo.substring(start, end);
	}
	
	// Reads the CPU info file from the current system
	private static String ReadCPUinfo() {
		ProcessBuilder cmd;
		String result = "";

		try {
			String[] args = { "cat", "/proc/cpuinfo" };
			cmd = new ProcessBuilder(args);

			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[1024];
			while (in.read(re) != -1) {
				System.out.println(new String(re));
				result = result + new String(re);
			}
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}    
    
	/** return value of getCPUInfo() for ARMV7  
	 */
	public static final int NEX_SUPPORT_CPU_ARMV7 = 0x7;
	/** return value of getCPUInfo() for ARMV6
	 */
	public static final int NEX_SUPPORT_CPU_ARMV6 = 0x6;
	/** return value of getCPUInfo() for ARMV5
	 */
	public static final int	NEX_SUPPORT_CPU_ARMV5 = 0x5;
	/** return value of getCPUInfo() for ARMV4
	 */
	
	/** return CPU Information
	 */
	public static int getCPUInfo()
	{
		int iCPUInfo = 0;
		String cpuInfo = ReadCPUinfo();
		String cpuArchitecture = getCPUInfoField(cpuInfo,"CPU architecture");
		String cpuFeature = getCPUInfoField(cpuInfo, "Features");
		Log.d(TAG, "cpuArchitecture: " + cpuArchitecture);
		
		Log.d(TAG, "CPU INFO: " + cpuInfo);
		
		boolean bNeon = cpuFeature.contains("neon");
		
		if( cpuArchitecture.startsWith("7")) 
		{
			if(bNeon == true)
			{
		 		iCPUInfo = NEX_SUPPORT_CPU_ARMV7;
		 	}
		 	else
		 	{
		 		iCPUInfo = NEX_SUPPORT_CPU_ARMV6;
		 	}
		}
		else if(cpuArchitecture.startsWith("6"))
		{
		 	iCPUInfo = NEX_SUPPORT_CPU_ARMV6;		
		}
		else 
		{
			iCPUInfo = NEX_SUPPORT_CPU_ARMV5;
		}
		return iCPUInfo;
	}
}
