package com.vconsulte.sij.splitter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class teste  {
	
		static int k = 0;
		
		public static void main(String[] args) throws Exception {
			
		//	verificaSemelhanca("23ª Vara do Trabalho do Recife","23ª Vara do Trabalho de Recife");
		//	verificaSemelhanca("23ª Vara do Trabalho do Recife","23ª Vara do Trabalho do Recife-PE");
		//	verificaSemelhanca("23ª Vara do Trabalho do São Paulo","23ª Vara do Trabalho do Recife");
		//	verificaSemelhanca("23ª Vara do Trabalho do Recife Tortor","Vulputate Tortor");
		//	verificaSemelhanca("Vulputate Tortor","Vulputate Tortor");
		//	verificaSemelhanca("23ª Vara do Trabalho do Recife","23ª Vara do Trabalho do Recife");
		//	verificaSemelhanca("23ª Vara do Trabalho do Recife","vigesima terceira Vara do Trabalho do Recife");
		//	verificaSemelhanca("aaaa","aabaa");
		//	verificaSemelhanca("mario","maria");
		//	verificaSemelhanca("Recife","Alagoas");
			verificaSemelhanca("SA","Vice-Presidência");
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, 5);
			
			
			Date data;
			String descricao = "30-10-2019";
			descricao = descricao.replace("-", "/");
	    //    SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
			SimpleDateFormat formato = new SimpleDateFormat("\"dd 'de' MMM 'de' yyyy\"");
	        data = formato.parse(descricao);
			
			k++;
		}
		
		public static int distance(String a, String b) {
	        a = a.toLowerCase();
	        b = b.toLowerCase();
	        int [] costs = new int [b.length() + 1];
	        for (int j = 0; j < costs.length; j++)
	            costs[j] = j;
	        for (int i = 1; i <= a.length(); i++) {
	            costs[0] = i;
	            int nw = i - 1;
	            for (int j = 1; j <= b.length(); j++) {
	                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
	                nw = costs[j];
	                costs[j] = cj;
	            }
	        }
	        return costs[b.length()];
	    }

		public static boolean verificaSemelhanca(String comparado, String comparador) {
	
			int palavrasIguais = 0;
			int palavrasDiferentes = 0;
			int esp1 = comparado.split(" ", -1).length - 1; 
			int esp2 = comparador.split(" ", -1).length - 1;
			int limite = 0;
			int custo = 0;
			int k = 0;
			String plvrsCmprdo[] = new String[esp1];  
			String plvrsCmprdr[] = new String[esp2];

			plvrsCmprdo = comparado.split(" ");
			plvrsCmprdr = comparador.split(" ");
			if(plvrsCmprdo.length > plvrsCmprdr.length) {
				limite = plvrsCmprdr.length-1;
			} else {
				limite = plvrsCmprdo.length-1;
			}

			for(int x = 0; x <= limite; x++) {

				if(plvrsCmprdo[x].equals(plvrsCmprdr[x])) {
					palavrasIguais++;										
				} else {
					custo = distance(plvrsCmprdo[x],plvrsCmprdr[x]);
					if(custo == 0) {														// iguais
						palavrasIguais++;
					} else if((plvrsCmprdr[x].length()/custo) < 3) {						// diferentes		
						palavrasDiferentes++;
					} else if((plvrsCmprdr[x].length()/custo) >= 3) {						// parecidas	
						palavrasIguais++;
					}
					k++;
				}
			}
			if(palavrasIguais == comparado.length()) {
				return true;
			}
			if((palavrasIguais / comparador.length()) < 3) {
				return true;
			}
			return false;
		}
	}
	
	/*
	 * 		//======================================
			String str1 = "Recife";
			String str2 = "Recife-PE";
			int z = 0;
			custo = distance(str1,str2);
			
			p1 = (double) custo;									
			p2 = (double) str1.length();
			rr = (p2/p1);
			
			p3 = (double) str2.length();
			ss = (p3/p1);
			
			xx = str2.length() - custo;
			
			k = (int) (p3/p1);
			
			k++;
			
			if(custo == 0) {
				System.out.println("iguais (" + str1 + " <-> " + str2 + ")");
			} else if((p3/p1) < 3) {			
				System.out.println("completamente diferentes (" + str1 + " <-> " + str2 + ")");
			} else if((p3/p1) >= 3) {		
				System.out.println("parecidas (" + str1 + " <-> " + str2 + ")");
			}

			k++;
		// =====================================
	 */

	
