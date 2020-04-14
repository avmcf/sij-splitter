package com.vconsulte.sij.splitter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ValidaData  {
	
		static int k = 0;
		
		
		public static void main(String[] args) throws Exception {
			boolean eData = false;
			boolean ehData = false;
			String data = "";
			
			data = obtemData("RECIFE, 5 de marco de 2020.");
			
			ehData = ehDataValida(data);
		//	eData = ehData("30 de Março de 2010");
			k++;
		}

		public static String obtemData(String linha) {
			ArrayList<String> meses = new ArrayList<String>();
			meses.add("janeiro"); 
			meses.add("fevereiro");
			meses.add("marco");
			meses.add("abril");
			meses.add("maio");
			meses.add("junho");
			meses.add("julho");
			meses.add("agosto");
			meses.add("setembro");
			meses.add("outubro");
			meses.add("novembro");
			meses.add("dezembro");
			String linhaData = linha;
			String dataFinal = "";
			String dummy = "";
			int tamanho = 0;
			int var = linhaData.split(" ", -1).length - 1;
			String linhaDecomposta[] = new String[var];

			if(linhaData.charAt(linhaData.length()-1) == '.') {
				linhaData = linhaData.substring(0, linhaData.length()-1);
			}
			linhaDecomposta = linhaData.split(" ");
			if(linhaDecomposta.length >= 6) {
				for(int i=0; i <= linhaDecomposta.length-1; i++) {
					k++;
					if(linhaDecomposta[i].length() <= 2 && ehInteiro(linhaDecomposta[i])) {
						if(linhaDecomposta[i].length() >= 1 && linhaDecomposta[i].length() <= 12) {
							if(dataFinal.isEmpty()) {
								dataFinal = linhaDecomposta[i];
							} else {
								dataFinal = dataFinal + "-" + linhaDecomposta[i];
							}
						}
					}
					if(linhaDecomposta[i].length() == 4 && ehInteiro(linhaDecomposta[i])) {
						dataFinal = dataFinal + "-" + linhaDecomposta[i];
					}	
					if(meses.contains(linhaDecomposta[i])) {
						k++;
						switch(linhaDecomposta[i]) {
						case "janeiro":
							dataFinal = dataFinal + "-" + "01";
							break;
						case "fevereiro":
							dataFinal = dataFinal + "-" + "02";
							break;
						case "marco":
							dataFinal = dataFinal + "-" + "03";
							break;
						case "abril":
							dataFinal = dataFinal + "-" + "04";
							break;
						case "maio":
							dataFinal = dataFinal + "-" + "05";
							break;
						case "junho":
							dataFinal = dataFinal + "-" + "06";
							break;
						case "julho":
							dataFinal = dataFinal + "-" + "07";
							break;
						case "agosto":
							dataFinal = dataFinal + "-" + "08";
							break;
						case "setembro":
							dataFinal = dataFinal + "-" + "09";
							break;
						case "outubro":
							dataFinal = dataFinal + "-" + "10";
							break;
						case "novembro":
							dataFinal = dataFinal + "-" + "11";
							break;
						case "dezembro":
							dataFinal = dataFinal + "-" + "12";
							break;
						}
					}
					k++;
				}
			} else {
				dummy = linhaData.replaceAll("[ABCDEFGHIJKLMNOPQRSTUVXZWYabcdefghijklmnoprstuvxzwy]","").trim();
				dummy = dummy.replaceAll("[/]","-");
			}
			
			return dataFinal;
		}
		
		public static boolean ehInteiro( String s ) {		    
		    char[] c = s.toCharArray();
		    boolean d = true;		    
		    if(s.equals("") || s.equals(" ")){
		    	return false;
		    }		    
		    for ( int i = 0; i < c.length; i++ ){		        
		        if (!Character.isDigit(c[ i ])) {
		            d = false;
		            break;
		        }
		    }
		    return d;
		}
		
		public static boolean ehDataValida(String data) {
	        try {
	            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	            sdf.parse(data);
	            return true;
	        } catch (ParseException ex) {
	            return false;
	        }
	    }
	}
