package mp3;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTable;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

public class Library {
	private myDB database = new myDB(this);
	private GUI gui;
	private BasicPlayer player;
	private ArrayList<playlist> playlists;
	private int index = 1;
	
	
	public Library(GUI gui, BasicPlayer play) throws SQLException {
		this.gui = gui;
		playlists = new ArrayList<>();
		player = play;
		database.connect();
    	database.clearDatabaseTable();
    	for(String g : database.getTablesNames()) {
    		playlists.add(new playlist(g,gui,database,this,index));
    	}
    	
	}
	
	public void makePlaylist(String name) throws SQLException {
		playlists.add(new playlist(name,gui,database,this,index));
		database.makeTable(name);
		index++;
	}
	
	public String[] addSong(JFrame frm) {
		final JFileChooser fc = new JFileChooser();
		File f = new File("C:/Users/iissa/Desktop/CECS 343/Semester_Project/songs to play");
		fc.setCurrentDirectory(f);
		fc.showOpenDialog(frm);
		File selected = fc.getSelectedFile();
		String wrongdirectory = selected.getPath();
		
		String directory = wrongdirectory.replace('\\','/');
		
		
		String[] songToAdd = new String[8];
		songToAdd[6] = directory;
		songToAdd[7] = "Null";
		// Now get the stuff from the mp3 tag
		
		// Store the songID
		
		if(gui.getTable().getRowCount()>=1) {
			songToAdd[0] = "Null";
		}
		else {
			songToAdd[0] = "1";
		}
		
		
		Mp3File mp3file = null;
		try {
			mp3file = new Mp3File(directory);
		} catch (UnsupportedTagException | InvalidDataException | IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("The directory is not right and the mp3file wasn't made");
			e1.printStackTrace();
			
		}
		
		
		if (mp3file != null && mp3file.hasId3v1Tag()){
			ID3v1 id3v1Tag = mp3file.getId3v1Tag();
			
			if(id3v1Tag.getTitle()!=null) {
				songToAdd[1] = id3v1Tag.getTitle();
			}
			else {
				songToAdd[1] = "Unknown";
			}
			
			if(id3v1Tag.getArtist()!=null) {
				songToAdd[2] = id3v1Tag.getArtist();
			}
			else {
				songToAdd[2] = "Unknown";
			}
			
			if(id3v1Tag.getGenreDescription()!=null) {
				songToAdd[3] = id3v1Tag.getGenreDescription();
			}
			else {
				songToAdd[3] = "Unknown";
			}
			
			if(id3v1Tag.getYear()!=null) {
				songToAdd[4] = id3v1Tag.getYear();
			}
			else {
				songToAdd[4] = "0";
			}
			
			if(id3v1Tag.getComment()!=null) {
				songToAdd[5] = id3v1Tag.getComment();
			}
			else {
				songToAdd[5] = "";
			}
			
		}
		else if(mp3file != null && mp3file.hasId3v2Tag()) {
			ID3v1 id3v2Tag = mp3file.getId3v2Tag();
			
			if(id3v2Tag.getTitle()!=null) {
				songToAdd[1] = id3v2Tag.getTitle();
			}
			else {
				songToAdd[1] = "Unknown";
			}
			
			if(id3v2Tag.getArtist()!=null) {
				songToAdd[2] = id3v2Tag.getArtist();
			}
			else {
				songToAdd[2] = "Unknown";
			}
			
			if(id3v2Tag.getGenreDescription()!=null) {
				songToAdd[3] = id3v2Tag.getGenreDescription();
			}
			else {
				songToAdd[3] = "Unknown";
			}
			
			if(id3v2Tag.getYear()!=null) {
				songToAdd[4] = id3v2Tag.getYear();
			}
			else {
				songToAdd[4] = "0";
			}
			
			if(id3v2Tag.getComment()!=null) {
				songToAdd[5] = id3v2Tag.getComment();
			}
			else {
				songToAdd[5] = "";
			}
		}
		
		else {
			songToAdd[1] = "Unknown";
			songToAdd[2] = "Unknown";
			songToAdd[3] = "Unknown";
			songToAdd[4] = "0";
			songToAdd[5] = "Unknown";
		}
		try {
			boolean done = database.addSong(songToAdd);
			if(done) {
				songToAdd[0] = database.getIDAdded();
				return songToAdd;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return songToAdd;
	}
	
	public void playSong(String location) throws BasicPlayerException {
		// TODO Auto-generated method stub
    	if(location != null) {
    		File f = new File(location);
    		player.open(f);
    		player.play();
    	}
	}
	
	public void deleteSong(String id) {
		try {
			database.deleteSong(id);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public JTable getSongs() throws SQLException {
		// TODO Auto-generated method stub
		return database.getSongs();
	}

	public String getLocation(String id) throws SQLException {
		// TODO Auto-generated method stub
		return database.getLocation(id);
	}

	public void insertSong(String[] songToAdd) throws SQLException {
		// TODO Auto-generated method stub
		boolean done = database.addSong(songToAdd);
		if(done) {
			songToAdd[0] = database.getIDAdded();
			gui.tableModel.addRow(songToAdd);
		}
	}
	
	public ArrayList<playlist> getPlaylists(){
		return playlists;
	}
	
	public String[] getSong(String ID) throws SQLException {
		String[] toReturn = database.getSong(ID);
		return toReturn;
	}
	public playlist getPlaylistCalled(String name) {
		for(playlist nb : playlists) {
			if(nb.getName().compareTo(name)==0) {
				return nb;
			}
		}
		return null;
	}

	public void deletePlaylist(String name) throws SQLException {
		// TODO Auto-generated method stub
		database.deletePlaylist(name);
	}
	public void rightAddToLibrary(String[] n) throws SQLException {
		database.addSong(n);
	}
	public void rightDeleteFromPlay(String id, String play) throws SQLException {
		database.deleteSongFromPlaylist(id, play);
	}
	public int hasSong(String title) throws SQLException {
		return database.hasSong(title);
	}
}
