import java.time.*;
class TableTuple
{
String id;
String ldt;
String lock;
TableTuple(String a,char b)
{
id=a;
char arr[] = LocalDateTime.now().toString().substring(0,19).toCharArray();
arr[10]=' ';
ldt=new String(arr);
lock=(b=='1')?"LOCKED":"UNLOCKED";
}
Object[] retrieveTuple()
{
return new Object[]{id,ldt,lock};
}
}