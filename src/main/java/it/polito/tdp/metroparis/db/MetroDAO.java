package it.polito.tdp.metroparis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.javadocmd.simplelatlng.LatLng;

import it.polito.tdp.metroparis.model.CoppiaFermate;
import it.polito.tdp.metroparis.model.Fermata;
import it.polito.tdp.metroparis.model.Linea;

public class MetroDAO {

	public List<Fermata> getAllFermate() {

		final String sql = "SELECT id_fermata, nome, coordx, coordy FROM fermata ORDER BY nome ASC";
		List<Fermata> fermate = new ArrayList<Fermata>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Fermata f = new Fermata(rs.getInt("id_Fermata"), rs.getString("nome"),
						new LatLng(rs.getDouble("coordx"), rs.getDouble("coordy")));
				fermate.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return fermate;
	}

	public List<Linea> getAllLinee() {
		final String sql = "SELECT id_linea, nome, velocita, intervallo FROM linea ORDER BY nome ASC";

		List<Linea> linee = new ArrayList<Linea>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Linea f = new Linea(rs.getInt("id_linea"), rs.getString("nome"), rs.getDouble("velocita"),
						rs.getDouble("intervallo"));
				linee.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return linee;
	}


	public boolean fermateConnesse(Fermata fp, Fermata fa) {
		String sql="SELECT COUNT(*) AS C FROM connessione WHERE id_stazP=? AND id_stazA=?";
		
		try{Connection conn = DBConnect.getConnection();
		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, fp.getIdFermata());
		st.setInt(2, fa.getIdFermata());
		ResultSet rs = st.executeQuery();
		
		rs.first();
		int linee = rs.getInt("C");
		
		
		conn.close();
		return linee>=1;
		
	
		
		}catch(SQLException e ) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione del database.");
		}
		
	}
	
	public List<Fermata> fermateSuccessive(Fermata fp, Map<Integer,Fermata> fermataIdMap){
		String sql = "SELECT DISTINCT id_stazA " + 
				"FROM connessione " + 
				"WHERE id_stazP=?";
		
		List<Fermata> result = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, fp.getIdFermata());
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
			int id = rs.getInt("id_stazA");
			result.add(fermataIdMap.get(id));
			}
			conn.close();
			return result;
			
		}catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al database.");
		}
	}

	public List<CoppiaFermate> coppieFermate(Map<Integer, Fermata> fermateIdMap) {
		String sql = "SELECT DISTINCT id_stazA,id_stazP " + 
				"FROM connessione";
        List<CoppiaFermate> result = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
			CoppiaFermate c = new CoppiaFermate(fermateIdMap.get(rs.getInt("id_stazA")),fermateIdMap.get(rs.getInt("id_stazP")));
			result.add(c);
			}
			conn.close();
			return result;
			
		}catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al database.");
		}
		
	}
}
