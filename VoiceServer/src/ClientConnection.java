
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;


public class ClientConnection extends Thread {
    
    private VoiceServer serv; 
    // 서버의 인스턴스, 서버의 브로드캐스트 큐에 메시지를 집어넣어야한다.
    private Socket s; //클라이언트와 연결 
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private long cId; 
    private ArrayList<Message> toSend = new ArrayList<Message>(); 
    // 큐의 메시지가 클라이언트에게 보내짐

    public InetAddress getInetAddress() { //클라이언트 아이피 주소 리턴
        return s.getInetAddress();
    }

    public int getPort() { //클라이언트 tcp 포트 리턴
        return s.getPort();
    }

    public long getCId() { //클라이언트 unique id 리턴
        return cId;
    }

    public ClientConnection(VoiceServer serv, Socket s) {
        this.serv = serv;
        this.s = s;
        byte[] addr = s.getInetAddress().getAddress();
        cId = (addr[0] << 48 | addr[1] << 32 | addr[2] << 24 | addr[3] << 16) + s.getPort(); 
    }

    public void addToQueue(Message m) {
        try {
            toSend.add(m);
        } catch (Throwable t) {
           
        }
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(s.getOutputStream()); 
            in = new ObjectInputStream(s.getInputStream());
        } catch (IOException ex) { 
            try {
                s.close();
                Server_log.add("ERROR " + getInetAddress() + ":" + getPort() + " " + ex);
            } catch (IOException ex1) {
            }
            stop();
        }
        for (;;) {
            try {
                if (s.getInputStream().available() > 0) { //클라이언트한테 음성 받음
                    Message toBroadcast = (Message) in.readObject(); //클라이언트로 부터 받은 데이터 읽음
                    if (toBroadcast.getCId() == -1) {
                        toBroadcast.setCId(cId);
                        toBroadcast.setTimeMoment(System.nanoTime() / 1000000L);
                        serv.addToBroadcastQueue(toBroadcast);
                    } else {
                        continue; 
                    }
                }
                try {
                    if (!toSend.isEmpty()) {
                        Message toClient = toSend.get(0); //클라이언트에게 보낼 무언가를 받았을 때
                        if (!(toClient.getData() instanceof SoundPacket) || toClient.getTimeMoment() + toClient.getTtl() < System.nanoTime() / 1000000L) { 
                            Server_log.add("dropping packet from " + toClient.getCId() + " to " + cId);
                            continue;
                        }
                        out.writeObject(toClient); //메시지 보내기
                        toSend.remove(toClient); //큐에서 지움
                    } else {
                        Utils.sleep(10); //바쁜 상황 피하기 위해서
                    }
                } catch (Throwable t) {
                    if (t instanceof IOException) {//연결 끊기거나, 연결 오류
                        throw (Exception) t;
                    } else {
                        System.out.println("cc fixmutex");
                        continue;
                    }
                }
            } catch (Exception ex) { //연결이 끊기거나, 연결이 오류가 나면 스레드 종료
                try {
                    s.close();
                } catch (IOException ex1) {
                }
                stop();
            }
        }

    }
}