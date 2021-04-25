package fr.pederobien.mumble.client.impl;

import java.util.concurrent.Semaphore;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import fr.pederobien.mumble.client.interfaces.observers.IObsMicrophone;
import fr.pederobien.utils.IObservable;
import fr.pederobien.utils.Observable;

public class Microphone extends Thread implements IObservable<IObsMicrophone> {
	private static final AudioFormat FORMAT = new AudioFormat(44100.0f, 16, 1, true, false);
	private static final int CHUNK_SIZE = 8192;
	private boolean pauseRequested;
	private TargetDataLine microphone;
	private Semaphore semaphore;
	private Observable<IObsMicrophone> observers;

	public Microphone() {
		super("Microphone");
		semaphore = new Semaphore(1, true);
		observers = new Observable<IObsMicrophone>();
		setDaemon(true);
	}

	@Override
	public synchronized void start() {
		try {
			microphone = (TargetDataLine) AudioSystem.getLine(new DataLine.Info(TargetDataLine.class, FORMAT));
			microphone.open(FORMAT);
			super.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		byte[] data = new byte[CHUNK_SIZE];
		microphone.start();
		while (!isInterrupted()) {
			try {
				semaphore.acquire();
				final int numBytesRead = microphone.read(data, 0, CHUNK_SIZE);

				if (pauseRequested) {
					semaphore.release();
					Thread.sleep(100);
					continue;
				}

				observers.notifyObservers(obs -> obs.onDataRead(data, numBytesRead));
				semaphore.release();
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	@Override
	public void interrupt() {
		if (microphone != null) {
			microphone.stop();
			microphone.close();
		}
		super.interrupt();
	}

	@Override
	public void addObserver(IObsMicrophone obs) {
		observers.addObserver(obs);
	}

	@Override
	public void removeObserver(IObsMicrophone obs) {
		observers.removeObserver(obs);
	}

	public void pause() {
		pauseRequested = true;
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void relaunch() {
		pauseRequested = false;
		semaphore.release();
	}
}
