import java.util.*;


public class FanControlBoard {
    private int slot;
    private FanSpeed speed;
    private FanBoardModeType mode;

    public FanControlBoard(int slot, FanSpeed speed, FanBoardModeType mode) {
        this.slot = slot;
        this.speed = speed;
        this.mode = mode;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public FanSpeed getSpeed() {
        return speed;
    }

    public void setSpeed(FanSpeed speed) {
        this.speed = speed;
    }

    public FanBoardModeType getMode() {
        return mode;
    }

    public void setMode(FanBoardModeType mode) {
        this.mode = mode;
    }

    public Status manualAdjust(FanSpeed speed){
        if(speed==null){return Status.FAILURE;}
        this.speed=speed;
        return Status.SUCCESS;
    }


}
