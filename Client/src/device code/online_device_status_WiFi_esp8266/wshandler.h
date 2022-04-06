#include "WiFiModuleEEPROM.h"
#include <ArduinoWebsockets.h>

void monitorActiveState();
void connectToServer();
void disconnectFromServer();

boolean serv_flg=false;
boolean auth_flg=false;
boolean lst=false;

uint64_t serv_snp=0;
uint64_t auth_snp=0;

using namespace websockets;
WebsocketsClient clnt;

void onMessageCallback(WebsocketsMessage message)
{
  if(message.isText()==true)
  {
    String msg=message.data();
    if(msg.equals("S:CONNECTED"))
    {
      auth_flg=true;
      Serial.println("AUTHENTICATED ...");
    }
    else if(msg.equals("S:CANNOT CONNECT"))
    {
      delay(2000);
      disconnectFromServer();
      Serial.println("AUTHENTICATION FAILED ...");
    }
    else if(msg.equals("S:PING"))
    {
      clnt.send("C:PONG");
      Serial.println("PING ...");
    }
  }
}

void onEventsCallback(WebsocketsEvent event, String data)
{
  if(event == WebsocketsEvent::ConnectionOpened)
  {
    serv_flg=true;
    Serial.println("SERVER CONNECTED ...");
  }
  else if(event == WebsocketsEvent::ConnectionClosed)
  {
    serv_flg=false;
    auth_flg=false;
    Serial.println("SERVER DISCONNECTED ...");
  }
}


void monitorActiveState()
{
  if(lst && !stat_actv)
  {
    Serial.println("DISCONNECTING ...");
    disconnectFromServer();
  }
  else if(!lst && stat_actv)
  {
    Serial.println("CONNECTING ...");
    connectToServer();
  }
  clnt.poll();
  if(stat_actv && !serv_flg && (millis()-serv_snp)>=2500)
  {
    serv_snp=millis();
    Serial.println("RECONNECTING ...");
    connectToServer();
  }
  clnt.poll();
  if(stat_actv && serv_flg && !auth_flg && (millis()-auth_snp)>=2500)
  {
    auth_snp=millis();
    String lock_flg_char=(lock_flg)?"1":"0";
    String auth_data="D:CONNECT:"+lock_flg_char+dev_id_str;
    Serial.println("AUTHENTICATING ...");
    clnt.send(auth_data);
  }
  clnt.poll();
  lst=stat_actv;
}

void connectToServer()
{
  clnt.connect(ws_link_str);
  Serial.println(ws_link_str);
  delay(1000);
}

void disconnectFromServer()
{
  clnt.close();
  delay(1000);
}
