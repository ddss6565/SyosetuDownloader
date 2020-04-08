package com.syosetu.downloader.EzTransXP;

import javax.annotation.PostConstruct;

import com.sun.jna.Native;
import com.sun.jna.WString;

import org.springframework.stereotype.Service;

@Service
public class EzTransXP
{
	public static native boolean J2K_InitializeEx(String A, String B);
	public static native WString J2K_TranslateMMNTW(int A, WString B);
	
	static {
		Native.register("C:/Program Files (x86)/ChangShinSoft/ezTrans XP/J2KEngineH.dll");
	}
	
	@PostConstruct
	public void init()
	{
		J2K_InitializeEx("CSUSER123455", "C:/Program Files (x86)/ChangShinSoft/ezTrans XP/Dat");
	}
}