package fr.pederobien.mumble.client.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class MicrophoneThread extends Thread {
	private TargetDataLine microphone;
	private DatagramSocket client;
	private InetSocketAddress address;
	private int udpPort;

	public MicrophoneThread(InetSocketAddress address, int udpPort) {
		this.address = address;
		this.udpPort = udpPort;
	}

	@Override
	public synchronized void start() {
		AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		try {
			microphone = (TargetDataLine) AudioSystem.getLine(info);
			microphone.open(format);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		super.start();
	}

	@Override
	public void run() {
		client.connect(address.getAddress(), udpPort);
		int numBytesRead;
		int CHUNK_SIZE = 1024;
		byte[] data = new byte[microphone.getBufferSize() / 5];
		microphone.start();
		while (!client.isClosed()) {
			numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
			try {
				client.send(new DatagramPacket(data, numBytesRead));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void interrupt() {
		client.close();
		super.interrupt();
	}
}
