# sender.py
# Sends a file over UDP
# Adapted from: https://wiki.python.org/moin/UdpCommunication
#
# Mark Wissink (mcw33) and Theron Tjapkes (tpt3)

import socket
import argparse
import sys
import random
import os
import packets

parser = argparse.ArgumentParser(description="A prattle client")

parser.add_argument("-f", "--filename", dest="filename",
                    help="The file to send")
parser.add_argument("-a", "--address", dest="address", default="127.0.0.1",
                    help="IP address (default: 127.0.0.1)")
parser.add_argument("-p", "--port", dest="port", type=int, default=22222,
                    help="TCP port the server is listening on (default 22222)")
parser.add_argument("-v", "--verbose", action="store_true", dest="verbose",
                    help="turn verbose output on")
args = parser.parse_args()
addr = (args.address, args.port)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.settimeout(1)
connection_id = random.getrandbits(32)
total_bytes = os.path.getsize(args.filename)
last_successful = -1
packet_number = 0
next_ack = 0
ack_gap = 0

print("Sending %s to %s:%s" % (args.filename, args.address, args.port))
with open(args.filename, 'rb') as f:
    while True:
        # Send the data we read from the file
        read_data = f.read(packets.DataPacket.DATA_SIZE)
        ack = 0
        if packet_number == next_ack:
            ack = 1
        data_packet = packets.DataPacket(
            connection_id, total_bytes, packet_number, ack, read_data)
        if args.verbose: print(data_packet.header_as_string())
        sock.sendto(data_packet.as_bytes(), addr)
        # Receive an ACK from the receiver
        if ack:
            if args.verbose: print("ACKING PACKET %d" % packet_number)
            try:
                recv_data, addr = sock.recvfrom(packets.ACKPacket.get_size())
                ack_packet = packets.ACKPacket.parse_bytes(recv_data)
                if packet_number == ack_packet.get_number():
                    if args.verbose: print("ACK SUCCESSFUL")
                    last_successful = packet_number
                    ack_gap += 1
                    next_ack += ack_gap
                    if args.verbose: print("Next ACK: %d"%next_ack)
                else:
                    if args.verbose: print("ACK RECIEVED FOR PACKET: %d"%ack_packet.get_number())
            except socket.timeout:
                if args.verbose: print("FAILED ACK")
                packet_number = last_successful + 1
                f.seek(packet_number * packets.DataPacket.DATA_SIZE)
                ack_gap = 0
                next_ack = packet_number
                continue

        if not read_data:  # EOF
            if args.verbose: print("end of file")
            break
        packet_number += 1
        #if packet_number == next_ack:


    f.close()
