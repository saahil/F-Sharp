import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.sql.*;
import java.io.*;

public class PhokUi {
	static JList artists,albums,tracks;
	static TextField dir;
	static Button scan;
	static String msg;
	static Connection conn;	
	static String driver;
	static String url;
	static String db;
	static String user;
	static String pass;
	static JScrollPane artistPanel;
	static JScrollPane albumPanel;
	static JScrollPane trackPanel;
	static ResultSet rs;
	static DefaultListModel artistModel, albumModel, trackModel;
	static JFrame window;
	static JLabel status;
	static Statement populate, popAlbum, popTrack;

	public static void main(String[] args) throws SQLException{
		try {
			driver = new String();
			driver = "com.mysql.jdbc.Driver";
			url = new String();
			url = "jdbc:mysql://localhost/";
			db = new String();
			db = "music";
			user = new String();
			user = "phoku";
			pass = new String();
			pass = "phoku";
			JPanel contentPane = new JPanel();
			contentPane.setLayout(new FlowLayout());
			Label title = new Label("*************************************** PhokU Media Library Lister ***************************************",Label.CENTER);
			contentPane.add(title);
			dir = new TextField(150);
			dir.setText("Browse Directory to list");
			contentPane.add(dir);
			scan = new Button("Scan");
			contentPane.add(scan);
			artistModel = new DefaultListModel();
			albumModel = new DefaultListModel();
			trackModel = new DefaultListModel();
			artists = new JList(artistModel);
			albums = new JList(albumModel);
			tracks = new JList(trackModel);

			/* add items dynamically */
		
			artistPanel = new JScrollPane(artists);
			albumPanel = new JScrollPane(albums);
			trackPanel = new JScrollPane(tracks);
			artists.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			albums.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tracks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			window = new JFrame("PhokU Media Library Manager");
			artistPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			artistPanel.setPreferredSize(new Dimension(250, 250));
			albumPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			albumPanel.setPreferredSize(new Dimension(250, 250));
			trackPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			trackPanel.setPreferredSize(new Dimension(250, 250));
			contentPane.add(artistPanel);
			contentPane.add(albumPanel);
			contentPane.add(trackPanel);
			status = new JLabel("Idle");
			contentPane.add(status);
			window.getContentPane().add(contentPane);
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url+db, user, pass);
			System.out.println("Connection formed");
			populate = conn.createStatement();
			popAlbum = conn.createStatement();
			popTrack = conn.createStatement();
			scan.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					System.out.println("Scanning directory...");
					status.setText("Scanning directory....");
					try {
					FileOps oFileOps = new FileOps(dir.getText());
					rs = populate.executeQuery("select distinct(artist) from main");
					while (rs.next()) {
						artistModel.addElement(rs.getString("artist"));
					}
					status.setText("Scan complete.");
					} catch (SQLException e) {System.out.println("SQLException occured in PhokUi");}
				}
			});
			artists.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent lse) {
					try {
					rs = popAlbum.executeQuery("select distinct(album) from main where artist = '" + artistModel.getElementAt(artists.getSelectedIndex()).toString() + "'");
					trackModel.clear();
					albumModel.clear();
					while (rs.next()) {
						albumModel.addElement(rs.getString("album"));
					}
					} catch (SQLException e) { }
				}
			});
			albums.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent lse) {
					try {
					rs = popTrack.executeQuery("select track from main where album = '" + albumModel.getElementAt(albums.getSelectedIndex()).toString() + "'");
					trackModel.clear();
					while (rs.next()) {
						trackModel.addElement(rs.getString("track"));
					}
					} catch (SQLException e) { }
				}
			});
		}
		catch (SQLException e) {System.out.println("SQLException caught in PhokUi"); }
		catch (ClassNotFoundException e) {System.out.println("Class not found"); }
		catch (Exception e) {System.out.println("Exception Occured in PhokUi"); }
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.show();
	}
}
