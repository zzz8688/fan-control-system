public class FanExpansionControlBoard extends FanControlBoard{

    public FanExpansionControlBoard(int slot, FanSpeed speed, FanBoardModeType mode) {
        super(slot, speed, mode);
    }



    public Status changeMode(FanBoardModeType mode){
        if(mode==null){return Status.FAILURE;}
        setMode(mode);
        return Status.SUCCESS;
    }

    public Status automaticAdjust(FanSpeed speed){
        if(speed==null){return Status.FAILURE;}
        setSpeed(speed);
        return Status.SUCCESS;
    }

}
