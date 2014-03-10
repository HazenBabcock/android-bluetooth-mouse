#!/usr/bin/python
#
# Convert bluetooth messages into Windows events.
#
# This is based in part on the rfcomm-server.py in the pybluez 
#    project available here: http://code.google.com/p/pybluez/
#
# Hazen 03/14
#

import bluetooth
import time
import win32api, win32con

server_sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
server_sock.bind(("",bluetooth.PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "1a68fa50-a83b-11e3-9be7-425861b86ab6"

bluetooth.advertise_service( server_sock, "AdBtMServer",
                             service_id = uuid,
                             service_classes = [ uuid, bluetooth.SERIAL_PORT_CLASS ],
                             profiles = [ bluetooth.SERIAL_PORT_PROFILE ],
                             )
                   
print("Waiting for connection on RFCOMM channel %d" % port)

client_sock, client_info = server_sock.accept()
print("Accepted connection from ", client_info)

android_x = 0.0
android_y = 0.0
mouse_x = 0
mouse_y = 0
gain = 1000.0
start_time = 0
while True:
    data = client_sock.recv(1024)
    if (len(data) == 0):
        break

    messages = data.split("<>")
    for message in messages:
        if (len(message) > 0):
            message_data = message.split(",")
            if (message_data[0] == "pageup"):
                #win32api.keybd_event(win32con.VK_PRIOR, 0x49, 0, 0)
                #win32api.keybd_event(win32con.VK_PRIOR, 0xC9, win32con.KEYEVENTF_KEYUP, 0)
                win32api.keybd_event(win32con.VK_PRIOR, 0, 0, 0)
                win32api.keybd_event(win32con.VK_PRIOR, 0, win32con.KEYEVENTF_KEYUP, 0)
            elif (message_data[0] == "pagedown"):
                #win32api.keybd_event(win32con.VK_NEXT, 0x51, 0, 0)
                #win32api.keybd_event(win32con.VK_NEXT, 0xD1, win32con.KEYEVENTF_KEYUP, 0)
                win32api.keybd_event(win32con.VK_NEXT, 0, 0, 0)
                win32api.keybd_event(win32con.VK_NEXT, 0, win32con.KEYEVENTF_KEYUP, 0)
            elif (message_data[0] == "actiondown"):
                start_time = time.time()
                android_x = float(message_data[1])
                android_y = float(message_data[2])
                [mouse_x, mouse_y] = win32api.GetCursorPos()
            elif (message_data[0] == "actionmove"):
                dx = float(message_data[1]) - android_x
                dy = float(message_data[2]) - android_y
                win32api.SetCursorPos((mouse_x + int(dx * gain),
                                       mouse_y + int(dy * gain)))
            elif (message_data[0] == "actionup"):
                if ((time.time() - start_time) < 0.2):
                    x = mouse_x + int((float(message_data[1]) - android_x) * gain)
                    y = mouse_y + int((float(message_data[2]) - android_y) * gain)
                    win32api.mouse_event(win32con.MOUSEEVENTF_LEFTDOWN,x,y,0,0)
                    win32api.mouse_event(win32con.MOUSEEVENTF_LEFTUP,x,y,0,0)


print("disconnected")
client_sock.close()
server_sock.close()


