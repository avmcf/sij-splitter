package com.vconsulte.sij.splitter;

public class IndiceEdicao {
 	
	public String secao;
	public int paginaSecao;
	public int linhaSecao;
	public String complementoSecao;
	public String grupo;
	public int paginaGrupo;
	public int linhaGrupo;
		
	public IndiceEdicao(String secao, 
			int paginaSecao, 
			int linhaSecao, 
			String complementoSecao, 
			String grupo, 
			int paginaGrupo,  
			int linhaGrupo) {
		
		this.secao = secao;
		this.paginaSecao = paginaSecao;
		this.linhaSecao = linhaSecao;
		this.complementoSecao = complementoSecao;
		this.grupo = grupo;
		this.paginaGrupo = paginaGrupo;
		this.linhaGrupo = linhaGrupo;
	}

	public String getSecao() { 
		return secao; 
	}
	public void setSecao(String secao) { 
		this.secao = secao; 
	}
	// ------------------------------------
	
	public int getPaginaSecao() { 
		return paginaSecao; 
	}
	public void setPaginaSecao(int paginaSecao) { 
		this.paginaSecao = paginaSecao; 
	}
	// ------------------------------------
		
	public int getLinhaSecao() { 
		return linhaSecao; 
	}
	public void setLinhaSecao(int linhaSecao) { 
		this.linhaSecao = linhaSecao; 
	}
	// ------------------------------------
	
	public String getcomplementoSecao() { 
		return complementoSecao; 
	}
	public void setcomplementoaSessao(String complementoSecao) { 
		this.complementoSecao = complementoSecao; 
	}
	// ------------------------------------
	
	public String getGrupo() { 
		return grupo; 
	}
	public void setGrupo(String grupo) { 
		this.grupo = grupo; 
	}
	// ------------------------------------
	
	public int getPaginaGrupo() { 
		return paginaGrupo; 
	}
	public void setPaginaGrupo(int paginaGrupo) { 
		this.paginaGrupo = paginaGrupo; 
	}
	
	// ------------------------------------
	
	public int getLinhaGrupo() { 
		return linhaGrupo; 
	}
	public void setLinhaGrupo(int linhaGrupo) { 
		this.linhaGrupo = linhaGrupo; 
	}
	// ------------------------------------
		
}

