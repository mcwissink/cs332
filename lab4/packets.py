# packets.py
# Represents various kinds of packets
#
# Mark Wissink (mcw33) and Theron Tjapkes (tpt3)

class DataPacket:
    DATA_SIZE = 1450
    FIELDS = {
        "connection_id": 4,
        "total_bytes": 4,
        "packet_number": 4,
        "ack": 1
    }

    def __init__(self, connection_id, total_bytes, number, ack, data):
        self._connection_id = connection_id
        self._total_bytes = total_bytes
        self._number = number
        self._ack = ack
        self._data = data
   
    @staticmethod
    def get_size():
        size = DataPacket.DATA_SIZE
        for value in DataPacket.FIELDS.values():
            size += value
        return size

    def get_connection_id(self):
        return self._connection_id

    def get_total_bytes(self):
        return self._total_bytes

    def get_number(self):
        return self._number

    def get_ack(self):
        return self._ack

    def get_data(self):
        return self._data
    
    def header_as_string(self):
        return "connection_id: %d, total_bytes: %d, packet_number: %d, ack: %d"%(self.get_connection_id(), self.get_total_bytes(), self.get_number(), self.get_ack())

    @staticmethod
    def parse_bytes(packet):
        field_keys = list(DataPacket.FIELDS.keys())
        header = [None] * len(field_keys)
        offset = 0
        # For each field in the header, parse the value of the field from the packet
        for field in DataPacket.FIELDS:
            header[field_keys.index(field)] = \
                int.from_bytes(
                    packet[offset:offset + DataPacket.FIELDS[field]], byteorder='big')
            offset += DataPacket.FIELDS[field]
        return DataPacket(header[0], header[1], header[2], header[3], packet[offset:])

    def as_bytes(self):
        return self._connection_id.to_bytes(DataPacket.FIELDS["connection_id"], byteorder='big') \
            + self._total_bytes.to_bytes(DataPacket.FIELDS["total_bytes"], byteorder='big') \
            + self._number.to_bytes(DataPacket.FIELDS["packet_number"], byteorder='big') \
            + self._ack.to_bytes(DataPacket.FIELDS["ack"], byteorder='big') \
            + self._data


class ACKPacket:
    FIELDS = {
        "connection_id": 4,
        "packet_number": 4
    }

    def __init__(self, connection_id, number):
        self._connection_id = connection_id
        self._number = number
    
    @staticmethod
    def get_size():
        size = 0
        for value in ACKPacket.FIELDS.values():
            size += value
        return size

    def get_connection_id(self):
        return self._connection_id

    def get_number(self):
        return self._number

    @staticmethod
    def parse_bytes(packet):
        field_keys = list(ACKPacket.FIELDS.keys())
        header = [None] * len(field_keys)
        offset = 0
        # For each field in the header, parse the value of the field from the packet
        for field in ACKPacket.FIELDS:
            header[field_keys.index(field)] = \
                int.from_bytes(
                    packet[offset:offset + ACKPacket.FIELDS[field]], byteorder='big')
            offset += ACKPacket.FIELDS[field]
        return ACKPacket(header[0], header[1])

    def as_bytes(self):
        return self._connection_id.to_bytes(ACKPacket.FIELDS["connection_id"], byteorder='big') \
            + self._number.to_bytes(ACKPacket.FIELDS["packet_number"], byteorder='big')
