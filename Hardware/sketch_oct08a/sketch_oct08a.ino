#define CUSTOM_SETTINGS
#define INCLUDE_INTERNET_SHIELD
#define INCLUDE_GPS_SHIELD
#define INCLUDE_PHONE_SHIELD
#define INCLUDE_VOICE_RECOGNIZER_SHIELD
#define INCLUDE_TERMINAL_SHIELD
#define INCLUDE_TEXT_TO_SPEECH_SHIELD
#define MAX 30
 
#include <OneSheeld.h>
//Code to put longitude and latitude to firebase
HttpRequest myRequest1("https://rd-year-group-project.firebaseio.com/latitude.json");
HttpRequest myRequest2("https://rd-year-group-project.firebaseio.com/longitude.json");
HttpRequest myRequest3("http://api.openweathermap.org/data/2.5/weather?id=2965139&appid=96eda09f98bc52edc930862d49636111");
//HttpRequest myRequest3("https://rd-year-group-project.firebaseio.com/1Sheeld.json");
//HttpRequest myRequest4("https://rd-year-group-project.firebaseio.com/1Sheeld/Game.json");
//HttpRequest lightsRequest("https://rd-year-group-project.firebaseio.com/users/Patients/Patient2/Lights.json");
//HttpRequest heatingRequest("https://rd-year-group-project.firebaseio.com/users/Patients/Patient2.json");

//Variables

int lightLedPin = 13;
int heatLedPin = 8;
int heatGndPin = 9;
int gamePin = 7;
const char lightsOnCommand[] = "lights on";
const char lightsOffCommand[] = "lights off";
const char heatingOnCommand[] = "heating on";
const char heatingOffCommand[] = "heating off";
const char weatherCommand[] = "weather";
char gameOn[MAX] = "game on";
char gameEasy[MAX] = "one";
char gameHard[100] = "two";
int lightOnApp = -1;
int r;
boolean alreadyEntered = false;
boolean gameOnBool = false;
boolean easyMode = false;
boolean hardMode = false;
char animals[4][20] = {"cow", "horse", "pig", "chicken"};
int alreadyDone[3];
int current = -1;
int gameStart = -1;

void onSuccess1(HttpResponse & response1)
{
  /* Using the response to query the Json chain till the required value. */
  /* i.e. Get the value of 'main' in the first object of the array 'weather' in the response. */
  /* Providing that the response is in JSON format. */
       response1["weather"][0]["main"].query();
delay(200);    
}

void onJsonReply1(JsonKeyChain & hell,char * output)
{
  if(hell == myRequest3.getResponse()["weather"][0]["main"])
  {
   TextToSpeech.say("the weather today in cork is");
  delay(2000);
  /* 1Sheeld responds using text-to-speech shield. */
  TextToSpeech.say(output);
    } 
}

void easy(){
  //Game Section
        char answer[MAX];
        int j = 0;
        if(!alreadyEntered){
          r = random(4);
          strcpy(gameEasy, animals[r]);
          TextToSpeech.say(gameEasy);
          alreadyDone[0] = r;
          delay(2000);
          alreadyEntered = true;
        }
        
            if(!strcmp(gameEasy,VoiceRecognition.getLastCommand())) {
                r = random(4);
                for(j = 0; j < current; j++)
                {
                    if(r == alreadyDone[j])
                    {
                        j = -1;
                        r = random(4);
                    }
                }

                if(!strcmp(gameEasy,VoiceRecognition.getLastCommand())) { 
                    char level[30] = "Good job you passed level ";
                    char buffer[4];
                    itoa(current, buffer, 10);
                    strcat(level, buffer);
                    TextToSpeech.say(level);
                    delay(2000);
                 }
                
                alreadyDone[current] = r;
                strcat(gameEasy, " ");
                strcat(gameEasy, animals[r]);
                TextToSpeech.say(gameEasy);
                delay(2000);

         }
}

void hard(){
     char answer[MAX];
     int j = 0;
     if(!alreadyEntered){
         r = random(4);
         strcpy(gameHard, animals[r]);
         TextToSpeech.say(gameHard);
         delay(2000);
         alreadyEntered = true;
     }

      if(!strcmp(gameHard,VoiceRecognition.getLastCommand())) {
         r = random(4);
  
        if(!strcmp(gameHard,VoiceRecognition.getLastCommand())) { 
           char level[30] = "Good job you passed level ";
           char buffer[4];
           itoa(current, buffer, 10);
           strcat(level, buffer);
           TextToSpeech.say(level);
           delay(2000);
        }
        
        strcat(gameHard, " ");
        strcat(gameHard, animals[r]);
        TextToSpeech.say(gameHard);
        delay(2000);
     }
}

void setup() {
  // put your setup code here, to run once:
  OneSheeld.begin();

  //Set the pins as OUTPUT
  pinMode(lightLedPin,OUTPUT);
  pinMode(heatLedPin,OUTPUT);
  pinMode(heatGndPin,OUTPUT); 
  pinMode(gamePin,OUTPUT); 

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
 
void loop() {
  // put your main code here, to run repeatedly

  //turnOnLight();
  //delay(2000);

  //Terminal.println(lightOnApp);
  /*if(lightOnApp == 1){
    //Terminal.println("in on");
    digitalWrite(lightLedPin,HIGH);
  } else {
    //Terminal.println("in off");
    digitalWrite(lightLedPin,LOW);
  }*/
  
  //myRequest3.setOnSuccess(&successFunction);
  //myRequest3.getResponse().setOnJsonResponse(&jsonResponseFunction);
  //Internet.performGet(myRequest3);
 // delay(2000);

 /* if(gameStart == 1){
    VoiceRecognition.start();
    myRequest4.addRawData("0");
    Internet.performPut(myRequest4);
  }*/

  if(digitalRead(gamePin) == HIGH){
    TextToSpeech.say("Entered");
    VoiceRecognition.start();
    digitalWrite(gamePin, LOW);
    delay(10000);
  }

  //Voice Recognition Section
  if(VoiceRecognition.isNewCommandReceived()){
      //makes pin 9 act as a GND pin
      digitalWrite(heatGndPin,LOW);

      if(!gameOnBool){
        if(!strcmp(lightsOnCommand,VoiceRecognition.getLastCommand())) {  
          /* Turn on the LED. */
          digitalWrite(lightLedPin,HIGH);
          TextToSpeech.say("Lights are on");
          delay(2000);
        } else if (!strcmp(lightsOffCommand,VoiceRecognition.getLastCommand())) {
          /* Turn off the LED. */
          digitalWrite(lightLedPin,LOW);
          TextToSpeech.say("Lights are off");
        }
        if(!strcmp(weatherCommand,VoiceRecognition.getLastCommand()))
        {
          Internet.performGet(myRequest3);
           /* Wait for 5 seconds. */
          OneSheeld.delay(5000);
        }

    
        if(!strcmp(heatingOnCommand,VoiceRecognition.getLastCommand())) {
          digitalWrite(heatLedPin,HIGH);
          TextToSpeech.say("Heating is on");
        } else if (!strcmp(heatingOffCommand,VoiceRecognition.getLastCommand())) {
          digitalWrite(heatLedPin,LOW);
          TextToSpeech.say("Heating is off");
        }

        if(!strcmp("call help",VoiceRecognition.getLastCommand())) {
          Phone.call("0873845770");
        }
        if(!strcmp("call four star pizza",VoiceRecognition.getLastCommand())) {
          Phone.call("0214274555");
        }
        if(!strcmp("call domino pizza",VoiceRecognition.getLastCommand())) {
          Phone.call("0214274555");
        }
        if(!strcmp("call lemon tree",VoiceRecognition.getLastCommand())) {
          Phone.call("0214966896");
        }
        if(!strcmp("call jumbo",VoiceRecognition.getLastCommand())) {
          Phone.call("0214395255");
        }
        if(!strcmp("call golden elephant",VoiceRecognition.getLastCommand())) {
          Phone.call("0214895697");
        }
        if(!strcmp("call cork taxi",VoiceRecognition.getLastCommand())) {
          Phone.call("0214272222");
        }
        if(!strcmp("call n r c taxi",VoiceRecognition.getLastCommand())) {
          Phone.call("6772222");
        }
        if(!strcmp("call Galway Taxis",VoiceRecognition.getLastCommand())) {
          Phone.call("061415566");
        }
        if(!strcmp("call treaty cabs",VoiceRecognition.getLastCommand())) {
          Phone.call("018202020");
        }
        if(!strcmp("call link taxi",VoiceRecognition.getLastCommand())) {
          Phone.call("0214496797");
        }
        if(!strcmp("call lidl",VoiceRecognition.getLastCommand())) {
          Phone.call("1800991828");
        }
        if(!strcmp("call tesco",VoiceRecognition.getLastCommand())) {
          Phone.call("0080000225533");
        }
        if(!strcmp("call aldi",VoiceRecognition.getLastCommand())) {
          Phone.call("1890253185");
        }
        if(!strcmp("call dunnes stores",VoiceRecognition.getLastCommand())) {
          Phone.call("1800201080");
        }
        if(!strcmp("call super value",VoiceRecognition.getLastCommand())) {
          Phone.call("1890456828");
        }
        if(!strcmp(gameOn,VoiceRecognition.getLastCommand())){
          TextToSpeech.say("Please choose a difficulty. One or two.");
          delay(2000);
        }
        if(!strcmp(gameEasy,VoiceRecognition.getLastCommand())){
          current++;
          gameOnBool = true;
          easyMode = true;
          easy();
        }
        if(!strcmp(gameHard,VoiceRecognition.getLastCommand())){
          current++;
          gameOnBool = true;
          hardMode = true;
          hard();
        }
        
      } else if(gameOnBool) {
        //Easy Mode
        if(easyMode){
          if(!strcmp(gameEasy,VoiceRecognition.getLastCommand())) {
            current++;
            if(current == 5) {
              TextToSpeech.say("Good job. You won");
              delay(2000);
              strcpy(gameEasy, "one");
              current = -1;
              alreadyEntered = false;
              int i;
              gameOnBool = false;
  
              for(i = 0; i < 3; i++){
                alreadyDone[i] = -1;
              }     
            
            } else {
              easy();
            }
          } else{
            TextToSpeech.say("That was incorrect. You lose.");
            delay(2000);
            strcpy(gameEasy, "one");
            current = -1;
            gameOnBool = false;
            alreadyEntered = false;
            easyMode = false;
            int i;
            for(i = 0; i < 3; i++){
              alreadyDone[i] = -1;
            }    
          }
        }
       //Hard Mode
       if(hardMode){
         if(!strcmp(gameHard,VoiceRecognition.getLastCommand())) {
            current++;
           if(current == 6) {
              TextToSpeech.say("Good job. You won");
              delay(2000);
              strcpy(gameHard, "two");
              current = -1;
              alreadyEntered = false;
              gameOnBool = false;       
            } else {
              hard();
            }
          } else{
            TextToSpeech.say("That was incorrect. You lose.");
            delay(2000);
            strcpy(gameHard, "two");
            current = -1;
            gameOnBool = false;
            alreadyEntered = false; 
            hardMode = false;
          }
        }
      }
  }
  
}

/*void turnOnLight(){
  lightsRequest.setOnSuccess(&findLight);
  lightsRequest.getResponse().setOnJsonResponse(&getValueOfJson);
  lightsRequest.getResponse().setOnError(&onResponseError);
  Internet.performGet(lightsRequest);
}*/

/*void successFunction(HttpResponse & myResponse){
  myResponse["Game"].query();
}

void jsonResponseFunction(JsonKeyChain & key, char * value){
  gameStart = atoi(value);
}*/

/*void findLight(HttpResponse & myResponse){
  myResponse["Lights"].query();
  //The status code is 200 meaning the response was succesful
  Terminal.println(myResponse.getStatusCode());
}

void getValueOfJson(JsonKeyChain & key, char * value){  
  Terminal.println(value);
  //lightOnApp= atoi(value);
  //Terminal.println(lightOnApp);
}*/

void onResponseError(int errorNumber)
{
  /* Print out error Number.*/
  Terminal.print("Response error:");
  switch (errorNumber)
  {
    case INDEX_OUT_OF_BOUNDS: Terminal.println("INDEX_OUT_OF_BOUNDS"); break;
    case RESPONSE_CAN_NOT_BE_FOUND: Terminal.println("RESPONSE_CAN_NOT_BE_FOUND"); break;
    case HEADER_CAN_NOT_BE_FOUND: Terminal.println("HEADER_CAN_NOT_BE_FOUND"); break;
    case NO_ENOUGH_BYTES: Terminal.println("NO_ENOUGH_BYTES"); break;
    case REQUEST_HAS_NO_RESPONSE: Terminal.println("REQUEST_HAS_NO_RESPONSE"); break;
    case SIZE_OF_REQUEST_CAN_NOT_BE_ZERO: Terminal.println("SIZE_OF_REQUEST_CAN_NOT_BE_ZERO"); break;
    case UNSUPPORTED_HTTP_ENTITY: Terminal.println("UNSUPPORTED_HTTP_ENTITY"); break;
    case JSON_KEYCHAIN_IS_WRONG: Terminal.println("JSON_KEYCHAIN_IS_WRONG"); break;
  }
}
