
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

// 이 스레드는 사용자 중 한명에게서 나오는 소리를 재생하는 스레드
// 서버에서 각 사용자는 자체 AudioChannel를 가지고 있음
public class AudioChannel extends Thread {

    private long cId;
    private ArrayList<Message> queue = new ArrayList<Message>();
    private int soundPacketLen = SoundPacket.defaultDataLenght;
    private long packetTime = System.nanoTime();

    public void AudioExit(){
        if (speaker != null) {
            speaker.close();
        }
        stop();
    }

    public AudioChannel(long cId) {
        this.cId = cId;
    }

    public long getCId() {
        return cId;
    }

    public void addToQueue(Message m) {
        queue.add(m);
    }
    private SourceDataLine speaker = null;

    @Override
    public void run() {
        try {
            AudioFormat af = SoundPacket.defaultFormat;
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
            speaker = (SourceDataLine) AudioSystem.getLine(info);
            speaker.open(af);
            speaker.start();
            for (;;) {
                if (queue.isEmpty()) {
                    Utils.sleep(10);
                    continue;
                } else {//we got something to play
                    packetTime = System.nanoTime(); //system.nanoTime()- 자바 코드 실행에 걸린 시간 측정
                    Message in = queue.get(0);
                    queue.remove(in);
                    if (in.getData() instanceof SoundPacket) {
                        SoundPacket m = (SoundPacket) (in.getData());
                        if (m.getData() == null) {
                            byte[] noise = new byte[soundPacketLen];
                            for (int i = 0; i < noise.length; i++) {
                                noise[i] = (byte) ((Math.random() * 3) - 1);
                            }
                            speaker.write(noise, 0, noise.length);
                        } else {
                            GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(m.getData()));
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            for (;;) {
                                int b = gis.read();
                                if (b == -1) {
                                    break;
                                } else {
                                    baos.write((byte) b);
                                }
                            }
                            //play decompressed data
                            byte[] toPlay = baos.toByteArray();
                            speaker.write(toPlay, 0, toPlay.length);
                            soundPacketLen = m.getData().length;
                        }
                    } else {
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("receiverThread " + cId + " error: " + e.toString());
            if (speaker != null) {
                speaker.close();
            }
            stop();
        }
    }
}
