DATA_FIELDS = {
    "connection_id": 4,
    "total_bytes": 4,
    "packet_number": 4
}
class DataPacket:
    def __init__(self, connection_id, total_bytes, number, data):
        self._connection_id = connection_id
        self._total_bytes = total_bytes
        self._number = number
        self._data = data

    def get_connection_id(self):
        return self._connection_id

    def get_total_bytes(self):
        return self._total_bytes

    def get_number(self):
        return self._number

    def get_data(self):
        return self._data

    @staticmethod
    def parse_bytes(packet):
        field_keys = list(DATA_FIELDS.keys())
        header = [None] * len(field_keys)
        offset = 0
        # For each field in the header, parse the value of the field from the packet
        for field in DATA_FIELDS:
            header[field_keys.index(field)] = \
                int.from_bytes(packet[offset:offset + DATA_FIELDS[field]], byteorder='big')
            offset += DATA_FIELDS[field]
        return DataPacket(header[0], header[1], header[2], packet[offset:])

    def as_bytes(self):
        return self._connection_id.to_bytes(DATA_FIELDS["connection_id"], byteorder='big') \
            + self._total_bytes.to_bytes(DATA_FIELDS["total_bytes"], byteorder='big') \
            + self._number.to_bytes(DATA_FIELDS["packet_number"], byteorder='big') \
            + self._data

ACK_FIELDS = {
    "connection_id": 4,
    "packet_number": 4
}
class ACKPacket:
    def __init__(self, connection_id, number):
        self._connection_id = connection_id
        self._number = number

    def get_connection_id(self):
        return self._connection_id

    def get_number(self):
        return self._number

    @staticmethod
    def parse_bytes(packet):
        field_keys = list(ACK_FIELDS.keys())
        header = [None] * len(field_keys)
        offset = 0
        # For each field in the header, parse the value of the field from the packet
        for field in ACK_FIELDS:
            header[field_keys.index(field)] = \
                int.from_bytes(packet[offset:offset + ACK_FIELDS[field]], byteorder='big')
            offset += ACK_FIELDS[field]
        return ACKPacket(header[0], header[1])

    def as_bytes(self):
        return self._connection_id.to_bytes(ACK_FIELDS["connection_id"], byteorder='big') \
            + self._number.to_bytes(ACK_FIELDS["packet_number"], byteorder='big')
