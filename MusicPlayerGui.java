package mp3;
import javazoom.jlgui.basicplayer.BasicPlayerException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.sql.SQLException;
import java.util.ArrayList;

import javazoom.jlgui.basicplayer.BasicPlayer;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;



public class MusicPlayerGui extends JFrame {

    BasicPlayer player = new BasicPlayer();
    myDB database = new myDB();
    
    JFrame main = new JFrame("Music Player by Jeffrey Viramontes & Issa Issa");
    JTable table = new JTable();
    
    DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
    
    JTable alreadyIn;
    
    int rowPlaying;
    
    boolean paused = false;
    
    JButton play;
    JButton pause;
    JButton stop;
    JButton previous;
    JButton next;
    JScrollPane scrollPane;
    JMenuBar menuBar;
    JMenu file;
    JMenuItem open, exit, add, delete;
    JPopupMenu rightClick;
    
    
    public MusicPlayerGui() throws SQLException {
    	
    	database.connect();

    	database.clearDatabaseTable();
    	
        //Button for bottom
        play = new JButton("Play");
        pause = new JButton("Pause");
        stop = new JButton("Stop");
        previous = new JButton("Previous");
        next = new JButton("Next");
        alreadyIn = database.getSongs();
        
        
        String[] columns = {"Song ID", "Title", "Artist", "Genre", "Release Year", "Comments"};
        tableModel.setColumnIdentifiers(columns);
        
        for(int n = 0; n < alreadyIn.getRowCount(); n++) {
        	String[] data = {alreadyIn.getValueAt(n, 0).toString() , alreadyIn.getValueAt(n, 1).toString() , alreadyIn.getValueAt(n, 2).toString()
        	 , alreadyIn.getValueAt(n, 3).toString() , alreadyIn.getValueAt(n, 4).toString() , alreadyIn.getValueAt(n, 5).toString()};
        	tableModel.addRow(data);
        }
        
       

        //Menu
        menuBar = new JMenuBar();
        file = new JMenu("File");
        open = new JMenuItem("Open");
        add = new JMenuItem("Add");
        delete= new JMenuItem("Delete");
        exit = new JMenuItem("Exit");
        

        file.add(open);
        file.add(add);
        file.add(delete);
        file.add(exit);

        menuBar.add(file);

        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// TODO Auto-generated method stub
        		final JFileChooser fc = new JFileChooser();
        		File f = new File("C:/Users/iissa/Desktop/CECS 343/Semester_Project/songs to play");
        		fc.setCurrentDirectory(f);
        		fc.showOpenDialog(main);
        		File selected = fc.getSelectedFile();
        		String wrongdirectory = selected.getPath();
        		
        		String directory = wrongdirectory.replace('\\','/');
        		try {
					playSong(directory);
				} catch (BasicPlayerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });


        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                forAddButtons();
            }
        });
        
        delete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				forDeleteButtons();
			}
        	
        });


        //Right Click Menu
        rightClick = new JPopupMenu("Popup");
        JMenuItem rAdd = new JMenuItem("Add");
        rAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				forAddButtons();
			}
        });
        
        
        JMenuItem rDelete = new JMenuItem("Delete");
        rDelete.addActionListener(new ActionListener() {
        	@Override
			public void actionPerformed(ActionEvent e) {
				forDeleteButtons();
			}
        	
        });
        
        
        rightClick.add(rAdd);
        rightClick.add(rDelete);


        table.addMouseListener(new MouseAdapter() {
        	
            @Override
            public void mousePressed(MouseEvent e) {
            	showPopup(e);
            }
            public void mouseReleased(MouseEvent me)
            {
            	showPopup(me);
            }
            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    rightClick.show(e.getComponent(),
                            e.getX(), e.getY());
                }
            }
            
        });

        JPanel keys = new JPanel();
        
        keys.add(play);
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                	pause.setText("Pause");
                	paused = false;
                	int Selected = table.getSelectedRow();
                	String id = table.getValueAt(Selected, 0).toString();
                	rowPlaying = Selected;
                	String locationToPlay = database.getLocation(id);
                    playSong(locationToPlay);
                } catch (BasicPlayerException ex) {
                    ex.printStackTrace();
                } catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        
        keys.add(pause);
        pause.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(paused) {
					try {
						pause.setText("Pause");
						player.resume();
						paused = false;
					} catch (BasicPlayerException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else {
					try {
						pause.setText("Un-Pause");
						player.pause();
						paused = true;
					} catch (BasicPlayerException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				
			}
        	
        });
        
        keys.add(stop);
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    player.stop();
                } catch (BasicPlayerException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        keys.add(previous);
        previous.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String locationNeeded = null;
				try {
					locationNeeded = database.getLocation(table.getValueAt(rowPlaying-1,0).toString());
					rowPlaying -= 1;
					table.setRowSelectionInterval(table.getSelectedRow()-1,table.getSelectedRow()-1);
					playSong(locationNeeded);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (BasicPlayerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
        });
        
        keys.add(next);
        next.addActionListener(new ActionListener() {
     			@Override
     			public void actionPerformed(ActionEvent e) {
     				// TODO Auto-generated method stub
     				String locationNeeded = null;
     				try {
     					locationNeeded = database.getLocation(table.getValueAt(rowPlaying+1,0).toString());
    					rowPlaying += 1;
     					table.setRowSelectionInterval(table.getSelectedRow()+1,table.getSelectedRow()+1);
     					playSong(locationNeeded);
     				} catch (SQLException e1) {
     					// TODO Auto-generated catch block
     					e1.printStackTrace();
     				} catch (BasicPlayerException e1) {
     					// TODO Auto-generated catch block
     					e1.printStackTrace();
     				}
     			}
             });
        
        
        scrollPane = new JScrollPane(table);
        
        scrollPane.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
            	showPopup(e);
            }
            public void mouseReleased(MouseEvent me)
            {
            	showPopup(me);
            }
            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    rightClick.show(e.getComponent(),
                            e.getX(), e.getY());
                }
            }
        });
		
        // Drag and Drop
        class MyDropTarget extends DropTarget{
        	String toInsert = null;
        	public void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                   
                    List result = new ArrayList();
                    result = (List) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    
               
                    for(Object o : result) {
                        toInsert = o.toString();

	            		String directory = toInsert.replace('\\','/');
	            		
	            		
	            		String[] songToAdd = new String[7];
	            		songToAdd[6] = directory;
	            		// Now get the stuff from the mp3 tag
	            		
	            		// Store the songID
	            		
	            		if(table.getRowCount()>=1) {
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
	            				tableModel.addRow(songToAdd);
	            			}
	            		} catch (SQLException e1) {
	            			// TODO Auto-generated catch block
	            			e1.printStackTrace();
	            		}
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        	
        }
        main.setDropTarget(new MyDropTarget());
        
        
        main.setSize(700, 500);
        main.add(scrollPane);
        main.add(keys, BorderLayout.SOUTH);
        main.add(menuBar, BorderLayout.NORTH);
    }

    public void go() {
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setVisible(true);
    }
    
    private void playSong(String location) throws BasicPlayerException {
		// TODO Auto-generated method stub
    	if(location != null) {
    		File f = new File(location);
    		player.open(f);
    		player.play();
    	}
	}
    
	private void forAddButtons() {
		// TODO Auto-generated method stub
		final JFileChooser fc = new JFileChooser();
		File f = new File("C:/Users/iissa/Desktop/CECS 343/Semester_Project/songs to play");
		fc.setCurrentDirectory(f);
		fc.showOpenDialog(main);
		File selected = fc.getSelectedFile();
		String wrongdirectory = selected.getPath();
		
		String directory = wrongdirectory.replace('\\','/');
		
		
		String[] songToAdd = new String[7];
		songToAdd[6] = directory;
		// Now get the stuff from the mp3 tag
		
		// Store the songID
		
		if(table.getRowCount()>=1) {
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
				tableModel.addRow(songToAdd);
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	private void forDeleteButtons() {
		// TODO Auto-generated catch block
		try {
			int curRow = table.getSelectedRow();
			String id = table.getValueAt(curRow, 0).toString();
			database.deleteSong(id);
			tableModel.removeRow(curRow);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
}

