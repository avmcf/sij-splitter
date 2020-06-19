package com.vconsulte.sij.splitter;

import java.util.ArrayList;
import java.util.List;

public class MapaEdicao {
	public String secao;
	public int sequenciaSecao;
	public String grupo;
	public int sequenciaGrupo;
	public String assunto;
	public int sequenciaAssunto;
	public String processo;
	public int sequenciaProcesso;
	public String atores;
	public int sequenciaAtores;
	public String intimados;
	public int sequenciaIntimados;
	
	public String getSecao() {
		return secao;
	}
	public void setSecao(String secao) {
		this.secao = secao;
	}
	public int getSequenciaSecao() {
		return sequenciaSecao;
	}
	public void setSequenciaSecao(int sequenciaSecao) {
		this.sequenciaSecao = sequenciaSecao;
	}
	public String getGrupo() {
		return grupo;
	}
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	public int getSequenciaGrupo() {
		return sequenciaGrupo;
	}
	public void setSequenciaGrupo(int sequenciaGrupo) {
		this.sequenciaGrupo = sequenciaGrupo;
	}
	public String getAssunto() {
		return assunto;
	}
	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}
	public int getSequenciaAssunto() {
		return sequenciaAssunto;
	}
	public void setSequenciaAssunto(int sequenciaAssunto) {
		this.sequenciaAssunto = sequenciaAssunto;
	}
	public String getProcesso() {
		return processo;
	}
	public void setProcesso(String processo) {
		this.processo = processo;
	}
	public int getSequenciaProcesso() {
		return sequenciaProcesso;
	}
	public void setSequenciaProcesso(int sequenciaProcesso) {
		this.sequenciaProcesso = sequenciaProcesso;
	}
	public String getAtores() {
		return atores;
	}
	public void setAtores(String atores) {
		this.atores = atores;
	}
	public int getSequenciaAtores() {
		return sequenciaAtores;
	}
	public void setSequenciaAtores(int sequenciaAtores) {
		this.sequenciaAtores = sequenciaAtores;
	}
	public String getIntimados() {
		return intimados;
	}
	public void setIntimados(String intimados) {
		this.intimados = intimados;
	}
	public int getSequenciaIntimados() {
		return sequenciaIntimados;
	}
	public void setSequenciaIntimados(int sequenciaIntimados) {
		this.sequenciaIntimados = sequenciaIntimados;
	}
	
	public MapaEdicao(String secao, int sequenciaSecao, String grupo, int sequenciaGrupo, String assunto,
			int sequenciaAssunto, String processo, int sequenciaProcesso, String atores, int sequenciaAtores,
			String intimados, int sequenciaIntimados, List<String> edicao) {
		super();
		this.secao = secao;
		this.sequenciaSecao = sequenciaSecao;
		this.grupo = grupo;
		this.sequenciaGrupo = sequenciaGrupo;
		this.assunto = assunto;
		this.sequenciaAssunto = sequenciaAssunto;
		this.processo = processo;
		this.sequenciaProcesso = sequenciaProcesso;
		this.atores = atores;
		this.sequenciaAtores = sequenciaAtores;
		this.intimados = intimados;
		this.sequenciaIntimados = sequenciaIntimados;

	}

}
