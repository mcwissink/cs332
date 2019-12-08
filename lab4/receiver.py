# sender.py
# Receives a file over UDP
# Adapted from: https://wiki.python.org/moin/UdpCommunication
#
# Mark Wissink (mcw33) Theron (tjs3)

import socket
import argparse
import sys

parser = argparse.ArgumentParser(description="A prattle client")

parser.add_argument("-o", "--out", dest="out", default="output",
                    help="The name of the file to write to (default output)")
parser.add_argument("-p", "--port", dest="port", type=int, default=22222,
                    help="TCP port the server is listening on (default 22222)")
parser.add_argument("-v", "--verbose", action="store_true", dest="verbose",
                    help="turn verbose output on")
args = parser.parse_args()

address = "127.0.0.1"

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.bind((address, args.port))
print("Listening on port %s:%s" % (address, args.port))

with open(args.out, 'wb') as f:
    while True:
        # Receive data from the sender
        data, addr = sock.recvfrom(1028)
        write_data = data[4:]
        # Send an ACK
        sock.sendto("ACK".encode(), addr)
        if not write_data: # EOF
            break
        # Write the data to the file
        f.write(write_data)
    f.close()
