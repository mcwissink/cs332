# TalkClient
# Authors: Mark Wissink and Joshua Wilson
# Date: 10/26/19

import select
import socket
import sys
import argparse
import logging


parser = argparse.ArgumentParser(description="A prattle client")

parser.add_argument("-n", "--name", dest="name", help="name to be prepended in messages (default: machine name)")
parser.add_argument("-s", "--server", dest="server", default="127.0.0.1",
                    help="server hostname or IP address (default: 127.0.0.1)")
parser.add_argument("-p", "--port", dest="port", type=int, default=12345,
                    help="TCP port the server is listening on (default 12345)")
parser.add_argument("-v", "--verbose", action="store_true", dest="verbose",
                    help="turn verbose output on")
args = parser.parse_args()

verbose = args.verbose
server = None
hostname = socket.gethostname()

# Initialize the socket
try:
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    if verbose:
        print("/Initializing socket on %s (%s)" % (hostname, socket.gethostbyname(hostname)))
except:
    print("Failed to initialize socket")
    exit()

# Connect to the server
try:
    server.connect((args.server, args.port))
    if verbose:
        print("/Connected to %s:%s" % server.getpeername())
except:
    print("Failed to connect to server")
    exit()


username = args.name if args.name else hostname
inputs = [server, sys.stdin]
print("Welcome to TalkClient. Type /exit to quit")
while True:
    readable, writable, exceptional = select.select(inputs, [], [])
    for r in readable:
        # Reading from server socket
        if r == server:
            data = None
            try:
                data = r.recv(2048).decode("utf-8")
                if verbose:
                    print("/Received data: '%s'" % data)
            except:
                print("Failed to read data from server")
                exit()
            # No data means the server went down
            if not data:
                print("Server disconnected. Try reconnecting to the server")
                exit()
            print(data)
        # Reading from stdin
        else:
            line = r.readline().rstrip()
            # Handle any commands from the user
            if line[0] == "/":
                command = line[1:]
                if command == "exit":
                    server.close()
                    exit()
            else:
                message = username + ": " + line
                try:
                    if verbose:
                        print("/Sending data: '%s'" % message);
                    server.send(message.encode())
                except:
                    print("Failed to send data to server")
                    exit()
