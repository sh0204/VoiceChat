
import java.io.Serializable;

public class Message implements Serializable{
    private long cId; // 클라이언트 Id
    private long timeMoment, ttl = 2000; ////-1은 클라이언트에서 서버로, 그렇지 않으면 서버가 메시지를 수신하는 순간의 timeMoment를 의미합니다. 2초 TTL
    private Object data; 
    
    public Message(long cId, long timeMoment, Object data) {
        this.cId = cId;
        this.timeMoment = timeMoment;
        this.data = data;
    }

    public void setTimeMoment(long timeMoment) {
        this.timeMoment = timeMoment;
    }

    public long getCId() {
        return cId;
    }

    public Object getData() {
        return data;
    }

    public long getTimeMoment() {
        return timeMoment;
    }

    public long getTtl() {
        return ttl;
    }
    public void setTtl(long ttl) {
        this.ttl = ttl;
    }
    
    public void setCId(long cId) {
        this.cId = cId;
    }
    
}
