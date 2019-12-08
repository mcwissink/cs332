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

    def as_bytes(self):
        return self._connection_id.to_bytes(4, byteorder='big') \
            + self._total_bytes.to_bytes(4, byteorder='big') \
            + self._number.to_bytes(4, byteorder='big') \
            + self._data