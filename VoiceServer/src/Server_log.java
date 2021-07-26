
/**

 * 서버의 로그, 서버에 클라이언트들의 항목을 추가시킨다.

 */
public class Server_log {
    private static String log="";
    public static void add(String s){log+=s+"\n";}
    public static String get(){return log;}
}
