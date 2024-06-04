package org.generation.italy.model;

import java.time.LocalDate;

public class Libro {
	public int id;
	public String titolo;
	public int idAutore;
	public String nomeAutore;
	public String cognomeAutore;

	public int idGenere;
	public String genere;

	public LocalDate dataPublicazione;
	public int numPagine;
	public int qnt;
	public int idEditore;
	public String editore;
	@Override
	public String toString() {
		return "Libro [id=" + id + ", titolo=" + titolo + ", idAutore=" + idAutore + ", nomeAutore=" + nomeAutore
				+ ", cognomeAutore=" + cognomeAutore + ", idGenere=" + idGenere + ", genere=" + genere
				+ ", dataPublicazione=" + dataPublicazione + ", numPagine=" + numPagine + ", qnt=" + qnt
				+ ", idEditore=" + idEditore + ", editore=" + editore + "]\n\n";
	}
	public String toString2() {
		return "Libro [id=" + id + ", titolo=" + titolo + "]";
	}

	

}
