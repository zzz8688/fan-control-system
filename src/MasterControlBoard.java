import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MasterControlBoard {
    private static MasterControlBoard instance=null;
    private int masterSlot=78;
    private Map<Integer,FanExpansionControlBoard> fanControlBoards = new HashMap<>();
    private Map<Integer, ConcreteBusinessBoard> businessBoards = new HashMap<>();
    private ScheduledExecutorService executor= Executors.newScheduledThreadPool(2);

    private MasterControlBoard(){}

    public static synchronized MasterControlBoard getInstance(){
        if(instance==null){
            instance=new MasterControlBoard();
        }
        return instance;
    }

    //初始化风扇控制板
    public void initializeFanExpansionControlBoard(){
            for(int i=90;i<=91;i++){
                FanExpansionControlBoard fanControlBoard=new FanExpansionControlBoard(i,FanSpeed.LOW,FanBoardModeType.MANUAL);
                fanControlBoards.put(i, fanControlBoard);
            }
    }


    //初始化业务板
    public void initializeConcreteBusinessBoard() {
        for (int i = 1; i <= 14; i++) {
            ConcreteBusinessBoard businessBorder = new ConcreteBusinessBoard(i, 20);
            AlarmObserver observer = new AlarmConcreteObserver();
            businessBorder.addObserver(observer);
            businessBoards.put(i, businessBorder);
        }
    }


    //定时任务，执行自动调速和预警上报
    public void scheduledTask(){
        Runnable task=()->{
            int low=0;
            int medium=0;
            int high=0;
            for(int i=1;i<=14;i++){
                ConcreteBusinessBoard businessBorder=businessBoards.get(i);
                FanSpeed speed=businessBorder.onSrvTempChanged();
                if(speed==FanSpeed.LOW){
                    low++;
                }
                if(speed==FanSpeed.MEDIUM){
                    medium++;
                }
                if(speed==FanSpeed.HIGH){
                    high++;
                }
                if(high>0){
                    for(int j = 90; j <=91; j++){
                        FanExpansionControlBoard fanControlBoard= fanControlBoards.get(j);
                        FanBoardModeType mode=fanControlBoard.getMode();
                        if(mode==FanBoardModeType.AUTOMATIC){
                            fanControlBoard.automaticAdjust(FanSpeed.HIGH);
                            fanControlBoards.put(i,fanControlBoard);
                        }
                    }
                }else if(medium>0){
                    for(int j = 90; j <=91; j++){
                        FanExpansionControlBoard fanControlBoard= fanControlBoards.get(j);
                        FanBoardModeType mode=fanControlBoard.getMode();
                        if(mode==FanBoardModeType.AUTOMATIC){
                            fanControlBoard.automaticAdjust(FanSpeed.MEDIUM);
                            fanControlBoards.put(i,fanControlBoard);
                        }
                    }
                }else{
                    for(int j = 90; j <=91; j++){
                        FanExpansionControlBoard fanControlBoard= fanControlBoards.get(j);
                        FanBoardModeType mode=fanControlBoard.getMode();
                        if(mode==FanBoardModeType.AUTOMATIC){
                            fanControlBoard.automaticAdjust(FanSpeed.LOW);
                            fanControlBoards.put(i,fanControlBoard);
                        }
                    }
                }
                businessBorder.temperatureWarn();
            }
        };
        executor.scheduleAtFixedRate(task,0,1, TimeUnit.SECONDS);

        //仿真模拟实际场景中业务板的温度变化
        Runnable simulate=()->{
            int low=0;
            int medium=0;
            int high=0;
            for(int i=90;i<=91;i++){
                FanExpansionControlBoard fanControlBoard=fanControlBoards.get(i);
                FanSpeed speed=fanControlBoard.getSpeed();
                if(speed==FanSpeed.LOW){
                    low++;
                }
                if(speed==FanSpeed.MEDIUM){
                    medium++;
                }
                if(speed==FanSpeed.HIGH){
                    high++;
                }
            }
            for(int i=1;i<=14;i++){
                ConcreteBusinessBoard businessBorder=businessBoards.get(i);
                double temperature=businessBorder.getTemperature();
                temperature+=0.5*Math.random();
                if(high>0){
                    temperature-=1*high*Math.random();
                }
                if(medium>0){
                    temperature-=0.3*medium*Math.random();
                }
                if(low>0){
                    temperature-=0.1*low*Math.random();
                }
                if(temperature<=10){temperature=10;}
                if(temperature>=100){temperature=100;}
                businessBorder.setTemperature(temperature);
                businessBoards.put(i,businessBorder);
            }
        };
        executor.scheduleAtFixedRate(simulate,0,1, TimeUnit.SECONDS);
    }

    //功能选择菜单
    public Status menu(){
        Scanner scanner=new Scanner(System.in);
        int n=0;
        Status status=Status.SUCCESS;
        Status status1=Status.SUCCESS;
        Status status2=Status.SUCCESS;
        Status status3=Status.SUCCESS;
        do{
            System.out.println("请选择功能（输入该功能对应序号）：");
            System.out.println("1.手工调速");
            System.out.println("2.设定风扇板调速的工作模式(手工/自动)");
            System.out.println("3.显示当前风扇情况");
            System.out.println("4.显示业务板温度");
            System.out.println("5.关闭系统");
            n=scanner.nextInt();
            if(n==1){
                System.out.println("请输入需要手工调速的风扇板的槽位编号：");
                int slot= scanner.nextInt();
                FanExpansionControlBoard fanControlBoard=fanControlBoards.get(slot);
                FanBoardModeType mode=fanControlBoard.getMode();
                if(mode==FanBoardModeType.AUTOMATIC){
                    System.out.println("当前选择风扇板为自动工作模式，不能手工调速！");
                }else{
                    System.out.println("请输入该风扇需调整的档位序号（1-LOW,2-MEDIUM,3-HIGH）：");
                    int s=scanner.nextInt();
                    switch(s){
                        case 1:
                            status1=fanControlBoard.manualAdjust(FanSpeed.LOW);
                            break;
                        case 2:
                            status2=fanControlBoard.manualAdjust(FanSpeed.MEDIUM);
                            break;
                        case 3:
                            status3=fanControlBoard.manualAdjust(FanSpeed.HIGH);
                            break;
                    }
                    if((status1==Status.FAILURE) || (status2==Status.FAILURE) || (status3==Status.FAILURE)){
                        status=Status.FAILURE;
                    }
                    fanControlBoards.put(slot, fanControlBoard);
                }
            }
            if(n==2){
                System.out.println("请输入需要设定工作模式的风扇板的槽位编号：");
                int slot= scanner.nextInt();
                FanExpansionControlBoard fanControlBoard=fanControlBoards.get(slot);
                System.out.println("请输入该风扇需设定的工作模式序号（1-手工调速,2-自动调速）：");
                int s=scanner.nextInt();
                switch(s){
                    case 1:
                        status1 = fanControlBoard.changeMode(FanBoardModeType.MANUAL);
                        break;
                    case 2:
                        status2 = fanControlBoard.changeMode(FanBoardModeType.AUTOMATIC);
                        break;
                }
                if((status1==Status.FAILURE) || (status2==Status.FAILURE)){
                    status=Status.FAILURE;
                }
                fanControlBoards.put(slot, fanControlBoard);
            }
            if(n==3){
                System.out.println("当前风扇情况为(风扇板槽位序号-风扇板风速档位-风扇板调速工作模式)：");
                for(int i=90;i<=91;i++){
                    FanExpansionControlBoard fanControlBoard=fanControlBoards.get(i);
                    System.out.println(i+"-"+fanControlBoard.getSpeed()+"-"+fanControlBoard.getMode());
                }
            }
            if(n==4){
                System.out.println("当前业务板温度为(业务板槽位序号-业务板温度)：");
                for(int i=1;i<=14;i++){
                    ConcreteBusinessBoard businessBoard=businessBoards.get(i);
                    System.out.println(i+"-"+businessBoard.getTemperature()+"摄氏度");
                }
            }
        if(status==Status.FAILURE){return status;}
        }while(n!=5);

        if(n==5){
            scanner.close();
            executor.shutdown();
        }

        return status;
    }
}
