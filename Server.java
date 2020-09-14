package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

import media.Sound;

public class Server implements Runnable {
	static private ServerSocket server;
	static private Socket connection; 
	static private ObjectOutputStream output;//отправка
	static private DataInputStream input;//принятие
	static private String action; 
	static private AudioFormat format;
	static private Sound sound;
	private boolean statusPlaying = false;
	
	public static void main(String[] args) {
		new Thread(new Server()).start();
		
	}
	
	
	@Override
	public void run() {
		byte[] byteArray = new byte[1024];
		int lenght;
				
		try {		
			server = new ServerSocket(8080);
			System.out.println("Started: " + server);
			connection = server.accept();
			while(true) {
				
				if(connection.isOutputShutdown()) {
					connection.getInputStream().close();
					return;
				}
				
				output = new ObjectOutputStream(connection.getOutputStream());
				input = new DataInputStream(connection.getInputStream());
				
				if(!connection.isConnected()) {
					
					break;
				}
				
				try {
				action = input.readUTF();
				
				switch(action) {
				case "play":
					if(!statusPlaying) {
					System.out.println("play");
					lenght =input.readInt();
					if(lenght>0) {
						byteArray = new byte[lenght];
						input.readFully(byteArray, 0, lenght);
					}
					format = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT,96000.0f,32,1,2,44100,false);
					
					sound = new Sound(format, byteArray);
					statusPlaying = true;
					}
					
					sound.play();
					
					break;
				case "stop":
					System.out.println("stop");
					sound.stop();
					break;
				case "pause":
					System.out.println("pause");
					sound.pause();
					break;
				case ">>":
					if(statusPlaying) {
					sound.stop();
					sound.close();
					}
					System.out.println("next");
					lenght =input.readInt();
					if(lenght>0) {
						byteArray = new byte[lenght];
						input.readFully(byteArray, 0, lenght);
					}
					format = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT,96000.0f,32,1,2,44100,false);
					sound = new Sound(format, byteArray);
					sound.play();
					break;
				case "<<":
					if(statusPlaying) {
					sound.stop();
					sound.close();}
					System.out.println("previous");
					lenght =input.readInt();
					if(lenght>0) {
						byteArray = new byte[lenght];
						input.readFully(byteArray, 0, lenght);
					}
					format = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT,96000.0f,32,1,2,44100,false);
					sound = new Sound(format, byteArray);
					sound.play();
					break;
					
				case "close":
					sound.stop();
					sound.close();
					connection.close();
					break;
				}
			}catch(SocketException e) {
				//e.printStackTrace();
				}
			
			}
		}
		 catch (UnknownHostException e) {
			
		} catch (IOException e) {	
			
		}	
		finally {
			try {
				server.close();
			} catch (IOException e) {
				
			}
		}
	}
}
