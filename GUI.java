package mp3;

import javazoom.jlgui.basicplayer.BasicPlayerException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
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

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.util.Random;


public class GUI extends JFrame {
    private BasicPlayer player = new BasicPlayer();
    Library library = new Library(this, player);
    ArrayList<playlistGUI> playGUIs;

    JFrame main = new JFrame("MyTunes Music Player by Jeffrey Viramontes & Issa Issa");
    JTable table = new JTable();
    

    public Component getFrame() {
        return main;
    }
    public JTable getTable() {
        return table;
    }

    DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
    DefaultTreeModel treeModel;

    
    
    

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
    JMenuItem open, exit, add, create, delete;
    JPopupMenu rightClick;
    JTree treeLibrary;
    JTree treePlaylist;
    JPanel sidePanel;
    JSlider volume;
    JScrollPane sideScrollPane;
    


    public GUI() throws SQLException {
    	playGUIs = new ArrayList<>();
    	//playlistPane.hide();
        //Button for bottom
        play = new JButton("Play");
        pause = new JButton("Pause");
        stop = new JButton("Stop");
        previous = new JButton("Previous");
        next = new JButton("Next");
        JTable alreadyIn = library.getSongs();

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
        create = new JMenuItem("Create Playlist");
        exit = new JMenuItem("Exit");
        JMenu playlists = new JMenu("Add to playlist");

        // This is for the side panel
        DefaultMutableTreeNode rootPlaylist = new DefaultMutableTreeNode("Playlist");
        
        table.setDragEnabled(true);
        
        

        file.add(open);
        file.add(add);
        file.add(delete);
        file.add(create);
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
                    library.playSong(directory);
                } catch (BasicPlayerException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        create.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	treeLibrary.clearSelection();
                // TODO Auto-generated method stub
                String name = JOptionPane.showInputDialog(main,"Playlist name:");
                final JPanel panel = new JPanel();
                if(name==null || name.isBlank()) {
                	JOptionPane.showMessageDialog(panel, "Invalid Playlist Name! Playlist name cannot be empty, please try again", "Error", JOptionPane.ERROR_MESSAGE);
                }
                for(playlist ply : library.getPlaylists()) {
                    if(ply.getName().compareTo(name)==0) {
                        JOptionPane.showMessageDialog(panel, "Playlist already exists, please use a different name", "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                    }
                }
                if(!name.isBlank() && name!=null) {
                	try {
                        JMenuItem itm = new JMenuItem(name);
                        playlists.add(itm);

                        itm.addActionListener(new ActionListener(){

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                int currow = table.getSelectedRow();
                                try {
                                    library.getPlaylistCalled(name).rightAddToPlaylist(library.getSong(table.getValueAt(currow,0).toString()));
                                } catch (SQLException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            }

                        });
                        library.makePlaylist(name);
                        DefaultMutableTreeNode toCreated = new DefaultMutableTreeNode(name);
                        rootPlaylist.add(toCreated);
                        treeModel.reload();
                        treePlaylist.addSelectionInterval(rootPlaylist.getIndex(toCreated)+1, rootPlaylist.getIndex(toCreated)+1);
                        tableModel.setRowCount(0);
                    } catch (SQLException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
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
                String[] addedd = library.addSong(main);
                tableModel.addRow(addedd);
            }
        });

        delete.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                int curRow = table.getSelectedRow();
                String id = table.getValueAt(curRow, 0).toString();
                library.deleteSong(id);
                tableModel.removeRow(curRow);
            }

        });


        //Right Click Menu
        rightClick = new JPopupMenu("Popup");
        JMenuItem rAdd = new JMenuItem("Add");
        rAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String[] detttail = library.addSong(main);
                tableModel.addRow(detttail);
            }
        });


        JMenuItem rDelete = new JMenuItem("Delete");
        rDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {  
				try {
					int curRow = table.getSelectedRow();
	                String id = table.getValueAt(curRow, 0).toString();
					String[] theSong = library.getSong(id);
					library.deleteSong(id);
	                tableModel.removeRow(curRow);
	                library.getPlaylistCalled(treePlaylist.getSelectionPath().getLastPathComponent().toString()).deleteSong(theSong);;
	                
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                
            }

        });


        playlists.setMnemonic(KeyEvent.VK_S);
        for(playlist pls : library.getPlaylists()) {
            JMenuItem itm = new JMenuItem(pls.getName());
            playlists.add(itm);

            itm.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    int currow = table.getSelectedRow();
                    try {
                        pls.rightAddToPlaylist(library.getSong(table.getValueAt(currow,0).toString()));
                    } catch (SQLException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }

            });

        }

        rightClick.add(rAdd);
        rightClick.add(rDelete);
        rightClick.addSeparator();
        rightClick.add(playlists);



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
                    String locationToPlay = library.getLocation(id);
                    library.playSong(locationToPlay);
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
                    pause.setText("Pause");
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
                    locationNeeded = library.getLocation(table.getValueAt(rowPlaying-1,0).toString());
                    rowPlaying -= 1;
                    table.setRowSelectionInterval(table.getSelectedRow()-1,table.getSelectedRow()-1);
                    library.playSong(locationNeeded);
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
                    locationNeeded = library.getLocation(table.getValueAt(rowPlaying+1,0).toString());
                    rowPlaying += 1;
                    table.setRowSelectionInterval(table.getSelectedRow()+1,table.getSelectedRow()+1);
                    library.playSong(locationNeeded);
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
                    List result = new ArrayList<>();
                    result = (List) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for(Object o : result) {
                        toInsert = o.toString();
                        String directory = toInsert.replace('\\','/');
                        String[] songToAdd = new String[8];
                        songToAdd[6] = directory;
                        // Now get the stuff from the mp3 tag

                        
						
                        
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
                        // Store the songID
                        Random ran = new Random();
                        int tempId = Math.abs(ran.nextInt());
                        int idCheck = library.hasSong(songToAdd[1]);
                        if(idCheck > -1) {
                            songToAdd[0] = Integer.toString(idCheck);
                        }
                        else {
                            songToAdd[0] = Integer.toString(tempId);
                        }
                        try {
                        	songToAdd[7] = "";
                            library.insertSong(songToAdd);
                            if(treeLibrary.isSelectionEmpty() && treePlaylist.getSelectionPath().getLastPathComponent().toString().compareTo("Playlist")!=0) {
                            	library.getPlaylistCalled(treePlaylist.getSelectionPath().getLastPathComponent().toString()).rightAddToPlaylist(songToAdd);
                            	tableModel.setRowCount(0);
                                for(String[] det : library.getPlaylistCalled(treePlaylist.getSelectionPath().getLastPathComponent().toString()).getSongs()) {
                                	tableModel.addRow(det);
                                }
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

        //Side Panel Trees
        DefaultMutableTreeNode rootLibrary = new DefaultMutableTreeNode("Library");
        
        for(playlist lst : library.getPlaylists()) {
        	rootPlaylist.add(new DefaultMutableTreeNode(lst.getName()));
        }

        treeLibrary = new JTree(rootLibrary);
        treeLibrary.addSelectionInterval(0, 0);
        treeLibrary.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
        		treePlaylist.clearSelection();
        		TreePath tp = treePlaylist.getPathForLocation(e.getX(), e.getY());
                if (tp != null) {
                    tableModel.setRowCount(0);
                    try {
                    	JTable getsongs = library.getSongs();
						for(int n = 0; n < getsongs.getRowCount(); n++) {
						    String[] data = {getsongs.getValueAt(n, 0).toString() , getsongs.getValueAt(n, 1).toString() , getsongs.getValueAt(n, 2).toString()
						            , getsongs.getValueAt(n, 3).toString() , getsongs.getValueAt(n, 4).toString() , getsongs.getValueAt(n, 5).toString()};
						    tableModel.addRow(data);
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                                        
                }	
        	}
        });
        
        
        treePlaylist = new JTree(rootPlaylist);
        treePlaylist.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	treeLibrary.clearSelection();
            	if(e.getClickCount()==2) {
                    TreePath tp = treePlaylist.getPathForLocation(e.getX(), e.getY());
                    if (tp != null) {
                    	if(treePlaylist.getSelectionPath().getLastPathComponent().toString().compareTo("Playlist")!=0) {
                    		tableModel.setRowCount(0);
                            for(String[] det : library.getPlaylistCalled(treePlaylist.getSelectionPath().getLastPathComponent().toString()).getSongs()) {
                            	tableModel.addRow(det);
                            }
                    	} 
                    	else {
                    		if(treePlaylist.getVisibleRowCount()>1) {
                    			int row = treePlaylist.getRowCount() - 1;
                    	        while (row > 0) {
                    	          treePlaylist.collapseRow(row);
                    	          row--;
                    	        }
                    		}
                    		else {
                    			for(int i=1;i<treePlaylist.getRowCount();++i){
                    		        treePlaylist.expandRow(i);
                    		    }
                    		}
                    	}
                    }	
            	}                
            }
        });
        
        
        sidePanel = new JPanel();
        sidePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        sidePanel.setMinimumSize(new Dimension(120,250));
        sidePanel.setMaximumSize(new Dimension(120,5000));
        sidePanel.setPreferredSize(new Dimension(120,250));
        sidePanel.add(treeLibrary);
        sidePanel.add(treePlaylist);
        sideScrollPane = new JScrollPane(sidePanel);

        //Volume Slider
        volume = new JSlider(0,100);
        volume.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                try {
                    player.setGain(((JSlider) ce.getSource()).getValue()/100.0);
                } catch (BasicPlayerException e) {
                    e.printStackTrace();
                }
            }
        });
        treeModel = (DefaultTreeModel) treePlaylist.getModel();
        
        
        JPopupMenu treeRightClick = new JPopupMenu("playlistOption");
        JMenuItem newWindow = new JMenuItem("Open In New Window");
        newWindow.addActionListener(new ActionListener() {
	        
			@Override
			public void actionPerformed(ActionEvent e) {
				playGUIs.add(new playlistGUI(GUI.this, library, player,library.getPlaylistCalled(treePlaylist.getSelectionPath().getLastPathComponent().toString())));
				tableModel.setRowCount(0);
                try {
                	JTable getsongs = library.getSongs();
					for(int n = 0; n < getsongs.getRowCount(); n++) {
					    String[] data = {getsongs.getValueAt(n, 0).toString() , getsongs.getValueAt(n, 1).toString() , getsongs.getValueAt(n, 2).toString()
					            , getsongs.getValueAt(n, 3).toString() , getsongs.getValueAt(n, 4).toString() , getsongs.getValueAt(n, 5).toString()};
					    tableModel.addRow(data);
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
        	
        });
        JMenuItem deletePlaylist = new JMenuItem("Delete Playlist");
        deletePlaylist.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String nametoDelete = treePlaylist.getSelectionPath().getLastPathComponent().toString();
					int input = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete "+nametoDelete+"?");
					// 0 = yes, 1=no, 2=cancel
					if(input == 0) {
						library.deletePlaylist(nametoDelete);
						rootPlaylist.remove((DefaultMutableTreeNode)treePlaylist.getSelectionPath().getLastPathComponent());
						treeModel.reload();
						Component[] rightClickComp = playlists.getMenuComponents();
						for(Component itemm : rightClickComp) {
							JMenuItem itt = (JMenuItem)itemm;
							if(itt.getText().compareTo(nametoDelete)==0) {
								playlists.remove(itemm);
								break;
							}
						}
					}
					getgui(nametoDelete).close();
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
        	
        });
        treeRightClick.add(newWindow);
        treeRightClick.add(deletePlaylist);
        treePlaylist.addMouseListener(new MouseAdapter() {
        	public void mousePressed(MouseEvent f) {
        		showMenu(f);
        	}
        	public void mouseReleased(MouseEvent f) {
        		showMenu(f);
        	}
        	public void showMenu(MouseEvent f) {
        		if(f.isPopupTrigger()) {
        			treeRightClick.show(f.getComponent(), f.getX(), f.getY());
        		}
        	}
        });
        
       
        
        main.setDropTarget(new MyDropTarget());
        main.setSize(900, 500);
        main.add(scrollPane);
        keys.add(volume);
        main.add(keys, BorderLayout.SOUTH);
        main.add(menuBar, BorderLayout.NORTH);
        main.add(sideScrollPane,BorderLayout.WEST);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		main.setLocation((dim.width/2-main.getSize().width/2)-40, (dim.height/2-main.getSize().height/2)-50);
    }
   

    public void go() {
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setVisible(true);
    }
	public boolean songInTable(String[] addedd) {
		for(int h = 0; h < tableModel.getRowCount(); h++) {
			if(tableModel.getValueAt(h, 1).toString().compareTo(addedd[1])==0) {
				return true;
			}
		}
		return false;
	}
	public String[] getDataFromRow(int ind) {
		String[] songDetail = new String[6];
		songDetail[0] = table.getValueAt(ind, 0).toString();
		songDetail[1] = table.getValueAt(ind, 1).toString();
		songDetail[2] = table.getValueAt(ind, 2).toString();
		songDetail[3] = table.getValueAt(ind, 3).toString();
		songDetail[4] = table.getValueAt(ind, 4).toString();
		songDetail[5] = table.getValueAt(ind, 5).toString();
		return songDetail;
	}
	public playlistGUI getgui(String name) {
		for(playlistGUI g : playGUIs) {
			if(g.getName().compareTo(name)==0) {
				return g;
			}
		}
		return null;
	}
}
