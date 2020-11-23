package mp3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTable;

public class myDB {
	private String user = "root";
	private String password = "";
	private String url = "jdbc:mysql://localhost:3306/mp3";
	
	Connection connection;
	Statement statement;
	
	private String idAdded = "Null";
	public void connect() {
		// TODO Auto-generated method stub
		System.out.println("Connecting to mp3 ...");
		try {
			connection = DriverManager.getConnection(url, user, password);
			statement = connection.createStatement();
			System.out.println("Connection to mp3 was successfully established");
		} catch(SQLException ex){
			Logger.getLogger(myDB.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public JTable getSongs() throws SQLException {
		// TODO Auto-generated method stub
		String[] columns = {"Song ID", "Title", "Artist", "Genre", "Release Year", "Comments"};
		// This line selects the table from the database
		ResultSet rs = statement.executeQuery("SELECT * FROM `songs`");
		ArrayList<Object[]> dataList = new ArrayList<>();
		// This means loop until there's no more rows left
		int x = 0;
		while(rs.next()) {
			Object[] theArray = {Integer.toString(rs.getInt("Song Id")),rs.getString("Title"),
					rs.getString("Artist"), rs.getString("Genre"), rs.getString("Release Year"),
					rs.getString("Comments")};
			
			dataList.add(x,theArray);
			x++;
		}
		Object[][] data = new Object[dataList.size()][5];
		for(int b = 0; b<dataList.size(); b++) {
			data[b] = dataList.get(b);
		}
		return new JTable(data,columns);
		
	}
	public boolean addSong(String[] details) throws SQLException {
		boolean added = false;
		String query = null;
		boolean found = false;
		ResultSet rs = statement.executeQuery("SELECT * FROM `songs`");
		while(rs.next()) {
			if(rs.getString("Location").compareTo(details[6])==0) {
				found = true;
			}
		}
		if(! found) {
			query = "INSERT INTO `songs`(`Song Id`, `Title`, `Artist`, `Genre`, `Release Year`, `Comments`, `Location`) VALUES (";
			query += details[0] + ",\"" + details[1] + "\",\"" + details[2] + "\",\"" + details[3] + "\"," + details[4] + ",\"" + 
			details[5]+ "\",\"" + details[6] +"\",\"" + details[7] +"\")";
			statement.execute(query);
			String secondQueryToGetID = "SELECT `Song Id` FROM `songs` WHERE `Location`=\""+details[6]+"\"";
			ResultSet ids = statement.executeQuery(secondQueryToGetID);
			while(ids.next()) {
				idAdded = Integer.toString(ids.getInt("Song Id"));
				break;
			}
			added = true;
		}
		return added;
		
	}
	public void deleteSong(String ID) throws SQLException {
		String query = "DELETE FROM `songs` WHERE `Song Id`="+ID;
		statement.execute(query);
	}
	
	public String getLocation(String ID) throws SQLException {
		String query = "SELECT `Song Id`, `Title`, `Artist`, `Genre`, `Release Year`, `Comments`, `Location`, `playlists` FROM `songs` WHERE `Song Id`="+ID;
		ResultSet song = statement.executeQuery(query);
		String location = null;
		while(song.next()) {
			location = song.getString("Location");
		}
		return location;
	}
	public void clearDatabaseTable() throws SQLException {
		String query = "TRUNCATE songs";
		statement.execute(query);
	}
	public String getIDAdded() {
		return idAdded;
	}
	// This will return the playlists associated with the song
	public String getPlaylists(String ID) throws SQLException {
		String query = "SELECT `Song Id`, `Title`, `Artist`, `Genre`, `Release Year`, `Comments`, `Location`, `playlists` FROM `songs` WHERE `Song Id`="+ID;
		ResultSet song = statement.executeQuery(query);
		String Playlists = null;
		while(song.next()) {
			Playlists = song.getString("playlists");
		}
		return Playlists;
	}
	
}
