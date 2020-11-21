package mp3;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JFileChooser;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class playlist {
	private String name;
	private GUI gui;
	private Library library;
	
	public playlist(String name, GUI gui, Library lib) {
		this.name = name;
		this.gui = gui;
		library = lib;
	}
	public String getName() {
		return name;
	}
	public void addToPlaylist() {
		
	}

}
