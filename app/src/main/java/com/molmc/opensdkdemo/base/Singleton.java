package com.molmc.opensdkdemo.base;

/**
 * features: 单例模板
 * Author：  hhe on 16-8-4 21:02
 * Email：   hhe@molmc.com
 */

public class Singleton {
	private volatile static Singleton singleton;
	private Singleton(){}
	public static Singleton getSingleton(){
		if (singleton==null){
			synchronized (Singleton.class){
				if (singleton==null) {
					singleton=new Singleton();
				}
			}
		}
		return singleton;
	}
}
