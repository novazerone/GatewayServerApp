package gatewayServer;

import java.util.List;

import database.ServerJDBCTemplate;

/**
 * Created by user on 12/3/2015.
 */
public class ReplicationScheduler extends Thread{

	private int timeGap = 10000;
	private ServerJDBCTemplate dbServer;
	private List<ServerHandler> servers;

	public ReplicationScheduler(List<ServerHandler> _servers){
		servers = _servers;
	}

	@Override
	public void run(){
		while(true){
			try {
				sleep(timeGap);

				for(ServerHandler sh : servers){

				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
