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
import packets  # our packet class

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

# setup the connection
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.settimeout(1)
connection_id = random.getrandbits(32)
total_bytes = os.path.getsize(args.filename)

# variables
last_successful = -1  # last packet that was ACKed successfully
packet_number = 0  # current packet to be sent
next_ack = 0  # next packet to be ACKed
ack_gap = 0  # gap between ACKed packets

print("Sending %s to %s:%s" % (args.filename, args.address, args.port))
with open(args.filename, 'rb') as f:
    while True:
        # Send the data we read from the file
        read_data = f.read(packets.DataPacket.DATA_SIZE)
        ack = 1 if packet_number == next_ack else 0

        data_packet = packets.DataPacket(
            connection_id, total_bytes, packet_number, ack, read_data)
        if args.verbose:
            print(data_packet.header_as_string())
        sock.sendto(data_packet.as_bytes(), addr)  # send the packet
        # Receive an ACK from the receiver
        if data_packet.get_ack():
            if args.verbose:
                print("ACKING PACKET %d" % packet_number)
            try:
                success = False
                while not success:  # Wait for successful ACK
                    recv_data, addr = sock.recvfrom(
                        packets.ACKPacket.get_size())
                    ack_packet = packets.ACKPacket.parse_bytes(recv_data)
                    if packet_number == ack_packet.get_number() and connection_id == ack_packet.get_connection_id():
                        if args.verbose:
                            print("ACK SUCCESSFUL")
                        last_successful = packet_number
                        ack_gap += 1
                        next_ack += ack_gap
                        if args.verbose:
                            print("Next ACK: %d" % next_ack)
                        success = True
                    else:
                        if args.verbose:
                            print("ACK RECIEVED FOR PACKET: %d WITH CONNECTION ID: %d" % (
                                ack_packet.get_number(), ack_packet.get_connection_id()))
            except socket.timeout:  # ACK Failed
                if args.verbose:
                    print("FAILED ACK")
                packet_number = last_successful + 1
                # Seek to last successful packet
                f.seek(packet_number * packets.DataPacket.DATA_SIZE)
                ack_gap = 0
                next_ack = packet_number
                # Only occurs when data field of packet empty (last packet)
                if data_packet.get_ack() != ack and not data_packet.get_data():
                    print("Failed ACK for last packet")
                    for i in range(0, 5):
                        try:
                            recv_data, addr = sock.recvfrom(
                                packets.ACKPacket.get_size())
                            ack_packet = packets.ACKPacket.parse_bytes(
                                recv_data)
                            if packet_number == ack_packet.get_number() and connection_id == ack_packet.get_connection_id():
                                if args.verbose:
                                    print("ACK SUCCESSFUL")
                        except:
                            if args.verbose:
                                print("Failed to ack final packet %d times" %
                                    (i + 1))
                    print("Failed to ACK final packet, receiver probably got it and exited")
                    break
                continue

        if not read_data:  # EOF
            if args.verbose:
                print("end of file")
            break
        packet_number += 1

    f.close()
