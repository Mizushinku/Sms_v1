# Sms_v1

## MainActivity


+ Click button "Send_0" ： send sms by the device default sms app.

  ➜  ```sendSmsByDefaultApp(ArrayList<String>)```.

+ Click button "Send_1" ： send sms by the app itself.

  ➜  `sendSmsBySelf(ArrayList<String>, String)`{:.class}  ➜  for each receiver  ➜  ```sendSmsToOne(String, String)```.
  
  
+ ```checkSmsPermission()```： Check permissions every time when app launch or call ```sendSmsBySelf(ArrayList<String>, String)```.

+ ```onRequestPermissionsResult(...)``` Callback function of ```requestPermissions(...)```.


## SmsReceiver

+ ```onReceive(...)``` triggered when receiving a sms message.

+ ```messages[i].getMessageBody()```： get the "text message part". 
