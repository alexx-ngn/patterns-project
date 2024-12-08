import controller.DatabaseController;

public class Main{
    public static void main(String[] args) {
        DatabaseController.createAllTables();
        Y.launch(Y.class, args);
    }
}