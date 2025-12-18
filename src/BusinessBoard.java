import java.util.LinkedList;
import java.util.List;

public abstract class BusinessBoard {
    private int slot;
    private double temperature;
    private List<AlarmObserver> alarmObservers;

    public BusinessBoard(int slot, double temperature) {
        this.slot= slot;
        this.temperature=temperature;
        this.alarmObservers = new LinkedList<>();
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public FanSpeed onSrvTempChanged(){
        double temperature=getTemperature();
        if(temperature<=35){
            return FanSpeed.LOW;
        }else if(temperature>35 && temperature<=60){
            return FanSpeed.MEDIUM;
        }else{
            return FanSpeed.HIGH;
        }
    }

    public void addObserver(AlarmObserver obs){
        this.alarmObservers.add(obs);
    }

    public void deleteObserver(AlarmObserver obs){
        this.alarmObservers.remove(obs);
    }

    protected void notifyObserver(){
        for(AlarmObserver o:this.alarmObservers){
            o.alarm(slot);
        }
    }

    public abstract void temperatureWarn();
}
