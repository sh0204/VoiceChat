
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Room extends javax.swing.JFrame implements Runnable {

    private MicTester micTester;
    static Socket s;
    public Socket sock;
    public String host;
    public int port;
    public boolean isStop = false;

    public static final int EXIT = 0;
    public ObjectOutputStream oos;
    public ObjectInputStream ois;

    public boolean serverStart = false;

    public static String nickname;
    public String changeNickname;
    public String time;

    private Login login;
    RoomServer rs = new RoomServer(this);

    private class MicTester extends Thread {

        private TargetDataLine mic = null;

        @Override
        public void run() {

            try {
                AudioFormat af = SoundPacket.defaultFormat;
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, null);
                mic = (TargetDataLine) (AudioSystem.getLine(info));
                mic.open(af);
                mic.start();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(rootPane, "Microphone not detected.\nPress OK to close this program", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            for (;;) {
                Utils.sleep(10);
                if (mic.available() > 0) {
                    byte[] buff = new byte[SoundPacket.defaultDataLenght];
                    mic.read(buff, 0, buff.length);
                    long tot = 0;
                    for (int i = 0; i < buff.length; i++) {
                        tot += MicThread.amplification * Math.abs(buff[i]);
                    }
                    tot *= 2.5;
                    tot /= buff.length;
                    micLev.setValue((int) tot);

                }
            }
        }

        private void close() {
            if (mic != null) {
                mic.close();
            }
            stop();
        }
    }

    public Room(String identification, Login login) {
        initComponents();
        micTester = new MicTester();
        micTester.start();
        this.getContentPane().setBackground(new java.awt.Color(176, 179, 214));
        this.login = login;
        this.time = this.login.time;
        this.host = this.login.host;
        this.nickname = this.login.nickname;
        this.port = 8888;

        if ("clientlogin".equals(identification)) {

            connect();
            lbNickname.setText(nickname);
            setTitle(nickname);
            serverStart = true;
            btStart.setText("오디오 연결");
            out.setVisible(false);

        }
        if ("hostlogin".equals(identification)) {
            lbNickname.setText(nickname);
            setTitle(nickname);

            int allRow = userTable.getRowCount();
            for (int i = 0; i < allRow; i++) {
                String nicks = (String) userTable.getModel().getValueAt(i, 0);
                if (nickname.equals(nicks)) {
                    userTable.setRowSelectionAllowed(true);
                    userTable.setOpaque(true);
                    break;
                }
            }
        }

    }

    public void showMsg(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lbNickname = new javax.swing.JLabel();
        btChangeNickname = new javax.swing.JButton();
        tpMessage = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        userTable = new javax.swing.JTable();
        out = new javax.swing.JButton();
        btStart = new javax.swing.JButton();
        micLev = new javax.swing.JProgressBar();
        micVol = new javax.swing.JSlider();
        logout = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 240, 182));

        lbNickname.setFont(new java.awt.Font("DX하늘구름", 0, 11)); // NOI18N
        lbNickname.setForeground(new java.awt.Color(94, 74, 58));
        lbNickname.setText("............");

        btChangeNickname.setBackground(new java.awt.Color(221, 171, 120));
        btChangeNickname.setFont(new java.awt.Font("DX하늘구름", 0, 11)); // NOI18N
        btChangeNickname.setForeground(new java.awt.Color(255, 255, 255));
        btChangeNickname.setText("변경");
        btChangeNickname.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btChangeNicknameMouseClicked(evt);
            }
        });
        btChangeNickname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btChangeNicknameActionPerformed(evt);
            }
        });

        tpMessage.setFont(new java.awt.Font("DX하늘구름", 0, 11)); // NOI18N
        tpMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tpMessageActionPerformed(evt);
            }
        });

        userTable.setFont(new java.awt.Font("DX하늘구름", 0, 11)); // NOI18N
        userTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "nickname", "time"
            }
        ));
        userTable.setSelectionBackground(new java.awt.Color(234, 194, 154));
        jScrollPane2.setViewportView(userTable);

        out.setBackground(new java.awt.Color(221, 171, 120));
        out.setFont(new java.awt.Font("DX하늘구름", 0, 11)); // NOI18N
        out.setForeground(new java.awt.Color(255, 255, 255));
        out.setText("강제 퇴장");
        out.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outActionPerformed(evt);
            }
        });

        btStart.setBackground(new java.awt.Color(221, 171, 120));
        btStart.setFont(new java.awt.Font("DX하늘구름", 0, 11)); // NOI18N
        btStart.setForeground(new java.awt.Color(255, 255, 255));
        btStart.setText("서버 연결");
        btStart.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btStartMousePressed(evt);
            }
        });
        btStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btStartActionPerformed(evt);
            }
        });

        micVol.setMaximum(300);
        micVol.setMinimum(50);
        micVol.setValue(100);
        micVol.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                micVolStateChanged(evt);
            }
        });

        logout.setBackground(new java.awt.Color(221, 171, 120));
        logout.setFont(new java.awt.Font("DX하늘구름", 0, 11)); // NOI18N
        logout.setForeground(new java.awt.Color(255, 255, 255));
        logout.setText("나가기");
        logout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("DX하늘구름", 0, 11)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(94, 74, 58));
        jLabel3.setText("오디오 신호");

        jLabel4.setFont(new java.awt.Font("DX하늘구름", 0, 11)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(94, 74, 58));
        jLabel4.setText("마이크 볼륨");

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgae/푸이모지.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(micLev, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(micVol, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tpMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 1, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btStart)
                        .addGap(30, 30, 30)
                        .addComponent(out)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(logout, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbNickname, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(btChangeNickname, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(lbNickname, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10))
                            .addComponent(btChangeNickname))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tpMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(micLev, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(micVol, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(logout)
                    .addComponent(btStart)
                    .addComponent(out))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void micVolStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_micVolStateChanged
        MicThread.amplification = ((double) (micVol.getValue())) / 100.0;
    }//GEN-LAST:event_micVolStateChanged

    private void btChangeNicknameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btChangeNicknameMouseClicked
        String newNick = JOptionPane.showInputDialog(null, "바꿀 닉네임을 입력하세요.","닉네임변경",JOptionPane.DEFAULT_OPTION);
        Icon icon =new javax.swing.ImageIcon(getClass().getResource("/error (1).png"));
        if (newNick == null || newNick.trim().isEmpty()) {
           JOptionPane.showMessageDialog(null, "닉네임을 입력하세요!!", "Error",JOptionPane.ERROR_MESSAGE,icon);
            return;
        }
        int allRow = userTable.getRowCount();
        for (int i = 0; i < allRow; i++) {
            String nicks = (String) userTable.getModel().getValueAt(i, 0);
            if (newNick.equals(nicks)) {
                JOptionPane.showMessageDialog(null, "이미 있는 닉네임입니다!", "Error",JOptionPane.ERROR_MESSAGE,icon);
                return;
            }
        }
        try {
            oos.writeObject("200|" + nickname + "|" + newNick);// 200|받는사람|기존닉넴|새닉넴
            oos.flush();
        } catch (IOException e) {
            System.out.println("예외: " + e);
        }

        nickname = newNick;
        lbNickname.setText(nickname);
        setTitle(nickname);
    }//GEN-LAST:event_btChangeNicknameMouseClicked

    private void btChangeNicknameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btChangeNicknameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btChangeNicknameActionPerformed

    private void tpMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tpMessageActionPerformed

    }//GEN-LAST:event_tpMessageActionPerformed

    private void btStartMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btStartMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btStartMousePressed

    private void btStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btStartActionPerformed
        if (!serverStart) {
            serverStart = true;
            rs.TCPserverStart();
            try {
                Thread.sleep(1000);
                connect();
                btStart.setText("오디오 연결");

            } catch (InterruptedException e1) {
                System.out.println("채팅방 입장에 실패하였습니다.");
            }

        } else {
            try {
                new Client(host, 2222).start();
            } catch (Exception ex) { //connection failed
                JOptionPane.showMessageDialog(rootPane, ex, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            micTester.close();
            micLev.setVisible(false);
            jLabel3.setVisible(false);
            btStart.setVisible(false);
        }

    }//GEN-LAST:event_btStartActionPerformed

    private void logoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutActionPerformed
        exitProcess();
        System.exit(0);
    }//GEN-LAST:event_logoutActionPerformed

    private void outActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outActionPerformed
        kick();

    }//GEN-LAST:event_outActionPerformed

    public void connect() {
        try {

            if (sock == null) {
                sock = new Socket(host, 8888); //소켓 열기
                tpMessage.setText("채팅 서버와 연결됨\n");

                oos = new ObjectOutputStream(sock.getOutputStream());
                ois = new ObjectInputStream(sock.getInputStream());

                Thread listener = new Thread(this);
                listener.start();
                oos.writeObject(nickname + "|" + time);
                oos.flush();

            }
        } catch (ConnectException e) {
            System.out.println("Room connect() 예외: " + e);
            showMsg("서버에 문제가 있거나 없는 호스트입니다.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Room connect() 예외: " + e);
        }
    }

    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Room.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    @Override

    public void run() {
        try {
            while (!isStop) {
                String serverMsg = (String) ois.readObject();
                if (serverMsg == null) {
                    return;
                }
                process(serverMsg);
            }
        } catch (Exception e) {
            System.out.println("Room run() 예외: " + e);
            showMsg("호스트가 방을 종료하였습니다.");
            System.exit(0);
            dispose();
        }
    }

    public void process(String msg) {
        String tokens[] = msg.split("\\|");
        switch (tokens[0]) {
            case "100": {
                DefaultTableModel userModel = (DefaultTableModel) userTable.getModel();//userTable에 정보를 올림
                String rowData[] = {tokens[1], tokens[2]};
                userModel.addRow(rowData);
                String str = "[" + tokens[1] + "] 님이 접속하셨습니다. :)";
                tpMessage.setText(str);
            }
            break;

            case "200": {
                String oldNick = tokens[1];//기존닉
                String newNick = tokens[2];//새닉
                String str = "[" + oldNick + "]님이 ☞ [" + newNick + "]님이 되었습니다.";
                changeNickAll(oldNick, newNick);
                tpMessage.setText(str);
            }
            break;
            case "300": {
                String exitMessage = tokens[1];//닉네임
                logout(exitMessage, EXIT);//닉네임과 EXIT속성을 갖고 logout메소드 호출
            }
            break;
            case "400": {
                String exitMessage = tokens[1];
                logout(exitMessage, EXIT);
            }
            break;
        }
    }

    public void exitProcess() {

        if (sock != null && !sock.isClosed()) {//서버 연결중일때
            try {
                oos.writeObject("300|" + nickname);
                oos.flush();
            } catch (IOException e) {
                System.out.println("Room exitProcess() 예외: " + e);

                System.exit(0);//프로그램 종료

            }
        } else {// 서버 연결중이 아닐때

            System.exit(0);//프로그램 종료

        }
    }

    public void logout(String logout, int mode) {//닉네임과 LOGOUT or EXIT
        //나가는 사람이 본인이 아니면
        DefaultTableModel userModel = (DefaultTableModel) userTable.getModel();
        String Id = "";
        for (int i = 0; i < userModel.getRowCount(); i++) {
            String name = (String) userModel.getValueAt(i, 0);
            if (name.contentEquals(logout)) {
                userModel.removeRow(i);//삭제
                Id = name;
                break;
            }
        }
        if (mode == EXIT) {//종료
            String str = "[" + Id + "] 님이 프로그램을 종료하셨습니다.";
            tpMessage.setText(str);

        }
        //나가는 사람이 본인이면 
        if (logout.contentEquals(nickname)) {
            isStop = true;
            try {
                oos.writeObject("500|" + nickname);
                oos.flush();
                System.exit(0);
            } catch (IOException e) {
                System.out.println("Room exitProcess() 예외: " + e);

                System.exit(0);//프로그램 종료

            }
            exitMessage(mode);//exitMessage(EXIT or LOGOUT)
        }

    }

    public void kick() {
        DefaultTableModel userModel = (DefaultTableModel) userTable.getModel();//userTable에 정보를 올림
        int rowIndex = userTable.getSelectedRow();
        String value = (String) userModel.getValueAt(rowIndex, 0);
        Icon warning = new javax.swing.ImageIcon(getClass().getResource("/error (3).png"));
        int answer = JOptionPane.showConfirmDialog(null, "[" + value + "] 님을 강제 퇴장시키시겠습니까?", "강제퇴장", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE, warning);
        if (answer == JOptionPane.YES_OPTION) {
            if (sock != null && !sock.isClosed()) {
                try {
                    oos.writeObject("400|" + value);
                    oos.flush();

                } catch (IOException e) {
                    System.out.println("Room exitProcess() 예외: " + e);
                    System.exit(0);//프로그램 종료
                }
            }
        }
    }

    public void exitMessage(int mode) {
        isStop = true;
        lbNickname.setText("");
        try {
            if (oos != null) {
                oos.close();
            }
            if (ois != null) {
                ois.close();
            }
            if (sock != null) {
                sock.close();
                sock = null;
            }
        } catch (Exception e) {
            System.out.println("Room exitMessage() 예외: " + e);
        }
        if (mode == EXIT) {//퇴장
            this.dispose();
            System.exit(0);//종료
        }
    }

    private void changeNickAll(String oldNick, String newNick) {
        int allRow = userTable.getRowCount();
        for (int i = 0; i < allRow; i++) {
            String nicks = (String) userTable.getModel().getValueAt(i, 0);
            if (oldNick.equals(nicks)) {
                userTable.setValueAt(newNick, i, 0);
                break;
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btChangeNickname;
    private javax.swing.JButton btStart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbNickname;
    private javax.swing.JButton logout;
    private javax.swing.JProgressBar micLev;
    private javax.swing.JSlider micVol;
    private javax.swing.JButton out;
    private javax.swing.JTextField tpMessage;
    private javax.swing.JTable userTable;
    // End of variables declaration//GEN-END:variables
}
