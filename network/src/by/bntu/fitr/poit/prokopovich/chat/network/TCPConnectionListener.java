package by.bntu.fitr.poit.prokopovich.chat.network;

public interface TCPConnectionListener {
    void OnConnectionReady(TCPConnection tcpConnection);
    void OnReceiveString(TCPConnection tcpConnection, String value);
    void OnDisconnect(TCPConnection tcpConnection);
    void OnException(TCPConnection tcpConnection, Exception e);
}
