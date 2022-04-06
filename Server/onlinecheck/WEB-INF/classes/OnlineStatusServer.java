import java.util.*;
import java.util.concurrent.*;
import javax.websocket.*;
import javax.websocket.server.*;
@ServerEndpoint("/status")
public class OnlineStatusServer
{
private Session sess=null;
private boolean admin_flg=false;
private boolean device_flg=false;
private static OnlineStatusHandle osh = new OnlineStatusHandle();
private static String admin_pass="discoveryindia@";
private static ConcurrentHashMap<String,Session> id_store = new ConcurrentHashMap<>();
private String dev_key;
private static SynchronousQueue<Session> loop_sq = new SynchronousQueue<>(true);
private static Thread loop = new Thread()
{
long time_ping=5000;
long snp=0;
Set<Session> tbd = new HashSet<>();
boolean accept_flg=false;
public void run()
{
while(true)
{
try
{
if((System.currentTimeMillis()-snp)>=time_ping)
{
snp=System.currentTimeMillis();
disconnectSessions();
mapDeviceSessions();
}
acceptPingBacks();
}
catch(Exception e)
{
}
}
}
void acceptPingBacks()
{
Session ss=loop_sq.poll();
if(accept_flg && ss!=null)
{
tbd.remove(ss);
}
}
void mapDeviceSessions()
{
try
{
tbd.clear();
tbd.addAll(id_store.values());
for(Session ss : tbd)
{
try
{
ss.getBasicRemote().sendText("S:PING");
}
catch(Exception ee)
{
}
}
}
catch(Exception e)
{
}
accept_flg=true;
}
void disconnectSessions()
{
try
{
accept_flg=false;
for(Session ss : tbd)
{
try
{
ss.close();
}
catch(Exception e)
{
}
}
}
catch(Exception e)
{
}
}
}
;
static
{
loop.start();
}
@OnOpen
public void onOpen(Session cli)
{
sess=cli;
}
@OnMessage
public void onMessage(Session cli,String str)
{
try
{
if(str!=null && str.equals("C:PONG"))
{
loop_sq.put(sess);
}
if(str!=null && str.equals("C:PING"))
{
cli.getBasicRemote().sendText("S:PONG");
}
else if(str.charAt(0)=='A')
{
handleAdmin(str.substring(2));
}
else if(str.charAt(0)=='D')
{
handleDevice(str.substring(2));
}
}
catch(Exception e)
{
}
}
@OnClose
public void onClose(CloseReason reason, Session session)
{
if(admin_flg)
{
handleAdmin("DISCONNECT");
}
else if(device_flg)
{
handleDevice("DISCONNECT");
}
}
@OnError
public void onError(Session session, Throwable t)
{
if(admin_flg)
{
handleAdmin("DISCONNECT");
}
else if(device_flg)
{
handleDevice("DISCONNECT");
}
}
void handleAdmin(String str)
{
try
{
if(str==null)
{
return;
}
if(str.equals("DISCONNECT"))
{
osh.cmdAdmin(new DeviceSession(sess,null,"pop"));
}
else if(str.indexOf("AUTH:")==0)
{
if(str.substring(5).equals(admin_pass))
{
osh.cmdAdmin(new DeviceSession(sess,null,"push"));
sess.getBasicRemote().sendText("S:AUTH1");
admin_flg=true;
}
else
{
sess.getBasicRemote().sendText("S:AUTH0");
}
}
else if(admin_flg && str.indexOf("MSG:")==0)
{
osh.msgAdmin(new DeviceSession(sess,null,str.substring(4)));
}
}
catch(Exception e)
{
}
}
void handleDevice(String str)
{
try
{
if(str==null)
{
return;
}
if(device_flg && str.equals("DISCONNECT"))
{
id_store.remove(dev_key);
osh.cmdDevice(new DeviceSession(sess,null,"pop"));
device_flg=false;
}
else if(!device_flg && str.indexOf("CONNECT:")==0)
{
dev_key=str.substring(8);
if(dev_key!=null && !id_store.containsKey(dev_key))
{
id_store.put(dev_key,sess);
osh.cmdDevice(new DeviceSession(sess,dev_key,"push"));
sess.getBasicRemote().sendText("S:CONNECTED");
device_flg=true;
}
else
{
sess.getBasicRemote().sendText("S:CANNOT CONNECT");
}
}
}
catch(Exception e)
{
}
}
}