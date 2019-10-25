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
    try:
        s.connect((args.server, args.port))
        username = args.name if args.name else socket.gethostname()
        s.settimeout(2)
        while True:
            readable, writable, exceptional = select.select([s, sys.stdin], [], [s, sys.stdin])
            for r in readable:
                if r == s:
                    try:
                        data = r.recv(2048).decode("utf-8")
                        if not data:
                            print("The server died")
                            exit()
                        print(data)
                    except:
                        print("Something went wrong")
                        exit()
                else:
                    try:
                        s.send((username + ": " + r.readline().rstrip()).encode())
                    except:
                        print("An error has occured")
                        exit()
    except:
        print("Couldn't connect")
