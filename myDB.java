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
	private Library library;
	Connection connection;
	Statement statement;
	
	private String idAdded = "Null";
	public myDB(Library lib) {
		library = lib;
	}
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
		String[] columns = {"SongId", "Title", "Artist", "Genre", "Release Year", "Comments"};
		// This line selects the table from the database
		ResultSet rs = statement.executeQuery("SELECT * FROM `songs`");
		ArrayList<Object[]> dataList = new ArrayList<>();
		// This means loop until there's no more rows left
		int x = 0;
		while(rs.next()) {
			Object[] theArray = {Integer.toString(rs.getInt("SongId")),rs.getString("Title"),
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
			query = "INSERT INTO `songs`(`SongId`, `Title`, `Artist`, `Genre`, `Release Year`, `Comments`, `Location`, `playlists`) VALUES (";
			query += details[0] + ",\"" + details[1] + "\",\"" + details[2] + "\",\"" + details[3] + "\"," + details[4] + ",\"" + 
			details[5]+ "\",\"" + details[6] +"\",\"" + details[7] +"\")";
			statement.execute(query);
			String secondQueryToGetID = "SELECT `SongId` FROM `songs` WHERE `Location`=\""+details[6]+"\"";
			ResultSet ids = statement.executeQuery(secondQueryToGetID);
			while(ids.next()) {
				idAdded = Integer.toString(ids.getInt("SongId"));
				break;
			}
			added = true;
		}
		return added;
		
	}
	
	public void deleteSong(String ID) throws SQLException {
		for(playlist table : library.getPlaylists()) {
			String query = "DELETE FROM `"+table.getName()+"` WHERE `SongId`="+ID;
			statement.execute(query);
		}
	}
	
	public String getLocation(String ID) throws SQLException {
		String query = "SELECT `SongId`, `Title`, `Artist`, `Genre`, `Release Year`, `Comments`, `Location`, `playlists` FROM `songs` WHERE `SongId`="+ID;
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
		for(String name : this.getTablesNames()) {
			query = "TRUNCATE `"+name+"`";
			statement.execute(query);
		}
	}
	public String getIDAdded() {
		return idAdded;
	}
	// This will return the playlists associated with the song
	public String getPlaylists(String ID) throws SQLException {
		String query = "SELECT `SongId`, `Title`, `Artist`, `Genre`, `Release Year`, `Comments`, `Location`, `playlists` FROM `songs` WHERE `SongId`="+ID;
		ResultSet song = statement.executeQuery(query);
		String Playlists = null;
		while(song.next()) {
			Playlists = song.getString("playlists");
		}
		return Playlists;
	}
	public String[] getSong(String ID) throws SQLException {
		String query = "SELECT `SongId`, `Title`, `Artist`, `Genre`, `Release Year`, `Comments`, `Location`, `playlists` FROM `songs` WHERE `SongId`="+ID;
		ResultSet song = statement.executeQuery(query);
		String[] songg = new String[6];
		while(song.next()) {
			songg[0] = song.getString("SongId");
			songg[1] = song.getString("Title");
			songg[2] = song.getString("Artist");
			songg[3] = song.getString("Genre");
			songg[4] = song.getString("Release Year");
			songg[5] = song.getString("Comments");
		}
		return songg;
	}
	public void makeTable(String tableName) throws SQLException {
		String query = "CREATE TABLE `" + tableName + "` (SongId int);";
		statement.execute(query);
	}
	public ArrayList<String> getTablesNames() throws SQLException{
		ArrayList<String> nms = new ArrayList<>();
		String query = "SELECT TABLE_NAME \r\n" + 
				"FROM INFORMATION_SCHEMA.TABLES\r\n" + 
				"WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_SCHEMA='mp3'";
		ResultSet names = statement.executeQuery(query);
		while(names.next()) {
			if(names.getString("TABLE_NAME").compareTo("songs")!=0) {
				nms.add(names.getString("TABLE_NAME"));
			}
		}
		return nms;
	}
	public void rightAddToPlaylist(String playlistName, String songID) throws SQLException {
		String query = "INSERT INTO `"+ playlistName + "`(`SongId`) VALUES ("+songID+")";
		statement.execute(query);
	}
	public void deletePlaylist(String name) throws SQLException {
		// TODO Auto-generated method stub
		String query = "DROP TABLE `"+name+"`;";
		statement.execute(query);
	}
	public void deleteSongFromPlaylist(String ID, String playlist) throws SQLException {
		String query = "DELETE FROM `"+playlist+"` WHERE `SongId`="+ID;
		statement.execute(query);
	}
	public int hasSong(String title) throws SQLException {
		int idAdded = -1;
		String queryToGetID = "SELECT `SongId` FROM `songs` WHERE `Title`=\""+title+"\"";
		ResultSet ids = statement.executeQuery(queryToGetID);
		while(ids.next()) {
			idAdded = ids.getInt("SongId");
			break;
		}
		return idAdded;
	}
}
