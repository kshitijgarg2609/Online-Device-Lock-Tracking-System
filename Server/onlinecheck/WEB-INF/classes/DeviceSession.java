import javax.websocket.*;
class DeviceSession
{
Session dev;
String id;
String cmd;
DeviceSession(Session a,String b,String c)
{
dev=a;
id=b;
cmd=c;
}
}