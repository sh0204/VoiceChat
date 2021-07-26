
import java.io.IOException;
import static java.lang.System.out;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class RoomServer implements Runnable {

    public ServerSocket ss;
    public int port;
    public Vector<Manager> userV = new Vector<Manager>(5, 3);

    Room rm;

    public RoomServer(Room rm) {
        this.rm = rm;
    }

    public void TCPserverStart() {
        try {
            port = rm.port;
            ss = new ServerSocket(8888);
            out.println("### 서버가 시작됨 ###");
            out.println("##[" + 8888 + "]번 포트에서 대기중 . . .");
            Thread listener = new Thread(this);//스레드 시작
            listener.start();
        } catch (IOException e) {
            out.println("TCPserverStart() 예외: " + e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket sock = ss.accept();//채팅방 접속을 감지
                out.println("###[" + sock.getInetAddress() + "] 님이 접속했습니다###");

                Manager mt = new Manager(sock, userV);//-기본 생성자 //-Manager유형의 클래스에서 생성자 속성을 맞춰 건내주는 객체 생성
                mt.start();//Manager의 스레드가 시작
            } catch (Exception e) {
                out.println("TCPserverStart run() 예외: " + e);
            }
        }
    }
}