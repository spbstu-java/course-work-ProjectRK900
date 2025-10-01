package lab1;

public class Lab1 {
    public interface MoveStrategy {
        String move(String from, String to);
    }

    public static final class Hero {
        private MoveStrategy moveStrategy;

        public Hero() { }

        public Hero(MoveStrategy moveStrategy) {
            this.moveStrategy = moveStrategy;
        }

        public void setMoveStrategy(MoveStrategy moveStrategy) {
            this.moveStrategy = moveStrategy;
        }

        public String move(String from, String to) {
            return moveStrategy != null ? moveStrategy.move(from, to) : "Способ перемещения героя не установлен!";
        }
    }

    public static final class WalkStrategy implements MoveStrategy {
        @Override
        public String move(String from, String to) {
            return "Иду пешком из " + from + " в " + to;
        }
    }

    public static final class RunStrategy implements MoveStrategy {
        @Override
        public String move(String from, String to) {
            return "Бегу из " + from + " в " + to;
        }
    }

    public static final class HorseRideStrategy implements MoveStrategy {
        @Override
        public String move(String from, String to) {
            return "Еду верхом на лошади из " + from + " в " + to;
        }
    }

    public static final class FlyStrategy implements MoveStrategy {
        @Override
        public String move(String from, String to) {
            return "Лечу из " + from + " в " + to;
        }
    }

    public static final class SwimStrategy implements MoveStrategy {
        @Override
        public String move(String from, String to) {
            return "Плыву по реке из " + from + " в " + to;
        }
    }
}