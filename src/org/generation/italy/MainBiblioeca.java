package org.generation.italy;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.generation.italy.model.Libro;

public class MainBiblioeca {
	static String url = "jdbc:mysql://localhost:3306/biblioteca";
	static String sql;
	static String username = "root";
	static String password = "";

	public static void main(String[] args) {
		ArrayList<Libro> elencoLibri = new ArrayList<Libro>();
		Scanner sc = new Scanner(System.in);
//			HashMap<String, String> fornitori=new HashMap<String, String>();
//			HashMap<String, String> clienti=new HashMap<String, String>();
//			HashMap<String, String> prodotti=new HashMap<String, String>();
//			HashMap<String, String> tipologieMovimento=new HashMap<String, String>();

		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // data cutomizzata dd(giorno) MM(mese)
																			// yyyy(anno 4 cifre)
		String sceltaMenu = ""; // menu principale
		String sceltaOpz; // menu secondario
		String sceltaMenu2 = "";
		String codiceProdottoScelto;
		String sceltaCancellazione = null;
		int giacenza = 0;
		boolean ricicla = false;
		int righeInteressate;

		Libro l;
		// TODO Auto-generated method stub

		caricaLibri(elencoLibri);
		do {

			System.out.println("Menu Principale: Scegli il numero corrispondente");
			System.out.println("(1) Inserimento nuovo libro");
			System.out.println("(2) Visualizzazione libri esistenti");
			System.out.println("(3) Cancellazione libro");
			System.out.println("(4) Modificare un libro");
			System.out.println("\n\n(qualsiasi numero) Per uscire");
			sceltaMenu = sc.nextLine();
////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////Inserimento LIBRI/////////////////////////////////////////////////////////
			if (sceltaMenu.equals("1")) {
				do {
					l = new Libro();
					System.out.println("Stai inserendo un nuovo libro");
					System.out.println("Inserisci il titolo del libro");
					l.titolo = sc.nextLine();

					System.out.println("Inserisci il numero delle pagine");
					l.numPagine = sc.nextInt();
					sc.nextLine();

					System.out.println("Inserisci la quantità");
					l.qnt = sc.nextInt();
					sc.nextLine();

					System.out.println("inserisci la data di pubblicazione (gg/mm/aaaa): ");
					while (true) {
						try {
							l.dataPublicazione = LocalDate.parse(sc.nextLine(), df);
							break;
						} catch (Exception e) {
							System.out.println("Data non valida. Riprova.");
						}
					}
					
					//HashMap<Integer, String> trovato = new HashMap<Integer, String>();
					////////////// recupero id autore////////////////
//					trovato = trovaId("autore");
//					for (int i : trovato.keySet()) {
//						l.idAutore = i;
//						l.nomeAutore = trovato.get(i);
//					}
//					////////// recupero id genere///////////
//					trovato = trovaId("genere");
//					for (int i : trovato.keySet()) {
//						l.idGenere = i;
//						l.genere = trovato.get(i);
//					}
//					//////// recupero id editore/////////////
//					trovato = trovaId("editore");
//					for (int i : trovato.keySet()) {
//						l.idEditore = i;
//						l.editore = trovato.get(i);
//					}
					l.idAutore = trovaId("autore");
					
					////////// recupero id genere///////////
					l.idGenere = trovaId("genere");
					
					//////// recupero id editore/////////////
					l.idEditore = trovaId("editore");
					
					////////////// carico il libro in DATABASE Biblioteca//////////
					righeInteressate = databaseLibro(l);
					System.out.println(l.toString());
					/////////////// carico il libro in HASHMAP elencoLibri//////////////
					elencoLibri.clear();
					caricaLibri(elencoLibri);
					System.out.println("Per tornare al menù principale scrivi (1)");
					System.out.println("Per uscire scrivi (2)");
					System.out.println("Per cancelare un altro libro scrivi (3)");
					sceltaMenu2 = sc.nextLine();
				}while(sceltaMenu2 == "3");
////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////Visualizza LIBRI/////////////////////////////////////////////////////////
			} else if (sceltaMenu.equals("2")) {
				elencoLibri.clear();
				caricaLibri(elencoLibri);
				System.out.println("Libri trovati: " + elencoLibri.size() + " libri");
				for (Libro lib : elencoLibri) {
					System.out.println(lib.toString());
				}
				
				System.out.println("Per tornare al menù principale scrivi (1)");
				System.out.println("Per uscire scrivi (2)");

				sceltaMenu2 = sc.nextLine();
////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////Cancellazione LIBRO/////////////////////////////////////////////////////////
			} else if (sceltaMenu.equals("3")) {
				do {
					elencoLibri.clear();
					caricaLibri(elencoLibri);
					System.out.println("Inserisci ID del libro da cancellare");
					// l=new Libro();
					for (Libro lib : elencoLibri) {
						System.out.println(lib.toString2());
					}
					int id = sc.nextInt();
					sc.nextLine();

//						
					for (Libro lib : elencoLibri) {
						if (lib.id == id) {
							System.out.println(
									"Sei sicuro di voler cancellare il libro con le seguenti caratteristiche?(s/n)");

							System.out.println("ID: " + lib.id);
							System.out.println("Titolo: " + lib.titolo);
							System.out.println("Nome autore: " + lib.nomeAutore);
							System.out.println("Cognome autore: " + lib.cognomeAutore);
							System.out.println("Editore: " + lib.editore);
							System.out.println("Genere: " + lib.genere);
							System.out.println("Data di pubblicazione: " + lib.dataPublicazione);
							System.out.println("numero delle pagine: " + lib.numPagine);

							sceltaCancellazione = sc.nextLine();
							break;
						}
					}

					if (sceltaCancellazione.equalsIgnoreCase("s")) {
						try (Connection conn = DriverManager.getConnection(url, username, password)) {
							sql = "DELETE FROM libriWHERE id = ?;";
							try (PreparedStatement ps = conn.prepareStatement(sql)) {
								ps.setInt(1, id);
								int righeEleminate = ps.executeUpdate();
								System.out.println("Libro cancellato con successo!!");
							}

						} catch (SQLTimeoutException e) {
							// si è verificato un time out.
							System.err.println("Ricordati di far partire il DBMS "); // stampare il tipo di errore
						} catch (SQLException e) {
							// si è verificato un problema SQL
							System.err.println("Conrolla l'istruzione sql " + e.getMessage()); // stampare il tipo di
																								// errore
						} catch (Exception e) {
							// si è verificato un errore. L'oggetto Exception contiene info sull'errore
							System.err.println("Si è verificato un errore: " + e.getMessage()); // stampare il tipo di
																								// errore
						}
					}

					System.out.println("Per tornare al menù principale scrivi (1)");
					System.out.println("Per uscire scrivi (2)");
					System.out.println("Per cancelare un altro libro scrivi (3)");

					sceltaMenu2 = sc.nextLine();
				} while (sceltaMenu2 == "3");
////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////Modifica LIBRO/////////////////////////////////////////////////////////
			} else if (sceltaMenu.equals("4")) {
				do {
					elencoLibri.clear();
					caricaLibri(elencoLibri);
					System.out.println("Inserisci ID del libro da modificare");
					// l=new Libro();
					for (Libro lib : elencoLibri) {
						System.out.println(lib.toString2());
					}
					int id = sc.nextInt();
					sc.nextLine();

//						
					for (Libro lib : elencoLibri) {
						if (lib.id == id) {
							System.out.println(
									"Sei sicuro di voler modificare il libro con le seguenti caratteristiche?(s/n)");

							System.out.println("ID: " + lib.id);
							System.out.println("Titolo: " + lib.titolo);
							System.out.println("Nome autore: " + lib.nomeAutore);
							System.out.println("Cognome autore: " + lib.cognomeAutore);
							System.out.println("Editore: " + lib.editore);
							System.out.println("Genere: " + lib.genere);
							System.out.println("Data di pubblicazione: " + lib.dataPublicazione);
							System.out.println("numero delle pagine: " + lib.numPagine);

							sceltaCancellazione = sc.nextLine();
							break;
						}
					}

					if (sceltaCancellazione.equalsIgnoreCase("s")) {
						l = new Libro();
						l.id = id;
						System.out.println("Inserisci il titolo del libro (Modifica): ");
						l.titolo = sc.nextLine();

						System.out.println("Inserisci il numero delle pagine (Modifica): ");
						l.numPagine = sc.nextInt();
						sc.nextLine();

						System.out.println("Inserisci la quantità (Modifica): ");
						l.qnt = sc.nextInt();
						sc.nextLine();

						System.out.println("inserisci la data di pubblicazione (gg/mm/aaaa) (Modifica): ");
						while (true) {
							try {
								l.dataPublicazione = LocalDate.parse(sc.nextLine(), df);
								break;
							} catch (Exception e) {
								System.out.println("Data non valida. Riprova.");
							}
						}

//						HashMap<Integer, String> trovato = new HashMap<Integer, String>();
//						////////////// recupero id autore////////////////
//						trovato = trovaId("autore");
//						for (int i : trovato.keySet()) {
//							l.idAutore = i;
//							l.nomeAutore = trovato.get(i);
//						}
//						////////// recupero id genere///////////
//						trovato = trovaId("genere");
//						for (int i : trovato.keySet()) {
//							l.idGenere = i;
//							l.genere = trovato.get(i);
//						}
//						//////// recupero id editore/////////////
//						trovato = trovaId("editore");
//						for (int i : trovato.keySet()) {
//							l.idEditore = i;
//							l.editore = trovato.get(i);
//						}
						l.idAutore = trovaId("autore");
						
						////////// recupero id genere///////////
						l.idGenere = trovaId("genere");
						
						//////// recupero id editore/////////////
						l.idEditore = trovaId("editore");
						
						////////////// carico il libro in DATABASE Biblioteca//////////
						righeInteressate = databaseLibro(l);
						System.out.println(l.toString());
						/////////////// carico il libro in ArrayList elencoLibri//////////////
						elencoLibri.clear();
						caricaLibri(elencoLibri);
//						try (Connection conn = DriverManager.getConnection(url, username, password)) {
//							sql = "DELETE FROM libriWHERE id = ?;";
//							try (PreparedStatement ps = conn.prepareStatement(sql)) {
//								ps.setInt(1, id);
//								int righeEleminate = ps.executeUpdate();
//								System.out.println("Libro cancellato con successo!! "+righeEleminate+ " righe eleminate");
//							}
//
//						} catch (SQLTimeoutException e) {
//							// si è verificato un time out.
//							System.err.println("Ricordati di far partire il DBMS "); // stampare il tipo di errore
//						} catch (SQLException e) {
//							// si è verificato un problema SQL
//							System.err.println("Conrolla l'istruzione sql " + e.getMessage()); // stampare il tipo di
//																								// errore
//						} catch (Exception e) {
//							// si è verificato un errore. L'oggetto Exception contiene info sull'errore
//							System.err.println("Si è verificato un errore: " + e.getMessage()); // stampare il tipo di
//																								// errore
//						}
					}

					System.out.println("Per tornare al menù principale scrivi (1)");
					System.out.println("Per uscire scrivi (2)");
					System.out.println("Per modificare un altro libro scrivi (3)");

					sceltaMenu2 = sc.nextLine();
				} while (sceltaMenu2.equals("3"));
			}

		} while (sceltaMenu2.equals("1"));

		// sc.close();
	}
	// caria i libri dal database al ArrayList elenco libri

	static void caricaLibri(ArrayList<Libro> elencoLibri) {

		try (Connection conn = DriverManager.getConnection(url, username, password)) {
			sql = "SELECT \r\n" + "libri.id AS id_libro,\r\n" + "libri.titolo AS titolo_libro,\r\n"
					+ "autori.id AS id_autore,\r\n" + "autori.nome AS nome_autore,\r\n"
					+ "autori.cognome AS cognome_autore,\r\n" + "editore.id AS id_editore,\r\n"
					+ "editore.nome AS nome_editore,\r\n" + "genere.id AS id_genere,\r\n"
					+ "genere.nome AS nome_genere,\r\n" + "libri.data_pubblicazione AS data_pubblicazione,\r\n"
					+ "libri.num_pagine AS num_pagine,\r\n" + "libri.qnt AS qnt\r\n" + "FROM \r\n" + "libri\r\n"
					+ "LEFT JOIN \r\n" + "autori ON libri.id_autore = autori.id\r\n" + "LEFT JOIN \r\n"
					+ "editore ON libri.id_editore = editore.id\r\n" + "LEFT JOIN \r\n"
					+ "genere ON libri.id_genere = genere.id;";
			try (PreparedStatement ps = conn.prepareStatement(sql)) { // provo a creare l'istruzione SQL

				try (ResultSet rs = ps.executeQuery()) { // il ResultSet mi consente di scorrere il risultato della
															// SELECT una riga alla volta
					while (rs.next()) {
						Libro l = new Libro();// rs.next() restituisce true se c'è ancora qualche riga da leggere, falso
												// altrimenti
						l.id = rs.getInt("id_libro");
						l.titolo = rs.getString("titolo_libro");

						l.nomeAutore = rs.getString("nome_autore");
						l.cognomeAutore = rs.getString("cognome_autore");
						l.editore = rs.getString("nome_editore");
						l.genere = rs.getString("nome_genere");
						l.idAutore = rs.getInt("id_autore");
						l.idGenere = rs.getInt("id_genere");
						l.idEditore = rs.getInt("id_editore");
						l.dataPublicazione = rs.getDate("data_pubblicazione").toLocalDate();
						l.numPagine = rs.getInt("num_pagine");
						l.qnt = rs.getInt("qnt");

						elencoLibri.add(l);
					}

				}
			}
		} catch (SQLTimeoutException e) {
			// si è verificato un time out.
			System.err.println("Ricordati di far partire il DBMS"); // stampare il tipo di errore
		} catch (SQLException e) {
			// si è verificato un problema SQL
			System.err.println("Conrolla l'istruzione sql" + e.getMessage()); // stampare il tipo di errore
		} catch (Exception e) {
			// si è verificato un errore. L'oggetto Exception contiene info sull'errore
			System.err.println("Si è verificato un errore: " + e.getMessage()); // stampare il tipo di errore
		}
	}
//HashMap<Integer, String>
	static int trovaId(String tabella) {
		Scanner sc = new Scanner(System.in);
		int id = 0;
		boolean ricicla = false;
		String nome;
		String cognome = "";

		HashMap<Integer, String> idEnome = new HashMap<Integer, String>();
		try (Connection conn = DriverManager.getConnection(url, username, password)) {
			do {
				if (tabella.equalsIgnoreCase("autore")) {
					System.out.println("Inserisci il nome del autore1");
					nome = sc.nextLine();
					System.out.println("Inserisci il cognome del autore");
					cognome = sc.nextLine();
					sql = "SELECT id FROM autori WHERE LOWER(nome) LIKE LOWER(?) AND LOWER(cognome) LIKE LOWER(?)";
				} else {
					System.out.println("Inserisci " + tabella);
					nome = sc.nextLine();
					sql = "SELECT id FROM " + tabella + " WHERE LOWER(nome) LIKE LOWER(?)"; // la query corretta senza i
																							// caratteri jolly

				}
				try (PreparedStatement ps = conn.prepareStatement(sql)) { // provo a creare l'istruzione sql
					if (tabella.equalsIgnoreCase("autore")) {
						ps.setString(1, "%" + nome + "%"); // aggiungo i caratteri jolly al parametro nomeAutore
						ps.setString(2, "%" + cognome + "%");
					} else {
						ps.setString(1, nome); // aggiungo i caratteri jolly al parametro
					}
					try (ResultSet rs = ps.executeQuery()) { // il ResultSet mi consente di scorrere il risultato della
																// SELECT una riga alla volta
						if (rs.next()) { // rs.next() restituisce true se c'è ancora qualche riga da leggere, falso
											// altrimenti
							id = rs.getInt("id"); // recupero il valore della colonna "id"
							System.out.println("preso id " + tabella);
							ricicla = false;

							//idEnome.put(id, nome.concat(" " + cognome));
						} else {
							System.out.println(tabella + " sbagliato o non esistente");
							ricicla = true;
						}
					}
				}
			} while (ricicla == true);
		} catch (SQLTimeoutException e) {
			// si è verificato un time out.
			System.err.println("Ricordati di far partire il DBMS"); // stampare il tipo di errore
		} catch (SQLException e) {
			// si è verificato un problema SQL
			System.err.println("Conrolla l'istruzione sql" + e.getMessage()); // stampare il tipo di errore
		} catch (Exception e) {
			// si è verificato un errore. L'oggetto Exception contiene info sull'errore
			System.err.println("Si è verificato un errore: " + e.getMessage()); // stampare il tipo di errore
		}
		//return idEnome;
		return id;
	}

	static int databaseLibro(Libro l) {
		String sql;
		int righeInteressate = 0;
		try (Connection conn = DriverManager.getConnection(url, username, password)) {
			if (l.id == 0) {
				sql = "INSERT INTO libri (titolo, id_autore, id_genere, data_pubblicazione, num_pagine, qnt,id_editore)"
						+ " VALUES (? , ? , ? , ? , ? , ? , ? )";
				System.out.println("Tentativo di esecuzione INSERT");
			} else {
				sql = "UPDATE libri SET titolo = ?, id_autore = ?, id_genere = ?, data_pubblicazione = ?, num_pagine = ?, qnt = ?, id_editore = ? WHERE id = ?";
				System.out.println("Tentativo di esecuzione UPDATE");
			}
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				// ps.setInt(1, id);//come si fa per farlo auto incrementante
				// ps.setObject(1, data);

				ps.setString(1, l.titolo);
				ps.setInt(2, l.idAutore);
				ps.setInt(3, l.idGenere);
				ps.setDate(4, Date.valueOf(l.dataPublicazione));
				ps.setInt(5, l.numPagine);
				ps.setInt(6, l.qnt);
				ps.setInt(7, l.idEditore);
				if(l.id==0) {
					
					System.out.println("Libro inserito con successo");
				}else {
					ps.setInt(8, l.id);
					System.out.println("Libro modificato con successo");
				}
				righeInteressate = ps.executeUpdate(); // eseguo l'istruzione //execteUpdate se la queru non
														// restituisce niente
				System.out.println("Righe : " + righeInteressate);

			}
		} catch (SQLTimeoutException e) {
			// si è verificato un time out.
			System.err.println("Ricordati di far partire il DBMS"); // stampare il tipo di errore
		} catch (SQLException e) {
			// si è verificato un problema SQL
			System.err.println("Conrolla l'istruzione sql" + e.getMessage()); // stampare il tipo di errore
		} catch (Exception e) {
			// si è verificato un errore. L'oggetto Exception contiene info sull'errore
			System.err.println("Si è verificato un errore: " + e.getMessage()); // stampare il tipo di errore
		}

		return righeInteressate;
	}
}
