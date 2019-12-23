package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import model.ConnessioneDB;
import model.bean.VigileDelFuocoBean;

/**
 * Classe che si occupa della gestione dei dati 
 * persistenti relativi all'entit� 'VigileDelFuoco'
 * @author Eugenio Sottile 
 */

public class VigileDelFuocoDao {

	
	/**
	 * Si occupa del salvataggio dei dati di un VigileDelFuocoBean nel database.
	 * @param vf � un oggetto di tipo VigileDelFuocoBean da memorizzare del database
	 * @return true se l'operazione va a buon fine, false altrimenti
	 */
	public static boolean salva(VigileDelFuocoBean vf) {
		
		//controlli
		if(vf == null)
			//lancio eccezione
			;

		try(Connection con = ConnessioneDB.getConnection()) {
			
			// Esecuzione query
			PreparedStatement ps = con.prepareStatement("insert into vigile(email, nome, cognome, turno, mansione, "
														+ "giorniferieannocorrente, giorniferieannoprecedente, caricolavoro, "
														+ "adoperabile, grado, username) values (?, ?, ? ,? ,? ,? ,? , ? ,?, ?, ?);");
			ps.setString(1, vf.getEmail());
			ps.setString(2, vf.getNome());
			ps.setString(3, vf.getCognome());
			ps.setString(4, vf.getTurno());
			ps.setString(5, vf.getMansione());
			ps.setInt(6, vf.getGiorniFerieAnnoCorrente());
			ps.setInt(7, vf.getGiorniFerieAnnoPrecedente());
			ps.setInt(8, vf.getCaricoLavoro());
			ps.setBoolean(9, vf.isAdoperabile());
			ps.setString(10, vf.getGrado());
			ps.setString(11, vf.getUsername());
			ps.executeUpdate();
			con.commit();

			return true;
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		
		
	} 
	
	/**
	 * Si occupa dell'ottenimento di un VigileDelFuocoBean dal database data la sua chiave.
	 * @param chiaveEmail � una stringa che identifica un VigileDelFuocoBean nel database
	 * @return Un tipo VigileDelFuocoBean identificato da chiaveEmail, null altrimenti
	 */
	public static VigileDelFuocoBean ottieni(String chiaveEmail) {
		
		//controlli
		if(chiaveEmail == null)
			//lancio eccezione
			;
		
		try(Connection con = ConnessioneDB.getConnection()) {
			
			// Esecuzione query
			PreparedStatement ps = con.prepareStatement("select * from Vigile where email = ?;");
			ps.setString(1, chiaveEmail);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) { 
				// Ottenimento dati dall'interrogazione
				String nome = rs.getString("nome");
				String cognome = rs.getString("cognome");
				String email = rs.getString("email");
				String turno = rs.getString("turno");
				String username = rs.getString("username");
				String mansione = rs.getString("mansione");
				int giorniFerieAnnoCorrente = rs.getInt("giorniferieannocorrente");
				int giorniFerieAnnoPrecedente = rs.getInt("giorniferieannoprecedente");
				int caricoLavoro = rs.getInt("caricolavoro");
				boolean adoperabile = rs.getBoolean("adoperabile");
				String grado = rs.getString("grado");
				
				//Instanziazione oggetto VigileDelFuocoBean
				VigileDelFuocoBean vf = new VigileDelFuocoBean();
				vf.setNome(nome);
				vf.setCognome(cognome);
				vf.setEmail(email);
				vf.setTurno(turno);
				vf.setUsername(username);
				vf.setMansione(mansione);
				vf.setGiorniFerieAnnoCorrente(giorniFerieAnnoCorrente);
				vf.setGiorniFerieAnnoPrecedente(giorniFerieAnnoPrecedente);
				vf.setCaricoLavoro(caricoLavoro);
				vf.setAdoperabile(adoperabile);
				vf.setGrado(grado);
				
				return vf;
			} else { 
				return null;
			}
				
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * Si occupa dell'ottenimento di una collezione di VigileDelFuocoBean dal database
	 * con campo 'adoperabile' settato a true.
	 * @return una collezione di VigileDelFuocoBean con campo 'adoperabile' settato a true 
	 */
	public static Collection<VigileDelFuocoBean> ottieni() {
		
		try(Connection con = ConnessioneDB.getConnection()) {
			
			// Esecuzione query
			PreparedStatement ps = con.prepareStatement("select * from Vigile where adoperabile = true;");
			ResultSet rs = ps.executeQuery();
			
			//Instanziazione del set dei Vigili del Fuoco
			HashSet<VigileDelFuocoBean> vigili = new HashSet<VigileDelFuocoBean>();
			
			//Iterazione sui risultati
			while(rs.next()) {
				
				// Ottenimento dati dall'interrogazione
				String nome = rs.getString("nome");
				String cognome = rs.getString("cognome");
				String email = rs.getString("email");
				String turno = rs.getString("turno");
				String username = rs.getString("username");
				String mansione = rs.getString("mansione");
				int giorniFerieAnnoCorrente = rs.getInt("giorniferieannocorrente");
				int giorniFerieAnnoPrecedente = rs.getInt("giorniferieannoprecedente");
				int caricoLavoro = rs.getInt("caricolavoro");
				boolean adoperabile = rs.getBoolean("adoperabile");
				String grado = rs.getString("grado");
				
				VigileDelFuocoBean vf = new VigileDelFuocoBean();
				vf.setNome(nome);
				vf.setCognome(cognome);
				vf.setEmail(email);
				vf.setTurno(turno);
				vf.setUsername(username);
				vf.setMansione(mansione);
				vf.setGiorniFerieAnnoCorrente(giorniFerieAnnoCorrente);
				vf.setGiorniFerieAnnoPrecedente(giorniFerieAnnoPrecedente);
				vf.setCaricoLavoro(caricoLavoro);
				vf.setAdoperabile(adoperabile);
				vf.setGrado(grado);
				
				vigili.add(vf);
				
			}
			
			return vigili;
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	
	}
	
	/**
	 * Si occupa del settaggio del campo 'adoperabile' di un Vigile del Fuoco
	 * nel database, identificato dalla sua chiave.
	 * @param chiaveEmail � una stringa che identifica un VigileDelFuocoBean nel database
	 * @param adoperabile � un booleano che indica l'adoperabilit� di un VigileDelFuocoBean
	 * @return true se l'operazione va a buon fine, false altrimenti
	 */
	public static boolean setAdoperabile(String chiaveEmail, boolean adoperabile) {
		
		//controlli
		if(chiaveEmail == null)
			//lancio eccezione
			;
		
		try(Connection con = ConnessioneDB.getConnection()) {
			
			// Esecuzione query
			PreparedStatement ps = con.prepareStatement("update Vigile set adoperabile = ? where email = ?;");
			ps.setBoolean(1, adoperabile);
			ps.setString(2, chiaveEmail);
			ps.executeUpdate();
			con.commit();
			
			return true;
				
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
	/**
	 * Si occupa della modifica dei dati di un Vigile del Fuoco nel database.
	 * @param chiaveEmail � una stringa che identifica un VigileDelFuocoBean nel database
	 * @param nuovoVF ,  � un oggetto di tipo VigileDelFuocoBean
	 * @return true se l'operazione va a buon fine, false altrimenti
	 */
	public static boolean modifica(String chiaveEmail, VigileDelFuocoBean nuovoVF) {
		
		//controlli
		if(chiaveEmail == null)
			//lancio eccezione
			;
		
		if(nuovoVF == null)
			//lancio eccezione
			;
		
		try(Connection con = ConnessioneDB.getConnection()) {
			
			// Esecuzione query
			PreparedStatement ps = con.prepareStatement("update Vigile set email = ?, nome = ?, cognome = ?,"
														+ " mansione = ?, turno = ?, giorniferieannocorrente = ?,"
														+ " giorniferieannoprecedente = ?, caricolavoro = ?,"
														+ " grado = ?, username = ? where email = ?;");		
			ps.setString(1, nuovoVF.getEmail());
			ps.setString(2, nuovoVF.getNome());
			ps.setString(3, nuovoVF.getCognome());
			ps.setString(4, nuovoVF.getMansione());
			ps.setString(5, nuovoVF.getTurno());
			ps.setInt(6, nuovoVF.getGiorniFerieAnnoCorrente());
			ps.setInt(7, nuovoVF.getGiorniFerieAnnoPrecedente());
			ps.setInt(8, nuovoVF.getCaricoLavoro());
			ps.setString(9, nuovoVF.getGrado());
			ps.setString(10, nuovoVF.getUsername());
			ps.setString(11, chiaveEmail);
			ps.executeUpdate();
			con.commit();
			
			return true;
				
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
	
	
	/**
	 * @param data , la data del giorno di cui si vuole avere la lista dei vigili disponibili
	 * @return una lista di VigileDelFuocoBean che hanno attributo adoperabile=true 
	 * 			e non sono in ferie o malattia nella data passata come parametro
	 */
	public static ArrayList<VigileDelFuocoBean> getDisponibili(Date data){
		try(Connection con = ConnessioneDB.getConnection()) {

			// Query di ricerca
			PreparedStatement ps = con.prepareStatement("SELECT v.email, v.nome, v.cognome, v.turno, v.mansione, "
					+ "v.giorniferieannocorrente, v.giorniferieannoprecedente, v.caricolavoro, v.adoperabile, v.grado, v.username " + 
					" FROM Vigile v " + 
					" WHERE v.adoperabile=true AND NOT EXISTS " + 
					" (SELECT *" + 
					" FROM Malattia m " + 
					" WHERE m.emailVF= v.email AND ? BETWEEN m.dataInizio AND m.dataFine)" + 
					" AND NOT EXISTS" + 
					" (SELECT *" + 
					" FROM Ferie f " + 
					" WHERE f.emailVF= v.email AND ? BETWEEN f.dataInizio AND f.dataFine);");
			ps.setDate(1, data);
			ps.setDate(2, data);
			ResultSet rs = ps.executeQuery();
			ArrayList<VigileDelFuocoBean> vigili=new ArrayList<>();

			//Iterazione dei risultati
			while(rs.next()) {

				String nome = rs.getString("nome");
				String cognome = rs.getString("cognome");
				String email = rs.getString("email");
				String turno = rs.getString("turno");
				String username = rs.getString("username");
				String mansione = rs.getString("mansione");
				int giorniFerieAnnoCorrente = rs.getInt("giorniferieannocorrente");
				int giorniFerieAnnoPrecedente = rs.getInt("giorniferieannoprecedente");
				int caricoLavoro = rs.getInt("caricolavoro");
				boolean adoperabile = rs.getBoolean("adoperabile");
				String grado = rs.getString("grado");
				VigileDelFuocoBean vf = new VigileDelFuocoBean();
				vf.setNome(nome);
				vf.setCognome(cognome);
				vf.setEmail(email);
				vf.setTurno(turno);
				vf.setUsername(username);
				vf.setMansione(mansione);
				vf.setGiorniFerieAnnoCorrente(giorniFerieAnnoCorrente);
				vf.setGiorniFerieAnnoPrecedente(giorniFerieAnnoPrecedente);
				vf.setCaricoLavoro(caricoLavoro);
				vf.setAdoperabile(adoperabile);
				vf.setGrado(grado);

				vigili.add(vf);

			}
			
			return vigili;
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	
	
	

}
