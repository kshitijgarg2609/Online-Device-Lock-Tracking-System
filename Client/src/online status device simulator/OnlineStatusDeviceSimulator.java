import java.awt.*;
import javax.swing.*;
class OnlineStatusDeviceSimulator
{
Dimension dim;
static int w,h;
int width=400,height=200;
JFrame jf;

JTextField device_id;
JLabel server_label,auth_label;
JButton toggle_lock,connect,disconnect;

boolean serv_flg=true;
boolean auth_flg=true;
boolean btn_flg=true;

OnlineStatusDeviceSimulator()
{
dim=(Toolkit.getDefaultToolkit()).getScreenSize();
w=(int)(dim.getWidth());
h=(int)(dim.getHeight());
jf=new JFrame("Device Simulator");
jf.setBounds(((w-width)/2),((h-height)/2),width,height);
jf.setResizable(false);
jf.getContentPane().setLayout(null);
addComponents();
indicateServerLabel(false);
indicateAuthLabel(false);
indicateLockButton(false);
jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
jf.setVisible(true);
jf.repaint();
jf.revalidate();
}

void addComponents()
{
device_id = new JTextField();
device_id.setBounds(139, 11, 133, 20);
jf.getContentPane().add(device_id);
device_id.setColumns(10);
JLabel device_id_lbl = new JLabel("Device ID : ",SwingConstants.CENTER);
device_id_lbl.setBounds(40, 14, 89, 14);
jf.getContentPane().add(device_id_lbl);
toggle_lock = new JButton("");
toggle_lock.setBounds(282, 10, 89, 23);
jf.getContentPane().add(toggle_lock);
server_label = new JLabel("",SwingConstants.CENTER);
server_label.setOpaque(true);
server_label.setBounds(97, 42, 176, 22);
jf.getContentPane().add(server_label);
auth_label = new JLabel("",SwingConstants.CENTER);
auth_label.setOpaque(true);
auth_label.setBounds(96, 75, 176, 23);
jf.getContentPane().add(auth_label);
connect = new JButton("Connect");
connect.setBounds(55, 118, 118, 23);
jf.getContentPane().add(connect);
disconnect = new JButton("Disconnect");
disconnect.setBounds(183, 118, 126, 23);
jf.getContentPane().add(disconnect);
}

void indicateServerLabel(boolean a)
{
if(serv_flg==a)
{
return;
}
serv_flg=a;
server_label.setText("Server : "+(serv_flg?"Connected":"Disconnected"));
server_label.setBackground(serv_flg?Color.GREEN:Color.MAGENTA);
}
void indicateAuthLabel(boolean a)
{
if(auth_flg==a)
{
return;
}
auth_flg=a;
auth_label.setText("Authenticated : "+(serv_flg?"YES":"NO"));
auth_label.setBackground(auth_flg?Color.GREEN:Color.MAGENTA);
}
void indicateLockButton(boolean a)
{
if(btn_flg==a)
{
return;
}
btn_flg=a;
toggle_lock.setText(btn_flg?"Lock":"Unlock");
}
}