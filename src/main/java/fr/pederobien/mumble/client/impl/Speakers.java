package fr.pederobien.mumble.client.impl;

import java.util.concurrent.Semaphore;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Speakers extends Thread {
	private static final AudioFormat FORMAT = new AudioFormat(44100.0f, 16, 2, true, false);
	private Mixer mixer;
	private SourceDataLine speakers;
	private Semaphore semaphore;

	public Speakers(Mixer mixer) {
		super("SpeakerThread");
		this.mixer = mixer;

		semaphore = new Semaphore(1, true);

		setDaemon(true);
	}

	@Override
	public synchronized void start() {
		try {
			speakers = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, FORMAT));
			speakers.open(FORMAT);
			semaphore.acquire();
			super.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		speakers.start();
		// 1-sec buffer
		int bufSize = (int) FORMAT.getFrameRate() * FORMAT.getFrameSize();
		byte[] audioBuffer = new byte[bufSize];
		// only buffer some maximum number of frames each update (25ms)
		int maxFramesPerUpdate = (int) ((FORMAT.getFrameRate() / 1000) * 25);
		int numBytesRead = 0;
		double framesAccrued = 0;
		long lastUpdate = System.nanoTime();
		// keep running until told to stop
		while (!isInterrupted()) {
			try {
				semaphore.acquire();
				// check the time
				long currTime = System.nanoTime();
				// accrue frames
				double delta = currTime - lastUpdate;
				double secDelta = (delta / 1000000000L);
				framesAccrued += secDelta * FORMAT.getFrameRate();
				// read frames if needed
				int framesToRead = (int) framesAccrued;
				int framesToSkip = 0;
				// check if we need to skip frames to catch up
				if (framesToRead > maxFramesPerUpdate) {
					framesToSkip = framesToRead - maxFramesPerUpdate;
					framesToRead = maxFramesPerUpdate;
				}
				// skip frames
				if (framesToSkip > 0) {
					int bytesToSkip = framesToSkip * FORMAT.getFrameSize();
					mixer.skip(bytesToSkip);
				}
				// read frames
				if (framesToRead > 0) {
					// read from the mixer
					int bytesToRead = framesToRead * FORMAT.getFrameSize();
					int tmpBytesRead = mixer.read(audioBuffer, numBytesRead, bytesToRead);
					numBytesRead += tmpBytesRead; // mark how many read
					// fill rest with zeroes
					int remaining = bytesToRead - tmpBytesRead;
					for (int i = 0; i < remaining; i++) {
						audioBuffer[numBytesRead + i] = 0;
					}
					numBytesRead += remaining; // mark zeroes read
				}
				// mark frames read and skipped
				framesAccrued -= (framesToRead + framesToSkip);
				// write to speakers
				if (numBytesRead > 0) {
					speakers.write(audioBuffer, 0, numBytesRead);
					numBytesRead = 0;
				}
				// mark last update
				lastUpdate = currTime;
				// give the CPU back to the OS for a bit

				semaphore.release();
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
	}

	@Override
	public void interrupt() {
		if (speakers != null) {
			speakers.stop();
			speakers.close();
		}
		super.interrupt();
	}

	public void pause() {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void relaunch() {
		semaphore.release();
	}
}
