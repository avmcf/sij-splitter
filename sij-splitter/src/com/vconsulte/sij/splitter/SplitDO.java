package com.vconsulte.sij.splitter;

//***************************************************************************************************
// SgjProcDO: Antiga rotina clippingDO atualizada para gravar os editais diretamente no Alfresco 	
// 
// Versão 1.7 	- 23 Março de 2017
//					Correção de falha na finalização geral da rotina
//					Correção na possibilidade de encerramento prematuro da rotina
//					Inclusão de lógica para descartar linhas desnecessárias
//					Quebra de pauta de julgamento
//					Processamento de acórdãos
//
// Versao 1.7.1 	- 29 Março de 2017
//					Vericar exceção ao tipo de quebra de editais (verificaExcecaoQuebra)
//					Limpeza do buffer de introdução quando mudar o assnto
//					Generalização do método validaPadrao e verificaAssunto (atender qualquer região)
//					Nova lógia para validação do "assunto", impedir que uma palavra igual a assunto engane a lógica
//
// Versão 1.8 		- 1º de Abril de 2017
//					Nova logica de controle de leitura de linhas e quebra de editais
//					Testado com os DO do RJ e SP
//
// Versão 1.8.1		- 29 de Abril de 2017
// 					Correção na carga de sessões
//
// Versão 1.8.2 	- 08 de Junho de 2017
//					Inclusão de mais dois padrões de nº de processo
//					Correção para gravação do último edital
//
// versao 1.8.3 	- 01 de Julho de 2017
//					Adcição do tribunal e  nº do edital ao nome do arquivo saida.
//
// versao 1.8.3.7 	- 10 de Julho de 2017
// 					conpilado com versão do Java = 7 para manter compatibilidade com sistemas do PJE.
//
// versao 1.9 		- 17 de Setembro de 2017
//					criação da lista "grupos".
//					nova nomeação do arquivos saida.
//					apagar arquivo intermediario.txt.
//					forçar quebra quando o assunto for "Edital de Notificacao"
//					correção na logica de quebra por nova secao - limpar grupoAnterior e assuntoAnterior
// 					melhorias na logica de separação dos grupos
//					melhorias na logina de quebra por grupo e assunto
//     				inclusão de mais um padrão de nº de processo: "\\w{8}\\s\\w\\W\\s\\w{6}\\W\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}"
//
// versao 1.9.1 	- 06 de Outubro de 2017
//					Pequenas correções
//					Alteração nas leituras do intermedio com unicode UTF-8
//
// versao 1.9.2 	- 30 de Outubro de 2017
//					Gravação do arquivo de saida em UTF-8
//					Correção na quebra por nº de processo
//
// versao 1.9.3 	- 28 de Fevereiro de 2018
//					Inclusão de mais dois padrões de numeração de processos
//					
//	versao 1.9.4 	- 25 de Maio de 2018
//					Evitar carregar a string "PODER JUDICIARIO" na tabela de assuntos
//
//	versao 1.9.5 	- 31 de Maio de 2018
//					Correção de loop infinito na função mapeiaLinhas
//
//	versao 1.9.6 	- 08 de Junho de 2018
//					Correção na funcão "mapeiaLinhas"
//
// ---------------------------------------------------------------------------------------------------------------------------
//	Nova versão da antiga classe clippingDO agora chamada de SplitDO
//
//	versao 2.0 		- .. de Junho de 2018
//					Classe renomeada para SplitDO.java
//					Integração CMIS
//					Gravação dos editais diretamente no servidor Alfresco
//
//	versao 2.1 		- 17 de Dezembro de 2018
//					Inclusão do tratamento do DO do Tribunal Superior do Trabalho - TST
//
//	versao 2.1.1 	- 27 de Dezembro de 2018
//					Correção do método obtemNumeroProcesso
//
//	versao 2.1.2 	- 28 de Janeiro de 2019
//					Correção do método validaPadrao incluindo mais um formato de nº de processo
//
//	versao 2.1.3 	- 06 de Fevereiro de 2019
//					Incluindo mais um formato de nº de processo
//					Correção do metodp carregaAssuntos para excluir a string "PODER JUDICIÀRIO"
//
//	versao 2.2 		- 26 de Fevereiro de 2019
//					Implementação de novo modelo de dados
//					reformulação do metodo obtemNumeroProcesso
//					reformulação do metodo mapeiaLinha para não utilizar o metodo validaPadrao
//
//	versao 2.2.3 	- 27 de Fevereiro de 2019
//					Correção na quebra de editais por nº de processo
//
//	versao 2.2.4 	- 13 de Março de 2019
//					Correção no metodo "mapearLinhas" para tratar qdo o grupo for igual a "Portaria"
//
//	versao 2.2.5 	- 24 de Março de 2019
//					Correção de erros da versão 2.2.4
//
//	versao 2.3.0 	- 16 de Abril de 2019
//					- Nova logica para determinar quebra por assunto
//					- inclusão do método validaAssunto
//					- inclusão do método contarPalavrasAssunto
//
//	versao 2.3.20 	- 27 de Abril de 2020
//					- Novo algorítimo de quebra de editais
//					
//	
//	versao 2.3.21 	- 04 de Maio 2020
//					- Correções na justificação do texto
//					- Diário oficial convertido para a memoria
//					- Novo método carregaIndice
//					
//	versao 2.3.21a	- 10 de Maio 2020
//					- Ajustes no método formataaParagrafo
//					- Atualização na tabela termosChaves
//					- Atualização na tabela falsoFinal
//
//
//	versao 2.3.21b	- 06 de Junho 2020
//					- Parametrização 
//					- Classe de MetodosComuns.java
//					- Correção na quebra das publicações com assunto Pauta
//									
//	versao 2.3.21c	- 17 de Junho 2020			
//					- Correção na quebra das publicações com assunto Pauta
//					- Ajustes no método trataIntimados
//					
//	versao 2.3.21d	- 18 de Junho 2020v2.3.21d		
//					- versão provivisoria sem o controle de nº de página
//
//					
//
//
// 	V&C Consultoria Ltda.
// 	Autor: Arlindo Viana.
//***************************************************************************************************

	import java.io.BufferedReader;
	import java.io.BufferedWriter;
	import java.io.File;
	import java.io.FileInputStream;
	import java.io.FileOutputStream;
	import java.io.FileWriter;
	import java.io.IOException;
	import java.io.InputStreamReader;
	import java.io.OutputStreamWriter;
	import java.io.PrintWriter;
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	import java.util.ArrayList;
	import java.util.Calendar;
	import java.util.Collection;
	import java.util.Date;
	import java.util.List;
	import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.StandardCharsets;
	import java.nio.file.*;
	import javax.swing.JFileChooser;
	import javax.swing.JOptionPane;

	import org.apache.chemistry.opencmis.client.api.Folder;
	import org.apache.chemistry.opencmis.client.api.Session;
	
	import org.apache.pdfbox.pdmodel.PDDocument;
	import org.apache.pdfbox.text.PDFTextStripper;

	import com.vconsulte.sij.splitter.IndiceEdicao;
	import com.vconsulte.sij.base.*;
	
public class SplitDO  {

	static File diarioInput;
	static File editaisDir;
	static File assuntos;
	static File config;
	static File diretorio;
	static File intermedio;
	
	static Path pathEdicao;
	static Date edicao;
	
	static Folder editalFolder;
	
	static FileWriter arquivoLog;
	static FileWriter arqSaida;
	static FileWriter fileW;
	static BufferedWriter buffW;

	// parametros de configuração
	static String usuario = com.vconsulte.sij.base.Parametros.USUARIO;
	static String password = com.vconsulte.sij.base.Parametros.PASSWORD;
	static String pastaBase = com.vconsulte.sij.base.Parametros.PASTABASE;
	static String tipoDocumento = com.vconsulte.sij.base.Parametros.TIPODOCUMENTO;
	static String versaoSplitter = com.vconsulte.sij.base.Parametros.VERSAOSPLITER;

	static List<com.vconsulte.sij.splitter.IndiceEdicao> Index = new ArrayList<com.vconsulte.sij.splitter.IndiceEdicao>();
	static com.vconsulte.sij.splitter.IndiceEdicao Indice = new com.vconsulte.sij.splitter.IndiceEdicao(null, 0, 0, null, null, 0,  0, 0, 0);

	static List <String> tabelaAssuntos = new ArrayList<String>();
	static List <String> assuntosUtilizados = new ArrayList<String>();
	static List <String> continuacoesPossiveis = new ArrayList<String>();
	static List <String> bufferEntrada = new ArrayList<String>();
	static List <String> tabelaAtores = com.vconsulte.sij.base.Parametros.TABELAUTORES;
	static List <String> juridiques = com.vconsulte.sij.base.Parametros.JURIDIQUES;
	static List <String> falsoFinal = com.vconsulte.sij.base.Parametros.FALSOFINAL;
	static List <String> stopWords = com.vconsulte.sij.base.Parametros.STOPWORDS;
	static List <String> keyWords = com.vconsulte.sij.base.Parametros.KEYWORDS;
	static List <String> funcoes = com.vconsulte.sij.base.Parametros.FUNCOES;
	static List <String> continuadores = com.vconsulte.sij.base.Parametros.CONTINUADORES;
	static List <String> meses = com.vconsulte.sij.base.Parametros.MESES;

	static Collection<String> bufferSaida = new ArrayList<String>();			// estar sem uso?
	
	static ArrayList<String> textoEdital = new ArrayList<String>();
	static ArrayList<String> textoSaida = new ArrayList<String>();
	static ArrayList<String> textoIntroducao = new ArrayList<String>();
	static ArrayList<String> edital = new ArrayList<String>();
	static ArrayList<String> introducao = new ArrayList<String>();
	static ArrayList<String> paragrafos = new ArrayList<String>();
	
	static ArrayList<String> padraoGrupo = new ArrayList<String>();

    static String editalTexto = "";
	static String textoTeste = "";	
	static String tribunal = "";
	static String strTribunal = ""; 
	static String titulo1 = "";
	static String titulo2 = "";
	static String titulo3 = "";
	static String titulo4 = "";
	static String titulo5 = "";
	static String rodape = "";
	static String dataEdicao = "";
	static String complementoDescricao = "";
	static String descricaoFolder = "";
	static String seqEdicao;
	static String strEdicao;
	static String secao = "";
	static String grupo = "";
	static String seqPublicacao = "";
	static String assunto = "";
	static String complementoAssunto = "";
	static String linhaMensagem = "";
	static String primeiraLinha = "";
	static String edtFolderName = "";
	static String secaoAnterior = "";
	static String linhaFormatada = "";
	static String linhaParagrafo = "";
	static String atores = "";
	static String intimados = "";
	static String linhaAnterior = "";
	static String novoProcesso = "";
	static String processoDummy = "";
	static String linhaPauta = "";
	static String processoLinhaPauta = "";
	static String linhaAntesAssunto = "";
	static String ordemDePauta = "";
	
	static String linhaPosProcesso = "";
	static String processoLinha = "";
	static String processoAnterior = "";
	static String processoNumero = "";

	static String cliente;
	static String tipoSaida;
	static String sysOp;
	static String url;
	static String logFolder;

	static int paginaAtual = 1;
	static int pagina = 1;
	static int sequencialSaida = 1;
	static int qtdPublicacoes = 0;
	static int maiorAssunto = 0;
	static int qtdPaginasDO = 0;
	static int sequencial = 0;	
	static int limiteGrupo = 0;
	static int sequencialSecao = 0;
	static int sequencialGrupo = 0;
	static int sequencialAssunto = 0;
	static int sequencialProcesso = 0;
	static int sequencialIndice = 0;
	static int linhaSumario = 0;
	static int indiceContador = 0;
	static int ultimaLinha = 0;
	static int ultimaPagina = 0;
	static int seqIndex = 0;
	static int sequencialSumario = 0;
	static int tamanhoLinha = 120;
	static int tamanhoLinhaAcumulado = 0;
	static int paginaGrupo = 0;
	static int paginaSecao = 0;
	
	static boolean salvarIntroducao = false;
	static boolean limparIntro = true;
	static boolean mudouAssunto = false;
	static boolean saida = false;
	static boolean encontrouIndice;
	static boolean atoresOK = false;
	static boolean intimadosOK = false;
	static boolean pauta = false;
	static boolean dtValida = false;
	static boolean grupoSemAssunto = true;
	static boolean ttt = true;
	
	static MsgWindow msgWindow = new MsgWindow();
	static InterfaceServidor conexao = new InterfaceServidor();
	
	static Session sessao;
	static SalvaPdf salvaPdf = new SalvaPdf();
	static GravaXml gravaXml = new GravaXml();
	static Edital Edital = new Edital();
	static Base base = new Edital();

	static int k = 0;								// para testes
	static int kk = 100;
		
	@SuppressWarnings("unlikely-arg-type")
	public static void main(String[] args) throws Exception {

		String strDummy = "";
		String dummy = "";
		String linha = "";
		String linhaDummy = "";
		String palavrasDaLinha[];
		
		int intDummy = 0;
		
		boolean salvarLinha = false;
		boolean primeiroEdital = true;
		boolean quebrouAssunto = false;
		boolean quebrouProcesso = false;
		boolean ignora = false;
		
		char ponto = ' ';

		inicializaArquivos();
		msgWindow.montaJanela();
		try {
		
	//gravaIntermedio(diarioInput);							// só pra teste não apagar																//só pra teste não apagar
			
			com.vconsulte.sij.base.Configuracao.carregaConfig();
			cliente = com.vconsulte.sij.base.Parametros.CLIENTE;
			tipoSaida = com.vconsulte.sij.base.Parametros.TIPOSAIDA;
			sysOp = com.vconsulte.sij.base.Parametros.SYSOP;
			url = com.vconsulte.sij.base.Parametros.URL;
			logFolder = com.vconsulte.sij.base.Parametros.LOGFOLDER;
			
			com.vconsulte.sij.base.Parametros.carregaTabelas();
			
			carregaDiario(diarioInput);
			
	//lista();														// somente para teste
			
			msgWindow.incluiLinha(obtemHrAtual() + " - Preparação para leitura");
			carregaIndice();
			mapeiaLinhas();
			carregaAssuntos();
		
			if (!conectaServidor()) { 
				msgWindow.incluiLinha(obtemHrAtual() +" - Falha na conexão com o Servidor");
				finalizaProcesso();
			}
		
	        if (bufferEntrada.get(0).contains("Caderno Judiciário")){
	        	gravaLog(obtemHrAtual() + " inicio do processamento");
	        	dummy = "";
	        	tribunal = obtemTribunal(bufferEntrada.get(0));		    	
		    	strTribunal = (completaEsquerda(obtemTribunal(bufferEntrada.get(0)), '0', 2));
		    	titulo1 = bufferEntrada.get(0).trim(); 
			    titulo2 = bufferEntrada.get(1).trim();
		        titulo3 = bufferEntrada.get(2).trim();			        
		        dummy = obtemEdicao(bufferEntrada.get(3));
		        strEdicao = dummy + "T00:00:00.000-03:00";
		        titulo4 = bufferEntrada.get(3).trim();
		        seqEdicao = bufferEntrada.get(3).trim().substring(2, 6);
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		        sdf.setLenient(false);
		        edicao = sdf.parse(dummy);
	        
		        titulo5 = bufferEntrada.get(4).trim();
				if (strTribunal.equals("00")) {
					msgWindow.incluiLinha(obtemHrAtual() + " - Início do processamento:" + "TST -"+" Edição: " + dataEdicao);
					edtFolderName = "TST - " + seqEdicao;
					descricaoFolder = "TST - Edição:" + dataEdicao;
				} else {
					msgWindow.incluiLinha(obtemHrAtual() + " - Início do processamento: " + "TRT "+strTribunal+"ª RG "+" Edição: " + dataEdicao);
					edtFolderName = "TRT-" + strTribunal + "-" + seqEdicao;
					descricaoFolder = "TRT " + strTribunal + "ª Região" + " - " + dataEdicao;
				}
	        } else {
	        	msgWindow.incluiLinha(obtemHrAtual() +" - Arquivo com Diário Oficial não reconhecido");
				finalizaProcesso();
	        }
	        
	        edtFolderName = "TRT " + strTribunal + " " + dataEdicao.replaceAll("[/]","-");
	        diretorio = new File(edtFolderName);
	        criarPastaSaida(diretorio);
	        File dir1 = new File (".");
	        abreLog(dir1 + "/" + strTribunal+seqEdicao + ".log");
	        gravaLog(obtemHrAtual() + " ini -> " + tribunal + " - " + descricaoFolder);

	       // for(int x=0; x <= Indice.linhaSecao; x++) {
	       // 	if(primeiraPalavra(bufferEntrada.get(x)).matches("\\d{4}\\W\\d{4}")){ 
		//			pagina = obtemPagina(bufferEntrada.get(x));
		//			break;
		//		}
	      //  }
	      //  if(intDummy != 0) {
        //		pagina = intDummy ;
       // 	}

	        for (IndiceEdicao Indice : Index) {					// Loop de indice (percorre o indice do documento)		        	

	        	// NOVA SESSÃO e GRUPO ---------------------------------------------------------------------------------
	        	if(!primeiroEdital && (textoEdital.size() > 0 || paragrafos.size() > 0)) {
	        		
	        	//	if(textoEdital.size() > 0) {
	        	//		textoEdital.add(linha);
	        	//	} else {
	        	//		paragrafos.add(linhaParagrafo);
	        	//	}
	        		
					seqPublicacao = completaEsquerda(Integer.toString(sequencialSaida), '0', 4);
    				edital = formataEdital(textoEdital);
    				fechaEdital((ArrayList<String>) edital);
				}
	        	
        		secao = bufferEntrada.get(Indice.linhaSecao).trim();
        		sequencialSecao = Indice.linhaSecao;
        		paginaSecao = Indice.paginaSecao;
        		gravaLog(obtemHrAtual() + " sca -> " + sequencial + " - nova secao: " + secao);
        		msgWindow.incluiLinha(obtemHrAtual() + " ------------------------------------------------");
        		msgWindow.incluiLinha(obtemHrAtual() + " - Local: " + secao + " - pg: " + Indice.paginaSecao + " / " + ultimaPagina);
        		if(Indice.complementoSecao != null && !Indice.complementoSecao.equals("complemento")) {
					secao = secao + " " + Indice.complementoSecao;
				}
        		padraoGrupo.clear();
        		grupo = bufferEntrada.get(Indice.linhaGrupo).trim();
        		paginaGrupo = Indice.paginaGrupo;
        		padraoGrupo.add("grupo");
        		sequencial = Indice.linhaGrupo + 1;
        		sequencialGrupo = Indice.linhaGrupo;
        		textoIntroducao.clear();
        		textoEdital.clear();        		
        		assunto = "";
        		processoLinha = "";
        		atores = "";
        		intimados = "";
        		salvarIntroducao = false;
        		linhaPauta = "";
        		ordemDePauta = "";
        		linhaAntesAssunto = "";
        		linha = "";
        		if(grupo.equals("Pauta")) {
        			linhaPauta = "";
        			processoLinhaPauta = "";
        			pauta = true;
        		}
        		
        		linhaDummy = carregaLinha(sequencial,false);
        		if((obtemNumeroProcesso(linhaDummy) != null)){
        			grupoSemAssunto = true;
        		} else if(validaAssunto(linhaDummy)) {
        			grupoSemAssunto = false;
	        	}
        		
        		msgWindow.incluiLinha(obtemHrAtual() + " - Grupo: " + grupo);
        		gravaLog(obtemHrAtual() + " grp -> " + sequencial + " - novo grupo: " + grupo + " - pg: " + Indice.paginaSecao + " / " + ultimaPagina);
        		limiteGrupo = localizaProximoGrupo(sequencial);

    			assunto = "";
    			quebrouAssunto = false; 
        		indiceContador++;
    			inicializaEdital();

	        	// Início do loop de linhas de assunto corpo  -------------------------------------------------------------------	   
    			while((sequencial < limiteGrupo)) {
    				linhaDummy = "";
	        		salvarLinha = true;
	        		linhaAnterior = linha;
	        		linha = carregaLinha(sequencial,true);
	        	/*
	        		for(int x=0; x <= Indice.linhaSecao; x++) {
	    	        	if(primeiraPalavra(linha).matches("\\d{4}\\W\\d{4}")){ 
	    					pagina = obtemPagina(bufferEntrada.get(x));
	    					break;
	    				}
	    	        }
	    	        if(intDummy != 0) {
	            		pagina = intDummy ;
	            	}
	        	*/
	        		if(verificaSeLinhaTemNumProcesso(linha)) {
	        			processoDummy = obtemNumeroProcesso(linha);
	        		} else {
	        			processoDummy = null;
	        		}
	        		//int var = linhaDummy.trim().split(" ", -1).length - 1;
	        		//String palavras[] = new String[var];                		
	        		//palavras = linhaDummy.split(" ");
	        		
	        		//intDummy = linha.trim().split(" ", -1).length - 1;               		
	        		//palavrasDaLinha = linha.split(" ");
	        		
	        		linhaDummy = formataPalavra(linha.replaceAll("[:0123456789]",""));
	        		linhaDummy = linhaDummy.trim();
	        		
	        		//System.out.println(sequencial + " - " + linha);
	        		
	        		if(sequencial >= 35800) {	// 
	        			k++;
	        		}
	        		
	        		if(sequencial >= 35838) {	// 
	        			k++;
	        		}
	        		
	        		if(sequencial >= 35846) {	// 
	        			k++;	
	        		}
	        		
	        		if(sequencial >= 35857 ) { 	// 
	        			k++;
	        		}
        		
	        		if(sequencial >= 35865) {  	//
	        			k++;					 
	        		}
	        		
	        		if(sequencial >= 35874) {
	        			k++;
	        		}
	        		
	        		if(sequencial >= 35882) {
	        			k++;
	        		}
	        		
	        		if(sequencial >= 35897) {
	        			k++;
	        		}
	        		
	        		if(sequencial >= 35906) {
	        			k++;
	        		}
	        		
	        		if(sequencial >= 35915) {
	        			k++;
	        		}
	        		
	        		if(sequencial >= 35924) {
	        			k++;
	        		}
	        		
	        		if(sequencial >= 36936) {
	        			k++;
	        		}
	        		
	        		if(sequencial >= 35944) {
	        			k++;
	        		}
	        		
	        		if(sequencial >= 35954) {
	        			k++;
	        		}	        		
	       			
	        		if(sequencial >= 15979) {
	        			k++;
	        		}
	        			
	        		if(sequencial >= 16001) {
	        			k++;
	        		}
	           		
	        		if(sequencial >=16017) {
	        			k++;
	        		}
	   /*  		
	        		if(sequencial >= 3000) {
	        			k++;
	        		}
	        		
	        		if(sequencial >= 36753) {
	        			k++;
	        		}
		
	        		if(sequencial >= 36786) {		
	        			k++;
	        		}
	        		
	        		if(sequencial >= 36812) {
	        			k++;
	        		}
	     	
	        		if(sequencial >= 36831) {
	        			k++;
	        		}
	       */
	        		if(sequencial >= sequencialSumario) {
		        		for (IndiceEdicao Ix1 : Index) {
		        			if(sequencial == Ix1.indexSecao || sequencial == Ix1.indexGrupo) {
		        				ignora = true;
		        				break;
		        			}
		        		}
	        		}
	        		if(ignora) {
	        			ignora = false;
	        			continue;
	        		}
	        		
	        		if(linha.equals("*** MARCA FIM ***")) {
	        			seqPublicacao = completaEsquerda(Integer.toString(sequencialSaida), '0', 4);
        				edital = formataEdital(textoEdital);
        				fechaEdital((ArrayList<String>) edital);
	        			break;
	        		}
	        	//	dtValida = verificaDataValida(linha);
	        		
	        	//	if(textoEdital.isEmpty()) {
	        	//		gravaLog(obtemHrAtual() + ".................... INICIO DA PUBLICAÇÃO .........................");
	        	//	}

	        		//gravaLog(obtemHrAtual() + "\t\t\t" + "--- >> " + sequencial + " - " + linha );
	        		
	        		//System.out.println(sequencial + " - " + linha);

					/*	
					 * Quebra por Assunto
					 * 
					 */

	        		// hoje if((tabelaAssuntos.contains(formataPalavra(primeiraPalavra(linha))) && !grupoSemAssunto) || (linhaDummy.equals("ordem"))) {
	        		if((validaAssunto(linha) && !grupoSemAssunto)) {
	        			if((quebraAssunto(sequencial-1,limiteGrupo) && !salvarIntroducao) || (linhaDummy.equals("ordem") && salvarIntroducao)) {						
	        				
	        				if(!padraoGrupo.contains("assunto")) {
								padraoGrupo.add("assunto");
							}
	        				
	        				if((!primeiroEdital && (textoEdital.size() > 0 || paragrafos.size() > 0) || textoIntroducao.size() > 0) &&
	        						!processoLinha.isEmpty()){
								seqPublicacao = completaEsquerda(Integer.toString(sequencialSaida), '0', 4);
		        				edital = formataEdital(textoEdital);
		        				fechaEdital((ArrayList<String>) edital);	
		        				ordemDePauta = "";
		        				linhaAntesAssunto = "";
							} else {
								primeiroEdital = false;
							}
							
							if(linhaDummy.equals("ordem")) {
								quebrouAssunto = false; 
								ordemDePauta = linha;
							} else {
								quebrouAssunto = true;
								assunto = linha;
							}
						
							sequencialAssunto = sequencial-1;
							linhaPauta = "";
							//gravaLog(obtemHrAtual() + " ass -> " + sequencial + " - " + assunto + " - arquivo --> " + sequencialSaida);

							//if(formataPalavra(assunto).equals("pauta de julgamento")) {
							//	pauta = true;
							//	textoIntroducao.clear();
							//} else {
							//	pauta = false;
							//}
							
							if(processoDummy != null) {
								if(!textoIntroducao.isEmpty() && salvarIntroducao) {
									textoIntroducao.clear();
									salvarIntroducao = false;
								}
							} else {
								if(textoIntroducao.isEmpty()) {
									salvarIntroducao = true;
									linhaPauta = "";
								}
							}
							continue;
	        			}
					} else { 
						if (sequencial-2 == sequencialGrupo && grupo.equals("Pauta")) {
							linhaAntesAssunto = linha;
							continue;
						}
					}

					/*
					 * Tratamento de introdução do Edital quando houve
					 * (introdução é um bloco de texto comum a vários editais de um mesmo assunto
					 * 
					 */
					if(salvarIntroducao){
						
						if(!padraoGrupo.contains("introducao")) {
							padraoGrupo.add("introducao");
						}
						
						if(linhaDummy.equals("ordem")) {
							ordemDePauta = linha;
							continue;
						}
						
						if(processoDummy != null){							
							salvarIntroducao = false;
						} else {								
							textoIntroducao.add(linha);
							//gravaLog(obtemHrAtual() + " itr -> " + sequencial + linha);
							continue;
						}
					}

					/*
					 * Quebra por Nº PROCESSO
					 */
					if(processoDummy != null) {
						if(!quebrouAssunto || processoLinha.isEmpty()) {
							if(quebraProcesso(sequencial-1)) {
								
								if(!padraoGrupo.contains("processo")) {
									padraoGrupo.add("processo");
								}
								
								novoProcesso = linha;
								if(!pauta) {					  				// se ñ é pauta a quebra é por assunto      								
									if(!primeiroEdital && (textoEdital.size() > 0 || paragrafos.size() > 0)) {
										seqPublicacao = completaEsquerda(Integer.toString(sequencialSaida), '0', 4);
				        				edital = formataEdital(textoEdital);
				        				fechaEdital((ArrayList<String>) edital);
									}						
								} else {										// se assunto = pauta a quebra é por nº processo
									if(!atores.isEmpty() || !intimados.isEmpty()){
										seqPublicacao = completaEsquerda(Integer.toString(sequencialSaida), '0', 4);
				        				edital = formataEdital(textoEdital);
				        				fechaEdital((ArrayList<String>) edital);
									}
								}
								if(primeiroEdital) {
									primeiroEdital = false;
								}
								salvarLinha = false;
								
								processoAnterior = processoLinha;
								processoLinha = linha;
								processoNumero = processoDummy;
								sequencialProcesso = sequencial-1;

							} else {
								salvarLinha = true;
							}
						} else {
							salvarLinha = true;
						}

						quebrouAssunto = false;
						if(!salvarLinha) {
							continue;
						}
						//gravaLog(obtemHrAtual() + " prc -> " + sequencial + " - " + processoLinha + " - arquivo --> " + sequencialSaida);
					}
					
			/*	Analisar por que fez isso, pois assim nunca carregará atores e intimados
					if(!atoresOK && !processoLinha.isEmpty()){
						dummy = formataPalavra(primeiraPalavra(linha));
						dummy = dummy.replaceAll(":", "");
						if(!tabelaAtores.contains(dummy)) {
							linhaPauta = linha;
							continue;
						}
					}
			*/
			/*	hoje
					if(!atoresOK && !processoLinha.isEmpty()){
						atores = trataAtores(linha);
						if(atores != "") {
							sequencial--;
							intimados = trataIntimados(carregaLinha(sequencial,true));
		        			atoresOK = true;
							continue;
						}
					}
			*/
					if(!atoresOK && !processoLinha.isEmpty()){
						if(validaAtor(linha)) {
							atores = trataAtores(linha);
							atoresOK = true;
							
							if(!padraoGrupo.contains("atores")) {
								padraoGrupo.add("atores");
								k++;
							}
							
						} else {
							linhaPosProcesso = linha;
						}
						continue;
					}
					
					if(!intimadosOK && !processoLinha.isEmpty()) {
						intimados = trataIntimados(linha);
	        			intimadosOK = true;
	        			
	        			if(!padraoGrupo.contains("intimados")) {
							padraoGrupo.add("intimados");
							k++;
						}
	        			
						continue;
					}
					
					/*
					 * Guarda linha do texto do Edital
					 */	
					
					if(textoEdital.size() == 0) {
        				primeiraLinha = linha;
        			}     
        			if(salvarLinha) {
        				if(!padraoGrupo.contains("texto")) {
							padraoGrupo.add("texto");
							k++;
						}
        				textoEdital.add(linha);
        			}
	        		salvarLinha = true;
	        		strDummy = "";	
	        		//	//gravaLog(obtemHrAtual() + " .................. FIM DA PUBLICAÇÃO ..................");

	        		if(indiceContador == Index.size()-1) {
	        			limiteGrupo = ultimaLinha;		// forçar até o final do bufferEntrada
	        		}

/*	Trecho em desenvolvimento, sobre a formatação do texto dividindo em paragrafos 
        			if(salvarLinha) {
        				if(tipoSaida.equals("DIRETA")) {
        					salvaLinha(linha);						// salva linhas para saida em texto
        				} else {
        					if(textoEdital.size() == 0) {
                				primeiraLinha = linha;
                			} else {
                				textoEdital.add(linha);				// salva linhas para saida em pdf
                			}
        				}
        			}
	        		salvarLinha = true;
	        		strDummy = "";	
	        		//	//gravaLog(obtemHrAtual() + " .................. FIM DA PUBLICAÇÃO ..................");

	        		if(indiceContador == Index.size()-1) {
	        			limiteGrupo = ultimaLinha;		// forçar até o final do bufferEntrada
	        		}
*/
	        		
	        		
	        		
	        	} 	// fim do WHILE de linhas ------------------------------------------------------------------------------
	        }	// fim do FOR do Indice
        if(textoEdital.size() > 0 || paragrafos.size() >0){       	
	        seqPublicacao = completaEsquerda(Integer.toString(sequencialSaida), '0', 4);
			edital = formataEdital(textoEdital);
			fechaEdital((ArrayList<String>) edital);
			edital.clear();
			textoEdital.clear();
        }
	    
		int i = 0;
		String outSubs;
		assuntos.delete();
		outSubs = assuntos.getParent() + "/subjects.txt";
		FileWriter subjAtualizado = new FileWriter(outSubs);
		PrintWriter outReg = new PrintWriter(subjAtualizado);		
		while(i <= tabelaAssuntos.size()-1){
			if(tabelaAssuntos.get(i) != null || !tabelaAssuntos.get(i).contains("PODER JUDICIÁRIO") || tabelaAssuntos.get(i).length() != 16){
				outReg.printf("%s\n",tabelaAssuntos.get(i));
				i++;
			}			
		}
		subjAtualizado.close();
    	finalizaProcesso();
		}	 // 002 - Fim do try 

      catch (IOException e) {
          JOptionPane.showMessageDialog(null, "Erro no processamento do intermedio: " + e);
      }																						
		msgWindow.incluiLinha("-------------------");	
      System.exit(0);
	
	}	// final do metodo main
	
	private static boolean verificaMaiuscula(String linhaDummy) {
		String dummy = linhaDummy.replaceAll("[:.-]","");
		int minusculos = 0;
		int maiusculos = 0;
		double percentual = 0.0;
		dummy = dummy.replaceAll("[ÁÀÃÂÄÅ]","A");
		dummy = dummy.replaceAll("[ÉÈÊË]","E");
		dummy = dummy.replaceAll("[ÍÌÎÏ]","I");
		dummy = dummy.replaceAll("[ÓÒÔÖÕ]","O");
		dummy = dummy.replaceAll("[ÚÙÛÜ]","U");
		dummy = dummy.replaceAll("[Ç]","C");
		dummy = dummy.replaceAll("[()]","");
		dummy = dummy.replaceAll("[' ']","");
		dummy = dummy.replaceAll("[ªº]","");
		dummy = dummy.replaceAll("[0123456789]","");
		for(int x = 0; x <= dummy.length()-1; x++) {

			if ((dummy.charAt(x) >= 'a' && dummy.charAt(x) <= 'z') ||
					dummy.charAt(x) == 'ã' || dummy.charAt(x) == 'á' ||dummy.charAt(x) == 'à' || dummy.charAt(x) == 'â' || 
					dummy.charAt(x) == 'é' || dummy.charAt(x) == 'è' || dummy.charAt(x) == 'ê' ||
					dummy.charAt(x) == 'í' || dummy.charAt(x) == 'ì' || dummy.charAt(x) == 'î' ||
					dummy.charAt(x) == 'ó' || dummy.charAt(x) == 'ò' || dummy.charAt(x) == 'ô' || dummy.charAt(x) == 'õ' ||
					dummy.charAt(x) == 'ú' || dummy.charAt(x) == 'ù' || dummy.charAt(x) == 'û'){
				minusculos++;
			}
		}
		if(minusculos > 0) {
			percentual = (float) (minusculos*100/dummy.length());
		} else {
			return true;
		}
		
		if(percentual >= 5.0) {
			return false;
		} else {
			return true;
		}
		
	}
	
	private static boolean ehMaiuscula(String linhaDummy) {
		String dummy = linhaDummy.replaceAll("[:.]","");
		dummy = dummy.replaceAll("[AÁÀÃÂÄÅ]","A");
		dummy = dummy.replaceAll("[EÉÈÊË]","E");
		dummy = dummy.replaceAll("[IÍÌÎÏ]","I");
		dummy = dummy.replaceAll("[OÓÒÔÖÕ]","O");
		dummy = dummy.replaceAll("[UÚÙÛÜ]","U");
		dummy = dummy.replaceAll("[()]","");
		dummy = dummy.replaceAll("[' ']","");
		dummy = dummy.replaceAll("[ªº]","");
		for(int x = 0; x <= dummy.length()-1; x++) {
			if ((dummy.charAt(x) >= 'A' && dummy.charAt(x) <= 'Z') ||
					(dummy.charAt(x) >= '0' && dummy.charAt(x) <= '9')){
				continue;
			} else {
				return false;
			}
		}
		return true;
	}
	
	private static boolean temContinuacao(String linhaDummy) {
		int var = linhaDummy.trim().split(" ", -1).length - 1;
		String palavras[] = new String[var];                		
		palavras = linhaDummy.split(" ");
		linhaDummy = formataPalavra(linhaDummy);
		if(continuadores.contains(palavras[palavras.length-1])) {
			return true;
		}
		return false;
	}
	
	public static String separaAtores(String linhaDummy) {
		
		int var = linhaDummy.trim().split(" ", -1).length - 1;
		String atoresSeparados = "";
		String linhaDeAtores = "";
		String var2[] = new String[var];                		
		var2 = linhaDummy.split(" ");
		for (int x = 0; x <= var2.length-1; x++) {
			if(var2[x].charAt(var2[x].length()-1) == ';' || var2[x].charAt(var2[x].length()-1) == ',' ){
				if(atoresSeparados.isEmpty()) {
					atoresSeparados = var2[x] + "\n";
				} else {
					atoresSeparados = atoresSeparados + " " + var2[x] + "\n";
				}
				linhaDeAtores = "";
			} else {
				if(linhaDeAtores.isEmpty()) {
					linhaDeAtores = var2[x];
				} else  {
					linhaDeAtores = linhaDeAtores + " " + var2[x];
				}
			}
		}
		return atoresSeparados;
	}

	private static void salvaLinha(String linhaDummy) {
		String nada = "";
		String dummy = obtemNumeroProcesso(linhaDummy);
		String linhaFormatada = formataPalavra(linhaDummy);
		char ponto = linhaDummy.charAt(linhaDummy.length()-1);
		boolean incioMaiuscula = false;
		
		int var = linhaDummy.trim().split(" ", -1).length - 1;
		String palavras[] = new String[var];
		String palavra = "";
		palavras = linhaDummy.split(" ");

		if(linhaDummy.charAt(0) >= 'A' && linhaDummy.charAt(0) <= 'Z') {
			incioMaiuscula = true;
		}
		
		 if(juridiques.contains(formataPalavra(linhaDummy))) {
			 k++;
		 }
							
		if(obtemNumeroProcesso(linhaDummy) != null){							// verifica se linha tem nº processo
			if(!linhaParagrafo.isEmpty()) {
				paragrafos.add(linhaParagrafo);
				paragrafos.add(linhaDummy + "\n");
				linhaParagrafo = "";
				tamanhoLinhaAcumulado = 0;
			} else {
				paragrafos.add(linhaDummy + "\n");
			}
		} else if(verificaDataFinal(linhaDummy, sequencial)) {					// Linha com data Valida
			if(!linhaParagrafo.isEmpty()) {
				paragrafos.add(linhaParagrafo + " " + linhaDummy + "\n");
				linhaParagrafo = "";
				tamanhoLinhaAcumulado = 0;
			} 
			paragrafos.add(linhaDummy + "\n");
		} else if(juridiques.contains(formataPalavra(linhaDummy))) {			// Linha com juridiques
			if(!linhaParagrafo.isEmpty()) {
				paragrafos.add(linhaParagrafo + " " + linhaDummy + "\n");
				linhaParagrafo = "";
				tamanhoLinhaAcumulado = 0;
			} 
			paragrafos.add(linhaDummy + "\n");
		} else if(funcoes.contains(linhaFormatada)) {							// Linha com função 
			if(!linhaParagrafo.isEmpty()) {
				paragrafos.add(linhaParagrafo + " " + linhaDummy);
				linhaParagrafo = "";
				tamanhoLinhaAcumulado = 0;
			} 
			paragrafos.add(linhaDummy + "\n");
		} else if(linhaFormatada.equals("poder judiciario")) {					// poder judiciario
			if(!linhaParagrafo.isEmpty()) {
				paragrafos.add(linhaParagrafo + " " + linhaDummy + "\n");
				linhaParagrafo = "";
				tamanhoLinhaAcumulado = 0;
			} 
			paragrafos.add(linhaDummy);											// Maiuscula + Atores
		} else if(ehMaiuscula(linhaDummy) && tabelaAtores.contains(primeiraPalavra(formataPalavra(linhaDummy)))) {
			if(!linhaParagrafo.isEmpty()) {
				if(tamanhoLinhaAcumulado > tamanhoLinha) {
					paragrafos.add(quebraLinha(linhaParagrafo));
				} else {
					paragrafos.add(linhaParagrafo);
				}
			} 
			linhaParagrafo = linhaDummy;
			tamanhoLinhaAcumulado = linhaDummy.length();
		} else if(linhaFormatada.equals("poder") || linhaFormatada.equals("judiciario")) { 	// PODER ou JUDICIARIO
		     if(linhaFormatada.equals("poder")) {
		       if(!linhaParagrafo.isEmpty()) {
		    	   if(tamanhoLinhaAcumulado > tamanhoLinha) {
						paragrafos.add(quebraLinha(linhaParagrafo));
					} else {
						paragrafos.add(linhaParagrafo);
					}
		         linhaParagrafo = linhaDummy;
		         tamanhoLinhaAcumulado = linhaDummy.length();
		       } else {
		         linhaParagrafo = linhaDummy;
		         tamanhoLinhaAcumulado = linhaDummy.length();
		       }
		     } else {
		       if(!linhaParagrafo.isEmpty() && linhaParagrafo.equals("PODER")) {
		    	   if(tamanhoLinhaAcumulado > tamanhoLinha) {
						paragrafos.add(quebraLinha(linhaParagrafo + " " +linhaDummy));
					} else {
						paragrafos.add(linhaParagrafo + " " +linhaDummy);
					}
		         linhaParagrafo = "";
		         tamanhoLinhaAcumulado = 0;
		       }
		     }
		} else if(ehMaiuscula(linhaDummy)) {											// Começa maiúscula
	       if(tabelaAtores.contains(primeiraPalavra(formataPalavra(linhaAnterior)))) {
	           for (int x = 0; x <= palavras.length-1; x++) {
	        	   if(palavras[x].isEmpty()) {
						palavras[x] = " ";
					}
	             if(palavras[x].charAt(palavras[x].length()-1) == ',' ||
	                 palavras[x].charAt(palavras[x].length()-1) == ';'){
	               linhaParagrafo = linhaParagrafo + " " + palavras[x] + "\n";
	               tamanhoLinhaAcumulado = tamanhoLinhaAcumulado + palavras[x].length() + 1;
	             } else if(palavras[x].charAt(palavras[x].length()-1) >= 'A' || 
	                 palavras[x].charAt(palavras[x].length()-1) <= 'Z' ||
	                 palavras[x].charAt(palavras[x].length()-1) >= 'a' ||
	                 palavras[x].charAt(palavras[x].length()-1) <='z') {
	               if(linhaParagrafo.isEmpty()) {
	                 linhaParagrafo = palavras[x];
	                 tamanhoLinhaAcumulado = palavras[x].length();
	               } else {
	                 linhaParagrafo = linhaParagrafo + " " + palavras[x];
	                 tamanhoLinhaAcumulado = tamanhoLinhaAcumulado + palavras[x].length() + 1;
	               }
	             } else if(palavras[x].charAt(palavras[x].length()-1) == ':') {
	               palavra = palavra + palavras[x];
	               if(tamanhoLinhaAcumulado > tamanhoLinha) {
	            	   paragrafos.add(quebraLinha(linhaDummy));
	               } else {
	            	   paragrafos.add(linhaDummy);
	               }
	               linhaParagrafo = palavra;
	               tamanhoLinhaAcumulado = palavra.length();
	             }
	           }
	         } else {
	           if(!linhaParagrafo.isEmpty()) {
	        	   if(tamanhoLinhaAcumulado > tamanhoLinha) {
	        		   paragrafos.add(quebraLinha(linhaParagrafo + " " + linhaDummy));
	        	   } else {
	        		   paragrafos.add(linhaParagrafo + " " + linhaDummy);
	        	   }
	             linhaParagrafo = "";
	             tamanhoLinhaAcumulado = 0;
	           } else {
	             paragrafos.add(linhaDummy);
	           }
	         }
		} else if(incioMaiuscula) {													// Começo é maiusculo
			if(juridiques.contains(linhaFormatada) && !verificaDataValida(linhaFormatada)) {
				if(!linhaParagrafo.isEmpty()) {
					if(tamanhoLinhaAcumulado > tamanhoLinha) {
						paragrafos.add(quebraLinha(linhaParagrafo));
					} else {
						paragrafos.add(linhaParagrafo);
					}
					linhaParagrafo = "";
					tamanhoLinhaAcumulado = 0;
				} 
				paragrafos.add(linhaDummy);
			} else {
				for (int x = 0; x <= palavras.length-1; x++) {
					if(palavras[x].isEmpty()) {
						palavras[x] = " ";
					}
					if(ehMaiuscula(palavras[x]) && palavras[x].charAt(palavras[x].length()-1) == '.'){
						paragrafos.add(quebraLinha(linhaParagrafo + " " + palavras[x]));
						linhaParagrafo = "";
						tamanhoLinhaAcumulado = 0;
			        } else {
			        	if(ehMaiuscula(palavras[x])) {
			        		if(linhaParagrafo.isEmpty()) {
			        			linhaParagrafo = palavras[x];
			        			tamanhoLinhaAcumulado = palavras[x].length();
			        		} else {
			        			linhaParagrafo = linhaParagrafo + " " + palavras[x];
			        			tamanhoLinhaAcumulado = tamanhoLinhaAcumulado + palavras[x].length() + 1;
			        		}
				        } else {
				        	if(!linhaParagrafo.isEmpty()) {
				        		linhaParagrafo = linhaParagrafo + " " + palavras[x];
				        		tamanhoLinhaAcumulado = tamanhoLinhaAcumulado + palavras[x].length() + 1;
					        } else {
					        	linhaParagrafo = palavras[x];
					        	tamanhoLinhaAcumulado = palavras[x].length();
					        }
				        }
			        }
				}

				if(!linhaParagrafo.isEmpty() && ponto == '.') {
					if(tamanhoLinhaAcumulado > tamanhoLinha) {
						paragrafos.add(quebraLinha(linhaParagrafo));
					} else {
						paragrafos.add(linhaParagrafo);
					}					
					linhaParagrafo = "";
					tamanhoLinhaAcumulado = 0;
				}
			}
		} else if(!incioMaiuscula) {												// tudo minusculo
			if(linhaParagrafo.isEmpty()) {
    			linhaParagrafo = linhaDummy;
    			tamanhoLinhaAcumulado = linhaDummy.length();
    		} else {
    			linhaParagrafo = linhaParagrafo + " " + linhaDummy;
    		}
			k++;
		}
		if(!linhaParagrafo.isEmpty() && linhaParagrafo.charAt(linhaParagrafo.length()-1) == '.') {
			paragrafos.add(quebraLinha(linhaParagrafo) + "\n");
			linhaParagrafo = "";
			tamanhoLinhaAcumulado = 0;
		}
	k++;
	}

	public static String obtemData(String linhaDummy) {
		String linhaData = linhaDummy;
		String dataFinal = "";
		String dummy = "";
		int num = 0;
		int var = linhaData.split(" ", -1).length - 1;
		String linhaDecomposta[] = new String[var];

		if(linhaData.charAt(linhaData.length()-1) == '.') {
			linhaData = linhaData.substring(0, linhaData.length()-1);
		}
		linhaDecomposta = linhaData.split(" ");
		if(linhaDecomposta.length >= 6) {
			for(int i=0; i <= linhaDecomposta.length-1; i++) {
				dummy = linhaDecomposta[i].replaceAll("[.,]","");
				if(dummy.length() == 2) {
					if(ehInteiro(dummy)) {
						num = Integer.parseInt(dummy);
						if((num >= 1 && num <= 31) || (num >= 1 && num <= 12)) {
							if(dataFinal.isEmpty()) {
								dataFinal = dummy;
							} else {
								dataFinal = dataFinal + "-" + dummy;
							}
						}
					}
				}
				if(dummy.length() == 4 && ehInteiro(dummy)) {
					dataFinal = dataFinal + "-" + dummy;
					break;
				}
				if(meses.contains(linhaDecomposta[i])) {
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
			}
		} else {
			dummy = linhaData.replaceAll("[ABCDEFGHIJKLMNOPQRSTUVXZWYabcdefghijklmnoprstuvxzwy]","").trim();
			dummy = dummy.replaceAll("[/]","-");
		}
		return dataFinal;
	}
	
	private static boolean verificaDataReduzida(String data) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.parse(data);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }
	
	private static boolean verificaDataValida(String linhaDummy){

		/**
		 * Verifica se a linha tem uma data válida no formato extenso (DD de MMMMM de AAAA)
		 */
		//String linhaDummy = dataTeste;
		String localidade = "";
		String dia = "";
		String mes = "";
		String ano = "";
		String linhaData = "";
		String dummy = formataPalavra(linhaDummy);
		dummy = dummy.replaceAll("[.]","");
		
		//int var = dummy.split(" ", -1).length - 1;
		//String palavras[] = new String[var];
		
		int var = dummy.trim().split(" ", -1).length - 1;
		String palavras[] = new String[var];
		palavras = dummy.split(" ");		
		
		if(!verificaSeLinhaTemNumProcesso(linhaDummy) && !validaAssunto(linhaDummy) && 
				!verificaStopWords(linhaDummy) && !validaAtor(linhaDummy) &&
				verificaMeses(linhaDummy)){
			
			if(!verificaDataReduzida(linhaDummy)) {
				for(int x=0; x <= palavras.length-1; x++) {
					if(verificaLetras(palavras[x])) {
						if(!localidade.endsWith(",")) {
							if(palavras[x].indexOf("/") != 0 && palavras[x].charAt(palavras[x].length()-1) == ',') {
								if(localidade.isEmpty()) {
									localidade = palavras[x];
								} else {
									localidade = localidade + " " + palavras[x];
								}
							} else {
								if(localidade.isEmpty()) {
									localidade = palavras[x];
								} else {
									localidade = localidade + " " + palavras[x];
								}
							}
						}
						if(palavras[x].equals("de")){
							continue;
						} else {
							if(meses.contains(palavras[x])) {
								mes = palavras[x];
							}
							continue;
						}
					}
					if(palavras[x].length() <=2) {
						dia = completaEsquerda(palavras[x], '0', 2);
					} else if(palavras[x].length() == 4) {
						ano = palavras[x];
					}
				}
				
				linhaData = dia + " " + mes + " " + ano;
				
				if(linhaData.matches("\\d{2}\\s\\w{4}\\s\\d{4}")) {					// Maio
					return true;
				} else if(linhaData.matches("\\d{2}\\s\\w{5}\\s\\d{4}")) {			// Março, Abril, Junho, Julho
					return true;
				} else if(linhaData.matches("\\d{2}\\s\\w{6}\\s\\d{4}")) {			// Agosto
					return true;
				} else if(linhaData.matches("\\d{2}\\s\\w{7}\\s\\d{4}")) {			// Janeiro, Outubro
					return true;
				} else if(linhaData.matches("\\d{2}\\s\\w{8}\\s\\d{4}")) {			// Setembro, Novembro, Dezembro
					return true;
				} else if(linhaData.matches("\\d{2}\\s\\w{9}\\s\\d{4}")) {			// Fevereiro
					return true;
				} else if(linhaDummy.startsWith(localidade) && linhaDummy.endsWith(ano + ".")) {
					return true;
				}
			} else {
				return true;
			}
		}	
		return false;
	}
	
	public static boolean verificaLetras(String palavra) {
		Pattern pattern = Pattern.compile("[0-9]");
		Matcher match = pattern.matcher(palavra);
		if(match.find()) { 
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean procuraIntimados() {
		int inicio = sequencial;
		String linhaDummy = "";

		for (int x=0; x <= 50; x++) {
			linhaDummy = formataPalavra(carregaLinha(inicio, false));
			if(linhaDummy.equals("intimado(s)/citado(s):") || linhaDummy.equals("PODER JUDICIÁRIO")){
				return true;
			}
			if(verificaDataFinal(linhaDummy, sequencial)) {
				saida = true;
			}
		}
		
		return false;
	}
	
	public static boolean avaliaMudancaProcesso(int indice) {
		String linhaAnterior = carregaLinha(indice-1,false);
		String linhaDummy = carregaLinha(indice,false);
		String procDummy = obtemNumeroProcesso(linhaDummy);
		String dummy = "";
		String dummy2 = "";
		String palavras[];
		String palavra = "";
		int qtdAtoresAnteriores = 0;
		int qtdAtoresPosteriores = 0;
		int contadorFuncoes = 0;
		int contadorAnterior = 0;
		int contadorPosterior = 0;
		int frente = 0;
		int tras = 0;
		
		if(verificaDataValida(dummy)) {

			if(sequencialAssunto == indice-2 || sequencialGrupo == indice-2) {
				return true;
			}
	
			// loop regressivo a procura de uma data válida
			for(int x = indice; x>=indice-30; x--) {					
				dummy = formataPalavra(carregaLinha(x,false));		// que é o final da publicação em processamento (atual)
				palavras = dummy.split(" ");
				
				if(verificaDataValida(dummy)) {						// o nº do processo estar depois de uma data valida
					if(verificaDataFinal(dummy, x)){
						return true;								// pode ser a dta válida da public anterior
					}										
				}

				if (x == sequencialGrupo) {
					return true;
				}

			/*														// analisar posteriormente
				if(verificaSeLinhaTemNumProcesso(dummy)) {
					if(procDummy.equals(obtemNumeroProcesso(dummy))) {
						if(sequencialAssunto == indice-1 || sequencialGrupo == indice-1) {
							return true;
						}
					}
				}
			*/
																			
			/*
				if(validaFuncao(dummy)) {							// analisar estatisticas para decidir
					contadorFuncoes++;
				}
				if(validaAtor(palavras[0])){
					qtdAtoresAnteriores++;							// decidir o que fazer depois (decidir por estatisticas
				}
			*/
				if(x == 0 ) {										// chegou ao limite superior do loop ou x = 0
					break;
				}
				contadorAnterior = contadorAnterior + contaKeyWords(dummy);
				tras++;
			}
			
			// loop proressivo
			for(int y = indice; y<=indice+40; y++) {				
				dummy = formataPalavra(carregaLinha(y,false));		// conta quantos atores achou
				palavras = dummy.split(" ");
				
				if(verificaSeLinhaTemNumProcesso(dummy)) {
					return true;
				}
				
				if(validaAtor(palavras[0])){
					qtdAtoresPosteriores++;							// decidir o que fazer depois (decidir por estatisticas
				}													// alguns atores + "intimados" é quebra de processo
				if(validaAssunto(dummy)) {
					return false;
				}
				if(verificaDataValida(dummy)) {						// localizou a data final da publicação em curso
					if(verificaDataFinal(dummy, y)){
						return false;								
					}										
				}
				if(dummy.equals("intimado(s)/citado(s):")) {		// localizar intimados sem ter processo indica
					return false;									// que ñ é assunto válido
				}
				if(validaFuncao(dummy)) {							// muitas funções podem indicar q ñ é quebra de processo
					contadorPosterior++;
				}
				if(validaJuridiques(dummy)) {						// juridiques pode indicar texto
					return true;
				}
				if(y == limiteGrupo){								// chegou ao fim
					return true;
				}
	
				contadorAnterior = contadorAnterior + contaKeyWords(dummy);			// muitas keyWords significa texto livre - ñ quebra processo
				frente++;
			}
		}
			
		if(contadorAnterior > 3 && contadorPosterior > 3) {
			return true;
		} else {
			return false;
		}	
	}

	private static boolean quebraProcesso(int indice) throws Exception {
		String strDummy = "";
		String dummy = "";
		String procDummy = "";
		String linhaDummy = carregaLinha(indice, false);
		strDummy = obtemNumeroProcesso(linhaDummy);

		int var = linhaDummy.trim().split(" ", -1).length - 1;
		String palavras[] = new String[var];
		String palavra = "";
		
		if(verificaSeLinhaTemNumProcesso(linhaDummy)) {
		//	if(avaliaMudancaProcesso(indice)) {
				dummy = formataPalavra(carregaLinha(sequencial-2,false));			// verifica linha anterior a linha do processo
				if(dummy.equals("poder") || dummy.equals("judiciario") || 
						dummy.equals("poder judiciario")) {
					gravaLog(obtemHrAtual() + " --- >> " + sequencial + " - quebra processo");
					return false;
				}
				
				if(assunto.isEmpty() && (textoEdital.isEmpty() && paragrafos.isEmpty())) {
					gravaLog(obtemHrAtual() + " --- >> " + sequencial + " - quebra processo");
					return true;
				}
				
				if(!strDummy.equals(processoNumero)) {
					if(!formataPalavra(assunto).equals("pauta de julgamento")) {						// se assuto ñ for pauta
						if(!validaAssunto(formataPalavra(linhaAnterior)) && !assunto.isEmpty()) {		// linha anterior ñ é um assunto válido
							if(procuraIntimados()) {
								return true;
							}
						}	
					}
				}

				if(strDummy != null) {																// linha tem nº processo
					if(!formataPalavra(linhaAnterior).contains("judiciario") || linhaAnterior.startsWith("Ordem:")) {					// linha anterior ñ é PODER JUDICIARIO
						
						// loop regressivo
						for(int x = indice; x>=indice-10; x--) {							
							dummy = formataPalavra(carregaLinha(x,false));
							//dataTeste = dummy;
							if(dummy.isEmpty()) {
			        			continue;
			        		}
							if(validaAtor(dummy)) {													// assunto ao qual o proc pertence
								return true;
							}
							if(validaAssunto(dummy) && x == sequencialAssunto) {					// assunto ao qual o proc pertence
								return true;
							}
							if(dummy.equals(formataPalavra(grupo)) && x == sequencialGrupo) {		// Grupo ao qual o proc pertence
								return true;
							}
							if((obtemNumeroProcesso(dummy) != null) && (x == sequencialProcesso)) { // o nº processo faz parte do texto
								return false;
							}
							if(verificaDataFinal(dummy,x)){											// data final da publicação anterior
								if(strDummy.equals(processoNumero)) {
									return false;
								} else {
									return true;
								}
							}
							if(formataPalavra(dummy).equals("intimado(s)/citado(s):") 				// o nº processo faz parte do texto
									&& formataPalavra(assunto).equals("pauta")) {
								return false;
							}
						}
						
						// loop progressivo 
						for(int x = indice; x<=indice+50; x++) { 
							dummy = formataPalavra(carregaLinha(x,false));
							if(dummy.isEmpty()) {
			        			continue;
			        		}

							palavras = dummy.split(" ");
							palavra = palavras[0].replaceAll(":", "");
							procDummy = obtemNumeroProcesso(dummy);

							//if(procDummy != null) {
							//	if(avaliaMudancaProcesso(x-1)) {
							//		k++;												// pode indicar uma falsa quebra
							//	}
							//}
							
							if(validaAtor(palavra)){
								return true;
							}
							
							if(verificaDataValida(obtemData(dummy))) {
								return true;
							}

							if(formataPalavra(dummy).equals("intimado(s)/citado(s):")) {
								gravaLog(obtemHrAtual() + " --- >> " + sequencial + " - quebra processo");
								return true;
							}
							
							if(x >= limiteGrupo) {										
								break;
							}
						}					
					}
				}
	//		}			Fim do if para o método avaliaMudançaProcesso
		}
		return false;
	}
/*	
	public static boolean quebraAssunto(int indice, int limite) throws Exception {
		int in = 0;
		int fm = 0;
		String dataInvertida = "";
		String dta = "";
		String dummy = "";
		String linhaDummy = formataPalavra(carregaLinha(indice, false).trim());
		linhaDummy = linhaDummy.replaceAll("[,:;-]"," ");
		linhaDummy = linhaDummy.replaceAll("[0123456789.]","");
		linhaDummy = linhaDummy.trim();
		
		if(validaAssunto(linhaDummy)) {
			k++;
		}

		if(indice == sequencialGrupo) {																// já valida o 1º assunto do grupo
			gravaLog(obtemHrAtual() + " --- >> " + sequencial + " - quebra assunto");
			return true;
		} else {
			if(validaAssunto(linhaDummy)) {	
				in = tabelaAssuntos.indexOf(linhaDummy );
				fm = tabelaAssuntos.lastIndexOf(linhaDummy);
				if(in >= 0 && fm >= 0 ) {
					for(int i = in; i <= fm; i++) {
						if(contaPalavras(linhaDummy ) == contaPalavras(tabelaAssuntos.get(i))){		// Qtd de palavras são iguais	<<<<<<
							if(linhaDummy .equals(tabelaAssuntos.get(i))) {																		
								for(int x=indice; x>=processoSequencial; x--) {						// regressivo a procura de intimados
									dummy = formataPalavra(carregaLinha(x,false));
									if(dummy.isEmpty()) {
					        			continue;
					        		}
									if(x == sequencialGrupo) {
										break;
									}
									if(x == sequencialSecao) {
										gravaLog(obtemHrAtual() + " --- >> " + sequencial + " - quebra assunto");
										return true;
									}
									dta = obtemData(dummy);
									if(!dta.isEmpty()) {
verificar lógica						if(ehDataValida(dta) && dta.length() == 10) {
deve ser uma data final						dataInvertida = obtemData(dummy).substring(6, 10) + obtemData(dummy).substring(2, 5);
											if(strEdicao.startsWith(dataInvertida)) {
												return true;
											}
										}
									}
									if(dummy.equals("poder") || 
											dummy.equals("judiciario") || 
											dummy.equals("poder judiciario")){
										return false;
									}										
									if(dummy.equals("intimado(s)/citado(s):")) {
										return false;
									}
									if(obtemNumeroProcesso(dummy) != null) {
										gravaLog(obtemHrAtual() + " --- >> " + sequencial + " - quebra assunto");
										return true;
									}										
								}
					
								for(int x=indice; x<=limite; x++) {								// progressivo a procura de nº processo
									dummy = formataPalavra(carregaLinha(x,false));
									if(atores.contains(primeiraPalavra(dummy))){
										return true;
									}
									if(dummy.isEmpty()) {
										continue;
					        		}
									if(dummy.equals("")) {
										continue;
									}
verificar lógica					if(ehDataValida(obtemData(dummy))) {
deve ser uma data final					return true;
									}

									if((obtemNumeroProcesso(dummy) != null) && pauta) {
										return true;
									}

									if(obtemNumeroProcesso(dummy) != null) {
										if(!assunto.isEmpty() && !processoLinha.isEmpty() && !textoEdital.isEmpty() || (indice == (x-1))) {
											gravaLog(obtemHrAtual() + " --- >> " + sequencial + " - quebra assunto");
											return true;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
*/
	public static boolean quebraAssunto(int indice, int limite) throws Exception {
		int in = 0;
		int fm = 0;
		String dataInvertida = "";
		String dta = "";
		String dummy = "";
		String linhaDummy = formataPalavra(carregaLinha(indice, false).trim());
		linhaDummy = linhaDummy.replaceAll("[,:;-]"," ");
		linhaDummy = linhaDummy.replaceAll("[0123456789.]","");
		linhaDummy = linhaDummy.trim();
		
		if(validaAssunto(linhaDummy)) {
			if(indice == sequencialGrupo) {																// já valida o 1º assunto do grupo
				gravaLog(obtemHrAtual() + " --- >> " + sequencial + " - quebra assunto");
				return true;
			} else {		
				for(int x=indice; x>=sequencialProcesso; x--) {										// loop regressivo 
					dummy = formataPalavra(carregaLinha(x,false));
					if(dummy.isEmpty()) {
	        			continue;
	        		}
					if(x == sequencialGrupo) {														// atingiu limite superior
						break;
					}
					if(x == sequencialSecao) {
						gravaLog(obtemHrAtual() + " --- >> " + sequencial + " - quebra assunto");
						return true;
					}
					if(verificaDataValida(dummy)) {													// localizou data final da publicação anterior
						if(verificaDataFinal(dummy, x)) {
							return true;
						}
					}
					if(dummy.equals("poder") || 
							dummy.equals("judiciario") || 
							dummy.equals("poder judiciario")){										// provavelmente assunto no meio da 
						return false;																// publicação em processamento
					}										
					if(dummy.equals("intimado(s)/citado(s):")) {									// idem
						return false;
					}
					if(obtemNumeroProcesso(dummy) != null) {
						gravaLog(obtemHrAtual() + " --- >> " + sequencial + " - quebra assunto");
						return true;
					}										
				}
					
				for(int x=indice; x<=limite; x++) {													// loop progressivo
					dummy = formataPalavra(carregaLinha(x,false));
					if(dummy.isEmpty()) {
						continue;
	        		}
					if(atores.contains(primeiraPalavra(dummy))){									// encontrou atores					
						return true;
					}
					if(verificaDataValida(dummy)) {													// encontrou data final da proxima publicação
						if(verificaDataFinal(dummy, x)) {
							return true;
						}
					}
					if((obtemNumeroProcesso(dummy) != null) && pauta) {								// encotrou nº processo e assunto = pauta
						return true;
					}
					if(obtemNumeroProcesso(dummy) != null) {
						if(!assunto.isEmpty() && !processoLinha.isEmpty() && !textoEdital.isEmpty() || (indice == (x-1))) {
							gravaLog(obtemHrAtual() + " --- >> " + sequencial + " - quebra assunto");
							return true;
						}
					}
				}						
			}
		}
		return false;
	}
	
	public static boolean validaAtor(String linhaDummy) {
		
		linhaDummy = formataPalavra(linhaDummy);
		linhaDummy = linhaDummy.replaceAll("[,:;-]"," ");
		linhaDummy = linhaDummy.replaceAll("[0123456789.]","");
		linhaDummy = linhaDummy.trim();
		
		int intDummy = 0;
		String strDummy = "";
		String dummy = "";
		
		int var = linhaDummy .split(" ", -1).length - 1;      
		String palavras[] = new String[var];                
		palavras = linhaDummy .split(" ");

		if(tabelaAtores.contains(linhaDummy)) {
			return true;
		} else {
			dummy = palavras[0].trim();
			for(int x=1; x<=palavras.length-1; x++ ) {
				if(tabelaAtores.contains(dummy)) {
					intDummy = tabelaAtores.indexOf(dummy);
					strDummy = tabelaAtores.get(intDummy).trim();
					if(strDummy.length() == dummy.length()){
						return true;
					}
				} else {
					dummy = dummy + " " + palavras[x];
					dummy = dummy.trim();
				}		
			}
		}
		return false;
	}
	
	public static boolean verificaStopWords(String linhaDummy) {
		
		linhaDummy = formataPalavra(linhaDummy);
		linhaDummy = linhaDummy.replaceAll("[,:;-]"," ");
		linhaDummy = linhaDummy.replaceAll("[0123456789.]","");
		linhaDummy = linhaDummy.trim();
		
		int intDummy = 0;
		String strDummy = "";
		String dummy = "";
		
		int var = linhaDummy .split(" ", -1).length - 1;      
		String palavras[] = new String[var];                
		palavras = linhaDummy .split(" ");

		if(stopWords.contains(linhaDummy)) {
			return true;
		} else {
			dummy = palavras[0].trim();
			for(int x=1; x<=palavras.length-1; x++ ) {
				if(stopWords.contains(dummy)) {
					intDummy = stopWords.indexOf(dummy);
					strDummy = stopWords.get(intDummy).trim();
					if(strDummy.length() == dummy.length()){
						return true;
					}
				} else {
					dummy = dummy + " " + palavras[x];
					dummy = dummy.trim();
				}		
			}
		}
		return false;
	}
	
	public static boolean validaFuncao(String linhaDummy) {
		
		linhaDummy = formataPalavra(linhaDummy);
		linhaDummy = linhaDummy.replaceAll("[,:;-]"," ");
		linhaDummy = linhaDummy.replaceAll("[0123456789.]","");
		linhaDummy = linhaDummy.trim();
		
		int intDummy = 0;
		String strDummy = "";
		String dummy = "";
		
		int var = linhaDummy .split(" ", -1).length - 1;      
		String palavras[] = new String[var];                
		palavras = linhaDummy .split(" ");

		if(funcoes.contains(linhaDummy)) {
			return true;
		} else {
			dummy = palavras[0].trim();
			for(int x=1; x<=palavras.length-1; x++ ) {
				if(funcoes.contains(dummy)) {
					intDummy = funcoes.indexOf(dummy);
					strDummy = funcoes.get(intDummy).trim();
					if(strDummy.length() == dummy.length()){
						return true;
					}
				} else {
					dummy = dummy + " " + palavras[x];
					dummy = dummy.trim();
				}		
			}
		}
		return false;
	}
	
	public static boolean verificaMeses(String linhaDummy) {

		String dummy = "";
		int var = linhaDummy .split(" ", -1).length - 1;      
		String palavras[] = new String[var];                
		palavras = linhaDummy .split(" ");

		for(int x=0; x<=palavras.length-1; x++ ) {
			dummy = palavras[x].trim();
			if(dummy.isEmpty()) {
				continue;
			} else {
				if(meses.contains(dummy)) {
					return true;
				}
			}
				
		}
		return false;
	}
/*	
	private static boolean validaAssunto(String linhaDummy) {

		linhaDummy = formataPalavra(linhaDummy).trim();
		int intDummy = 0;
		String strDummy = "";
		String parametro = "";
		String dummy = "";
		
		int var = linhaDummy .split(" ", -1).length - 1;      
		String palavras[] = new String[var];                
		palavras = linhaDummy .split(" ");
	
		if(!linhaDummy.isEmpty()) {
			for(int x=0; x<=palavras.length-1; x++ ) {
				if(palavras[x].trim().isEmpty()) {
					continue;
				}
				if(tabelaAssuntos.contains(palavras[x].trim())) {
					intDummy = tabelaAssuntos.indexOf(palavras[x].trim());
					if(strDummy.isEmpty()) {
						strDummy = tabelaAssuntos.get(intDummy).trim();
					} else {
						strDummy = strDummy + " " + palavras[x].trim();
					}
					if(strDummy.length() == linhaDummy.trim().length()){
						return true;
					}
				}
				k++;
			}
			k++;
		}
		return false;
	}
*/
	private static boolean validaAssunto(String linhaDummy) {

		linhaDummy = formataPalavra(linhaDummy).trim();
		int intDummy = 0;

		if(tabelaAssuntos.contains(linhaDummy)) {
			intDummy = tabelaAssuntos.indexOf(linhaDummy);
			if(tabelaAssuntos.get(intDummy).length() == linhaDummy.length()){
				return true;
			}
		}
		return false;
	}
	
	public static int contaKeyWords(String linhaDummy) {
		String dummy = "";
		int contador = 0;

		linhaDummy  = formataPalavra(linhaDummy );	
		linhaDummy  = linhaDummy.replaceAll("[(),;.:-]"," ");
		
		int var = linhaDummy .split(" ", -1).length - 1;      
		String var2[] = new String[var];                
		var2 = linhaDummy .split(" ");	
		for(int x=0; x<= var2.length-1; x++) {
			if(var2[x].isEmpty()) {
				continue;
			} else {
				if(keyWords.contains(var2[x].trim())) {
					contador++;
				}
			}
		}
		return contador;
	}
	
	private static String carregaLinha(int indice, boolean incrementar) {
		int ix = indice;
		int in = 0;
		String strPagina = "";
		char digito;
		boolean saida = false;
		
		while(bufferEntrada.get(ix).trim().equals("") || bufferEntrada.get(ix).trim().isEmpty()) {
			ix++;
		}

		if(bufferEntrada.get(ix).startsWith("Código para aferir autenticidade") ||
				bufferEntrada.get(ix).startsWith("Data da Disponibilização:") ||
				primeiraPalavra(bufferEntrada.get(ix)).matches("\\d{4}\\W\\d{4}")){
			in = ix;

			while(!saida) {
				if(bufferEntrada.get(in).startsWith("Código para aferir autenticidade") ||
						bufferEntrada.get(in).startsWith("Data da Disponibilização:") ||
						primeiraPalavra(bufferEntrada.get(in)).matches("\\d{4}\\W\\d{4}") ||
								(bufferEntrada.get(in).trim().equals("") || bufferEntrada.get(in).trim().isEmpty())){
					//if(primeiraPalavra(bufferEntrada.get(ix)).matches("\\d{4}\\W\\d{4}")){ 
					//	pagina = obtemPagina(bufferEntrada.get(ix));
					//}
					in++;
					continue;
				} else {
					ix = in;
					break;
				}
			}
		}

		if(incrementar) {
			if(in > 0) {
				sequencial = in+1;
			} else {
				sequencial++;
			}
		} 
		return bufferEntrada.get(ix).trim();
	}
/*
	public static String carregaLinha(int indice, boolean incrementar) {
		int ix = indice;
		int in = 0;
		boolean saida = false;
		
		while(bufferEntrada.get(ix).trim().equals("") || bufferEntrada.get(ix).trim().isEmpty()) {
			ix++;
		}

		if(bufferEntrada.get(ix).startsWith("Código para aferir autenticidade") ||
				bufferEntrada.get(ix).startsWith("Data da Disponibilização:") ||
				primeiraPalavra(bufferEntrada.get(ix)).matches("\\d{4}\\W\\d{4}")){
			in = ix;
			
			while(!saida) {
				if(bufferEntrada.get(in).startsWith("Código para aferir autenticidade") ||
						bufferEntrada.get(in).startsWith("Data da Disponibilização:") ||
						primeiraPalavra(bufferEntrada.get(in)).matches("\\d{4}\\W\\d{4}") ||
								(bufferEntrada.get(in).trim().equals("") || bufferEntrada.get(in).trim().isEmpty())){
					in++;
					continue;
				} else {
					ix = in;
					break;
				}
			}
		}

		if(incrementar) {
			if(in > 0) {
				sequencial = in+1;
			} else {
				sequencial++;
			}
		} 
		return bufferEntrada.get(ix).trim();
	}
*/
	private static int obtemPagina(String linhaDummy) {
		int idx = 0;
		String strPagina = "";
		char digito;
		boolean saida = false;
		
		if(primeiraPalavra(linhaDummy).matches("\\d{4}\\W\\d{4}")){
			idx = linhaDummy.length()-1;
			while(!saida){
				digito =linhaDummy.charAt(idx);
				if(linhaDummy.charAt(idx) >= '0' && linhaDummy.charAt(idx) <= '9'){						
					if(strPagina.isEmpty()) {
						strPagina = Character.toString(digito);
					} else {
						strPagina = Character.toString(digito) + strPagina;
					}
				} else {
					saida = true;
					break;
				}
				idx--;
			}
		} else {
			strPagina = "0";
		}
		if(strPagina.isEmpty()) {
			return 0;
		} else {
			return Integer.parseInt(strPagina);
		}
	}
	
	private static String carregaLinhaIndice() {
		int x = 0;
		String linhaDummy  = "";
		
		if(primeiraPalavra(bufferEntrada.get(sequencialIndice)).matches("\\d{4}\\W\\d{4}")){
			for(x = sequencialIndice; x<=bufferEntrada.size()-1;x++) {
				if(!bufferEntrada.get(x).startsWith("Código para aferir autenticidade")){
					continue; 
				} else {
					sequencialIndice = x+1;
					break;
				}
			}
		}
		if(sequencialIndice == bufferEntrada.size()) {
			seqIndex = sequencialIndice;
			sequencialIndice = sequencialIndice-1;
			linhaDummy  = bufferEntrada.get(bufferEntrada.size()-1);
		} else {
			seqIndex = sequencialIndice;
			linhaDummy  = bufferEntrada.get(sequencialIndice);
			sequencialIndice++;
		}
		seqIndex = sequencialIndice;
		return linhaDummy ;
	}
	
	private static boolean verifcaLetras(String linha) {
		char[] c = linha.toCharArray();
		boolean d = true;
		for ( int i = 0; i < c.length; i++ ) {
		    // verifica se o char não é um dígito
		    if ( !Character.isDigit( c[ i ] ) ) {
		        d = false;
		        break;
		    }
		}
		return d;
	}

	private static String trataAtores(String linhaDummy) throws Exception {
		
		char pto = ' ';
		boolean saida = false;
		String bloco = "";	
		String registro = "";
		linhaDummy  = limpaCaracteres(linhaDummy );
		if(linhaDummy .contains("&")) {
			linhaDummy  = limpaCaracteres(linhaDummy );
		}
		String dummy = "";

		while(!saida) {
			linhaDummy  = linhaDummy.replaceAll("[():-]"," ");
			if(tabelaAtores.contains(formataPalavra(primeiraPalavra(linhaDummy )))) {
				registro = formataPalavra(linhaDummy );
				registro = registro.replaceAll("[():-]"," ");
				while(!saida) {
					dummy = formataPalavra(linhaDummy );
					if(dummy.equals("intimado(s)/citado(s):") || 
							dummy.equals("poder") ||
							dummy.equals("judiciario") ||
							dummy.startsWith("ordem:") ||
							verificaDataValida(linhaDummy) ||
							verificaSeLinhaTemNumProcesso(linhaDummy)){						
						saida = true;
						break;
					}
					if(assunto.contains("Edital EDHPI-") && bloco.contains("Executado")) {
						saida = true;
						break;
					} else {
						if(!linhaDummy .contains("-----")) {
							if(validaAtor(registro)) {
								if(bloco.isEmpty()) {
									bloco = linhaDummy ;
								} else {
									bloco = bloco + "\n" + linhaDummy ;
								}
							} else {				
								if(pto == '.' || pto == ')' || pto == ':') {
									bloco = bloco + "\n" + linhaDummy ;
								} else {
									bloco = bloco + " " + linhaDummy ;
								}
							}
						}
					}
					pto = linhaDummy .charAt(linhaDummy .length()-1);
					linhaDummy  = carregaLinha(sequencial,true);
					linhaDummy  = linhaDummy .replaceAll("&[():-]"," ");
					registro = formataPalavra(linhaDummy );
					registro = registro.replaceAll("[():-]"," ");
		    	}
			}
			linhaDummy = carregaLinha(sequencial,true);
		}	
		// hoje sequencial--;		
		//gravaLog(obtemHrAtual() + " tratamento de atores");
		sequencial = sequencial - 2;
		return bloco;
	}
	
	public static int proximaSequencial(int sequencia) {
		int in = sequencia;
		if(!bufferEntrada.get(in+1).startsWith("Código para aferir autenticidade") &&
				!bufferEntrada.get(in+1).startsWith("Data da Disponibilização:") &&
				!primeiraPalavra(bufferEntrada.get(in+1)).matches("\\d{4}\\W\\d{4}")) {
			return in+1;
		} else {
			while(in <= sequencia+4) {
				if(bufferEntrada.get(in).startsWith("Código para aferir autenticidade") ||
						bufferEntrada.get(in).startsWith("Data da Disponibilização:") ||
						primeiraPalavra(bufferEntrada.get(in)).matches("\\d{4}\\W\\d{4}")){				
					in++;
					continue;
				} else {
					break;
				}
			}
		}
		return in;
	}
	
	public static boolean verificaPalavraChave(String linhaAtual) {
		int igualidades = 0;
		int var = linhaAtual.split(" ", -1).length - 1;      
		String var2[] = new String[var];                
		var2 = linhaAtual.split(" ");
		
		for(int i = 0; i <= var; i++){
		    if(stopWords.contains(var2[i])) {
		    	igualidades++;
		    }
		}

		if(igualidades >= 5) {
			return true;
		} else {
			return false;
		}
	}

	public static double contaJuridiques(String linhaDummy) {
		double resultado = 0.0;
		double acertos = 0.0;
		int var = linhaDummy.split(" ", -1).length - 1;
		String palavras[] = new String[var];
		palavras = linhaDummy.trim().split(" ");
		for(int x=0; x <= palavras.length-1; x++) {
			if(juridiques.contains(formataPalavra(palavras[x]))) {
				acertos++;
			}
		}
		resultado = (acertos / palavras.length);
		return resultado;
	}

/*	Este método faz parte do desenvolvimento da versão com formatação de texto
	public static boolean verificaSeEhParagrafo() {
		String linhaDummy = "";
		String dummy = "";
		int inx = sequencial-1;
		boolean ehMaiusculo = false;

		for(int x = inx; x <= inx + 50; x++){
			linhaDummy = carregaLinha(x,false);
			if(linhaDummy.equals("Intimado(s)/Citado(s):")) {
				break;
			}
			if(linhaDummy.charAt(0) >= 'A' && linhaDummy.charAt(0) <= 'Z' && !ehMaiusculo) {
				if(linhaDummy.charAt(linhaDummy.length()-1) == '.' && contaJuridiques(linhaDummy) > 2) {
					return true;
				}
				ehMaiusculo = true;
			} else {
				if (ehMaiusculo){
					if(contaPalavras(linhaDummy) <= 3 && linhaDummy.charAt(linhaDummy.length()-1) == '.') {
						continue;
					}
					if(linhaDummy.charAt(linhaDummy.length()-1) == '.') {
						if(contaJuridiques(dummy + " " + linhaDummy) > 2) {
							return true;
						}
					}	
					if(verificaSeLinhaTemNumProcesso(linhaDummy)) {
						break;
					}
					if(verificaDataValida(linhaDummy)) {
						break;
					}
					if(validaAssunto(linhaDummy)) {
						break;
					}
					if(x == limiteGrupo) {
						break;
					}
				}
				dummy = dummy + linhaDummy;
			}	
			k++;
		}
		return false;
	}
*/

/*	Este método faz parte do desenvolvimento da versão com formatação de texto
	public static String trataIntimados(String linhaDummy) throws IOException {
		//gravaLog(obtemHrAtual() + " tratamento de intimados");
		//String linhaDummy = carregaLinha(sequencial,true);
		if(linhaDummy.contains("&")) {
			linhaDummy = limpaCaracteres(linhaDummy);
		}
		String bloco = "";
		String dummy = "";
		String dummy2 = "";
		int ix = 0;
		boolean saida = false;
		boolean exit = false;
		String var2[]; 
		var2 = linhaDummy.split(" ");
		dummy = formataPalavra(linhaDummy);

		if((!verificaSeLinhaTemNumProcesso(linhaDummy) || dummy == "intimado(s)/citado(s):") && !assunto.contains("Edital EDHPI-")){
			while(!saida) {	
				//if(pauta  && !bloco.isEmpty()) {
				//	if(verificaSeEhParagrafo()) {
				//		break;
				//	}
				//}
				if(bloco.isEmpty()) {
					bloco = linhaDummy;
					linhaDummy = carregaLinha(sequencial,true);
					if(linhaDummy.contains("&")) {
						linhaDummy = limpaCaracteres(linhaDummy);
					}
					continue;
				} else {
					if(linhaDummy.charAt(0) == '-') {
						bloco = bloco + "\n" + linhaDummy;
						linhaDummy = carregaLinha(sequencial,true);
						if(linhaDummy.contains("&")) {
							linhaDummy = limpaCaracteres(linhaDummy);
						}
						continue;
					} else {
						if(!verificaSemelhanca(linhaDummy,secao) && !verificaSeLinhaTemNumProcesso(linhaDummy)){
							ix = sequencial-1;
							while(!exit) {
							//	if(formataPalavra(linha).startsWith("fundamentacao")) {
							//		break;
							//	}	
							//	if(verificaPalavraChave(linha)) {
							//		break;
							//	}
							//	
							
								if(linhaDummy.length() > 0) {
									if(linhaDummy.trim().charAt(0) != '-') {
										dummy = formataPalavra(carregaLinha(ix,false));						
										if(contaPalavras(dummy) > 2) {
											var2 = dummy.split(" ");
											dummy = var2[0];
										}
										if(tabelaAssuntos.contains(dummy) || 
												dummy.startsWith("poder") || 
												formataPalavra(linhaDummy).startsWith("fundamentacao")) {
											saida = true;
											sequencial--;
											break;
										}
										if(formataPalavra(linhaDummy).equals("poder") ||
												formataPalavra(linhaDummy).startsWith("intimacao em processo") ||
												(obtemNumeroProcesso(linhaDummy) == null && assunto.equals("pauta")) ||
												validaAssunto(dummy)){
											saida = true;
											break;	
										} else {
											if(pauta && !bloco.isEmpty()) {
												if(verificaSeEhParagrafo()) {
													saida = true;
													sequencial--;
													break;
												}
											} else {
												if(!verificaSeEhParagrafo()) {
													bloco = bloco + " " + linhaDummy;
												}
												k++;
											}
											if(pauta && !bloco.isEmpty()) {
												if(verificaSeEhParagrafo()) {
													saida = true;
													sequencial--;
													break;
												}
											} else {
												if(!verificaSeEhParagrafo()) {
													bloco = bloco + " " + linhaDummy;
												}
												k++;
											}
										}
									} else {
										if(pauta) {									
											if(verificaSeEhParagrafo()) {
												saida = true;
												break;
											}
										} else {
											if(verificaSeEhParagrafo()) {
												saida = true;
												break;
											}
											bloco = bloco + "\n" + linhaDummy;
											saida = true;
											break;	
										}
										k++;
									}
									dummy2 = formataPalavra(carregaLinha(sequencial,false));
									if(dummy2.equals("poder") || dummy2.equals("poder judiciario") ||
											dummy2.startsWith("intimacao em processo") ||
											(obtemNumeroProcesso(dummy2) == null && assunto.equals("pauta")) ||
											validaAssunto(dummy)){
										saida = true;
										break;
									}
									

									//else {
									//	if(pauta) {
									//		if(verificaSeEhParagrafo()) {
									//			break;
									//		}
									//	}
									//}

								}
								linhaDummy = carregaLinha(sequencial,true);
								if(linhaDummy.contains("&")) {
									linhaDummy = limpaCaracteres(linhaDummy);
								}
								if(verificaSemelhanca(linhaDummy,secao)){
									saida = true;
									break;
								}
								k++;
							}
							k++;
						} 
						else {
							if(verificaSeLinhaTemNumProcesso(linhaDummy)) {
								sequencial--;
							}
							break;
						}
					}
				}
			}
			k++;
		}
		if(!bloco.isEmpty()) {
			bloco = bloco + "\n";
		} else {
			bloco = "";
		}
		return bloco;
	}
*/
	
/*		Backup do metodo
	public static String trataIntimados(String linhaDummy) throws IOException {
		//gravaLog(obtemHrAtual() + " tratamento de intimados");
		//String linhaDummy = carregaLinha(sequencial,true);
		if(linhaDummy.contains("&")) {
			linhaDummy = limpaCaracteres(linhaDummy);
		}
		String bloco = "";
		String dummy = "";
		String dummy2 = "";
		int ix = 0;
		boolean saida = false;
		boolean exit = false;
		String var2[]; 
		var2 = linhaDummy.split(" ");
		dummy = formataPalavra(linhaDummy);

		if((!verificaSeLinhaTemNumProcesso(linhaDummy) || dummy == "intimado(s)/citado(s):") && !assunto.contains("Edital EDHPI-")){
			while(!saida) {	
				if(bloco.isEmpty()) {									// guarda intimados
					bloco = linhaDummy;
					linhaDummy = carregaLinha(sequencial,true);
					if(linhaDummy.contains("&")) {
						linhaDummy = limpaCaracteres(linhaDummy);
					}
					continue;
				} else {
					if(linhaDummy.charAt(0) == '-') {
						bloco = bloco + "\n" + linhaDummy;
						linhaDummy = carregaLinha(sequencial,true);
						if(linhaDummy.contains("&")) {
							linhaDummy = limpaCaracteres(linhaDummy);
						}
						continue;
					} else {
						
							
								if(!verificaSemelhanca(linhaDummy,secao) && !verificaSeLinhaTemNumProcesso(linhaDummy)){
									ix = sequencial-1;
									while(!exit) {
										if(linhaDummy.length() > 0) {
											if(linhaDummy.trim().charAt(0) != '-') {			// se linha ñ começa com '-'
												dummy = formataPalavra(carregaLinha(ix,false));						
												if(contaPalavras(dummy) > 2) {
													var2 = dummy.split(" ");
													dummy = var2[0];
												}
												if(tabelaAssuntos.contains(dummy) || 
														dummy.startsWith("poder") || 
														formataPalavra(linhaDummy).startsWith("fundamentacao")) {
													saida = true;
													sequencial--;
													break;
												}
												if(formataPalavra(linhaDummy).equals("poder") ||
														formataPalavra(linhaDummy).startsWith("intimacao em processo") ||
														(obtemNumeroProcesso(linhaDummy) == null && assunto.equals("pauta")) ||
														validaAssunto(dummy)){
													saida = true;
													break;	
												} else {
													
													if(pauta && !bloco.isEmpty()) {
														
														saida = true;
														sequencial--;
														break;
		
													} else {
														if(!juridiques.contains(formataPalavra(linhaDummy))) {
															if(!verificaSeEhParagrafo(sequencial-1)) {
																bloco = bloco + " " + linhaDummy;
															}
														} else {
															saida = true;
															sequencial--;
															break;
														}
														
														
														
														
														
														//if(!verificaSeEhParagrafo(sequencial)) {
														//	bloco = bloco + " " + linhaDummy;
														//} else {
														//	saida = true;
														//	sequencial--;
														//	break;
														//}
													}
													
												}
											} else {
												if(pauta) {									
													
													saida = true;
													break;
						
												} else {
													if(!juridiques.contains(formataPalavra(linhaDummy))) {
														if(!verificaSeEhParagrafo(sequencial)) {
															bloco = bloco + " " + linhaDummy;
														}
													} else {
														saida = true;
														sequencial--;
														break;
													}
													
													
													
													//if(!verificaSeEhParagrafo(sequencial)) {
													//	bloco = bloco + "\n" + linhaDummy;
													//}
													
													//saida = true;
													//break;
			
												}
											}
											dummy2 = formataPalavra(carregaLinha(sequencial,false));
											if(dummy2.equals("poder") || dummy2.equals("poder judiciario") ||
													dummy2.startsWith("intimacao em processo") ||
													(obtemNumeroProcesso(dummy2) == null && assunto.equals("pauta")) ||
													validaAssunto(dummy)){
												saida = true;
												break;
											}
										}

										linhaDummy = carregaLinha(sequencial,true);
										if(linhaDummy.contains("&")) {
											linhaDummy = limpaCaracteres(linhaDummy);
										}
										if(verificaSemelhanca(linhaDummy,secao)){
											saida = true;
											break;
										}

									}
								
								} 
								else {
									if(verificaSeLinhaTemNumProcesso(linhaDummy)) {
										sequencial--;
									}
									break;
								}

						}
						
				}
			}
		}
		if(!bloco.isEmpty()) {
			bloco = bloco + "\n";
		} else {
			bloco = "";
		}
		return bloco;
	}
*/	
	private static String trataIntimados(String linhaDummy) throws IOException {
		//gravaLog(obtemHrAtual() + " tratamento de intimados");
		//String linhaDummy = carregaLinha(sequencial,true);
		if(linhaDummy.contains("&")) {
			linhaDummy = limpaCaracteres(linhaDummy);
		}
		String bloco = "";
		String dummy = "";
		String linhaDummyAnterior = "";
		int ix = 0;
		boolean saida = false;
		boolean exit = false;
		String var2[]; 
		var2 = linhaDummy.split(" ");
		dummy = formataPalavra(linhaDummy);

		if(dummy.equals("intimado(s)/citado(s):") && 
				!assunto.contains("Edital EDHPI-") && 
				!verificaSeLinhaTemNumProcesso(linhaDummy))
			{
				while(!saida) {	
					if(bloco.isEmpty()) {									// guarda intimados
						bloco = linhaDummy;
						linhaDummy = carregaLinha(sequencial,true);
						if(verificaSeLinhaTemNumProcesso(linhaDummy)) {
							saida = true;
							break;
						}
						if(linhaDummy.contains("&")) {
							linhaDummy = limpaCaracteres(linhaDummy);
						}
						continue;
					} else {
						if(linhaDummy.charAt(0) == '-') {
							bloco = bloco + "\n" + linhaDummy;
							linhaDummyAnterior = linhaDummy;
							linhaDummy = carregaLinha(sequencial,true);
							if(verificaSeLinhaTemNumProcesso(linhaDummy)) {
								saida = true;
								break;
							}
							if(linhaDummy.contains("&")) {
								linhaDummy = limpaCaracteres(linhaDummy);
							}
							continue;
						} else {
							if(!juridiques.contains(linhaDummy)) {
								if(!validaParagrafo(sequencial)) {
								//	if(temContinuacao(linhaDummyAnterior)) {
										if(linhaDummy.charAt(0) != '-' && verificaMaiuscula(linhaDummy) && verificaMaiuscula(linhaDummyAnterior)) {
											if(bloco.length() < 100 && (bloco.length() - linhaDummy.length() > linhaDummy.length() )) {
												bloco = bloco + " " + linhaDummy;
											} else {
												bloco = bloco + "\n" + linhaDummy;
											}
											linhaDummyAnterior = linhaDummy;
											linhaDummy = carregaLinha(sequencial,true);
											if(verificaSeLinhaTemNumProcesso(linhaDummy)) {
												saida = true;
												break;
											}
											if(linhaDummy.contains("&")) {
												linhaDummy = limpaCaracteres(linhaDummy);
											}
											continue;
										}
								//	}
								}
							}					
							saida = true;
							break;
						}	
					}
				}
			}
		if(!bloco.isEmpty()) {
			bloco = bloco + "\n";
		}
		sequencial--;											// ajuste para no retorno continua na próxima linha após os intimados
		return bloco;
	}
		
	public static int contaPalavras(String linhaDummy) {
		int ix = 0;
		int var = linhaDummy.split(" ", -1).length - 1;      	//pega a quantidade de espaços em branco
		String var2[] = new String[var];                		//define o vetor que conterá as palavras separadas da string
		var2 = linhaDummy.split(" ");                        	//separa a string colocando as palavras no vetor
		for(int i = 0; i <= var; i++){
		    ix++;
		}
		return ix;
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
		int letrasIguais = 0;
		int esp1 = comparado.split(" ", -1).length - 1; 
		int esp2 = comparador.split(" ", -1).length - 1;
		int limite = 0;
		int edge = 0;
		double percent = 0.f;

		int k = 0;
		String plvrsCmprdo[] = new String[esp1];  
		String plvrsCmprdr[] = new String[esp2];

		plvrsCmprdo = comparado.split(" ");
		plvrsCmprdr = comparador.split(" ");
		
		if(plvrsCmprdo.length <= plvrsCmprdr.length) {
			limite = plvrsCmprdo.length-1;
		} else {
			limite = plvrsCmprdr.length-1;
		}

		for(int ix=0; ix<=limite; ix++) {
			if(plvrsCmprdo[ix].equals(plvrsCmprdr[ix])) {
				palavrasIguais++;										
			} else {
				if(plvrsCmprdo[ix].length() <= plvrsCmprdr[ix].length()) {
					edge = plvrsCmprdo[ix].length()-1;
				} else {
					edge = plvrsCmprdr[ix].length()-1;
				}
				for(int il=0; il<=edge; il++) {
					if(plvrsCmprdo[ix].charAt(il) == plvrsCmprdr[ix].charAt(il)){
						letrasIguais++;
					}
				}
				percent = (double)letrasIguais/plvrsCmprdr[ix].length();
				if(letrasIguais > 7.0) {
					palavrasIguais++;
				}
			}
		}
		
		if(palavrasIguais > 0) {
			if(palavrasIguais == comparado.length()) {
				return true;
			}
			if((palavrasIguais / comparador.length()) < 3) {	// esse teste tá errado
				return true;
			}
		}
		return false;
	}
	
	public static String trataNumEdicao(String linhaDummy) {
		String[] gabaritos = new String[3];
		gabaritos[0] = "\\d{4}\\W\\d{4}";
		gabaritos[1] = "\\d{5}\\W\\d{4}";
		gabaritos[2] = "\\d{6}\\W\\d{4}";
		int var = linhaDummy.split(" ", -1).length - 1;      
		String var2[] = new String[var];                
		var2 = linhaDummy.split(" ");                        
		if (!var2[0].equals("")){
			for (int inx = 0; inx <= 2; inx++){
				if(var2[0].matches(gabaritos[inx])){
					return var2[0];
				}					
			}		
		}
		return null;
	}
		
	public static String ultimaPalavra(String linhaDummy) {
		int ix = 0;
		int var = linhaDummy.split(" ", -1).length - 1;
		String var2[] = new String[var];                
		var2 = linhaDummy.split(" ");   
		return var2[var2.length-1];
	}
	
	public static String primeiraPalavra(String linhaDummy) {
		int var = linhaDummy.split(" ", -1).length - 1;
		String var2[] = new String[var];                
		var2 = linhaDummy.split(" ");   
		if(var2.length == 0) {
			return " ";
		}
		return var2[0];
	}

	public static void finalizaProcesso() throws IOException {
		msgWindow.incluiLabel("Fim do processamento, incluidos " + qtdPublicacoes + "no servidor");
		JOptionPane.showMessageDialog(null, "Fim do Processamento");
		gravaLog(obtemHrAtual() + " fim do processamento");
	//	arquivoLog.close();
        System.exit(0);
	}
		
	public static boolean conectaServidor() throws IOException {
		msgWindow.incluiLinha(obtemHrAtual() + " - Conexão com o servidor ");
		conexao.setUser(usuario);
		conexao.setPassword(password);
		conexao.setUrl(url);
		sessao = InterfaceServidor.serverConnect();
		if (sessao == null) {
			msgWindow.incluiLinha(obtemHrAtual() + " - Houve erro na conexão com o servidor ");
			finalizaProcesso();
			return false;
		}
		return true;
	}
		
	public static void carregaDiario(File input) throws Exception{								
		gravaLog(obtemHrAtual() + " Carga do Diário Oficial");
	    String texto = "";
		msgWindow.incluiLinha(obtemHrAtual() +" - Carregando o Diário Oficial - Aguarde ...");
		try {
			PDDocument pd = PDDocument.load(input);
	        PDFTextStripper stripper = new PDFTextStripper();  	
	        texto = stripper.getText(pd);
	        separaLinhas(texto);
	        pd.close();
	    }
	    catch (IOException e) {
	    	JOptionPane.showMessageDialog(null, "Erro no carregamento do Diário Oficial  -> " + e);
	    }
	}
		
	public static void separaLinhas(String texto) {
		String result = "";
		int endIndex = 0 ;
		result = texto.replaceAll("\\n", "%%");
		result = result.replaceAll("\\r", "");
		int beginIndex = result.indexOf("%%");

		for(int i = 0; i <= result.length()-1; i++) {
			endIndex = result.indexOf("%%", beginIndex+2);
			if(endIndex < 0) break;
			bufferEntrada.add(result.substring(beginIndex+2, endIndex));
			beginIndex = endIndex;
		}
		bufferEntrada.add("*** MARCA FIM ***");
	}
		
	public static boolean contemNumeros(String str) {
		
		int numDigitos = 0;
        if (str == null || str.length() == 0) return false; 
        for (int i = 0; i <= str.length()-1; i++) {
            if (Character.isDigit(str.charAt(i)))
                numDigitos++;
        }
        if(numDigitos >= 5) {
        	return true;
        }
        return false;
    }
		
	public static String obtemNumeroProcesso(String linhaDummy){				

		String sequencia = "";
		String[] gabaritos = new String[17];

		gabaritos[0] = "\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}";
		gabaritos[1] = "\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{3}";
		gabaritos[2] = "\\d{5}\\W\\d{4}\\W\\d{3}\\W\\d{2}\\W\\d{2}\\W\\d";
		gabaritos[3] = "\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{3}\\W\\d{3}";
		gabaritos[4] = "\\d{5}\\W\\d{4}\\W\\d{3}\\W\\d{2}\\W\\d{2}\\W\\d";
		gabaritos[5] = "\\d\\W\\d{5}\\W\\d\\W\\d{3}\\W\\d{2}\\W\\d{2}\\W\\d";
		gabaritos[6] = "\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\{2}\\W\\d{4}";
		gabaritos[7] = "\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}\\W{2}\\d{5}\\W\\d{4}\\W\\d{3}\\W\\d{2}\\W\\d{2}\\W\\d\\W}";
		gabaritos[8] = "\\w{7}\\W\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}";
		gabaritos[9] = "\\d{3}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}";
		gabaritos[10] = "\\d{5}\\W\\d\\W\\d{3}\\W\\d{2}\\W\\d{2}\\W\\d";
		gabaritos[11] = "\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}\\W";
		gabaritos[12] = "\\w{8}\\s\\w\\W\\s\\w{3}\\W\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}";
		gabaritos[13] = "\\w{8}\\s\\w\\W\\s\\w{3}\\W\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}\\s\\W\\w{4}\\W";
		gabaritos[14] = "\\w{6}\\s\\{2}w\\s\\w{8}\\W\\s\\W\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}\\s\\W\\s\\w{3}";
		gabaritos[15] = "\\w{8}\\s\\w\\W\\s\\w{3}\\s\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}\\s\\W\\d{2}\\W";
		gabaritos[16] = "\\w{4}\\W\\s\\w\\W\\s\\w{3}\\s\\W\\s\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{3}";
		
		if(!contemNumeros(linhaDummy)) {
			return null;
		}
				
		linhaDummy = linhaDummy.replace(" ", ".");
	
		for (int ix = 0; ix <= linhaDummy.length()-1; ix++) {
			if ((linhaDummy.charAt(ix) >= '0' && linhaDummy.charAt(ix) <= '9') || 
					(linhaDummy.charAt(ix) == '.' || 
					linhaDummy.charAt(ix) == '-' || 
					linhaDummy.charAt(ix) == '/' || 
					linhaDummy.charAt(ix) == ',')){
				
					if ((linhaDummy.charAt(ix) >= '0' && linhaDummy.charAt(ix) <= '9')||
						((linhaDummy.charAt(ix) == '.' || 
						linhaDummy.charAt(ix) == '/' || 
						linhaDummy.charAt(ix) == ',' || 
						linhaDummy.charAt(ix) == '-')) && sequencia.length() > 0) {						
								sequencia = sequencia + linhaDummy.charAt(ix);
					}		 
			}
		}
				
		if(sequencia.length() >= 26) {
			sequencia = sequencia.substring(0, 25);
		}

		if (!sequencia.equals("")){
			for (int inx = 0; inx <= 16; inx++){
				if(sequencia.matches(gabaritos[inx])){
					return sequencia;
				}					
			}		
		}
		return null;
	} 			// fim do metodo extraiprocessoLido
		
	public static void inicializaArquivos() throws IOException{	
		
		JFileChooser arquivo = new JFileChooser();
		arquivo.showDialog(null, "Selecionar um Arquivo");
        File diario = arquivo.getSelectedFile();

        if (diario != null){
        //	logFolder = new File(diario.getParentFile()+logFolder);
    		intermedio = new File(diario.getParentFile()+"/intermedio.txt");
    		assuntos = new File(diario.getParentFile()+"/subjects.txt");
    	//	config = new File(diario.getParentFile()+"/split.cnf");			// ñ é usado
        	diarioInput = diario;
        }
	}																		// Fim do método InicializaArquivos
	
	public static String limpaCaracteres(String linhaDummy) { 
		linhaDummy = linhaDummy.replaceAll("&","e");
		return linhaDummy;
	}
		
	public static String formataPalavra(String palavra) {    			// retira acentos e transforma para minusculas
		 
		palavra = palavra.replaceAll("[aáàãâäåAÁÀÃÂÄÅ]","a");
        palavra = palavra.replaceAll("[eéèêëEÉÈÊË]","e");
        palavra = palavra.replaceAll("[iíìîïIÍÌÎÏ]","i");
        palavra = palavra.replaceAll("[oóòôöOÓÒÔÖÕ]","o");
        palavra = palavra.replaceAll("[uúùûüUÚÙÛÜ]","u");
        palavra = palavra.replaceAll("[çÇ]","c");
        palavra = palavra.replaceAll("[B]","b");
        palavra = palavra.replaceAll("[C]","c");
        palavra = palavra.replaceAll("[D]","d");
        palavra = palavra.replaceAll("[F]","f");
        palavra = palavra.replaceAll("[G]","g");
        palavra = palavra.replaceAll("[H]","h");
        palavra = palavra.replaceAll("[J]","j");
        palavra = palavra.replaceAll("[L]","l");
        palavra = palavra.replaceAll("[M]","m");
        palavra = palavra.replaceAll("[N]","n");
        palavra = palavra.replaceAll("[P]","p");
        palavra = palavra.replaceAll("[R]","r");
        palavra = palavra.replaceAll("[S]","s");
        palavra = palavra.replaceAll("[T]","t");
        palavra = palavra.replaceAll("[Q]","q");
        palavra = palavra.replaceAll("[V]","v");
        palavra = palavra.replaceAll("[X]","x");
        palavra = palavra.replaceAll("[Z]","z");
        palavra = palavra.replaceAll("[W]","w");
        palavra = palavra.replaceAll("[Y]","y");
        palavra = palavra.replaceAll("[K]","k");
        
        String palavraFormatada = "";

        for(int i=0; i <= palavra.length()-1; i++) {
        	if ((palavra.charAt(i) == ' ' && (i > 0 && palavra.charAt(i-1) == ' '))){
        		continue;
        	} else {
        		palavraFormatada = palavraFormatada + palavra.charAt(i);
        	}        	
        }      
    return palavraFormatada;  
    }

	public static void carregaIndice() throws IOException {
		//gravaLog(obtemHrAtual() + " Carregamento do indice");
		String grupo = "";
		String strPagina = "";
		String paginaSecao = "";
		String secao = "";
		String dummy = "";
		String paginaGrupo = "";
		String linha = "";
		String rabo = "";
		String complemento = "complemento";
		int indexSecao = 0;
		int indexGrupo = 0;
		int seq = 0;
		boolean continua = false;
		boolean ehGrupo = false;

		sequencialIndice = bufferEntrada.indexOf("SUMÁRIO")+1;
		bufferEntrada.remove(sequencialIndice-1);
		sequencialIndice--;
		
		if(sequencialIndice > 0) {
			while(sequencialIndice <= bufferEntrada.size()-1) {	
				linha = carregaLinhaIndice();
				seq = seqIndex;
				if(linha.charAt(0) == ' ')  {
					ehGrupo = true;
				}	
				rabo = ultimaPalavra(linha).trim();
				linha = linha.trim();
				if(linha.equals("*** MARCA FIM ***")) {
					ultimaLinha = sequencialIndice;
					break;
				}
				
				if(!continua) {
					if(ehInteiro(rabo) && contaPalavras(linha) > 1) {
						strPagina = rabo;
						dummy = linha.substring(0, linha.length()-strPagina.length());
						bufferEntrada.remove(sequencialIndice-1);
						sequencialIndice--;
					} 

					if(dummy.isEmpty()) {
						dummy = linha;
						bufferEntrada.remove(sequencialIndice-1);
						sequencialIndice--;
						if(!ehInteiro(rabo)) {
							continua = true;
						} 
					} 
				} else {
					dummy = dummy + " " + linha;
					continua = false;
					bufferEntrada.remove(sequencialIndice-1);
					sequencialIndice--;
					continue;
				}

				if(ehInteiro(rabo) && contaPalavras(linha) == 1) {
					strPagina = linha.trim();
					bufferEntrada.remove(sequencialIndice-1);
					sequencialIndice--;
				}

				if(ehInteiro(rabo)) {
					if(!ehGrupo){
						secao = dummy.trim();																													
						paginaSecao = strPagina;
						indexSecao = seqIndex;
						grupo = "";
						dummy = "";
					} else {
						grupo = dummy.trim();
						indexGrupo = seqIndex;
						paginaGrupo = rabo;
						IndiceEdicao regIndice = new IndiceEdicao(secao, 
																	Integer.parseInt(paginaSecao), 
																	0, 
																	complemento, 
																	grupo, 
																	Integer.parseInt(paginaGrupo), 
																	0, 
																	indexSecao, 
																	indexGrupo);
						Index.add(regIndice);
						ultimaPagina = Integer.parseInt(paginaGrupo);
						strPagina = "";
						dummy = "";
						ehGrupo = false;
					}
				}
			}	// fim do while
		} else {
			msgWindow.incluiLinha(obtemHrAtual() + " - Índice do Diário Oficial não localizado ");
			finalizaProcesso();
		}
	}							
		
	public static boolean ehInteiro( String linhaDummy ) {		    
	    char[] c = linhaDummy.toCharArray();
	    boolean d = true;		    
	    if(linhaDummy.equals("") || linhaDummy.equals(" ")){
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
	
	private static String obtemEdicao(String linhaDummy){			

		int i = 0;
		int ln = 0;
		int numero, alfa, conv, x, ix;
		String[] grupos = new String[160];
		ArrayList<String> meses = new ArrayList<String>();
		char[] digitos;
		String dia = ""; 
		String mes = ""; 
		String ano = "";
		String argumento;
		
		grupos[ln] = "";
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
		
		// decomposição da grupos em palavras/numeros
		while (i <= linhaDummy.length()-1) {	
			if((linhaDummy.charAt(i) >= 'A' && linhaDummy.charAt(i) <= 'Z') || 
					(linhaDummy.charAt(i) >= 'a' && linhaDummy.charAt(i) <= 'z') ||
					(linhaDummy.charAt(i) == 'á' || linhaDummy.charAt(i) == 'é' ||
					linhaDummy.charAt(i) == 'í' || linhaDummy.charAt(i) == 'ó' ||
					linhaDummy.charAt(i) == 'ú' || linhaDummy.charAt(i) == 'ã' ||
					linhaDummy.charAt(i) == 'õ' || linhaDummy.charAt(i) == 'ç' ||
					linhaDummy.charAt(i) == 'Á' || linhaDummy.charAt(i) == 'É' ||
					linhaDummy.charAt(i) == 'Í' || linhaDummy.charAt(i) == 'Ó' ||
					linhaDummy.charAt(i) == 'Ú' || linhaDummy.charAt(i) == 'Ã' ||
					linhaDummy.charAt(i) == 'Õ' || linhaDummy.charAt(i) == 'Ç')) {					
						grupos[ln] = grupos[ln] + linhaDummy.charAt(i);					
				}
			
			if((linhaDummy.charAt(i) >= '0' && linhaDummy.charAt(i) <= '9') || (linhaDummy.charAt(i) == '/')){
				grupos[ln] = grupos[ln] + linhaDummy.charAt(i);
			}			
			if (linhaDummy.charAt(i) == ' ' || linhaDummy.charAt(i) == ','){
				ln++;
				grupos[ln] ="";
			}
			i++;
		}
		
		// análise dos grupos decompostos
		alfa = 0;
		numero = 0;
		for(ix = 0; ix <= ln; ix++){								// verifica grupos iniciais
			argumento = formataPalavra(grupos[ix]);
			digitos = argumento.toCharArray();
			for (x = 0; x <= argumento.length()-1; x++){
				if(!Character.isDigit(digitos[x])){
					alfa++;
				} else {
					numero++;
				}
			}
			
			if(alfa >0 && numero == 0){								// verifica se o grupo é um mês
				if(alfa >= 4 && alfa <= 9){
					if(meses.contains(argumento)){

						switch(argumento) {
						case "janeiro":
							mes = "01";	
							break;
						case "fevereiro":
							mes = "02";	
							break;
						case "marco":
							mes = "03";
							break;
						case "abril":
							mes = "04";
							break;
						case "maio": 
							mes = "05";
							break;
						case "junho":
							mes = "06";
							break;
						case "julho":
							mes = "07";
							break;
						case "agosto":
							mes = "08";
							break;
						case "setembro":
							mes = "09";
							break;
						case "outubro":
							mes = "10";
							break;
						case "novembro":
							mes = "11";
							break;
						case "dezembro":
							mes = "12";
							break;
						default:
							mes = "01";
						}
					}					
				}
			}
		
			if((numero == 2 || numero == 1) && alfa == 0){							// verifica se o grupo é um dia
				conv = Integer.parseInt(argumento);
				if (conv >= 1 && conv <= 31){
					dia = argumento;
				}
			}
			
			if((numero == 4 || numero == 2) && alfa == 0){							// verifica se o grupo é um ano
				ano = argumento;
			}
			
		alfa = 0;
		numero = 0;
		}
		dataEdicao = dia + "-" + mes + "-" + ano;
		return ano + "-" + mes + "-" + dia;
	}
	
/*	
	public static boolean verificaDataValida(String linha){

		String linhaData = linha;
		String registro = formataPalavra(linhaData);
		registro = registro.replaceAll("/","");

		if(registro.contains("recifepe")) {
			linhaData = registro.replaceAll("recifepe","recife");
		}
		
		int var = linhaData.split(" ", -1).length - 1;
		String linhaDecomposta[] = new String[var];
		ArrayList<String> uf = new ArrayList<String>();

		uf.add("rio de janeiro");
		uf.add("sao paulo");
		uf.add("belo horizonte");
		uf.add("salvador");
		uf.add("rio de janeiro");
		uf.add("recife");
		uf.add("fortaleza");
		uf.add("belem");
		uf.add("curitiba");
		uf.add("brasilia");
		uf.add("manaus");
		uf.add("florianopolis");
		uf.add("porto velho");
		uf.add("campinas");
		uf.add("sao luiz");
		uf.add("vitoria");
		uf.add("goiania");
		uf.add("aracaju");
		uf.add("natal");
		uf.add("teresina");
		uf.add("cuiaba");
		uf.add("campo grande");
		uf.add("barreiros");

		if(linhaData.charAt(linhaData.length()-1) == '.') {
			linhaData = linhaData.substring(0, linhaData.length()-1);
		}

		linhaDecomposta = linhaData.split(" ");
	
		//if(ehInteiro(linhaDecomposta[0]) && linhaDecomposta[0].length() == 2) {							// DD/MM/AAAA
		//	if(linhaData.matches("\\d{2}\\W\\d{2}\\W\\d{4}") && linhaData.length() == 10){
		//		return true;
		//	}
		//} 

		//if(!ehInteiro(linhaDecomposta[0]) && ehInteiro(linhaDecomposta[linhaDecomposta.length-1])) {	// extenso			
		//	if((linhaDecomposta[0].charAt(linhaDecomposta[0].length()-1) == ',') && 
		//			uf.contains(formataPalavra(linhaDecomposta[0].substring(0, linhaDecomposta[0].length()-1)))) {
		//		return true;
		//	}
		//} 

		if((((ehInteiro(linhaDecomposta[0]) && linhaDecomposta[0].length() == 2)) && (linhaData.matches("\\d{2}\\W\\d{2}\\W\\d{4}") && linhaData.length() == 10)) ||
				((!ehInteiro(linhaDecomposta[0]) && ehInteiro(linhaDecomposta[linhaDecomposta.length-1])) && 
				((linhaDecomposta[0].charAt(linhaDecomposta[0].length()-1) == ',') && uf.contains(formataPalavra(linhaDecomposta[0].substring(0, linhaDecomposta[0].length()-1))))))
			{
				return true;
		} 

		return false;
	}
*/
	private static boolean verificaDataFinal(String linhaComData, int sequencialReferencia){
		
		/**
		 * Verfica se a data lida é a data_final da publicação
		 */
		sequencialReferencia++;
		String linhaDummy = "";
		boolean encontrouAssunto = false;
		boolean encontrouProcesso = false;
		boolean encontrouFuncao = false;
		boolean encontrouGrupo = false;
		int proximoGrupo = localizaProximoGrupo(sequencialReferencia);
		
		if(verificaDataValida(linhaComData)) {	
			for(int x=sequencialReferencia; x<=sequencialReferencia+12; x++) {			// localiza proximo grupo
				linhaDummy = carregaLinha(x, false).trim();
				if(validaFuncao(linhaDummy) && !encontrouFuncao) {
					encontrouFuncao = true;
				}
				if(validaAssunto(linhaDummy) && !encontrouAssunto) {
					encontrouAssunto = true;
				}
				if(verificaSeLinhaTemNumProcesso(linhaDummy) && !encontrouProcesso) {
					if(!obtemNumeroProcesso(linhaDummy).equals(processoNumero)) {
					//	if(encontrouFuncao || encontrouAssunto) {						// avaliar se é uma regra valida
							encontrouProcesso = true;									// pode encontrar processo sem antes encontrar assunto ou funcao?
					//	}																// 
					}
				}
				if(x == proximoGrupo) {													// atigiu o limite antes das 12 linhas
					break;
				}
			}

			if(encontrouFuncao) {
				return true;
			} else if(encontrouAssunto || encontrouProcesso) {
				return true;
			} else if(encontrouGrupo) {
				return true;
			}
		}		
		return false;
	}
		
	public static String completaEsquerda(String value, char c, int size) {
		String result = value;
		while (result.length() < size) {
			result = c + result;
		}
		return result;
	}
		
	public static int atualizaPagina(String argumento){
		
		int posicao = argumento.length()-1;
		String numeroPagina = "";
		
		while(posicao >= 0){

			if ((argumento.charAt(posicao) >= 'a' && argumento.charAt(posicao) <= 'z') || (argumento.charAt(posicao) >= 'A' && argumento.charAt(posicao) <= 'Z')){
				break;
			} else {
				if(argumento.charAt(posicao) >= '0' && argumento.charAt(posicao) <= '9'){	// a linha do indice tem nº da pagina
					numeroPagina = argumento.charAt(posicao) + numeroPagina;
				}
			}
			posicao--;
		}
		return Integer.parseInt(numeroPagina);
	}
		
	public static boolean verificaGrupo(String argumento){

		for (IndiceEdicao linhas : Index) {
			if(linhas.grupo.equals(argumento)){
				return true;
			}
		} 
		return false;
	}
		
	public static int localizaIndice(String sec, String cmp, String grp){

		for(int i = 0; i < Index.size(); i++){

			if(Index.get(i).secao.equals(sec) && Index.get(i).complementoSecao.equals(cmp) && Index.get(i).grupo.equals(grp)){
				return i;
			}
		}
		return -1;
	}
		
	private static void mapeiaLinhas() throws IOException{		// mapeia o numero de linha de cada Secao e cada Grupo

		int pagina = 0;
		int sequencia = 0;
		int linhaSecao = 0;
		int regIndice = 0;
		int countador = 0;
		int linhasLidas = 0;
		String linha = "";
		String secaoAnterior = " ";
		String linhaDummy = "";

        for (IndiceEdicao Indice : Index) {								// loop do Index
        	if(!secaoAnterior.equals(Indice.secao)){
        		secaoAnterior = "";
        	}							
        	while(sequencia <= bufferEntrada.size()-1){					// 	Loop de procura pela SECAO

    	        linha = bufferEntrada.get(sequencia);
		        sequencia++;

		        if(linha == null) {
		        	break;
		        }

		        if(linha.contains("DEJT Nacional")){
	        		pagina = 1;
	        	}
		        
		        if(linha.contains("Tribunal Regional do Trabalho da") && (linha.substring(0, 18).matches("\\d{4}\\W\\d{4}\\s\\w{8}"))){
		        	pagina = atualizaPagina(linha);										
		        }

	        	if((Indice.secao.contains(linha.trim()) && Indice.paginaSecao == pagina && secaoAnterior.equals(""))){
	        		if(linha.trim().length() == Indice.secao.length()){
	        			linhaSecao = sequencia-1;
	    	        	secaoAnterior = Indice.secao;
	        		} else if(linha.length() < Indice.secao.length()){
	        			countador = sequencia;
						while(countador <= 3){		
							linhaDummy = linha = bufferEntrada.get(countador).trim();
	        				if(linhaDummy.contains("Código para aferir autenticidade deste caderno") || 
	        						linhaDummy.contains("Tribunal Regional do Trabalho da") ||
	        						linhaDummy.contains("Data da Disponibilização:")){
	        					continue;
		        			} else if(Indice.secao.contains(linha.trim() + " " + linhaDummy.trim())){
		        				linhaSecao = sequencia-1;
			    	        	secaoAnterior = Indice.secao;
			    	        	countador = 4;
		        			}
	        				countador++;	        				
	        			}
						linhaDummy = "";
	        		}
	        	}

	        	if(Indice.grupo.equals(linha.trim()) && Indice.paginaGrupo == pagina){
	        		countador = sequencia;
	        		linhasLidas = 0;
					while(countador <= 30){
						linhaDummy = bufferEntrada.get(countador).trim();
        				if(linhaDummy.contains("Código para aferir autenticidade deste caderno") || 
        						linhaDummy.contains("Tribunal Regional do Trabalho da") ||
        						linhaDummy.contains("Data da Disponibilização:")){
        					continue;
        				} else {	
    						if(obtemNumeroProcesso(linhaDummy) != null || linha.equals("Portaria")){	
	        					break;       					
	        				}
	        			}
        				countador++;
        				linhasLidas++;
        			}						
					linhaDummy = "";						
					if(linhasLidas <= 30){
	    	        	if(secaoAnterior != null){	
		    	        	regIndice = localizaIndice(Indice.secao, Indice.complementoSecao, Indice.grupo);  	        			    	        	
		    	        	if(regIndice >= 0){
		    	        		Index.get(regIndice).setLinhaSecao(linhaSecao);
		    	        		Index.get(regIndice).setLinhaGrupo(sequencia-1);
		    	        		saida = true;
		    	        		break;		    	        		
		    	        	}    	        	     	
	    	        	}
					} else {
						continue;
					}
		        }
        	}				// fim do while
        }					// fim do for
	}
		
	public static int localizaProximoGrupo(int numeroLinha){

		for (IndiceEdicao elemento : Index) {
			if(elemento.linhaSecao > numeroLinha){
				return elemento.linhaSecao;
			}				
			if(elemento.linhaGrupo > numeroLinha){
				return elemento.linhaGrupo;
			}
		} 
		return -1;
	}
		
	public static void carregaAssuntos() throws IOException{
		gravaLog(obtemHrAtual() + "Carga da tabela de assuntos");
		String linha = "";
		String linhaTratada = "";
		String [] arrayAssunto;
        FileInputStream arquivoIn = new FileInputStream(assuntos);
		//BufferedReader registro = new BufferedReader(new InputStreamReader((arquivoIn), "UTF-8"));
		BufferedReader registro = new BufferedReader(new InputStreamReader((arquivoIn)));
        while(linha != null){
	    	linha = registro.readLine();
	    	
	    	if(linha == null) {
	    		break;
	    	} else {
	    		linhaTratada = formataPalavra(linha);
	    	}

    		if(!linhaTratada.contains("PODER JUDICIÁRIO") || linhaTratada.length() != 16){
    			if(!tabelaAssuntos.contains(linhaTratada)) {
    				tabelaAssuntos.add(linhaTratada);
    				arrayAssunto = linhaTratada.split(" ");
    				if(arrayAssunto.length > maiorAssunto) {
    					maiorAssunto = arrayAssunto.length;
    				}
    			}	    			
    		}	    			    	
        }
        registro.close();
	}

	public static void inicializaEdital() throws Exception {
		gravaLog(obtemHrAtual() + "Publicação inicializada " + seqEdicao);
		Edital.setTribunal(strTribunal);
		Edital.setSeqEdicao(seqEdicao);
		Edital.setDescricao(descricaoFolder);
		Edital.setEdicao(edicao);
		Edital.setStrEdicao(strEdicao);
		Edital.setFolder(edtFolderName);
		Edital.setCliente(cliente);
		editalFolder = InterfaceServidor.verificaEdtFolder(sessao, pastaBase, edtFolderName, descricaoFolder, strTribunal, edicao);
	}

	public static void abreSaida() {
		try {
			//arqSaida = new FileWriter("C:\\srv\\kraio\\saida.txt");
			arqSaida = new FileWriter("/Users/avmcf/SIJ/saida.txt");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void gravaSaida(String linha) throws IOException {
		
		try {
			arqSaida.write(linha + "\r");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void abreLog(String logName) throws IOException {
		/*
		try {
			fileW = new FileWriter (logName);
			BufferedWriter buffW = new BufferedWriter (fileW);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}
		
	public static void gravaLog(String registroLog) throws IOException {
/*
		try {
			arquivoLog.write(registroLog + "\n");
			buffW.write (registroLog + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
*/
	}
	
	public static void lista() throws IOException{
		
		//String fileName = "C:\\srv\\kraio\\indice.txt";
		FileWriter fileW = new FileWriter ("/Users/avmcf/Projetos/sij/desenvolvimento/_DOS/numerada.txt");
		BufferedWriter buffW = new BufferedWriter (fileW);
		//FileWriter fileW = null;
		//BufferedWriter buffW = null;
		try {
		//	fileW = (FileWriter) new OutputStreamWriter(new FileOutputStream(fileW), StandardCharsets.UTF_8);
		//	fileW = (FileWriter) new OutputStreamWriter(new FileOutputStream("/Users/avmcf/SIJ/_DOS/lista.txt"), StandardCharsets.UTF_8);
			//for (IndiceEdicao Indice : Index) {
			for (int x = 0; x <= bufferEntrada.size()-1; x++) {
			//for (int x = 0; x <= paragrafos.size()-1; x++) {
				//buffW.write (Indice.secao + " - " + Indice.linhaSecao + " - " + Indice.grupo + " - " + Indice.linhaGrupo);
				buffW.write (x + " - " + bufferEntrada.get(x) + "\n");
				//buffW.write (x + " - " + paragrafos.get(x));
				//buffW.write (paragrafos.get(x));
				//buffW.newLine ();
			}
	        buffW.close ();
		} catch (IOException io)
        {
			JOptionPane.showMessageDialog(null, "Falha na listagem ");
        }
	}
	
	public static void gravaIndice() throws IOException{
		
		String fileName = "C:\\srv\\kraio\\indice.txt";
		//FileWriter fileW = new FileWriter ("C:\\srv\\kraio\\indice.txt");
		//BufferedWriter buffW = new BufferedWriter (fileW);
		FileWriter fileW = null;
		BufferedWriter buffW = null;
		try {
			fileW = (FileWriter) new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8);
			for (IndiceEdicao Indice : Index) {
				buffW.write (Indice.secao + " - " + Indice.linhaSecao + " - " + Indice.grupo + " - " + Indice.linhaGrupo);
	            buffW.newLine ();
			}
	        buffW.close ();
		} catch (IOException io)
        {
			JOptionPane.showMessageDialog(null, "Falha na gravação do indice ");
        }
		
	}

/*	Método enviaEdital em desenvolvimento, juntamente com a formatação do texto da publicação
	public static void enviaEdital() throws ParseException, IOException{

		int ultimoSequencial = 1;
		String nomeFile = strTribunal + "-" + seqEdicao + "-" + seqPublicacao + ".txt";
		String textoDummy = "";
		String introducao = "";
		String linhaDummy = "";
		base.setFileName(nomeFile);
				
		Edital.setTitulo1(titulo1);
		Edital.setTitulo2(titulo2);
		Edital.setTitulo3(titulo3);
		Edital.setTitulo4(titulo4);
		Edital.setTitulo5(titulo5);
		Edital.setStrEdicao(strEdicao);
    	Edital.setVara(secao);
    	Edital.setGrupo(grupo);
		Edital.setAssunto(assunto);
		Edital.setAtores(atores);
		Edital.setIntimados(intimados);
    	Edital.setProcesso(processo);

		String linhaTraco =  "------------------------------------------------------------------------------------------------------------------------";
		String linhaRodaPe = "SplitDO " + versaoSplitter + "                  V&C Consultoria Ltda. (81) ‭982 386 404                             vconsulte@gmail.com‬" ;

		textoSaida.add(titulo1);
		textoSaida.add(titulo2);
		textoSaida.add(titulo3);
		textoSaida.add(titulo4);
		textoSaida.add(titulo5 + "\n");
		textoSaida.add(linhaTraco + "\n");
		textoSaida.add(secao);
		textoSaida.add(grupo);
		textoSaida.add(assunto + " " + complementoAssunto  + "\n");
		textoSaida.add(processo);
		textoSaida.add(linhaPauta);
		textoSaida.add(atores + "\n");
		textoSaida.add(intimados);

		if(textoIntroducao.size() > 0) {
			textoSaida.add(formataParagrafo(textoIntroducao));
			textoSaida.add("\n");
		}
		
		if(paragrafos.size() > 0) {
			for(String linha : paragrafos){
				textoSaida.add(linha);
			}
		}
		
		if(!linhaParagrafo.isEmpty() && pauta) {
			textoSaida.add(linhaParagrafo);
		}

		textoSaida.add(" \n");
		//textoSaida.add(cliente);				Não pode porque vai confundir o CLIPPING
		textoSaida.add(linhaTraco);
		textoSaida.add(linhaRodaPe);
		textoSaida.add(" \n");

		Edital.setTexto(textoSaida);
		
		if (InterfaceServidor.incluiEdital(sessao, editalFolder)) {
			if((sequencialSaida - ultimoSequencial == 100) || sequencialSaida == 1) {
				ultimoSequencial = sequencialSaida;
				gravaLog(obtemHrAtual() + "publicação " + sequencialSaida + " enviada com sucesso");
			}
		} else {
			msgWindow.incluiLinha("Houve erro na gravação da publicação Nº " + sequencialSaida + " Gravado com sucesso");
			gravaLog(obtemHrAtual() + "Erro no envio da publicação " + sequencialSaida );
			finalizaProcesso();
		}

		//msgWindow.incluiLinha(obtemHrAtual() + " Publicação nº: " + seqPublicacao);
		textoSaida.clear();
		qtdPublicacoes++;
	}
*/
	
	public static boolean examinaPublicacao(ArrayList<String> texto) {
		boolean processoLocalizado = false;
		boolean processoRepetido = false;
		boolean assuntoLocalizado = false;
		boolean assuntoRepetido = false;

		String linha = "";
		
		for(int x=0; x<=texto.size()-1; x++) {
			if(linha != null) {
				if(validaAssunto(linha)) {
					if(validaAssunto(linha) && assuntoLocalizado) {
						assuntoRepetido = true;
					}
					if(validaAssunto(linha) && !assuntoLocalizado) {
						assuntoLocalizado = true;
					}
					continue;
				}
				
				linha = obtemNumeroProcesso(texto.get(x));
				if(linha != null && linha.equals(processoNumero) && !processoLocalizado) {
					processoLocalizado = true;
				}
				if(processoLocalizado && linha != null) {
					processoRepetido = true;
				}
				continue;
			} else {
				continue;
			}
		}
		
		if(assuntoLocalizado && processoLocalizado) {
			return true;
		}
		return false;
	}
	
	private static void enviaEdital() throws ParseException, IOException{
		String labelFile = "";
		int ultimoSequencial = 1;
	
		if((processoLinha.isEmpty() || textoEdital.isEmpty()) && !grupo.equals("Pauta")) {
			labelFile = "verificar";
		} else {
			labelFile = "Publicação";
		}
		
		base.setFileName(strTribunal + "-" + seqEdicao + "-" + seqPublicacao + ".txt");
				
		Edital.setTitulo1(titulo1);
		Edital.setTitulo2(titulo2);
		Edital.setTitulo3(titulo3);
		Edital.setTitulo4(titulo4);
		Edital.setTitulo5(titulo5);
		Edital.setStrEdicao(strEdicao);
    	Edital.setVara(secao);
    	Edital.setGrupo(grupo);
		Edital.setAssunto(assunto);
		Edital.setAtores(atores);
		Edital.setIntimados(intimados);
    	Edital.setProcesso(processoNumero);
    	Edital.setLabel(labelFile);

		String linhaTraco =  "------------------------------------------------------------------------------------------------------------------------";
		String linhaRodaPe = "SplitDO " + versaoSplitter + "                  V&C Consultoria Ltda. (81) ‭982 386 404                             vconsulte@gmail.com‬" ;

		textoSaida.add(titulo1);
		textoSaida.add(titulo2);
		textoSaida.add(titulo3);
		textoSaida.add(titulo4);
		textoSaida.add(titulo5 + "\n");
		textoSaida.add(linhaTraco + "\n");
		textoSaida.add(secao);
		textoSaida.add(grupo);
		
		if(!ordemDePauta.isEmpty()) {
			if(!linhaAntesAssunto.isEmpty()) {
				textoSaida.add(linhaAntesAssunto);
			}			
			textoSaida.add(assunto + " " + complementoAssunto  + "\n");
			if(textoIntroducao.size() > 0) {
				for(String linha : textoIntroducao){
					textoSaida.add(linha);
				}
				textoSaida.add("\n");
			}
			textoSaida.add(ordemDePauta);
			textoSaida.add(processoLinha);
			if(!linhaPosProcesso.isEmpty()) {
				textoSaida.add(linhaPosProcesso);
	    	}
			textoSaida.add(atores + "\n");
			textoSaida.add(intimados);
		} else {
			textoSaida.add(assunto + " " + complementoAssunto  + "\n");
			textoSaida.add(processoLinha);
			if(!linhaPosProcesso.isEmpty()) {
				textoSaida.add(linhaPosProcesso);
	    	}
			textoSaida.add(linhaPauta);
			textoSaida.add(atores + "\n");
			textoSaida.add(intimados);
			if(textoIntroducao.size() > 0) {
				for(String linha : textoIntroducao){
					textoSaida.add(linha);
				}
			}
		}

		if(textoEdital.size() > 0) {
			for(String linha : textoEdital){
				textoSaida.add(linha);
			}
		}
		
		if(!linhaParagrafo.isEmpty() && pauta) {
			textoSaida.add(linhaParagrafo);
		}

		//textoSaida.add("\n" + "Página " + pagina + " do Diario Oficial TRT " + strTribunal + "ª Região / Edição: " + dataEdicao);
		textoSaida.add("(S:" + sequencialSecao + "/G:" + sequencialGrupo + "/A:" + sequencialAssunto + "/P:" + sequencialProcesso + ")");
		textoSaida.add(linhaTraco);
		textoSaida.add(linhaRodaPe);
		textoSaida.add(" \n");

		Edital.setTexto(textoSaida);
		if(!examinaPublicacao(textoSaida)) {
			Edital.setDescricao("examinar edital" + "\n" + descricaoFolder);
		}
		
		if (InterfaceServidor.incluiEdital(sessao, editalFolder)) {
			if((sequencialSaida - ultimoSequencial == 100) || sequencialSaida == 1) {
				ultimoSequencial = sequencialSaida;
				gravaLog(obtemHrAtual() + "publicação " + sequencialSaida + " enviada com sucesso");
			}
		} else {
			msgWindow.incluiLinha("Houve erro na gravação da publicação Nº " + sequencialSaida + " Gravado com sucesso");
			gravaLog(obtemHrAtual() + "Erro no envio da publicação " + sequencialSaida );
			finalizaProcesso();
		}

		//msgWindow.incluiLinha(obtemHrAtual() + " Publicação nº: " + seqPublicacao);
		textoSaida.clear();
		qtdPublicacoes++;
	}
	
	private static boolean validaParagrafo(int sequencia) {
		
		int referencia = sequencia;
		boolean saida = false;
		String linhaDummy = carregaLinha(referencia, false);
	
		if(ehMaiuscula(linhaDummy)) {
			while(!saida){
				if(linhaDummy.charAt(linhaDummy.length()-1) == '.') {
					return true;
				} else {
					linhaDummy = carregaLinha(referencia++, false);
				}
			}
		}
		
		return false;
	}
	
	private static boolean validaPublicacao(ArrayList<String> texto) {			// em desenvolvimento
		
		/**
		 * Desenvolver método que avalie se a publicação estar consistente, caso contrario e mesma será gravada com
		 * um indicador no nome do arquivo para sinaliza-lo que há suspeita de erro.
		 */
		
		
		return false;
	}
	
	private static void fechaEdital(ArrayList<String> texto) throws Exception, IOException {

		if(tipoSaida.equals("DIRETA")) {
			enviaEdital();
		} else {
		//	gravaEdital(texto);
		}
		
		if((kk - 100 == 100) || kk == 0) {
			k++;
		} else {
			kk++;
		}
		
		if(assunto.equals("Pauta")) {
			k++;
		}
	
		if(edital.size()<5 && !formataPalavra(assunto).equals("pauta de julgamento")) {
			gravaLog(obtemHrAtual() + " edital " + sequencialSaida + " texto vazio");
		}
		if(atores == null) {
			gravaLog(obtemHrAtual() + " edital " + sequencialSaida + " edital sem atores");
		}
		if(intimados == null) {
			gravaLog(obtemHrAtual() + " edital " + sequencialSaida + " edital sem intimados");
		}
		if(processoNumero == null) {
			gravaLog(obtemHrAtual() + " edital " + sequencialSaida + " edital sem processoLinha");
		}
		gravaLog(obtemHrAtual() + "Publicação fechada " + sequencialSaida + " / " + secao + " / " + grupo + " / " + assunto + " / " + processoNumero);

		processoAnterior = processoLinha;
		processoLinha = "";
		processoNumero = "";
		sequencialSaida++;
		atores = "";
		intimados = "";
		atoresOK = false;
		intimadosOK = false;
		sequencialAssunto = 0;
		dtValida = false;
		textoEdital.clear();
		paragrafos.clear();
		linhaParagrafo = "";
		linhaPosProcesso = "";
	}

	public static boolean gravaEdital(ArrayList<String> texto) throws Exception {
		String nomeFile = strTribunal + "-" + seqEdicao + "-" + seqPublicacao + ".pdf";
	
		Edital.setTitulo1(titulo1);
		Edital.setTitulo2(titulo2);
		Edital.setTitulo3(titulo3);
		Edital.setTitulo4(titulo4);
		Edital.setTitulo5(titulo5);
		Edital.setStrEdicao(strEdicao);
    	Edital.setVara(secao);
    	Edital.setGrupo(grupo);
		Edital.setAssunto(assunto);
    	Edital.setProcesso(processoNumero);
    	
    	if(!atores.isEmpty()) {
    		Edital.setAtores(atores);
    	} else Edital.setAtores("---");
    	if(!intimados.isEmpty()) {
    		Edital.setIntimados(intimados);
    	} else Edital.setIntimados("---");
    	
    	Edital.setTexto(formataTexto(edital));
    	Edital.setTipoDocumento(tipoDocumento);
    	Edital.setCliente(cliente);
    	if(!textoIntroducao.isEmpty()) {
    		Edital.setIntroducao(formataTexto(textoIntroducao));
    	}
    	base.setFileName(nomeFile);

		SalvaPdf.gravaPdf();
		GravaXml.main();
		gravaLog(obtemHrAtual() + "Publicação " + sequencialSaida + " / " + secao + " / " + grupo + " / " + assunto + " / " + processoLinha);        
		edital.clear();
		return true;
	}
		
	public static ArrayList<String> formataEdital(ArrayList<String> buffer) throws Exception {
		gravaLog(obtemHrAtual() + " Formatação da Publicação");
		String linha = "";
		String dummy = "";
		int ix = 0;
		boolean parteInicial= false;
		boolean parteFinal = false;
		boolean hasLowercase = false;
		ArrayList<String> editalFormatado = new ArrayList<String>();

		while(ix <= buffer.size()-1) {
			if(buffer.get(ix).equals("")) {
				ix++;
				continue;
			}
			if(ix <= buffer.size()-1) {
				if(verificaDataValida(buffer.get(ix))){
					if(!assunto.equals("acordao") && ((buffer.size()-1)-ix) <= 2) {
						for(int i = ix; i<= buffer.size()-1; i++) {
							editalFormatado.add(buffer.get(i).trim());
						}
						break;
					} else {
						editalFormatado.add(buffer.get(ix).trim());
						ix++;
						continue;
					}
				}
				
				if((contaPalavras(buffer.get(ix).trim()) == 1) && buffer.get(ix).trim().equals("Assinatura") &&
						((buffer.size()-1) - ix) > 2) {
					editalFormatado.add(buffer.get(ix).trim());
					parteFinal = true;
					ix++;
					continue;
				}
				
				// Formatação da 1ª parte do edital ------------------------------------------
				if(obtemNumeroProcesso(buffer.get(ix)) == null) {
					if(!parteInicial) {
						dummy = formataPalavra(buffer.get(ix).trim());
						if((contaPalavras(dummy) == 1) && (dummy.equals("poder") || dummy.equals("judiciario"))) {	
							editalFormatado.add("PODER JUDICIÁRIO");
							ix = ix + 2;
							if(ix > buffer.size()-1) {
								break;
							} else {
								ix++;
								continue;
							}
						}					
						if((contaPalavras(buffer.get(ix).trim()) == 1) && !verificaPontoFinal(buffer.get(ix).trim())) {	// fundamentação
							editalFormatado.add(buffer.get(ix).trim());
							dummy = buffer.get(ix).trim();
							ix++;
							if(ix > buffer.size()-1) {
								break;
							} else {
								ix++;
								continue;
							}
						}						
						editalFormatado.add(buffer.get(ix).trim());
						parteInicial = true;
						ix++;
						if(ix > buffer.size()-1) {
							break;
						}
					}
					if(parteFinal) {												// Finalização do edital
						if(!linha.isEmpty()) {
							editalFormatado.add(linha);
						}
						editalFormatado.add(buffer.get(ix).trim());
						ix++;
						linha = "";
						if(ix > buffer.size()-1) {
							break;
						} else {
							ix++;
							continue;
						}
					}
					if(!buffer.get(ix).trim().isEmpty()) {
						dummy = buffer.get(ix).trim();
						hasLowercase = !dummy.equals(dummy.toUpperCase());				// se negativo -> ñ tem Lowcase
						if(!hasLowercase) {												// qdo a linha só tem Uppercase
							if((verificaPontoFinal(buffer.get(ix).trim()) && !hasLowercase)) {
								linha = linha + " " + buffer.get(ix).trim();
			 				} else { 
			 					if(!verificaPontoFinal(buffer.get(ix).trim()) && !hasLowercase) {
			 						if(ix+1 >= buffer.size()) {
			 							editalFormatado.add(linha);
			 							break;
			 						}
			 						dummy = buffer.get(ix+1).trim();
									hasLowercase = !dummy.equals(dummy.toUpperCase());
									if(!hasLowercase) {								// proxima linha é Uppercase ?
										if(linha.isEmpty()) {	
											editalFormatado.add(buffer.get(ix).trim() + " " + buffer.get(ix+1).trim());
										} else {
											editalFormatado.add(linha);
											editalFormatado.add(buffer.get(ix).trim() + " " + buffer.get(ix+1).trim());
										}

				 					} else {
				 						if(linha.isEmpty()) {	
											editalFormatado.add(buffer.get(ix).trim());

										} else {
											editalFormatado.add(linha);
											editalFormatado.add(buffer.get(ix).trim());
											linha = "";
										}
				 					}	
			 					}	
			 				}
							ix++;
							if(ix > buffer.size()-1) {
								break;
							} else {
								continue;
							}
						}
					}
				}	
			}	

			if(linha.isEmpty()) {
				linha = (buffer.get(ix).trim());
			} else {
				if(((buffer.size()-1) - ix) > 2) {
					linha = linha + " " + (buffer.get(ix).trim());
				} else {
					editalFormatado.add(linha);
					editalFormatado.add(buffer.get(ix).trim());
					linha = "";	
				}	
			}
			if(verificaPontoFinal(buffer.get(ix).trim()) || obtemNumeroProcesso(buffer.get(ix)) != null){
				linha = linha + "\n";
				editalFormatado.add(linha);
				linha = "";
			} 
			ix++;
			if(ix == buffer.size()-1) {
				if(linha.isEmpty()) {
					editalFormatado.add(linha);
				}
				editalFormatado.add(buffer.get(ix).trim());
				break;
			}
		}
		return editalFormatado;
	}
	
	public static String obtemHrAtual() {

		String hr = "";
		String mn = "";
		String sg = "";
		Calendar data = Calendar.getInstance();
		hr = Integer.toString(data.get(Calendar.HOUR_OF_DAY));
		mn = Integer.toString(data.get(Calendar.MINUTE));
		sg = Integer.toString(data.get(Calendar.SECOND));

		return completaEsquerda(hr,'0',2)+":"+completaEsquerda(mn,'0',2)+":"+completaEsquerda(sg, '0', 2);
	}
/* obsoleta
	public static boolean validaAssunto(String linhaDummy) {
		linhaDummy = formataPalavra(linhaDummy);
		linhaDummy = linhaDummy.replaceAll("[,:;-]"," ");
		linhaDummy = linhaDummy.replaceAll("[0123456789.]","");
		linhaDummy = linhaDummy.trim();
		if(tabelaAssuntos.contains(linhaDummy)) {
			return true;
		}
		return false;
	}
*/	
	public static boolean validaJuridiques(String linhaDummy) {
		linhaDummy = formataPalavra(linhaDummy);
		linhaDummy = linhaDummy.replaceAll("[,:;-]"," ");
		linhaDummy = linhaDummy.replaceAll("[0123456789.]","");
		linhaDummy = linhaDummy.trim();
		if(juridiques.contains(linhaDummy)) {
			return true;
		}
		return false;
	}
	
	
/*		
		@SuppressWarnings("null")
		public static boolean validaAssunto(String linha) {
			String linhaDummy = formataPalavra(linha);
			String [] arrayAssunto = null;
			String [] arrayLinha = linha.split(" ");
			int igualidades = 0;
			double percentual = 0;
			//if(!tabelaAssuntos.contains(linha)) {
			//	return false;
			//}
			//if(assuntosUtilizados.contains(linha)) {					// tabela de assuntos já encontrados no processamento atual
			//	return true;
			//}
			for(int i=0; i <= tabelaAssuntos.size()-1; i++) {			// loop para percorrer toda a tabela de assunto
				if(tabelaAssuntos.get(i).length() == linha.length() && tabelaAssuntos.get(i).equals(linha)){
					assuntosUtilizados.add(linha);
					return true;
				}
				if(tabelaAssuntos.get(i).length() > linha.length()){
					arrayAssunto = tabelaAssuntos.get(i).split(" ");
					igualidades = comparaPalavrasAssunto(tabelaAssuntos.get(i), linha);
					
					if(igualidades == arrayAssunto.length) {			// todas as palavras de tabelaAssuntos existem em linha
						return true;
					}
					
					if(igualidades > 0) {								// apurar o resutado					
						if(arrayAssunto.length < arrayLinha.length) {
							percentual = igualidades / arrayLinha.length;
							if(percentual >= 1.0) {
								return true;
							} else {
								return false;
							}
						}
					}
				}	
			}
			return false;	
		}
*/		
	public static int comparaPalavrasAssunto(String assunto, String linha) {
		
		// Igualidades é o numero de palavras em tabelaAssuntos que existem na linha em ordem sequencial (inic -> fim) 
		
		String [] arrayLinha = linha.split(" ");
		String [] arrayAssunto = assunto.split(" ");
		int igualidades = 0;
		
		if(arrayLinha.length == 0) return 0;

		for(int x=0; x <= arrayAssunto.length-1; x++) {			// conta qtas palavras de linha existem em um assunto da tabela
			if(x > arrayLinha.length-1) {						// fim das palavras exietntes na linha
				break;
			}
			if(arrayAssunto[x].equals(arrayLinha[x])) {
				igualidades++;									// qtd de palavras iguais encontradas
			}			
		}
		return igualidades;		
	}
		
	public static boolean verificaSeLinhaTemNumProcesso(String linha) {
		String linhaDummy = formataPalavra(linha);
		String [] arrayLinha = linhaDummy.split(" ");

		if(arrayLinha.length == 0) {
			return false;
		} else {
			if(arrayLinha.length >= 2) {
				if((arrayLinha[0].equals("processo") && arrayLinha[1].equals("nº")) ||
					(arrayLinha[0].equals("numero") &&arrayLinha[1].equals("do")) ||
					(arrayLinha[0].equals("proc.") && arrayLinha[1].equals("nº") && arrayLinha[2].equals("trt"))) {
		
					if(obtemNumeroProcesso(linha) != null) {
						return true;
					}
				}
			}
		}		
		return false;	
	}
		
		public static boolean verificaContinuacaoLinha(String linha) {
			
			String linhaFormatada = formataPalavra(linha);
			String [] arrayLinha = linhaFormatada.split(" ");
			String criterio = "";
			String palavraFinal = "";
			int kl = 0;

			if(arrayLinha.length == 0) return false;
			
			if(linha.lastIndexOf(',') == linha.length()-1 || linha.lastIndexOf('-') == linha.length()-1 || 
					linha.lastIndexOf(':') == linha.length()-1) {
				return true;
			}
			
			for(int x=0; x <= continuacoesPossiveis.size()-1; x++) {
				if(arrayLinha[arrayLinha.length-1].equals(continuacoesPossiveis.get(x))) {
					return true;
				}
			}

			palavraFinal = arrayLinha[arrayLinha.length-1];
			
			if(linha.length() >= 5 && palavraFinal.length() >= 4) {
				palavraFinal = arrayLinha[arrayLinha.length-1];
				kl = palavraFinal.length()-4;
				criterio = palavraFinal.substring(kl);
				if(arrayLinha[arrayLinha.length-1].substring(kl).equals("oab:")) {
					return true;
				}
			}
			return false;	
		}
			
		public static boolean verificaPontoFinal(String linha) {
			char ultimaLetra;
			String ultimaPalavra = "";
			String[] palavras;
			palavras = linha.split(" ");
			ultimaPalavra = palavras[palavras.length - 1];
			ultimaLetra = ultimaPalavra.charAt(ultimaPalavra.length()-1);
			if(ultimaLetra != '.') {
				return false;
			}
			return true;
		}
		
		public static void criarPastaSaida(File folder) {
	        try {
	        //  File diretorio = new File("/Users/avmcf/jaq/" + tribunal + "_" + edicao);
	        //	File diretorio = new File(tribunal + "_" + edicao);
	            if(folder.exists()) {
	            	folder.delete();
	            } 
	            folder.mkdir();        
	        } catch (Exception ex) {
	            JOptionPane.showMessageDialog(null, "Falha ao criar Pasta dno repositório");
	            System.out.println(ex);
	        }
	    }
		
	
		
	public static String quebraLinha(String texto) {
		String textoTruncado = "";
		String linhaPreparada = "";
		int var = texto.trim().split(" ", -1).length - 1;      
		String palavras[] = new String[var];                		
		palavras = texto.split(" ");
		texto = texto.trim();
		
		if(texto.length() <= tamanhoLinha){
			textoTruncado = texto;
		} else {
			for (int x=0; x <= palavras.length-1; x++) {
				if((tamanhoLinha - linhaPreparada.length()) <= palavras[x].length()){
					if(textoTruncado.isEmpty()) {
						textoTruncado = linhaPreparada + "\n";
					} else {
						textoTruncado = textoTruncado + " " + linhaPreparada + "\n";
					}
					textoTruncado = textoTruncado + " " + palavras[x].trim();
					linhaPreparada = "";
				} else {
					if(linhaPreparada.isEmpty()) {
						linhaPreparada = palavras[x];
					} else {
						linhaPreparada = linhaPreparada + " " + palavras[x];
					}				
				}
			}
			if(!linhaPreparada.isEmpty()) {
				textoTruncado = textoTruncado + " " + linhaPreparada;
			}
		}
		return textoTruncado;
	}
	
	public static String quebraLinhaT(String texto, int limite) {
		String textoTruncado = "";
		String linhaPreparada = "";
		int var = texto.trim().split(" ", -1).length - 1;      
		String palavras[] = new String[var];                		
		palavras = texto.split(" ");
		texto = texto.trim();
		
		if(texto.length() <= limite){
			textoTruncado = texto;
		} else {
			for (int x=0; x <= palavras.length-1; x++) {
				if((limite - linhaPreparada.length()) <= palavras[x].length()){
					textoTruncado = linhaPreparada + "\n";
					textoTruncado = textoTruncado + palavras[x].trim();
					linhaPreparada = "";
				} else {
					if(linhaPreparada.isEmpty()) {
						linhaPreparada = palavras[x];
					} else {
						linhaPreparada = linhaPreparada + " " + palavras[x];
					}				
				}
			}
			if(!linhaPreparada.isEmpty()) {
				textoTruncado = textoTruncado + " " + linhaPreparada;
			}
		}
		return textoTruncado;
	}
		
	public static String formataParagrafo(ArrayList<String> texto) {
		
		String linhaDummy = "";
		String textoJustificado = "";
		String linhaDoParagrafo = "";
		int limite = tamanhoLinha;
		int tamanhoAcumulado = 0;

		for (int x=0; x<=texto.size()-1; x++ ) {
			linhaDummy = texto.get(x);
			if(linhaDummy.length() > limite) {
				textoJustificado = quebraLinha(linhaDummy);
				continue;
			} else {
				if(linhaDoParagrafo.isEmpty()) {
					linhaDoParagrafo = linhaDummy;
					tamanhoAcumulado = linhaDummy.length();
				} else {
					linhaDoParagrafo = linhaDoParagrafo + " " + linhaDummy;
					tamanhoAcumulado = tamanhoAcumulado + linhaDummy.length();
					if(tamanhoAcumulado > limite) {
						if(textoJustificado.isEmpty()) {
							textoJustificado = quebraLinha(linhaDoParagrafo);
						} else {
							textoJustificado = textoJustificado + " " + quebraLinha(linhaDoParagrafo);
						}
						linhaDoParagrafo = "";
						tamanhoAcumulado = 0;
					}
				}
			}
		}
		if(!linhaDoParagrafo.isEmpty()) {
			if(tamanhoAcumulado > limite) {
				if(textoJustificado.isEmpty()) {
					textoJustificado = quebraLinha(linhaDoParagrafo);
				} else {
					textoJustificado = textoJustificado + " " + quebraLinha(linhaDoParagrafo);
				}
			} else {
				textoJustificado = textoJustificado + " " + quebraLinha(linhaDoParagrafo);
			}
		}
		return textoJustificado;
	}
		
	public static ArrayList<String> formataTexto(ArrayList<String> texto) {
		
		String linha = "";
		String linhaContinua = "";
		String [] palavras;
		int ip = 0;
		int ix = 0;
		int posicao = 0;
		boolean saida = true;

		ArrayList<String> textoFormatado = new ArrayList<String>();

		while (ix <= (texto.size()-1)){				
			linhaContinua = linhaContinua + texto.get(ix) +" ";
			ix++;
		}				
		saida = false;
		posicao = 0;

		linha = "";
		palavras = linhaContinua.split(" ");
		
		while(!saida) {
			
			if(linha.length() + palavras[posicao].length() <= 150) {		
				while(posicao <= palavras.length-1) {
					if(!linha.isEmpty()) {
						linha = linha + " " + palavras[posicao].trim();
					} else {
						linha = palavras[posicao].trim();
					}	
					if(contaPalavras(linha) > 1) {	
						if(!verificaPontoFinal(linha) && (posicao == palavras.length)) {
							textoFormatado.add(linha);
							break;
						}
						if(posicao < (palavras.length-1)) {
							if((linha.length() + palavras[posicao+1].length()) > 150) {
								textoFormatado.add(linha);
								linha = "";
							}
						} else {
							textoFormatado.add(linha);
							linha = "";
							break;
						}
						ip++;
					}
					posicao++;
				}						
			} else {
				saida = true;
			}
			if(posicao > palavras.length-1) {
				saida = true;
			}
		}							
		return textoFormatado;
	}
		
	public static String obtemTribunal(String linhaDummy){

		String sequencia = "";
		int i = 0;
		if (linhaDummy.startsWith("Caderno Judiciário do Tribunal")){
			if (!linhaDummy.contains("Superior")) {
				while (i <= linhaDummy.length()-1) {
					if ((linhaDummy.charAt(i) >= '0' && linhaDummy.charAt(i) <= '9')){
						sequencia = sequencia + linhaDummy.charAt(i);
					}
					i++;
				}
			} else {
				sequencia = "00";
			}
		}
		return sequencia;
	}
	
	public static void gravaIntermedio(File input){
		PDDocument pd;
	    BufferedWriter wr;

		msgWindow.incluiLinha(obtemHrAtual() +" - Conversão do Diário Oficial - Aguarde ...");
	    try {
		    if(intermedio.exists()) intermedio.delete();
	        pd = PDDocument.load(input);
	        PDFTextStripper stripper = new PDFTextStripper();
	        wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(intermedio),StandardCharsets.UTF_8));
	        stripper.writeText(pd, wr);
	        pd.close();
	        wr.close();
	    }
	    catch (IOException e) {
	    	JOptionPane.showMessageDialog(null, "Erro na conversão do PDF  -> " + e);
	    }
		msgWindow.incluiLinha(obtemHrAtual() + " - Fim da conversão do Diário Oficial");
	}
	
}	// final da classe SplitDO
	
	