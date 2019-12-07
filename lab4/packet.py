#! /usr/local/bin/python3
from ctypes import *


class Packet(LittleEndianStructure):
    _fields_ = [
        ("connection_id", c_uint32),
        ("total_bytes", c_uint32),
        ("packet_number", c_uint32),
        ("packet_data", c_char_p)
    ]


if __name__ == "__main__":
    import random
    import os
    file = open("packet.py")
    data = c_char_p(file.read(1450).encode('utf-8'))
    file.close()
    connection_id = random.getrandbits(32)
    total_bytes = os.path.getsize("packet.py")
    packet_number = 0
    packet = Packet(connection_id, total_bytes, packet_number, data)
    print("connection_id: %i\ntotal_bytes: %i\npacket_number: %i\ndata:%s" %
          (packet.connection_id, packet.total_bytes, packet.packet_number, packet.packet_data.decode('utf-8')))
