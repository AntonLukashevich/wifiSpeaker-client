package media;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound  {
	private boolean released = false;
	private AudioInputStream stream = null;
	private Clip clip = null;
	private FloatControl volumeControl = null;
	private boolean playing = false;
	private long position = 0;
	
	public Sound(String filePath) {
		File soundFile = new File(filePath);
		try {
			stream = AudioSystem.getAudioInputStream(soundFile);
			clip = AudioSystem.getClip();
			clip.open(stream);
			clip.addLineListener(new Listener());
			volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			released = true;
		} catch (IOException | UnsupportedAudioFileException | LineUnavailableException exc) {
			exc.printStackTrace();
			released = false;
			close();
		}

	}
	
	public Sound(AudioFormat format, byte[] byteArray) {
		try {
			clip = AudioSystem.getClip();
			clip.open(format,byteArray,0,byteArray.length);
			released = true;
		} catch (LineUnavailableException e) {
			released = false;
			e.printStackTrace();
			close();
		}
		
	}
	
	public long framePosition() {
		return clip.getMicrosecondPosition();
	}
	
	public void play() {
		clip.setMicrosecondPosition(position);
		System.out.println(position);
		clip.start();
	}
	
	public void play(boolean breakOld) {
		if (released) {
			if (breakOld) {
				clip.stop();
				clip.setFramePosition(0);
				clip.start();
				playing = true;
			} else if (!isPlaying()) {
				clip.setFramePosition(0);
				clip.start();
				playing = true;
			}
		}
	}
	
	
	public void pause() {
		position = framePosition();
		clip.stop();

	}
	
	public void stop() {
		position = 0;
			clip.stop();
	}
	
	public boolean isPlaying() {
		return playing;
	}
	
	public void close() {
		if (clip != null)
			clip.close();
		
		if (stream != null)
			try {
				stream.close();
			} catch (IOException exc) {
				exc.printStackTrace();
			}
	}

	public void setVolume(float x) {
		if (x<0) x = 0;
		if (x>1) x = 1;
		float min = volumeControl.getMinimum();
		float max = volumeControl.getMaximum();
		volumeControl.setValue((max-min)*x+min);
	}
	
	public float getVolume() {
		float v = volumeControl.getValue();
		float min = volumeControl.getMinimum();
		float max = volumeControl.getMaximum();
		return (v-min)/(max-min);
	}
	private class Listener implements LineListener {
		public void update(LineEvent ev) {
			if (ev.getType() == LineEvent.Type.STOP) {
				playing = false;
				synchronized(clip) {
					clip.notify();
				}
			}
		}
	}
}
