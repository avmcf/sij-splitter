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
//					Correção do método obtemNumProcesso
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
//					reformulação do metodo obtemNumProcesso
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
//					Nova logica para determinar quebra por assunto
//					inclusão do método validaAssunto
//					inclusão do método contarPalavrasAssunto
//
//	versao 2.2.19 	- 27 de Abril de 2020
//					Versão intermediaria para JAQ
//					Nova lógica de quebra de publicações
//					
//
//	versao 3.0 	- 	...... de 2019
//					- Novo algorítimo de quebra de editais
//					- Diário oficial convertido para a memoria
//					- Novo método carregaIndice
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
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
	import java.util.ArrayList;
	import java.util.Calendar;
	import java.util.Collection;
	import java.util.Date;
	import java.util.List;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
	import javax.swing.JFileChooser;
	import javax.swing.JOptionPane;

	import org.apache.chemistry.opencmis.client.api.Folder;
	import org.apache.chemistry.opencmis.client.api.Session;
	
	import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;

	import com.vconsulte.sij.splitter.IndiceEdicao;
	import com.vconsulte.sij.base.*;
	
public class SplitDO  {
		
	static File diarioInput;
	static File editaisDir;
	static File assuntos;
	static File config;
	static File diretorio;
	
	static FileWriter arquivoLog;
	static FileWriter arqSaida;

//	    public static String url = "http://192.168.1.30:8080";		// JAQ
//	    public static String url = "http://192.168.25.9:8080";		// Catacumba		
	public static String url = "http://127.0.0.1:8080";				// Vagrant local

    public static String baseFolder = "/Sites/advocacia/documentLibrary/secretaria/carregamento";
    public static String usuario = "sgj";
    public static String password = "934769386";
     
	static List<com.vconsulte.sij.splitter.IndiceEdicao> Index = new ArrayList<com.vconsulte.sij.splitter.IndiceEdicao>();
	static com.vconsulte.sij.splitter.IndiceEdicao Indice = new com.vconsulte.sij.splitter.IndiceEdicao(null, 0, 0, null, null, 0,  0, 0, 0);

	static List <String> tabelaAssuntos = new ArrayList<String>();
	static List <String> assuntosUtilizados = new ArrayList<String>();
	static List <String> continuacoesPossiveis = new ArrayList<String>();
	static List <String> tabAtores = new ArrayList<String>();
	static List <String> bufferEntrada = new ArrayList<String>();
	static Collection<String> bufferSaida = new ArrayList<String>();
	
	static ArrayList<String> textoEdital = new ArrayList<String>();
	static ArrayList<String> textoSaida = new ArrayList<String>();
	static ArrayList<String> textoIntroducao = new ArrayList<String>();
	static ArrayList<String> edital = new ArrayList<String>();
	static ArrayList<String> introducao = new ArrayList<String>();
	
    static String editalTexto = "";
	static String versaoSplitter = "v2.3.20";
	static String processo = "";
	static String numProcesso = "";
	static String processoAnterior = "";
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
	static String atores = "";
	static String intimados = "";
	static String linha = "";
	static String linhaAnterior = "";
	static String logFolder = "";

	// parametros de configuração
	static String cliente = "V&C Consultoria Ltda";		// Nome do cliente do sistema
	static String tipoSaida = "TEXTO";					// indica se a saída será PDF ou TXT
	static String sysOp = "LINUX";
	
	static Path pathEdicao;
	static File intermedio;
	
	static Date edicao;

	static int paginaAtual = 1;
	static int pagina = 0;
	static int sequencialSaida = 1;
	static int qtdPublicacoes = 0;
	static int maiorAssunto = 0;
	static int qtdPaginasDO = 0;
	static int sequencial = 0;	
	static int limiteGrupo = 0;
	static int sequencialSecao = 0;
	static int sequencialGrupo = 0;
	static int sequencialAssunto = 0;
	static int linhaProcesso = 0;
	static int linhaAssunto = 0;
	static int sequencialIndice = 0;
	static int linhaSumario = 0;
	static int indiceContador = 0;
	static int ultimaLinha = 0;
	static int ultimaPagina = 0;
	static int seqIndex = 0;
	static int sequencialSumario = 0;
	
	static boolean salvarIntro = false;
	static boolean limparIntro = true;
	static boolean mudouAssunto = false;
	static boolean saida = false;
	static boolean encontrouIndice;
	static boolean atoresOK = false;
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
	
	static Folder editalFolder;

	static int k = 0;								// para testes
		
	@SuppressWarnings("unlikely-arg-type")
	public static void main(String[] args) throws Exception {

		String strDummy = "";	
		
		boolean salvarLinha = false;
		boolean primeiroEdital = true;
		boolean quebraPorAssunto = false;

		// todos em minúsculo
		tabAtores.add("advogado");
		tabAtores.add("agravado");
		tabAtores.add("agravante");
		tabAtores.add("autor");
		tabAtores.add("autoridade coatora");
		tabAtores.add("custos legais");
		tabAtores.add("custos legis");
		tabAtores.add("exequente");
		tabAtores.add("executado");
		tabAtores.add("impetrante");
		tabAtores.add("impetrado");
		tabAtores.add("juizo recorrente");
		tabAtores.add("perito");
		tabAtores.add("procedencia");
		tabAtores.add("reclamante");
		tabAtores.add("reclamado");
		tabAtores.add("recorrido");
		tabAtores.add("recorrente");
		tabAtores.add("requerente");
		tabAtores.add("requerido");
		tabAtores.add("relator");
		tabAtores.add("reu");
		tabAtores.add("solicitado");
		tabAtores.add("solicitante");
		tabAtores.add("terceiro interessado");
		tabAtores.add("testemunha");

		inicializaArquivos();
		msgWindow.montaJanela();
		try {
		
		//	gravaIntermedio(diarioInput);			// só pra teste não apagar
			carregaConfig();
			carregaDiario(diarioInput);
			msgWindow.incluiLinha(obtemHrAtual() + " - Preparação para leitura");
			carregaIndice();
			
		//	lista();								//só pra teste não apagar
			
			mapeiaLinhas();
			carregaAssuntos();
		
			if (!conectaServidor()) {
				msgWindow.incluiLinha(obtemHrAtual() +" - Falha na conexão com o Servidor");
				finalizaProcesso();
			};
		
	        if (bufferEntrada.get(0).contains("Caderno Judiciário")){
	        	gravaLog(obtemHrAtual() + " inicio do processamento");
	        	String dummy = "";
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
	        abreLog(strTribunal+seqEdicao);
	        gravaLog(obtemHrAtual() + " ini -> " + tribunal + " - " + descricaoFolder);

	        for (IndiceEdicao Indice : Index) {					// Loop de indice (percorre o indice do documento)		        	
	        	// NOVA SESSÃO	 ---------------------------------------------------------------------------------

	        	if(sequencial >= 371797) {
        			k++;
	        	}
	        	
	        	if(!primeiroEdital && textoEdital.size() > 0) {
	        		textoEdital.add(linha);
					seqPublicacao = completaEsquerda(Integer.toString(sequencialSaida), '0', 4);
    				edital = formataEdital(textoEdital);
    				fechaEdital((ArrayList<String>) edital);
				}
	        	
        		secao = bufferEntrada.get(Indice.linhaSecao).trim();
        		sequencialSecao = Indice.linhaSecao;
        		gravaLog(obtemHrAtual() + " sca -> " + sequencial + " - nova secao: " + secao);
        		msgWindow.incluiLinha(obtemHrAtual() + " ------------------------------------------------");
        		msgWindow.incluiLinha(obtemHrAtual() + " - Local: " + secao + " - pg: " + Indice.paginaSecao + " / " + ultimaPagina);
        		if(Indice.complementoSecao != null && !Indice.complementoSecao.equals("complemento")) {
					secao = secao + " " + Indice.complementoSecao;
				}
        		
        		grupo = bufferEntrada.get(Indice.linhaGrupo).trim();
        		sequencial = Indice.linhaGrupo + 1;
        		sequencialGrupo = Indice.linhaGrupo;
        		
        		if(verificaSeLinhaTemNumProcesso(bufferEntrada.get(sequencial+1))) {
        			grupoSemAssunto = false;
        		} else if(!grupo.equals("Pauta")){
        			grupoSemAssunto = true;
	        	}
        		
        		msgWindow.incluiLinha(obtemHrAtual() + " - Grupo: " + grupo);

        		gravaLog(obtemHrAtual() + " grp -> " + sequencial + " - novo grupo: " + grupo + " - pg: " + Indice.paginaSecao + " / " + ultimaPagina);
        		limiteGrupo = localizaProximoGrupo(sequencial);
    			quebraPorAssunto = false;
    			
    			
    			
    			if(sequencial >= 371828) {
        			k++;
	        	}
    			
    			assunto = "";
    			quebraPorAssunto = false; 
        		indiceContador++;
    			inicializaEdital();
    			
    			boolean ignora = false;
    			
	        	// Início do loop de linhas de assunto corpo  -------------------------------------------------------------------	   
	        	while((sequencial < limiteGrupo)) {	
	        		
	        		salvarLinha = true;
	        		linhaAnterior = linha;
	        		linha = carregaLinha(sequencial,true);

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
	        		dtValida = verificaDataValida(linha);
	        		if(textoEdital.isEmpty()) {
	        			//gravaLog(obtemHrAtual() + ".................... INICIO DA PUBLICAÇÃO .........................");
	        		}

	        		//gravaLog(obtemHrAtual() + "\t\t\t" + "--- >> " + sequencial + " - " + linha );
	        		
	        		//System.out.println(sequencial + " - " + linha);

	        		if(sequencial >= 371884) {
	        			k++;
	        		}
	        		
	        		if(sequencial >= 371903) {
	        			k++;
	        		}
	        		
	        		if(sequencial >= 371922) {
	        			k++;
	        		}
	        		
	        		if(sequencial >= 371954) {
	        			k++;
	        		}
	        		
	        		if(sequencial >= 371984) {
	        			k++;
	        		}
	        		
	        		if(sequencial >= 376853) {
	        			k++;
	        		}

					/*	
					 * Quebra por Assunto
					 * 
					 */
	        		if(tabelaAssuntos.contains(formataPalavra(primeiraPalavra(linha))) && !grupoSemAssunto) {
	        			if(quebraAssunto(sequencial-1,limiteGrupo)) {
							if(!primeiroEdital && textoEdital.size() > 0) {
								seqPublicacao = completaEsquerda(Integer.toString(sequencialSaida), '0', 4);
		        				edital = formataEdital(textoEdital);
		        				fechaEdital((ArrayList<String>) edital);		        				
							} else {
								primeiroEdital = false;
							}
							
							quebraPorAssunto = true;
							assunto = linha;
							sequencialAssunto = sequencial;
							linhaAssunto = sequencial;
							quebraPorAssunto = true;
							gravaLog(obtemHrAtual() + " ass -> " + sequencial + " - " + assunto + " - arquivo --> " + sequencialSaida);

							//if(!verificaSeLinhaTemNumProcesso(carregaLinha(sequencial,false))) {
	        				//	msgWindow.incluiLinha("----> NO");
	        				//} 
	        			
							if(assunto.equals("Pauta de Julgamento")) {
								pauta = true;
							} else {
								pauta = false;
							}
							if(verificaSeLinhaTemNumProcesso(carregaLinha(sequencial,false))) {
								if(!textoIntroducao.isEmpty() && salvarIntro) {
									textoIntroducao.clear();
									salvarIntro = false;
								}
							} else {
								if(textoIntroducao.isEmpty()) {
									textoIntroducao.clear();
									salvarIntro = true;
								}
							}
							continue;
	        			}
					}
	        		
					/*
					 * Tratamento de introdução do Edital quando houve
					 * (introdução é um bloco de texto comum a vários editais de um mesmo assunto
					 * 
					 */
					if(salvarIntro){
						if(obtemNumProcesso(linha) != null){
							salvarIntro = false;
						} else {								
							textoIntroducao.add(linha);
							//gravaLog(obtemHrAtual() + " itr -> " + sequencial + linha);
							continue;
						}
					}

					/*
					 * Quebra por Nº PROCESSO
					 */
					if(verificaSeLinhaTemNumProcesso(linha)) {
						strDummy = obtemNumProcesso(linha);
						if(!quebraPorAssunto) {
							if(quebraProcesso(sequencial-1)) {

								if(!pauta) {					  				// se ñ é pauta a quebra é por assunto      								
									if((textoEdital.size() > 0)){
										seqPublicacao = completaEsquerda(Integer.toString(sequencialSaida), '0', 4);
				        				edital = formataEdital(textoEdital);
				        				fechaEdital((ArrayList<String>) edital);
									}						
								} else {										// se assunto = pauta a quebra é por nº processo
									if(!atores.isEmpty() && !intimados.isEmpty()){
										seqPublicacao = completaEsquerda(Integer.toString(sequencialSaida), '0', 4);
				        				edital = formataEdital(textoEdital);
				        				fechaEdital((ArrayList<String>) edital);
									}
								}
								salvarLinha = false;
							} else {
								salvarLinha = true;
							}
						}
						processoAnterior = processo;
        				processo = linha;
						numProcesso = strDummy;
						linhaProcesso = sequencial;
						quebraPorAssunto = false;
						//gravaLog(obtemHrAtual() + " prc -> " + sequencial + " - " + processo + " - arquivo --> " + sequencialSaida);
					}

					if(!atoresOK && !processo.isEmpty()){
						atores = trataAtores();
						if(atores != "") {
							sequencial--;
							
							if(sequencial >= 371845) {		
			        			k++;
			        		}
							
							intimados = trataIntimados();
		        			atoresOK = true;
							k++;
							continue;
						}
					}

					/*
					 * Registro das linha do texto do Edital
					 */		        			
        			if(textoEdital.size() == 0) {
        				primeiraLinha = linha;
        			}     
        			if(salvarLinha) {
        				textoEdital.add(linha);
        			}
	        		salvarLinha = true;
	        		strDummy = "";	
	        		//	//gravaLog(obtemHrAtual() + " .................. FIM DA PUBLICAÇÃO ..................");

	        		if(indiceContador == Index.size()-1) {
	        			limiteGrupo = ultimaLinha;		// forçar até o final do bufferEntrada
	        		}
	        	} 	// fim do WHILE de linhas ------------------------------------------------------------------------------
	        }	// fim do FOR do Indice
        if(textoEdital.size() > 0){       	
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
	
	public static boolean ehDataValida(String data) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.parse(data);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }
	
	public static boolean procuraIntimados() {
		int inicio = sequencial;
		String linha = "";

		for (int x=0; x <= 50; x++) {
			linha = formataPalavra(carregaLinha(inicio, false));
			if(linha.equals("intimado(s)/citado(s):") || linha.equals("PODER JUDICIÁRIO")){
				return true;
			}
			if(verificaDataValida(linha)) {
				saida = true;
			}
		}
		
		return false;
	}

	public static boolean quebraProcesso(int indice) throws Exception {
		String strDummy = "";
		String linha = carregaLinha(indice, false);
		String dummy = "";
		strDummy = obtemNumProcesso(linha);
		
		if(strDummy != null) {
			if(sequencialAssunto == sequencial-1) {
				gravaLog(obtemHrAtual() + " --- >> " + sequencial + " - quebra processo");
				return true;
			}
			if(assunto.isEmpty() && textoEdital.isEmpty()) {
				gravaLog(obtemHrAtual() + " --- >> " + sequencial + " - quebra processo");
				return true;
			}
			if(!strDummy.equals(numProcesso)) {
				if(!formataPalavra(assunto).equals("pauta de julgamento")) {								// se assuto ñ for pauta
					if(!tabelaAssuntos.contains(formataPalavra(linhaAnterior)) && !assunto.isEmpty()) {		// linha anterior ñ é um assunto válido
						
						
						if(procuraIntimados()) {
							return true;
						}
						
						
						
					//	return false;
					}	
				}
			}
		}
		if(strDummy != null) {																// linha tem nº processo
			if(ehInteiro(Character.toString(linha.charAt(linha.length()-1)))){				// ultimo caracter é numérico
				if(!formataPalavra(linhaAnterior).contains("judiciario")) {					// linha anterior ñ é PODER JUDICIARIO
					
					for(int x = indice; x>=indice-6; x--) {
						dummy = formataPalavra(carregaLinha(x,false));
						
						if(dummy.isEmpty()) {
		        			continue;
		        		}
						
						if(ehDataValida(obtemData(dummy))) {	
							if((linhaProcesso - x) <= 15) {
								return true;
							}
						}
						
						if(formataPalavra(dummy).equals("intimado(s)/citado(s):")) {
							return false;
						}
						
						if(verificaSeLinhaTemNumProcesso(dummy) && (x == linhaProcesso)) {
							return false;
						}
					}

					for(int x = indice; x<=indice+50; x++) { 						// progressivo a procura de: assunto válido, data válida
						dummy = formataPalavra(carregaLinha(x,false));
						if(dummy.isEmpty()) {
		        			continue;
		        		}
						if(x >= limiteGrupo) {
							break;
						}
						if(ehDataValida(obtemData(dummy))) {
							//return false;
							return true;
						}
						if(formataPalavra(dummy).equals("intimado(s)/citado(s):")) {
							gravaLog(obtemHrAtual() + " --- >> " + sequencial + " - quebra processo");
							return true;
						}
					}					
				}
			}
		}
		return false;
	}
	
	public static boolean quebraAssunto(int indice, int limite) throws Exception {
		int in = 0;
		int fm = 0;
		String dataInvertida = "";
		String dta = "";
		String dummy = "";
		String linha = formataPalavra(carregaLinha(indice, false));
		if(indice == sequencialGrupo) {														// já valida o 1º assunto do grupo
			gravaLog(obtemHrAtual() + " --- >> " + sequencial + " - quebra assunto");
			return true;
		} else {
			if(tabelaAssuntos.contains(primeiraPalavra(linha))) {								// linha tem assunto
				in = tabelaAssuntos.indexOf(linha);
				fm = tabelaAssuntos.lastIndexOf(linha);
				if(in >= 0 && fm >= 0 ) {
					for(int i = in; i <= fm; i++) {
						if(contaPalavras(linha) == contaPalavras(tabelaAssuntos.get(i))){		// Qtd de palavras são iguais	<<<<<<
							if(linha.equals(tabelaAssuntos.get(i))) {																		
								for(int x=indice; x>=linhaProcesso; x--) {						// regressivo a procura de intimados
									dummy = formataPalavra(carregaLinha(x,false));
									if(dummy.isEmpty()) {
										k++;
					        			continue;
					        		}
									if(x == sequencialGrupo) {
										k++;
										break;
									}
									if(x == sequencialSecao) {
										gravaLog(obtemHrAtual() + " --- >> " + sequencial + " - quebra assunto");
										k++;
										return true;
									}
									dta = obtemData(dummy);
									if(!dta.isEmpty()) {
										if(ehDataValida(dta) && dta.length() == 10) {
											dataInvertida = obtemData(dummy).substring(6, 10) + obtemData(dummy).substring(2, 5);
											if(strEdicao.startsWith(dataInvertida)) {
												k++;
												return true;
											}
										}
									}
									if(dummy.equals("poder") || 
											dummy.equals("judiciario") || 
											dummy.equals("poder judiciario")){
										k++;
										return false;
									}										
									if(dummy.equals("intimado(s)/citado(s):")) {
										k++;
										return false;
									}
									if(verificaSeLinhaTemNumProcesso(dummy)) {
										gravaLog(obtemHrAtual() + " --- >> " + sequencial + " - quebra assunto");
										k++;
										return true;
									}										
								}
								int kk = 0;
								//for(int x=indice+1; x<=limite; x++) {							// progressivo a procura de nº processo
								for(int x=indice; x<=limite; x++) {
									kk = x;
									
									if(x >= 371820) {
										k++;
									}
									
									dummy = formataPalavra(carregaLinha(x,false));
									if(dummy.isEmpty()) {
					        			k++;
										continue;
					        		}
									if(dummy.equals("")) {
										k++;
										continue;
									}
									if(ehDataValida(obtemData(dummy))) {
										k++;
										//return false;
										return true;
									}
									if(verificaSeLinhaTemNumProcesso(dummy)) {
										if(!assunto.isEmpty() && !processo.isEmpty() && !textoEdital.isEmpty() || (indice == (x-1))) {
											gravaLog(obtemHrAtual() + " --- >> " + sequencial + " - quebra assunto");
											k++;
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
	
	public static boolean validaAtor(String linha) {
		if(!linha.trim().isEmpty()) {
			linha = formataPalavra(linha);	
			int var = linha.split(" ", -1).length - 1;      
			String var2[] = new String[var];                
			var2 = linha.split(" ");		
			String palavra1 = var2[0].replaceAll(":", "");
			if(tabAtores.contains(palavra1) && linha != " ") {
				return true;
			} else {
				if((var2.length > 1) && (tabAtores.contains(palavra1 + " " + var2[1] ))) {
					return true;
				}
			}
		}
		return false;
	}
	
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
	
	public static String carregaLinhaIndice() {
		int x = 0;
		boolean saida = false;
		String linha = "";
		
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
			linha = bufferEntrada.get(bufferEntrada.size()-1);
		} else {
			seqIndex = sequencialIndice;
			linha = bufferEntrada.get(sequencialIndice);
			sequencialIndice++;
		}
		seqIndex = sequencialIndice;
		return linha;
	}
	
	public static boolean verifcaLetras(String linha) {
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

	public static String trataAtores() throws Exception {
		//gravaLog(obtemHrAtual() + " tratamento de atores");
		char pto = ' ';
		boolean saida = false;
		String bloco = "";	
		String linha = carregaLinha(sequencial,true);
		String registro = "";
		linha = limpaCaracteres(linha);
		if(linha.contains("&")) {
			linha = limpaCaracteres(linha);
		}
		//String registro = formataPalavra(linha);
		//registro = registro.replaceAll("[():-]"," ");
		String dummy = "";

		while(!saida) {
			linha = linha.replaceAll("[():-]"," ");
			if(tabAtores.contains(formataPalavra(primeiraPalavra(linha)))) {
				registro = formataPalavra(linha);
				registro = registro.replaceAll("[():-]"," ");
				while(!saida) {
					dummy = formataPalavra(linha);
					if(dummy.equals("intimado(s)/citado(s):") || 
							dummy.equals("poder") ||
							dummy.equals("judiciario") ||
							verificaDataValida(linha)){
						saida = true;
						break;
					}
					if(assunto.contains("Edital EDHPI-") && bloco.contains("Executado")) {
						saida = true;
						break;
					} else {
						if(!linha.contains("-----")) {
							if(validaAtor(registro)) {
								if(bloco.isEmpty()) {
									bloco = linha;
								} else {
									bloco = bloco + "\n" + linha;
								}
							} else {				
								if(pto == '.' || pto == ')') {
									bloco = bloco + "\n" + linha;
								} else {
									bloco = bloco + " " + linha;
								}
							}
						}
					}
					pto = linha.charAt(linha.length()-1);
					linha = carregaLinha(sequencial,true);
					linha = linha.replaceAll("&[():-]"," ");
					//if(linha.contains("&")) {
					//	linha = limpaCaracteres(linha);
					//}
		    	}
			}
			linha = carregaLinha(sequencial,true);
			//if(linha.contains("&")) {
			//	linha = limpaCaracteres(linha);
			//}
		}	
		sequencial--;		
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
	
	public static String trataIntimados() throws IOException {
		//gravaLog(obtemHrAtual() + " tratamento de intimados");
		String linha = carregaLinha(sequencial,true);
		if(linha.contains("&")) {
			linha = limpaCaracteres(linha);
		}
		String bloco = "";
		String dummy = "";
		String linhaDummy = "";
		int ix = 0;
		boolean saida = false;
		boolean exit = false;
		String var2[]; 
		var2 = linha.split(" ");
		dummy = formataPalavra(linha);
		if((!verificaSeLinhaTemNumProcesso(linha) || dummy == "intimado(s)/citado(s):") && !assunto.contains("Edital EDHPI-")){
			while(!saida) {	
				if(bloco.isEmpty()) {
					bloco = linha;
					linha = carregaLinha(sequencial,true);
					if(linha.contains("&")) {
						linha = limpaCaracteres(linha);
					}
					continue;
				} else {
					if(linha.charAt(0) == '-') {
						bloco = bloco + "\n" + linha;
						linha = carregaLinha(sequencial,true);
						if(linha.contains("&")) {
							linha = limpaCaracteres(linha);
						}
						continue;
					} else {
						if(!verificaSemelhanca(linha,secao) && !verificaSeLinhaTemNumProcesso(linha)){
							ix = sequencial-1;
							while(!exit) {
							//	if(formataPalavra(linha).startsWith("fundamentacao")) {
							//		break;
							//	}

								if(linha.length() > 0) {
									if(linha.trim().charAt(0) != '-') {
										dummy = formataPalavra(carregaLinha(ix,false));						
										if(contaPalavras(dummy) > 2) {
											var2 = dummy.split(" ");
											dummy = var2[0];
										}
										if(tabelaAssuntos.contains(dummy) || 
												dummy.startsWith("poder") || 
												formataPalavra(linha).startsWith("fundamentacao")) {
											saida = true;
											sequencial--;
											break;
										}
										if(formataPalavra(linha).equals("poder") ||
												formataPalavra(linha).startsWith("intimacao em processo") ||
												(obtemNumProcesso(linha) == null && assunto.equals("pauta")) ||
												validaAssunto(dummy)){
											saida = true;
											break;	
										} else {
											bloco = bloco + " " + linha;
										}
									} else {
										bloco = bloco + "\n" + linha;
										saida = true;
										break;		
									}
									linhaDummy = formataPalavra(carregaLinha(sequencial,false));
									if(linhaDummy.equals("poder") || linhaDummy.equals("poder judiciario") ||
											linhaDummy.startsWith("intimacao em processo") ||
											(obtemNumProcesso(linhaDummy) == null && assunto.equals("pauta")) ||
											validaAssunto(dummy)){
										saida = true;
										break;
									} 
								/*
									else {		
										linha = carregaLinha(sequencial,true);
										if(linha.contains("&")) {
											linha = limpaCaracteres(linha);
										}
										if(verificaSemelhanca(linha,secao)){
											saida = true;
											break;
										}
									}
								*/
									
								}
								linha = carregaLinha(sequencial,true);
								if(linha.contains("&")) {
									linha = limpaCaracteres(linha);
								}
								if(verificaSemelhanca(linha,secao)){
									saida = true;
									break;
								}
							}
						} 
						else {
							if(verificaSeLinhaTemNumProcesso(linha)) {
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
		
	public static int contaPalavras(String linha) {
		int ix = 0;
		int var = linha.split(" ", -1).length - 1;      //pega a quantidade de espaços em branco
		String var2[] = new String[var];                //define o vetor que conterá as palavras separadas da string
		var2 = linha.split(" ");                        //separa a string colocando as palavras no vetor
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
	
	public static String trataNumEdicao(String linha) {
		String[] gabaritos = new String[3];
		gabaritos[0] = "\\d{4}\\W\\d{4}";
		gabaritos[1] = "\\d{5}\\W\\d{4}";
		gabaritos[2] = "\\d{6}\\W\\d{4}";
		int var = linha.split(" ", -1).length - 1;      
		String var2[] = new String[var];                
		var2 = linha.split(" ");                        
		if (!var2[0].equals("")){
			for (int inx = 0; inx <= 2; inx++){
				if(var2[0].matches(gabaritos[inx])){
					return var2[0];
				}					
			}		
		}
		return null;
	}
		
	public static String ultimaPalavra(String linha) {
		int ix = 0;
		int var = linha.split(" ", -1).length - 1;
		String var2[] = new String[var];                
		var2 = linha.split(" ");   
		return var2[var2.length-1];
	}
	
	public static String primeiraPalavra(String linha) {
		int var = linha.split(" ", -1).length - 1;
		String var2[] = new String[var];                
		var2 = linha.split(" ");   
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
		
	public static String obtemNumProcesso(String linhaEntrada){				

		String sequencia = "";
		String[] gabaritos = new String[13];

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
		
		if(!contemNumeros(linhaEntrada)) {
			return null;
		}
				
		linhaEntrada = linhaEntrada.replace(" ", ".");
	
		for (int ix = 0; ix <= linhaEntrada.length()-1; ix++) {
			if ((linhaEntrada.charAt(ix) >= '0' && linhaEntrada.charAt(ix) <= '9') || 
					(linhaEntrada.charAt(ix) == '.' || 
					linhaEntrada.charAt(ix) == '-' || 
					linhaEntrada.charAt(ix) == '/' || 
					linhaEntrada.charAt(ix) == ',')){
				
					if ((linhaEntrada.charAt(ix) >= '0' && linhaEntrada.charAt(ix) <= '9')||
						((linhaEntrada.charAt(ix) == '.' || 
						linhaEntrada.charAt(ix) == '/' || 
						linhaEntrada.charAt(ix) == ',' || 
						linhaEntrada.charAt(ix) == '-')) && sequencia.length() > 0) {						
								sequencia = sequencia + linhaEntrada.charAt(ix);
					}		 
			}
		}
				
		if(sequencia.length() >= 26) {
			sequencia = sequencia.substring(0, 25);
		}

		if (!sequencia.equals("")){
			for (int inx = 0; inx <= 12; inx++){
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
    		config = new File(diario.getParentFile()+"/split.cnf");
        	diarioInput = diario;
        }
	}																		// Fim do método InicializaArquivos
	
	public static String limpaCaracteres(String linha) { 
		linha = linha.replaceAll("&","e");
		return linha;
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
		String pagina = "";
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
						pagina = rabo;
						dummy = linha.substring(0, linha.length()-pagina.length());
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
					pagina = linha.trim();
					bufferEntrada.remove(sequencialIndice-1);
					sequencialIndice--;
				}

				if(ehInteiro(rabo)) {
					if(!ehGrupo){
						secao = dummy.trim();																													
						paginaSecao = pagina;
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
						pagina = "";
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
		
	public static String obtemEdicao(String linhaEntrada){			

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
		while (i <= linhaEntrada.length()-1) {	
			if((linhaEntrada.charAt(i) >= 'A' && linhaEntrada.charAt(i) <= 'Z') || 
					(linhaEntrada.charAt(i) >= 'a' && linhaEntrada.charAt(i) <= 'z') ||
					(linhaEntrada.charAt(i) == 'á' || linhaEntrada.charAt(i) == 'é' ||
					linhaEntrada.charAt(i) == 'í' || linhaEntrada.charAt(i) == 'ó' ||
					linhaEntrada.charAt(i) == 'ú' || linhaEntrada.charAt(i) == 'ã' ||
					linhaEntrada.charAt(i) == 'õ' || linhaEntrada.charAt(i) == 'ç' ||
					linhaEntrada.charAt(i) == 'Á' || linhaEntrada.charAt(i) == 'É' ||
					linhaEntrada.charAt(i) == 'Í' || linhaEntrada.charAt(i) == 'Ó' ||
					linhaEntrada.charAt(i) == 'Ú' || linhaEntrada.charAt(i) == 'Ã' ||
					linhaEntrada.charAt(i) == 'Õ' || linhaEntrada.charAt(i) == 'Ç')) {					
						grupos[ln] = grupos[ln] + linhaEntrada.charAt(i);					
				}
			
			if((linhaEntrada.charAt(i) >= '0' && linhaEntrada.charAt(i) <= '9') || (linhaEntrada.charAt(i) == '/')){
				grupos[ln] = grupos[ln] + linhaEntrada.charAt(i);
			}			
			if (linhaEntrada.charAt(i) == ' ' || linhaEntrada.charAt(i) == ','){
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
		
		if(ehInteiro(linhaDecomposta[0]) && linhaDecomposta[0].length() == 2) {			// DD/MM/AAAA
			if(linhaData.matches("\\d{2}\\W\\d{2}\\W\\d{4}") && linhaData.length() == 10){
				return true;
			}
		} 

		if(!ehInteiro(linhaDecomposta[0]) && ehInteiro(linhaDecomposta[linhaDecomposta.length-1])) {	// extenso			
			if((linhaDecomposta[0].charAt(linhaDecomposta[0].length()-1) == ',') && 
					uf.contains(formataPalavra(linhaDecomposta[0].substring(0, linhaDecomposta[0].length()-1)))) {
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
		
	public static void mapeiaLinhas() throws IOException{		// mapeia o numero de linha de cada Secao e cada Grupo

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
    						if(obtemNumProcesso(linhaDummy) != null || linha.equals("Portaria")){	
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
	
	public static void carregaConfig() throws IOException{
		//gravaLog(obtemHrAtual() + "Carga configuração");
		String linha = "";
		String linhaTratada = "";
		int x = 0;
        FileInputStream arquivoIn = new FileInputStream(config);
		BufferedReader registro = new BufferedReader(new InputStreamReader((arquivoIn), "UTF-8"));
        
        while(linha != null){
	    	linha = registro.readLine();
	    	
	    	if(linha == null) {
	    		break;
	    	} else {
	    		linhaTratada = formataPalavra(linha);
	    	}
	    	switch(x) {
				case 0:
					cliente = linha;
					break;
				case 1:
					tipoSaida = linha;
					break;
				case 2:
					sysOp = linha;
					break;
				case 3:
					url = linha;
					break;
				case 4:
					logFolder = linha;
					break;
	    	}
    		x++;
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
		editalFolder = InterfaceServidor.verificaEdtFolder(sessao, baseFolder, edtFolderName, descricaoFolder, strTribunal, edicao);
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
	
	public static void abreLog(String logName) {
	/*
		try {
			//arquivoLog = new FileWriter(logFolder+strTribunal+seqEdicao+".log");
			arquivoLog = new FileWriter("splt" + logName+ ".log");
			k++;
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
		} catch (IOException e) 
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	*/
	}
	
	public static void lista() throws IOException{
		
		//String fileName = "C:\\srv\\kraio\\indice.txt";
		FileWriter fileW = new FileWriter ("/Users/avmcf/SIJ/_DOS/lista.txt");
		BufferedWriter buffW = new BufferedWriter (fileW);
		//FileWriter fileW = null;
		//BufferedWriter buffW = null;
		try {
		//	fileW = (FileWriter) new OutputStreamWriter(new FileOutputStream(fileW), StandardCharsets.UTF_8);
		//	fileW = (FileWriter) new OutputStreamWriter(new FileOutputStream("/Users/avmcf/SIJ/_DOS/lista.txt"), StandardCharsets.UTF_8);
			//for (IndiceEdicao Indice : Index) {
			for (int x = 0; x <= bufferEntrada.size()-1; x++) {
				//buffW.write (Indice.secao + " - " + Indice.linhaSecao + " - " + Indice.grupo + " - " + Indice.linhaGrupo);
				buffW.write (x + " - " + bufferEntrada.get(x));
				buffW.newLine ();
			}
	        buffW.close ();
		} catch (IOException io)
        {
			JOptionPane.showMessageDialog(null, "Falha na listagem ");
        }
		k++;
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
	
	public static void enviaEdital() throws ParseException, IOException{

		int ultimoSequencial = 1;
		String nomeFile = strTribunal + "-" + seqEdicao + "-" + seqPublicacao + ".txt";
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

		String linhaTraco =  "-----------------------------------------------------------------------------------------";
		String linhaRodaPe = "V&C Consultoria / SplitDO " + versaoSplitter ;

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
		textoSaida.add(atores);
		textoSaida.add(intimados);

		if(introducao.size() > 0) {
			for(String linha : introducao){
				textoSaida.add(linha);
			}
		}

		if(textoEdital.size() > 0) {
			for(String linha : textoEdital){
				textoSaida.add(linha);
			}
		}

		textoSaida.add(" \n");
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
	
	public static void fechaEdital(ArrayList<String> texto) throws Exception, IOException {
		
		if(tipoSaida.equals("DIRETA")) {
			if(sequencialSaida >= 5452) {
				enviaEdital();
			}
			
		} else {
			gravaEdital(texto);
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
		if(numProcesso == null) {
			gravaLog(obtemHrAtual() + " edital " + sequencialSaida + " edital sem linhaProcesso");
		}
		gravaLog(obtemHrAtual() + "Publicação fechada " + sequencialSaida + " / " + secao + " / " + grupo + " / " + assunto + " / " + processo);

		processoAnterior = processo;
		processo = "";
		numProcesso = "";
		sequencialSaida++;
		atores = "";
		intimados = "";
		textoEdital.clear();
		atoresOK = false;
		sequencialAssunto = 0;
		dtValida = false;
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
    	Edital.setProcesso(processo);
    	
    	if(!atores.isEmpty()) {
    		Edital.setAtores(atores);
    	} else Edital.setAtores("---");
    	if(!intimados.isEmpty()) {
    		Edital.setIntimados(intimados);
    	} else Edital.setIntimados("---");
    	
    	Edital.setTexto(edital);
    	Edital.setTipoDocumento("publicacao");
    	Edital.setCliente("Jairo Aquino Advogados");
    	if(!textoIntroducao.isEmpty()) {
    		Edital.setIntroducao(formataTexto(textoIntroducao));
    	}
    	base.setFileName(nomeFile);

		SalvaPdf.gravaPdf();
		GravaXml.main();
		gravaLog(obtemHrAtual() + "Publicação " + sequencialSaida + " / " + secao + " / " + grupo + " / " + assunto + " / " + processo);
    /*		
    	
    	
		if(!pauta) {
			assunto = "";
		}
		processoAnterior = processo;
		processo = "";
		numProcesso = "";
		sequencialSaida++;
		atores = "";
		intimados = "";
		edital.clear();
		textoEdital.clear();
		atoresOK = false;
		sequencialAssunto = 0;
		dtValida = false;   
	*/          
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
				if(obtemNumProcesso(buffer.get(ix)) == null) {
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
			if(verificaPontoFinal(buffer.get(ix).trim()) || obtemNumProcesso(buffer.get(ix)) != null){
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
		
		@SuppressWarnings("null")
		public static boolean validaAssunto(String linha) {

			String [] arrayAssunto = null;
			String [] arrayLinha = linha.split(" ");
			int igualidades = 0;
			double percentual = 0;
			if(!tabelaAssuntos.contains(linha)) {
				return false;
			}
			if(assuntosUtilizados.contains(linha)) {					// tabela de assuntos já encontrados no processamento atual
				return true;
			}
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

			String [] arrayLinha = linha.split(" ");
			
			if(arrayLinha.length == 0) {
				return false;
			} else {
				if(arrayLinha.length >= 2) {
					if((formataPalavra(arrayLinha[0]).equals("processo") && 
							(arrayLinha[1].equals("nº") || arrayLinha[1].equals("Nº")))) {
						return true;
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
		
	public static String obtemTribunal(String linhaEntrada){

		String sequencia = "";
		int i = 0;
		if (linhaEntrada.startsWith("Caderno Judiciário do Tribunal")){
			if (!linhaEntrada.contains("Superior")) {
				while (i <= linhaEntrada.length()-1) {
					if ((linhaEntrada.charAt(i) >= '0' && linhaEntrada.charAt(i) <= '9')){
						sequencia = sequencia + linhaEntrada.charAt(i);
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
	
	