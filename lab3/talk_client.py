import select
import socket
import sys
import argparse


parser = argparse.ArgumentParser(description="A prattle client")

parser.add_argument("-n", "--name", dest="name", help="name to be prepended in messages (default: machine name)")
parser.add_argument("-s", "--server", dest="server", default="127.0.0.1",
                    help="server hostname or IP address (default: 127.0.0.1)")
parser.add_argument("-p", "--port", dest="port", type=int, default=12345,
                    help="TCP port the server is listening on (default 12345)")
parser.add_argument("-v", "--verbose", action="store_true", dest="verbose",
                    help="turn verbose output on")
args = parser.parse_args()
 
with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect((args.server, args.port))
    while True:
        readable, writable, exceptional = select.select([s, sys.stdin], [], [s])
        for r in readable:
            if r == s:
                print(r.recv(4096).decode("utf-8"))
            else:
                s.send(r.readline().rstrip().encode())
