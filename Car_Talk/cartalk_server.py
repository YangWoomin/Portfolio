import sys
from bluetooth import *
import time
import json
import urllib, urllib2
import os
import signal
import gpioYang
import signal

def quitWriteProcess(sig, f):
	pid, status = os.wait()

signal.signal(signal.SIGCHLD, quitWriteProcess)

server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("",PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

advertise_service( server_sock, "YangGPIOEmbedded",
                   service_id = uuid,
                   service_classes = [ uuid, SERIAL_PORT_CLASS ],
                   profiles = [ SERIAL_PORT_PROFILE ], 
                    )

try:
	while True:
		try:
			print "Waiting for connection on RFCOMM channel %d" % port
			client_sock, client_info = server_sock.accept()
			print "Accepted connection from ", client_info[0]
			try:
				params = urllib.urlencode({'mac':client_info[0]})
				conn = urllib2.Request('http://218.150.182.12/check.php', params)
				res = urllib2.urlopen(conn)
				id = res.read()
				if res.read() == 'NO':
					print "Unregistered user!"
					client_sock.close()
					continue
			except IOError:
				print "Http connection failed"
				client_sock.close()
				continue
			pid = 0
			while True:
				data = client_sock.recv(1010)
				if len(data) == 0: break
				jdata = json.loads(data)
				if jdata['id'] != id:
					print "Not allowed user!"
					client_sock.close()
					break
				rotate_num = jdata['rotate_num']
				speed = jdata['speed']
				message = jdata['message'] + ' '

				print "-----------------------------------------------------"
				if rotate_num != 9:
					print "Rotate number [ %d ]" % rotate_num
				else:
					print "Rotate number [infinite]"
				print "Speed [ %d ]" % speed
				print "Received [ %s ]" % message
				print "Displaying.."

				try:
					if pid :
						os.kill(pid, signal.SIGKILL)
						pid, status = os.wait()
				except Exception:
					pass
				pid = os.fork()
				if pid == 0:
					try:
						gpioYang.writeMessage(rotate_num, speed, str(message))
					except Exception:
						print "Finish connection"
						
						
		except IOError:
			print "Disconnected : ", client_info[0]
			client_sock.close()
except IOError:
	print "Unknown Exception"

client_sock.close()
server_sock.close()
print "all done"
