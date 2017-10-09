#define CUSTOM_SETTINGS
#define INCLUDE_INTERNET_SHIELD
#define INCLUDE_GPS_SHIELD
 
#include <OneSheeld.h>
HttpRequest myRequest1("https://rd-year-group-project.firebaseio.com/latitude.json");
HttpRequest myRequest2("https://rd-year-group-project.firebaseio.com/longitude.json");
char myBuffer[10];
 
void setup() {
  // put your setup code here, to run once:
  OneSheeld.begin();
}
 
void loop() {
  // put your main code here, to run repeatedly:
  float latitude = GPS.getLatitude();
  float longitude = GPS.getLongitude();
  myRequest1.addRawData(itoa(latitude,myBuffer,10));
  myRequest2.addRawData(itoa(longitude,myBuffer,10));
  Internet.performPut(myRequest1);
  Internet.performPut(myRequest2);
  OneSheeld.delay(60000*5);
}
