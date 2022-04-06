import java.net.*;
import java.util.concurrent.*;
import javax.websocket.*;
@ClientEndpoint
public class WSClientCmdProcess
{
private InstanceJFrame ijf = new InstanceJFrame();
private WebSocketContainer container = null;
private Session server = null;
private String w_url = null;
private String admin_pass = null;
private SynchronousQueue<String> cmd_sq = new SynchronousQueue<>(true);
private Thread loop = new Thread()
{
boolean serv_flg=false;
boolean auth_flg=false;
int ping_flg=0;
long time_out=2500;
long snp_serv=0;
long snp_auth=0;
public void run()
{
while(true)
{
try
{
String cmd=cmd_sq.poll();
if(cmd!=null)
{
if(cmd.equals("AUTH1"))
{
auth_flg=true;
ijf.osa.indicateAuthLabel(true);
ijf.tm.removeRows();
sendMessage("A:MSG:refresh");
}
else if(cmd.equals("AUTH0"))
{
break;
}
else if(cmd.equals("RECONNECT TO SERVER"))
{
try
{
System.out.println("DISCONNECTING THE SERVER");
server.close();
}
catch(Exception ee)
{
}
serv_flg=false;
auth_flg=false;
ijf.osa.indicateServerLabel(false);
ijf.osa.indicateAuthLabel(false);
}
}
if(!serv_flg && (System.currentTimeMillis()-snp_serv)>=time_out)
{
snp_serv=System.currentTimeMillis();
if(connectServer())
{
serv_flg=true;
ijf.osa.indicateServerLabel(true);
}
}
if(!auth_flg && (System.currentTimeMillis()-snp_auth)>=time_out)
{
snp_auth=System.currentTimeMillis();
authenticate();
}
}
catch(Exception e)
{
}
}
}
}
;
WSClientCmdProcess(String a,String b)
{
container = ContainerProvider.getWebSocketContainer();
w_url=a;
admin_pass=b;
loop.start();
}
boolean connectServer()
{
try
{
server = container.connectToServer(this, new URI(w_url));
return true;
}
catch(Exception e)
{
}
return false;
}
void authenticate()
{
sendMessage("A:AUTH:"+admin_pass);
}
void sendMessage(String a)
{
try
{
server.getBasicRemote().sendText(a);
}
catch(Exception e)
{
}
}
@OnOpen
public void onOpen(Session session)
{
}
@OnMessage
public void onMessage(Session session,String msg)
{
try
{
if(msg.charAt(0)=='S')
{
cmd_sq.put(msg.substring(2));
}
else if(msg.charAt(0)=='T')
{
ijf.tm.processLogs(msg.substring(2));
}
}
catch(Exception e)
{
}
}
@OnClose
public void onClose(Session session, CloseReason closeReason)
{
requestReconnect();
}
@OnError
public void onError(Session session, Throwable t)
{
requestReconnect();
}
void requestReconnect()
{
try
{
cmd_sq.put("RECONNECT TO SERVER");
}
catch(Exception e)
{
}
}
}