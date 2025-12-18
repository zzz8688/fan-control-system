public class ConcreteBusinessBoard extends BusinessBoard {

    public ConcreteBusinessBoard(int slot, int temperature) {
        super(slot, temperature);
    }

    @Override
    public void temperatureWarn() {
        double temperature=getTemperature();
        if(temperature>75){
            this.notifyObserver();
        }
    }
}
