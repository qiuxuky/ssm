package com.qiuxu.common.util;

import org.apache.shiro.crypto.hash.SimpleHash;

public class PasswordEncoder {
	
	
	public static String encode(String algorithmName,String password) {
		if (password == null || "".equals(password)) {
            return null;
        }
		
		return new SimpleHash(algorithmName,password).toHex();
	}
	
	
	public static String encode(String algorithmName,String password,Object salt,int hashIterations) {
		if (password == null  || "".equals(password)) {
            return null;
        }
		
		return new SimpleHash(algorithmName,password,salt,hashIterations).toHex();
	}


}
