import java.util.*;
class TableManagement
{
void removeRows()
{
for(int i=InstanceJFrame.osa.dtm.getRowCount()-1;i>=0;i--)
{
InstanceJFrame.osa.dtm.removeRow(i);
}
}
void processLogs(String a)
{
try
{
processLogs(a.charAt(0),a.charAt(1),a.substring(2));
}
catch(Exception e)
{
}
}
void processLogs(char c,char l,String id)
{
try
{
int i=searchIdInTable(id);
if(c=='A')
{
if(i==-1)
{
InstanceJFrame.osa.dtm.addRow((new TableTuple(id,l)).retrieveTuple());
}
else
{
processLogs('D',l,id);
processLogs('A',l,id);
}
}
else if(c=='D')
{
if(i!=-1)
{
InstanceJFrame.osa.dtm.removeRow(i);
}
}
}
catch(Exception e)
{
}
}
int searchIdInTable(String id)
{
for(int i=0;i<InstanceJFrame.osa.dtm.getRowCount();i++)
{
String r_id=(String)InstanceJFrame.osa.dtm.getValueAt(i,0);
if(r_id.equals(id))
{
return i;
}
}
return -1;
}
}