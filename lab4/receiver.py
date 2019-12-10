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

address = socket.gethostbyname(socket.gethostname())

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.bind((address, args.port))
print("Listening on port %s:%s" % (address, args.port))
packet_number = 0

with open(args.out, 'wb') as f:
    while True:
        # Receive data from the sender
        data, addr = sock.recvfrom(packets.DataPacket.get_size())
        data_packet = packets.DataPacket.parse_bytes(data)
        if args.verbose: print(data_packet.header_as_string())
        # Send an ACK
        if data_packet.get_ack() and data_packet.get_number() <= packet_number:
            if args.verbose: print("ACKING PACKET %d" % data_packet.get_number())
            ack_packet = packets.ACKPacket(data_packet.get_connection_id(), data_packet.get_number())
            sock.sendto(ack_packet.as_bytes(), addr)
        if not data_packet.get_data(): # EOF
            break
        # Write the data to the file
        if data_packet.get_number() == packet_number:
            if args.verbose: print("RECIEVED PACKET %d"%data_packet.get_number())
            f.write(data_packet.get_data())
            packet_number += 1
        else:
            if args.verbose: print("waiting for packet_number %d received number %d"%(packet_number, data_packet.get_number()))
    f.close()
