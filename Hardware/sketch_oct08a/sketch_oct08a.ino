#define CUSTOM_SETTINGS
#define INCLUDE_INTERNET_SHIELD
#define INCLUDE_GPS_SHIELD
#define INCLUDE_VOICE_RECOGNIZER_SHIELD
#define INCLUDE_TERMINAL_SHIELD
#define INCLUDE_TEXT_TO_SPEECH_SHIELD
#define MAX 100
 
#include <OneSheeld.h>
#include <time.h>
//Code to put longitude and latitude to firebase
HttpRequest myRequest1("https://rd-year-group-project.firebaseio.com/latitude.json");
HttpRequest myRequest2("https://rd-year-group-project.firebaseio.com/longitude.json");

//Variables

int lightLedPin = 13;
int heatLedPin = 8;
int heatGndPin = 9;
const char lightsOnCommand[] = "lights on";
const char lightsOffCommand[] = "lights off";
const char heatingOnCommand[] = "heating on";
const char heatingOffCommand[] = "heating off";
char gameEasy[] = "game on";
int r;
boolean alreadyEntered = false;
char animals[4][20] = {"cow", "sheep", "horse", "chicken"};
int alreadyDone[3];

void easy(){
  //Game Section
  srand(time(NULL));
        char answer[MAX];
        int i = 0;
        int j = 0;
        if(!alreadyEntered){
          r = rand() % 4;
          strcpy(gameEasy, animals[r]);
          TextToSpeech.say(gameEasy);
          alreadyDone[0] = r;
          delay(2000);
          alreadyEntered = true;
        }
        
            if(!strcmp(gameEasy,VoiceRecognition.getLastCommand())) {
              for(i = 1; i < 4; i++)
              {
                r = rand() % 4;
                for(j = 0; j < i; j++)
                {
                    if(r == alreadyDone[j])
                    {
                        j = -1;
                        r = rand() % 4;
                    }
                }
                alreadyDone[i] = r;
                strcat(gameEasy, " ");
                strcat(gameEasy, animals[r]);
                strcat(gameEasy, "\0");
                Terminal.println(gameEasy);
                TextToSpeech.say(gameEasy);
                delay(2000);

                      if(!strcmp(gameEasy,VoiceRecognition.getLastCommand())) { 
                            TextToSpeech.say("Good job you passed level the level");
                            delay(2000);
                            break;
                      }
            } 
         }
}
 
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

    if(!strcmp(gameEasy,VoiceRecognition.getLastCommand())) {
        easy();
    } 

  }
  
  //GPS Section
  char myBuffer[15];
  float latitude = GPS.getLatitude();
  float longitude = GPS.getLongitude();
  dtostrf(latitude, 13, 7, myBuffer);
  myRequest1.addRawData(myBuffer);
  dtostrf(longitude, 13, 7, myBuffer);
  myRequest2.addRawData(myBuffer);
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
