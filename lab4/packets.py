FIELDS = {
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
        field_keys = list(FIELDS.keys())
        header = [None] * len(field_keys)
        current_offset = 0
        # For each field in the header, parse the value of the field from the packet
        for field in FIELDS:
            header[field_keys.index(field)] = \
                int.from_bytes(packet[current_offset:FIELDS[field]], byteorder='big')
            current_offset += FIELDS[field]
        return DataPacket(header[0], header[1], header[2], packet[current_offset:])

    def as_bytes(self):
        return self._connection_id.to_bytes(FIELDS["connection_id"], byteorder='big') \
            + self._total_bytes.to_bytes(FIELDS["total_bytes"], byteorder='big') \
            + self._number.to_bytes(FIELDS["packet_number"], byteorder='big') \
            + self._data
