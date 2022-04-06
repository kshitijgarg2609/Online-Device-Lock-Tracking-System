import java.awt.event.*;
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
private boolean rcflg=true;
private Thread loop = new Thread()
{
boolean serv_flg=false;
boolean auth_flg=false;
boolean eflg=true;
long time_out=2500;
long snp_serv=0;
long snp_auth=0;
public void run()
{
while(true)
{
try
{
String cmd;
if(eflg)
{
cmd=cmd_sq.take();
if(cmd.equals("RESUME"))
{
eflg=false;
}
else
{
continue;
}
}
else
{
cmd=cmd_sq.poll();
}
if(cmd!=null)
{
if(cmd.equals("PING"))
{
sendMessage("C:PONG");
}
else if(cmd.equals("CONNECTED"))
{
auth_flg=true;
ijf.osd.indicateAuthLabel(true);
}
else if(cmd.equals("CANNOT CONNECT") || cmd.equals("PAUSE"))
{
disconnectServer(true);
delay(500);
serv_flg=false;
auth_flg=false;
ijf.osd.indicateServerLabel(false);
ijf.osd.indicateAuthLabel(false);
eflg=true;
continue;
}
else if(cmd.equals("TOGGLE LOCK"))
{
if(auth_flg)
{
disconnectServer(true);
serv_flg=false;
auth_flg=false;
ijf.osd.indicateServerLabel(false);
ijf.osd.indicateAuthLabel(false);
delay(500);
}
ijf.osd.indicateLockButton(!ijf.osd.btn_flg);
}
else if(cmd.equals("RECONNECT TO SERVER"))
{
serv_flg=false;
auth_flg=false;
ijf.osd.indicateServerLabel(false);
ijf.osd.indicateAuthLabel(false);
}
}
if(!serv_flg && (System.currentTimeMillis()-snp_serv)>=time_out)
{
snp_serv=System.currentTimeMillis();
if(connectServer())
{
serv_flg=true;
rcflg=true;
ijf.osd.indicateServerLabel(true);
}
}
if(!auth_flg && (System.currentTimeMillis()-snp_auth)>=time_out)
{
snp_auth=System.currentTimeMillis();
try
{
String id=ijf.osd.device_id.getText();
if(id.length()>=4)
{
authenticate(id);
continue;
}
}
catch(Exception ee)
{
}
disconnectServer(true);
delay(500);
serv_flg=false;
auth_flg=false;
eflg=true;
ijf.osd.indicateServerLabel(false);
ijf.osd.indicateAuthLabel(false);
}
}
catch(Exception e)
{
}
}
}
}
;
WSClientCmdProcess(String a)
{
ijf.osd.connect.addActionListener(new ActionListener()
{
public void actionPerformed(ActionEvent e)
{
try
{
cmd_sq.put("RESUME");
}
catch(Exception ee)
{
}
}
}
);
ijf.osd.disconnect.addActionListener(new ActionListener()
{
public void actionPerformed(ActionEvent e)
{
try
{
cmd_sq.put("PAUSE");
}
catch(Exception ee)
{
}
}
}
);
ijf.osd.toggle_lock.addActionListener(new ActionListener()
{
public void actionPerformed(ActionEvent e)
{
try
{
cmd_sq.put("TOGGLE LOCK");
}
catch(Exception ee)
{
}
}
}
);
container = ContainerProvider.getWebSocketContainer();
w_url=a;
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
void disconnectServer(boolean a)
{
if(a)
{
rcflg=false;
}
try
{
server.close();
}
catch(Exception e)
{
}
}
void authenticate(String a)
{
sendMessage("D:CONNECT:"+(ijf.osd.btn_flg?'0':'1')+a);
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
if(!rcflg)
{
return;
}
try
{
cmd_sq.put("RECONNECT TO SERVER");
}
catch(Exception e)
{
}
}
void delay(long a)
{
try
{
Thread.currentThread().sleep(a);
}
catch(Exception e)
{
}
}
}