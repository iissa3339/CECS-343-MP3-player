package mp3;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

public class playlistGUI {
	private GUI gui;
	private Library library;
	private BasicPlayer player;
	private playlist playlis;
	private boolean listpaused = false;
	private int listRowPlaying;
	private JMenu rToPlaylist;
	private JTable playTable = new JTable();
	DefaultTableModel tablenew = (DefaultTableModel) playTable.getModel();
	JSlider volume;
	
	public playlistGUI(GUI Gui, Library lib, BasicPlayer pl, playlist playlis) {
		gui = Gui;
		library = lib;
		player = pl;
		this.playlis = playlis;
		JFrame playFrame = new JFrame(gui.treePlaylist.getSelectionPath().getLastPathComponent().toString());
		playFrame.setSize(900, 500);
		// This is used to have the jframe be centered in the middle of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		playFrame.setLocation(dim.width/2-playFrame.getSize().width/2, dim.height/2-playFrame.getSize().height/2);
		playFrame.setVisible(true);
		playFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		String[] columns = {"Song ID", "Title", "Artist", "Genre", "Release Year", "Comments"};
		tablenew.setColumnIdentifiers(columns);
		for(String[] newSong : library.getPlaylistCalled(gui.treePlaylist.getSelectionPath().getLastPathComponent().toString()).getSongs()) {
			tablenew.addRow(newSong);
		}
		JScrollPane newPane = new JScrollPane(playTable);
		playFrame.add(newPane);
		
		JPanel playKeys = new JPanel();
		JButton listplay, listpause, liststop, listprevious, listnext;
		listplay = new JButton("Play"); listpause = new JButton("Pause"); liststop = new JButton("Stop");
		listprevious = new JButton("Previous"); listnext = new JButton("Next");
		
		listplay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					listpause.setText("Pause");
					listpaused = false;
					int listSelected = playTable.getSelectedRow();
					String listId = playTable.getValueAt(listSelected,0).toString();
					listRowPlaying = listSelected;
					String listLocationPlay = library.getLocation(listId);
					library.playSong(listLocationPlay);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (BasicPlayerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
			}	
		});
		listpause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(listpaused) {
					try {
						listpause.setText("Pause");
						player.resume();
						listpaused = false;
					} catch(BasicPlayerException xxxx){
						xxxx.printStackTrace();
					}
				}
				else {
					try {
						listpause.setText("Un-Pause");
						player.pause();
						listpaused = true;
					} catch(BasicPlayerException xxxx){
						xxxx.printStackTrace();
					}
				}
			}					
		});
		liststop.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent e) {
                try {
                    listpause.setText("Pause");
                    player.stop();
                } catch (BasicPlayerException ex) {
                    ex.printStackTrace();
                }
            }
		});
		listprevious.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String listLocationNeeded = null;
				try {
                    listLocationNeeded = library.getLocation(playTable.getValueAt(listRowPlaying-1,0).toString());
                    listRowPlaying -= 1;
                    playTable.setRowSelectionInterval(playTable.getSelectedRow()-1,playTable.getSelectedRow()-1);
                    library.playSong(listLocationNeeded);
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (BasicPlayerException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
			}					
		});
		listnext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String listLocationNeeded = null;
				try {
                    listLocationNeeded = library.getLocation(playTable.getValueAt(listRowPlaying+1,0).toString());
                    listRowPlaying += 1;
                    playTable.setRowSelectionInterval(playTable.getSelectedRow()+1,playTable.getSelectedRow()+1);
                    library.playSong(listLocationNeeded);
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (BasicPlayerException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
			}
		});
		playKeys.add(listplay);playKeys.add(listpause);playKeys.add(liststop);playKeys.add(listprevious);playKeys.add(listnext);
		
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
		playKeys.add(volume);
		
		
        playFrame.add(playKeys, BorderLayout.SOUTH);
        JMenuBar menuBar = new JMenuBar();
        JMenu file;
        JMenuItem open, add, delete;
        file = new JMenu("File");
        open = new JMenuItem("Open");
        add = new JMenuItem("Add");
        delete= new JMenuItem("Delete");
        file.add(open);
        file.add(add);
        file.add(delete);
        

        menuBar.add(file);

        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                final JFileChooser fc = new JFileChooser();
                File f = new File("C:/Users/iissa/Desktop/CECS 343/Semester_Project/songs to play");
                fc.setCurrentDirectory(f);
                fc.showOpenDialog(playFrame);
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

        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] addedd = library.addSong(playFrame);
                if(playlistGUI.this.songInTable(addedd)==false) {
                	tablenew.addRow(addedd);
                	try {
						library.getPlaylistCalled(gui.treePlaylist.getSelectionPath().getLastPathComponent().toString()).rightAddToPlaylist(addedd);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                }
                if(gui.songInTable(addedd)==false) {
                	gui.tableModel.addRow(addedd);
                }
            }
        });

        delete.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                int curRow = playTable.getSelectedRow();
                String id = playTable.getValueAt(curRow, 0).toString();
                library.deleteSong(id);
                tablenew.removeRow(curRow);
            }

        });
        
        JPopupMenu rightClick = new JPopupMenu("Popup");
        JMenuItem rAdd = new JMenuItem("Add song to Library");
        rAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	int roww = playTable.getSelectedRow();
                String[] deta = {tablenew.getValueAt(roww, 0).toString(), tablenew.getValueAt(roww, 1).toString(),
                		tablenew.getValueAt(roww, 2).toString(), tablenew.getValueAt(roww, 3).toString(),
                		tablenew.getValueAt(roww, 4).toString(), tablenew.getValueAt(roww, 5).toString(),};
                if(gui.songInTable(deta)==false) {
                	gui.tableModel.addRow(deta);
                }
                try {
					String[] toAdd = {deta[0],deta[1],deta[2],deta[3],deta[4],deta[5],library.getLocation(deta[0]),""};
					library.rightAddToLibrary(toAdd);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        rToPlaylist = new JMenu("Add to Playlist");
        for(playlist pls : library.getPlaylists()) {
            JMenuItem itm = new JMenuItem(pls.getName());
            rToPlaylist.add(itm);
            itm.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    int currow = playTable.getSelectedRow();
                    try {
                        pls.rightAddToPlaylist(library.getSong(playTable.getValueAt(currow,0).toString()));
                    } catch (SQLException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            });
        }
        JMenuItem rDeleteCur = new JMenuItem("Delete currently selected song");
        rDeleteCur.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                try {
                	int curRow = playTable.getSelectedRow();
                    String id = playTable.getValueAt(curRow, 0).toString();
					playlis.deleteSong(library.getSong(id));
					library.rightDeleteFromPlay(id, playlis.getName());
					tablenew.removeRow(curRow);
					
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                
			}       	
        });
        rightClick.add(rAdd);
        rightClick.add(rToPlaylist);
        rightClick.addSeparator();
        rightClick.add(rDeleteCur);
        playTable.addMouseListener(new MouseAdapter() {

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
        class MyDropTarget extends DropTarget{
            String toInsert = null;
            public void drop(DropTargetDropEvent evt) {
            	boolean isRow = false;
            	for(DataFlavor flv : evt.getTransferable().getTransferDataFlavors()) {
            		if(flv.equals(DataFlavor.javaFileListFlavor)) {
            			isRow = false;
            			break;
            		}
            		else {
            			isRow = true;
            		}
            	}
            	if(!isRow) {
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

                            songToAdd[0] = "Null";

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
                            int idCheck = library.hasSong(songToAdd[1]);
                            if(idCheck > -1) {
                                songToAdd[0] = Integer.toString(idCheck);
                                
                            }
                            else {
                                songToAdd[0] = "Null";
                            }
                            try {
                            	songToAdd[7] = "";
                                library.insertSong(songToAdd);
                                playlis.rightAddToPlaylist(songToAdd);
                                tablenew.setRowCount(0);
                                for(String[] newSong : library.getPlaylistCalled(gui.treePlaylist.getSelectionPath().getLastPathComponent().toString()).getSongs()) {
                        			tablenew.addRow(newSong);
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
            	else {
            		try {
	            		int[] selectedRows = gui.table.getSelectedRows();
	            		for(int row : selectedRows) {
	            			String[] songg = gui.getDataFromRow(row);
	            			tablenew.addRow(songg);
	                        playlis.rightAddToPlaylist(songg);
	            		}
            		}catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            }
        }
        playFrame.setDropTarget(new MyDropTarget());
        playFrame.add(menuBar, BorderLayout.NORTH);
	}
	public boolean songInTable(String[] addedd) {
		for(int h = 0; h < tablenew.getRowCount(); h++) {
			if(tablenew.getValueAt(h, 1).toString().compareTo(addedd[1])==0) {
				return true;
			}
		}
		return false;
	}
	public void onePlaylistDeleted(String nametoDelete) {
		Component[] rightClickComp = rToPlaylist.getMenuComponents();
		for(Component itemm : rightClickComp) {
			JMenuItem itt = (JMenuItem)itemm;
			if(itt.getText().compareTo(nametoDelete)==0) {
				rToPlaylist.remove(itemm);
				break;
			}
		}
	}
	public void onePlaylistAdded(String name) {
		JMenuItem itm = new JMenuItem(name);
		rToPlaylist.add(itm);
	}
	
}
