package com.leoncio.minhasfinancas.api.resource;

public class AplicacaoUtil {
	
	public static boolean validarString(String string) {
		if (string == null || string.trim().equals("")) {
			return false;
		} 
		return true;
	}

}
