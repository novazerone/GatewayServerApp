package server;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import app.ByteCache;
import app.Driver;
import gatewayServer.FileToServerHandler;

public class Duplicator extends Thread{
	
	private ByteCache cache;
	private String[] ports;
	
	private Socket socket;
	
	public Duplicator(ByteCache _cache, String[] _ports){
		cache = _cache;
		ports = _ports;
	}
	
	@Override
	public void run(){
		for(int x = 0; x < ports.length; x++){
			// Connect to the port.
			try {
				socket = new Socket(Driver.getHost(), Integer.parseInt(ports[x]));
				Server.window.log("Connected to port " + ports[x] + "\n", Color.BLACK);
				DuplicateToServerHandler dtsh = new DuplicateToServerHandler(socket, new BufferedReader(new InputStreamReader(socket.getInputStream())), new PrintWriter(socket.getOutputStream(), true), cache);
				dtsh.run(); // Do this sequentially.
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
