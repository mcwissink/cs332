# sender.py
# Receives a file over UDP
# Adapted from: https://wiki.python.org/moin/UdpCommunication
#
# Mark Wissink (mcw33) and Theron Tjapkes (tpt3)

import socket
import argparse
import sys
import packets

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
        data, addr = sock.recvfrom(packets.DataPacket.get_size())
        data_packet = packets.DataPacket.parse_bytes(data)
        # Send an ACK
        if data_packet.get_ack():
            #print("sending ack for packet %d" % data_packet.get_number())
            ack_packet = packets.ACKPacket(data_packet.get_connection_id(), data_packet.get_number())
            sock.sendto(ack_packet.as_bytes(), addr)
        if not data_packet.get_data(): # EOF
            break
        # Write the data to the file
        f.write(data_packet.get_data())
    f.close()
