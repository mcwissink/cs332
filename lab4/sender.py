# sender.py
# Sends a file over UDP
# Adapted from: https://wiki.python.org/moin/UdpCommunication
#
# Mark Wissink (mcw33) Theron (tjs3)

import socket
import argparse
import sys
import random
import os
import packets

parser = argparse.ArgumentParser(description="A prattle client")

parser.add_argument("-f", "--filename", dest="filename", help="The file to send")
parser.add_argument("-a", "--address", dest="address", default="127.0.0.1",
                    help="IP address (default: 127.0.0.1)")
parser.add_argument("-p", "--port", dest="port", type=int, default=22222,
                    help="TCP port the server is listening on (default 22222)")
parser.add_argument("-v", "--verbose", action="store_true", dest="verbose",
                    help="turn verbose output on")
args = parser.parse_args()
addr = (args.address, args.port)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
connection_id = random.getrandbits(32)
total_bytes = os.path.getsize(args.filename)
packet_number = 0;

print("Sending %s to %s:%s" % (args.filename, args.address, args.port))
with open(args.filename, 'rb') as f:
    while True:
        # Send the data we read from the file
        read_data = f.read(1024)
        rcmp_packet = packets.DataPacket(connection_id, total_bytes, packet_number, read_data)
        print("sending data")
        sock.sendto(rcmp_packet.as_bytes(), addr)
        # Receive an ACK from the receiver
        recv_data, addr = sock.recvfrom(1024)
        # Check if we got an ACK
        if recv_data.decode("utf-8") != "ACK":
            print("Failed to ACK")
            break
        if not read_data: # EOF
            break
        packet_number += 1
    f.close()

