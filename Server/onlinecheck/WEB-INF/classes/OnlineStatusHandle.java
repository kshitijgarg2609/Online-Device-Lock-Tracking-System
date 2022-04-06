import java.util.*;
import java.util.concurrent.*;
import javax.websocket.*;
class OnlineStatusHandle
{
private Set<Session> admins = new HashSet<>();
private Map<Session,String> devices = new HashMap<>();
private SynchronousQueue<DeviceSession> admin_sq = new SynchronousQueue<>(true);
private SynchronousQueue<DeviceSession> admin_req = new SynchronousQueue<>(true);
private SynchronousQueue<DeviceSession> device_sq = new SynchronousQueue<>(true);
private Thread loop = new Thread()
{
public void run()
{
while(true)
{
try
{
DeviceSession admin_ds = admin_sq.poll();
if(admin_ds!=null)
{
if(admin_ds.cmd.equals("push"))
{
admins.add(admin_ds.dev);
}
else if(admin_ds.cmd.equals("pop"))
{
admins.remove(admin_ds.dev);
}
}
DeviceSession device_ds = device_sq.poll();
if(device_ds!=null)
{
if(device_ds.cmd.equals("push"))
{
devices.put(device_ds.dev,device_ds.id);
for(Session a : admins)
{
try
{
a.getBasicRemote().sendText("T:A"+device_ds.id);
}
catch(Exception ee)
{
}
}
}
else if(device_ds.cmd.equals("pop") && devices.containsKey(device_ds.dev))
{
String ids=devices.get(device_ds.dev);
devices.remove(device_ds.dev);
for(Session a : admins)
{
try
{
a.getBasicRemote().sendText("T:D"+ids);
}
catch(Exception ee)
{
}
}
}
}
DeviceSession admin_cmd=admin_req.poll();
if(admin_cmd!=null && admins.contains(admin_cmd.dev) && admin_cmd.cmd.equals("refresh"))
{
refreshAdminTable(admin_cmd.dev);
}
}
catch(Exception e)
{
}
}
}
}
;
OnlineStatusHandle()
{
loop.start();
}
void refreshAdminTable(Session sess)
{
try
{
for(String od : devices.values())
{
sess.getBasicRemote().sendText("T:A"+od);
}
}
catch(Exception ee)
{
}
}
void cmdAdmin(DeviceSession ds)
{
try
{
admin_sq.put(ds);
}
catch(Exception e)
{
}
}
void msgAdmin(DeviceSession ds)
{
try
{
admin_req.put(ds);
}
catch(Exception e)
{
}
}
void cmdDevice(DeviceSession ds)
{
try
{
device_sq.put(ds);
}
catch(Exception e)
{
}
}
}