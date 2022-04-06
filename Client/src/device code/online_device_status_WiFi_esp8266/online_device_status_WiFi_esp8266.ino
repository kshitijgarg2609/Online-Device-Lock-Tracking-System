#include "wshandler.h"

void setup()
{
  clnt.onMessage(onMessageCallback);
  clnt.onEvent(onEventsCallback);
  initWiFiModuleEEPROM(true);
  Serial.begin(115200);
  pinMode(D5,INPUT_PULLUP);
  Serial.println("STARTING ...");
}

void loop()
{
  confProcess();
  monitorActiveState();
  monitorLock();
}

void monitorLock()
{
  if(stat_actv && lock_flg && digitalRead(D5)==HIGH)
  {
    Serial.println("LOCK BROKEN ##############################");
    switchLockFlag();
    disconnectFromServer();
  }
}
