#include <Arduino.h>
#include "SHT1x.h"

//#define TESTING

#ifdef  TESTING
#define commPort  Serial      // for testing
#else
#define commPort  Serial1     // for comm. with raspberrypi
#endif

// 
// SHT11 device interface object & current value variables
//
#define SHT11_DATA_PIN    2
#define SHT11_CLK_PIN     3

SHT1x sht11Dev(SHT11_DATA_PIN, SHT11_CLK_PIN);

float tmpValue = 0, hmdValue = 0;

//
// Request commands & command buffer
//
#define MAX_COMMANDS    3

char* reqCmds[MAX_COMMANDS] = {
  "TMP", "HMD", "ACK"
};

char cmdBuf[32];

//
// Low-pass filters for noise removal
//
#define TMP_ALPHA     0.7
#define HMD_ALPHA     0.8

void tmpLPF(float tmp) {
  tmpValue = TMP_ALPHA * tmpValue + (1 - TMP_ALPHA) * tmp;
}

void hmdLPF(float hmd) {
  hmdValue = HMD_ALPHA * hmdValue + (1 - HMD_ALPHA) * hmd;
}

//
// Request command receive function
//
// Return value = 0: no received command
//              > 0: a command is received
//
int getReqCmd(char* cmd) {
  static char recvBuf[32];
  static int i = 0;
  int ch, len = 0;
  
  if (commPort.available()) {
    ch = commPort.read();
    while (ch != -1 && ch != '\n' && i < 32) {
//      Serial.write(ch);
      recvBuf[i++] = ch;
      ch = commPort.read();
    }
   
    if (ch == '\n' && i < 32) {   // command end char.
      recvBuf[i] = '\0';

      // truncate the rear delimiter chars
      i = strlen(recvBuf);
      do {
        ch = recvBuf[--i];
      } while (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n');
      recvBuf[++i] = '\0';
      
      // truncate the front delimiter chars
      i = -1;
      do {
        ch = recvBuf[++i];
      } while (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n');

      // copy received command to cmd string
      strcpy(cmd, recvBuf+i);

      // initialize receive buffer
      i = 0;
    }
    else {
      if (i >= 32)  i = 0;
      *cmd = '\0';
    }
    len = strlen(cmd);
  }
  
  return len;
}

//
// Request command processing function
//
void processReqCmd(char *cmd) {
  int idx = 0;

  for (idx=0; idx<MAX_COMMANDS; idx++) {
    if (strcmp(reqCmds[idx], cmd) == 0)
      break;
  }

  switch (idx) {
  case 0:
    commPort.print("T="); commPort.print(tmpValue); commPort.write('\n');
    break;
  case 1:
    commPort.print("H="); commPort.print(hmdValue); commPort.write('\n');
    break;
  case 2:
    commPort.print("OK"); commPort.write('\n');
    break;
  default:
    commPort.print("ERR"); commPort.write('\n');
    break;
  }
}


void setup() {
  commPort.begin(115200);
  Serial.begin(115200);

  tmpValue = sht11Dev.readTemperatureC();
  hmdValue = sht11Dev.readHumidity();
}


void loop() {
  // update temperature & humidity values
  tmpLPF(sht11Dev.readTemperatureC());
  hmdLPF(sht11Dev.readHumidity());

  // process the received command
  if (getReqCmd(cmdBuf)) {
    processReqCmd(cmdBuf);
  }
}
