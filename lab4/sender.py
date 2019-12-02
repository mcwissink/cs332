# sender.py
# Sends a file over UDP
# Adapted from: https://wiki.python.org/moin/UdpCommunication
#
# Mark Wissink (mcw33) Theron (tjs3)

import socket
import argparse
import sys

parser = argparse.ArgumentParser(description="A prattle client")

parser.add_argument("-a", "--address", dest="address", default="127.0.0.1",
                    help="IP address (default: 127.0.0.1)")
parser.add_argument("-p", "--port", dest="port", type=int, default=22222,
                    help="TCP port the server is listening on (default 22222)")
parser.add_argument("-v", "--verbose", action="store_true", dest="verbose",
                    help="turn verbose output on")
args = parser.parse_args()

data = "Hello, World!\n"
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.sendto(data.encode(), (args.address, args.port))
