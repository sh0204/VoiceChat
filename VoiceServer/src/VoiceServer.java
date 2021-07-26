
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;


/**
 * 소켓을 열면 연결이 시작된다. 각 clientconnection이 클라이언트에게 생성되게 된다.
 * broadcastQueue부터 ClientConnection 인스턴스로 메시지가 전달되는 broadcastThread가 생성된다. 
 */
public class VoiceServer {
 
    private ArrayList<Message> broadCastQueue = new ArrayList<Message>();    
    private ArrayList<ClientConnection> clients = new ArrayList<ClientConnection>();
    private int port1;
    
    private UpnpService u; //UPnP 프로토콜을 구축하는 기반 역할을 한다.
    //UPnP 포로토콜을 캡슐화하는 역할을 하는 것으로 송수신의 순서 및 전달을 보장한다.

    
    public void addToBroadcastQueue(Message m) { 
        //broadcast 큐에 메시지 추가, 이 메소드는 모든 clientConnection 인스턴스에서 사용된다.
        try {
            broadCastQueue.add(m);
        } catch (Throwable t) {
           
            Utils.sleep(1);
            addToBroadcastQueue(m);
        }
    }
    private ServerSocket s;
    
    public VoiceServer(int port1) throws Exception{
        this.port1 = 2222;
        
        try {
            s = new ServerSocket(port1); //port1이 열림
            Server_log.add("포트번호" + port1 + ": 서버 시작");
        } catch (IOException ex) {
            Server_log.add("Server error " + ex + "(port1 " + port1 + ")");
            throw new Exception("Error "+ex);
        }
        new BroadcastThread().start(); //BroadcastThread 생성하고 실행 
        for (;;) { //연결 받아드림
            try {
                Socket c = s.accept();
                ClientConnection cc = new ClientConnection(this, c); //ClientConnection 스레드 생성
                cc.start();
                addToClients(cc);
                Server_log.add("새로운 클라이언트 접속 " + c.getInetAddress() + ":" + c.getPort() + " on port " + port1);
            } catch (IOException ex) {
            }
        }
    }

    private void addToClients(ClientConnection cc) {
        try {
            clients.add(cc); //연결 리스트에 새로운 연결이 들어온 결과를 추가한다.
        } catch (Throwable t) {
           
            Utils.sleep(1);
            addToClients(cc);
        }
    }

    /**
     * 각 클라이언트 연결에 broadcast로 메시지를 전달하고, 연결이 끊긴 클라이언트를 제거한다.
     */
    private class BroadcastThread extends Thread {
        
        public BroadcastThread() {
        }
        
        @Override
        public void run() {
            for (;;) {
                try {
                    ArrayList<ClientConnection> toRemove = new ArrayList<ClientConnection>(); //create a list of dead connections
                    for (ClientConnection cc : clients) {
                        if (!cc.isAlive()) { //연결이 끊기면 
                            Server_log.add("연결이 끊김: " + cc.getInetAddress() + ":" + cc.getPort() + " on port " + port1);
                            toRemove.add(cc);
                        }
                    }
                    clients.removeAll(toRemove); //끊긴 연결 삭제
                    if (broadCastQueue.isEmpty()) { //보내지는 게 없을 때
                        Utils.sleep(10); //바쁜대기 피하기 위해 
                        continue;
                    } else { //broadcast할 무언가를 받았을 때, 
                        Message m = broadCastQueue.get(0);
                        for (ClientConnection cc : clients) { //메시지 broadcast 
                            if (cc.getCId() != m.getCId()) {
                                cc.addToQueue(m);
                            }
                        }
                        broadCastQueue.remove(m); //broadcast 큐에서 삭제
                    }
                } catch (Throwable t) {
                  
                }
            }
        }
    }
}
