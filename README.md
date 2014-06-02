## Android Bluetooth Mouse ##
This is an Android client and a Python server that emulates a (simple) bluetooth mouse. In its current form it will only work on Windows and it is geared towards Powerpoint.

Getting started:

1. Install and start the app on your android device. 

2. Start the Python server (adbtm_server.py), either at the DOS prompt or by clicking on it. 

3. Select "connect" from the Android applications drop down menu and pair the device with the computer.

4. Press "Page Up" or "Page Down" to generate these keyboard events.

5. Press & drag in the center area to move the mouse cursor.

6. A Quick tap (< 0.2 seconds) in the center area will generate a mouse click event.

(Note: The application requires Bluetooth, and will probably crash if Bluetooth is not already enabled. Just restart it once Bluetooth is enabled.)

## Installation ##
On the server side you will need Python and the Pywin32 library.

## Directory Layout ##
ADBTM - This folder contains the Android client application (created using Eclipse).

Python - This folder contains the Python server (adbtm_server.py).

## General notes ##
Inspiration for this code comes from these sources:

   1. [Remote-Bluetooth-Android](https://github.com/luugiathuy/Remote-Bluetooth-Android)
   
   2. [Android Documentation](http://developer.android.com/guide/topics/connectivity/bluetooth.html)
   
   3. [pybluez](https://code.google.com/p/pybluez/)
