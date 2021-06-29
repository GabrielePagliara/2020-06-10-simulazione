package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.LinkActors;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO {
	
	public void  listAllActors(Map<Integer, Actor> mapActor){
		String sql = "SELECT * FROM actors";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				
				mapActor.put(actor.getId(), actor);
			}
			res.close();
			st.close();
			conn.close();	
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database 1");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Movie> listAllMovies(){
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), 
						res.getInt("year"), res.getDouble("rank"));
				
				result.add(movie);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Director> listAllDirectors(){
		String sql = "SELECT * FROM directors";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<String> listAllGenre() {
		String sql = "SELECT distinct genre\n"
				+ "FROM movies_genres m";
		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				String s = res.getString("Genre");
				result.add(s);
			}
			conn.close();
			return result;
			
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	public List<Actor> listActorForGenre(String g) {

		String sql = "SELECT a.id, a.first_name, a.last_name, a.gender "
				+ "FROM actors a, roles r, movies m, movies_genres mv "
				+ "WHERE a.id = r.actor_id AND r.movie_id=m.id AND m.id = mv.movie_id AND mv.genre = ? "
				+ "GROUP BY a.id";
		List<Actor> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, g);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),res.getString("gender"));
				result.add(actor);			
			}
			conn.close();
			return result;			
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	public List<LinkActors> getAllLinkForGenre(Map<Integer, Actor> mapActor, String g) {

		String sql = "SELECT r1.actor_id AS a1, r2.actor_id AS a2 , COUNT(DISTINCT r1.movie_id) AS peso "
				+ "FROM roles r1, movies_genres mv, "
				+ "     roles r2 "
				+ "WHERE r1.movie_id = mv.movie_id AND mv.genre = ?  "
				+ "  AND r1.movie_id = r2.movie_id "
				+ "  AND r1.actor_id > r2.actor_id "
				+ "GROUP BY r1.actor_id, r2.actor_id ";
		List<LinkActors> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, g);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				LinkActors linkactor = new LinkActors(mapActor.get(res.getInt("a1")) , mapActor.get(res.getInt("a2")), res.getInt("peso"));
				result.add(linkactor);			
			}
			res.close();
			st.close();
			conn.close();
			return result;				
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
		
	
	
	
	}
	
	
	
	
	
	
}
