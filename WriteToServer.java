package Client;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public record WriteToServer() implements Runnable {

    @Override
    public void run() {

        Client.ui.setSendBtnActionListener(this::sendButtonActionPerformed);

    }

    private void sendButtonActionPerformed(ActionEvent evt) {
        try {
            String str = Client.ui.getInputTextAndClear();
            if (!str.trim().isEmpty()) {
                Client.outputStream.writeUTF(Client.name + " -> " + str.trim());
            }
        } catch (IOException e) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
