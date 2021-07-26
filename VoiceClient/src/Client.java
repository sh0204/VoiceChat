
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client extends Thread {

    private Socket s;
    private ArrayList<AudioChannel> channels = new ArrayList<AudioChannel>();
    private MicThread sendThread;

    public Client(String serverIp, int serverPort) throws UnknownHostException, IOException {
        s = new Socket(serverIp, serverPort);
    }

    @Override
    public void run() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(s.getInputStream()); //음성 저장
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(s.getOutputStream()); //음성 출력
            try {
                Utils.sleep(100);
                sendThread = new MicThread(objectOutputStream);
                sendThread.start();
            } catch (Exception e) {
                System.out.println("mic unavailable " + e);
            }
            for (;;) {
                if (s.getInputStream().available() > 0) {
                    Message msg = (Message) (objectInputStream.readObject());
                    AudioChannel sendTo = null;
                    for (AudioChannel ach : channels) {
                        if (ach.getCId() == msg.getCId()) {
                            sendTo = ach;
                        }
                    }
                    if (sendTo != null) {
                        sendTo.addToQueue(msg);
                    } else {
                        AudioChannel ach = new AudioChannel(msg.getCId());
                        ach.addToQueue(msg);
                        ach.start();
                        channels.add(ach);
                    }
                } else {
                    ArrayList<AudioChannel> killAudio = new ArrayList<AudioChannel>();
                    for (AudioChannel ch : killAudio) {
                        ch.AudioExit();
                        channels.remove(ch);
                    }
                    Utils.sleep(1);
                }
            }
        } catch (Exception e) {
            System.out.println("client err " + e.toString());
        }
    }
}
