#define CUSTOM_SETTINGS
#define INCLUDE_INTERNET_SHIELD
#define INCLUDE_GPS_SHIELD
#define INCLUDE_VOICE_RECOGNIZER_SHIELD
#define INCLUDE_TERMINAL_SHIELD
#define INCLUDE_TEXT_TO_SPEECH_SHIELD
 
#include <OneSheeld.h>
//Code to put longitude and latitude to firebase
HttpRequest myRequest1("https://rd-year-group-project.firebaseio.com/latitude.json");
HttpRequest myRequest2("https://rd-year-group-project.firebaseio.com/longitude.json");

//Variables
char myBuffer[10];
int lightLedPin = 13;
int heatLedPin = 8;
int heatGndPin = 9;
const char lightsOnCommand[] = "lights on";
const char lightsOffCommand[] = "lights off";
const char heatingOnCommand[] = "heating on";
const char heatingOffCommand[] = "heating off";
 
void setup() {
  // put your setup code here, to run once:
  OneSheeld.begin();

  //Set the pins as OUTPUT
  pinMode(lightLedPin,OUTPUT);
  pinMode(heatLedPin,OUTPUT);
  pinMode(heatGndPin,OUTPUT);

  //Error handling for voice recognition
  VoiceRecognition.setOnError(error);
  
  VoiceRecognition.start();
}
 
void loop() {
  // put your main code here, to run repeatedly
  
  //Voice Recognition Section
  if(VoiceRecognition.isNewCommandReceived()) {
    //makes pin 9 act as a GND pin
    digitalWrite(heatGndPin,LOW);
    if(!strcmp(lightsOnCommand,VoiceRecognition.getLastCommand())) {
      /* Turn on the LED. */
      digitalWrite(lightLedPin,HIGH);
      TextToSpeech.say("Lights are on");
    } else if (!strcmp(lightsOffCommand,VoiceRecognition.getLastCommand())) {
      /* Turn off the LED. */
      digitalWrite(lightLedPin,LOW);
      TextToSpeech.say("Lights are off");
    }

    if(!strcmp(heatingOnCommand,VoiceRecognition.getLastCommand())) {
      digitalWrite(heatLedPin,HIGH);
      TextToSpeech.say("Heating is on");
    } else if (!strcmp(heatingOffCommand,VoiceRecognition.getLastCommand())) {
      digitalWrite(heatLedPin,LOW);
      TextToSpeech.say("Heating is off");
    }
  }
  
  //GPS Section
  float latitude = GPS.getLatitude();
  float longitude = GPS.getLongitude();
  myRequest1.addRawData(itoa(latitude,myBuffer,10));
  myRequest2.addRawData(itoa(longitude,myBuffer,10));
  Internet.performPut(myRequest1);
  Internet.performPut(myRequest2);
  //Causes bug where led won't light up until delay is over
  //OneSheeld.delay(5000);
  
}

/* Error checking function. */
void error(byte errorData)
{
  /* Switch on error and print it on the terminal. */
  switch(errorData)
  {
    case NETWORK_TIMEOUT_ERROR: Terminal.println("Network timeout");break;
    case NETWORK_ERROR: Terminal.println("Network Error");break;
    case AUDIO_ERROR: Terminal.println("Audio error");break;
    case SERVER_ERROR: Terminal.println("No Server");break;
    case SPEECH_TIMEOUT_ERROR: Terminal.println("Speech timeout");break;
    case NO_MATCH_ERROR: Terminal.println("No match");break;
    case RECOGNIZER_BUSY_ERROR: Terminal.println("Busy");break;
  }
}
