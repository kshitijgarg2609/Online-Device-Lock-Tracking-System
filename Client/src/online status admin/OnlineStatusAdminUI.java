import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
class OnlineStatusAdminUI
{
Dimension dim;
static int w,h;
int width=800,height=600;
JFrame jf;
JTable itable;
JScrollPane scrtable;
DefaultTableModel dtm = new DefaultTableModel();
JLabel server_label,auth_label;
boolean serv_flg=true;
boolean auth_flg=true;
OnlineStatusAdminUI()
{
dim=(Toolkit.getDefaultToolkit()).getScreenSize();
w=(int)(dim.getWidth());
h=(int)(dim.getHeight());
jf=new JFrame("ADMIN");
jf.setBounds(((w-width)/2),((h-height)/2),width,height);
jf.setResizable(false);
jf.getContentPane().setLayout(null);
addComponents();
indicateServerLabel(false);
indicateAuthLabel(false);
jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
jf.setVisible(true);
jf.repaint();
jf.revalidate();
}
void addComponents()
{
itable=new JTable(dtm);
scrtable = new JScrollPane(itable);
scrtable.setBounds(30,84,716,453);
scrtable.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
scrtable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
jf.getContentPane().add(scrtable);
server_label = new JLabel("",SwingConstants.CENTER);
server_label.setOpaque(true);
server_label.setBounds(30, 31, 173, 35);
jf.getContentPane().add(server_label);
auth_label = new JLabel("",SwingConstants.CENTER);
auth_label.setOpaque(true);
auth_label.setBounds(225, 31, 173, 35);
jf.getContentPane().add(auth_label);
dtm.addColumn("Date Time");
dtm.addColumn("ID");
dtm.addColumn("Lock");
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
}