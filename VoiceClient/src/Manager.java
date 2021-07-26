
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;

public class Manager extends Thread {

    public Socket sock;
    public Vector<Manager> userV;
    public ObjectInputStream ois;
    public ObjectOutputStream oos;
    public String nickName, time;

    public Manager(Socket sock, Vector<Manager> userV) {
        super();
        this.sock = sock;
        this.userV = userV;
        try {
            ois = new ObjectInputStream(this.sock.getInputStream());
            oos = new ObjectOutputStream(this.sock.getOutputStream());
        } catch (IOException e) {
            System.out.println("Manager() 예외: " + e);
        }
    }

    public void run() {

        try {
            Object obj = ois.readObject();// Room으로 부터 닉네임|입장시간 형식의 객체 수신
            if (obj instanceof String) {// obj가 String형이라면
                String str = (String) obj;// obj를 String으로 형변환
                String tokens[] = str.split("\\|");// |로 구분하여 배열에 담음
                this.nickName = tokens[0];
                this.time = tokens[1];
            }
            boolean isExist = duplicateName(nickName);// 닉네임 중복여부 체크 중복이면 true
            if (isExist) {// 만약 닉네임이 중복?
                this.sendMessaageTo("700|");// 중복된 닉네임이라면 700을 보냄

            } else {// 중복이 아니면 이미 접속된 사람 정보를 접속한 지금 사람에게 보여줌
                for (Manager userTable : userV) {
                    String msg = "100|" + userTable.nickName + "|" + userTable.time;
                    sendMessaageTo(msg);
                }
                userV.add(this);
                String msg = "100|" + this.nickName + "|" + this.time;
                // 지금접속한사람의정보를이미접속한사람에게보여줌
                sendMessageAll(msg);// 100|닉네임|입장시간을 보냄
            }

            while (true) {
                String readMsg = (String) ois.readObject();// ObjectInputStream으로 메시지를 듣고 readMsg에 담아
                parsing(readMsg);// parsing으로 보낸다
            }
        } catch (Exception e) {
            System.out.println("Manager run()예외 : " + e);
        }
    }

    private boolean duplicateName(String nickName) {
        Iterator<Manager> it = userV.iterator();// userV순차적으로 접근
        while (it.hasNext()) {
            Manager user = it.next();
            if (user.nickName.contentEquals(nickName)) { // 닉네임이 서로 중복이면
                return true;// true 반환
            }
        }
        return false;
    }

    private void parsing(String readMsg) {
        String tokens[] = readMsg.split("\\|");
        switch (tokens[0]) {//프로토콜 번호로 구별

            case "200": {//200|기존닉|바꿀닉

                String oldNick = tokens[1];//기존닉
                String newNick = tokens[2];//새닉
                try {
                    changeNick(oldNick, newNick);
                    sendMessageAll("200|" + oldNick + "|" + newNick);
                } catch (IOException e) {
                    System.out.println("Manager parsing 200 에러 ");
                }

            }
            break;
            case "300": {
                String exit = tokens[1];
                sendMessageAll("300|" + exit);//300|닉네임 정보를 담아 sendMessageAll메소드 호출
                userV.remove(this);//벡터에서 퇴장하는 클라이언트와 통신하는 Manager 제거
                closeAll();
            }
            break;
            case "400": {
                String exit = tokens[1];//닉네임
                sendMessageAll("400|" + exit);
            }
            break;
            case "500": {
                userV.remove(this);
                closeAll();
            }
            break;

        }
    }

    private void closeAll() {
        try {
            if (ois != null) {
                ois.close();
            }
            if (oos != null) {
                oos.close();
            }
            if (sock != null) {
                sock.close();
                sock = null;
            }
        } catch (Exception e) {
            System.out.println("Manager closeAll()예외: " + e);
        }
    }

    private synchronized void sendMessageAll(String msg) {
        for (Manager user : userV) {
            try {
                user.sendMessaageTo(msg);// msg정보가 담긴 sendMessaageTo메소드 호출
            } catch (IOException e) {
                System.out.println("Manager sendMessageAll()예외; " + e);
                userV.removeElement(user);
                break;
            }

        }
    }

    private synchronized void sendMessaageTo(String msg) throws IOException {
        oos.writeObject(msg);
        oos.flush();
    }

    private synchronized void changeNick(String oldNick, String newNick) throws IOException {
        for (Manager user : userV) {
            if (user.nickName.equals(oldNick)) {
                user.nickName = newNick;
                return;
            }
        }
    }
}
