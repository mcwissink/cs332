# receiver.py
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
connection_id = 0
total_bytes = 0
packet_number = 0

with open(args.out, 'wb') as f:
    while True:
        # Receive data from the sender
        data, addr = sock.recvfrom(packets.DataPacket.get_size())
        data_packet = packets.DataPacket.parse_bytes(data)
        if connection_id == 0 and data_packet.get_number() == 0: # Set the connection ID
            connection_id = data_packet.get_connection_id()
            total_bytes = data_packet.get_total_bytes()

        if args.verbose: print(data_packet.header_as_string())
        if connection_id == data_packet.get_connection_id(): # Ignore packets not matching connection ID
            # Send an ACK
            if data_packet.get_ack() and data_packet.get_number() <= packet_number: # Don't ACK packets we don't have
                if args.verbose: print("ACKING PACKET %d" % data_packet.get_number())
                ack_packet = packets.ACKPacket(data_packet.get_connection_id(), data_packet.get_number())
                sock.sendto(ack_packet.as_bytes(), addr)
            # Write the data to the file
            if data_packet.get_number() == packet_number:
                if args.verbose: print("RECIEVED PACKET %d"%data_packet.get_number())
                f.write(data_packet.get_data())
                total_bytes -= len(data_packet.get_data())
                if total_bytes <= 0:
                    print("DONE")
                    break
                packet_number += 1
            else:
                if args.verbose: print("waiting for packet_number %d received number %d"%(packet_number, data_packet.get_number()))
            # Check if we have received the whole file
    f.close()
