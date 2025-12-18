public class AlarmConcreteObserver implements AlarmObserver{
    @Override
    public void alarm(int slot) {
        System.out.println("槽位序号为"+slot+"的业务板温度超出警戒值！");
    }
}
