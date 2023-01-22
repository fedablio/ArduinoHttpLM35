#include <SPI.h>
#include <String.h>
#include <Ethernet.h>

byte mac[] = {0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
IPAddress ip(192, 168, 0, 199);
IPAddress gateway(192, 168, 0, 1);
IPAddress subnet(255, 255, 255, 0);
EthernetServer server(8090);
String readString = String(30);
const int sensor = A0;
double medida;

void setup(){
Ethernet.begin(mac, ip, gateway, subnet);
server.begin();
}

void loop(){
EthernetClient client = server.available();
if (client) {
while (client.connected()){
if (client.available()){
char c = client.read();
if (readString.length() < 30){
readString += (c);
}
if (c == '\n'){
client.println("HTTP/1.1 200 OK");
client.println("Content-Type: text/html");
client.println();
if(readString.indexOf("fedablio")>=0){
medida = (float(analogRead(sensor))*5/(1023))/0.01;
String valor =  String(medida, 2);
client.print(valor);
}
readString = "";
client.stop();
}
}
}
}
}
