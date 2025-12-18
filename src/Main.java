public class Main{
    public static void main(String[] args) {
        MasterControlBoard masterControlBoard = MasterControlBoard.getInstance();
        SpareMasterControlBoard spareMasterControlBoard = SpareMasterControlBoard.getInstance();
        masterControlBoard.initializeFanExpansionControlBoard();
        masterControlBoard.initializeConcreteBusinessBoard();
        masterControlBoard.scheduledTask();
        Status status = masterControlBoard.menu();
        if(status==Status.FAILURE){
            spareMasterControlBoard.initializeFanExpansionControlBoard();
            spareMasterControlBoard.initializeConcreteBusinessBoard();
            spareMasterControlBoard.scheduledTask();
            masterControlBoard.menu();
        }
    }
}
