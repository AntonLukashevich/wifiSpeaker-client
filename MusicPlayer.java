package player;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;

public class MusicPlayer {
	static private Socket connection; 
	static private DataOutputStream output;//отправка
	static private DataInputStream input;//принятие
	private static String[] playList;
	private static int indexOfSendSound = 0;
	private static boolean playing = false;
	private JFrame frame;
	private static JList<String> list;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					MusicPlayer window = new MusicPlayer();
					window.frame.pack();
					window.frame.setVisible(true);
										
					connection = new Socket(InetAddress.getByName(null),8080);
					while(true) {
						output = new DataOutputStream(connection.getOutputStream());
						input = new DataInputStream(connection.getInputStream());
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	/**
	 * Create the application.
	 * @wbp.parser.entryPoint
	 */
	public MusicPlayer() {
		myPlayList();
		initialize();
		
	}
	
	private static void myPlayList() {
		File myFolder = new File("D:\\PSU\\OSiSP\\work\\radioSC\\Client\\Client2\\music");
		File[] files = myFolder.listFiles();
		playList = new String[files.length];
		for(int i =0 ; i<files.length; i++) {playList[i] = files[i].getName();}
	}
	
	private static void sendData(String object) {
		try {
			output.flush();
			output.writeUTF(object);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void sendSound(int index) {
		String sendSound = "D:\\PSU\\OSiSP\\work\\radioSC\\Client\\Client2\\music\\";
		sendSound = sendSound.concat(playList[index]);
		try {
			FileInputStream soundFile = new FileInputStream(sendSound);
			byte[] byteArray = new byte[1024];
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        	BufferedInputStream myStream = new BufferedInputStream(soundFile);
    		AudioInputStream ais = AudioSystem.getAudioInputStream(myStream);
    		AudioFormat format = ais.getFormat();
    		for(int readNum; (readNum = soundFile.read(byteArray)) != -1; ) {
    			arrayOutputStream.write(byteArray,0,readNum);
    		}
    		byte[] bytes = arrayOutputStream.toByteArray();
    		output.writeInt(bytes.length);
    		output.write(bytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		
		frame.setResizable(false);
		frame.setBounds(100, 100, 320, 420);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton btnNewButton = new JButton("Play");
		btnNewButton.setFont(new Font("Georgia", Font.PLAIN, 16));
		
		btnNewButton.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent arg) {
				if(arg.getSource() == btnNewButton) {
					sendData((String)"play");
					if(!playing) {
					sendSound(indexOfSendSound);
					playing = true;
					}
				}
				
			}
		});
		
		JButton btnStop = new JButton("Stop");
		btnStop.setFont(new Font("Georgia", Font.PLAIN, 16));
		btnStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg) {
				if(arg.getSource() == btnStop) {
					sendData((String)"stop");
				
				}
				
			}
		});
		
		
		JButton btnPause = new JButton("Pause");
		btnPause.setFont(new Font("Georgia", Font.PLAIN, 16));
		btnPause.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg) {
				if(arg.getSource() == btnPause) {
					sendData((String)"pause");
					System.out.println("pause");
				}
				
			}
		});
		
		JButton button = new JButton("<<");
		button.setFont(new Font("Corbel", Font.PLAIN, 11));
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg) {
				if(arg.getSource() == button) {
					sendData((String)"<<");
					indexOfSendSound--;
					if(indexOfSendSound < 0) indexOfSendSound = playList.length -1 ;
					sendSound(indexOfSendSound);
					list.setSelectedIndex(indexOfSendSound);
				}
				
			}
		});
		
		JButton button_1 = new JButton(">>");
		button_1.setFont(new Font("Corbel", Font.PLAIN, 11));
		button_1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg) {
				if(arg.getSource() == button_1) {
					sendData((String)">>");
					indexOfSendSound++;
					if(indexOfSendSound > playList.length - 1) indexOfSendSound = 0;
					sendSound(indexOfSendSound);
					list.setSelectedIndex(indexOfSendSound);
				}
				
			}
		});
		
		list = new JList<String>(playList);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0);
		
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnPause, GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(button, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
																.addComponent(button_1, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)))
						.addComponent(btnStop, GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
						.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(list, GroupLayout.PREFERRED_SIZE, 172, GroupLayout.PREFERRED_SIZE)
					.addGap(15))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(26)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(list, GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnStop, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnPause, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(button, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
								.addComponent(button_1, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE))))
					.addContainerGap())
		);
		
		frame.getContentPane().setLayout(groupLayout);
	}
	

}
