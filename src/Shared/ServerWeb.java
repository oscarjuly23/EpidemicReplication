package Shared;

public class ServerWeb implements ReadCallback{
    private Server serverWeb;
    private ServerWebCallback callback;

    public ServerWeb(int webPort, ServerWebCallback callback) {
        this.serverWeb = new Server(webPort, this);
        this.callback = callback;
    }

    private void send() {
        this.serverWeb.write(String.valueOf(callback.getWebValue()));
    }

    public void update() {
        send();
    }

    @Override
    public void read(String msg) {
        send();
    }
}
