package mp3;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class playlist {
	private String name;
	private GUI gui;
	private Library library;
	private int id;
	private ArrayList<String[]> songs;
	private myDB database;
	
	public playlist(String name, GUI gui, myDB data, Library lib, int id) {
		this.name = name;
		this.gui = gui;
		library = lib;
		this.id = id;
		database = data;
		songs = new ArrayList<>();
	}
	public String getName() {
		return name;
	}
	public int getId() {
		return id;
	}
	public void rightAddToPlaylist(String[] song) throws SQLException {
		if(songs.contains(song)==false) {
			songs.add(song);
			database.rightAddToPlaylist(name, song[0]);
		}
	}
	public ArrayList<String[]> getSongs(){
		return songs;
	}
	public void deleteSong(String[] songgg) {
		for(String[] ss : songs) {
			if(ss[0].compareTo(songgg[0])==0) {
				songs.remove(ss);
				break;
			}
		}		
	}
}
