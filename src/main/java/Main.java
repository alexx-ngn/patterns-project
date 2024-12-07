import controller.DatabaseController;
import view.*;

import javax.xml.crypto.Data;

public class Main{
    public static void main(String[] args) {
        DatabaseController.createAllTables();
        Y.launch(Y.class, args);
    }
}