package cc.cubone.turbo.ui.demo.snake.engine.part;

public enum Direction {
    LEFT,
    UP,
    RIGHT,
    DOWN;

    public static Direction turnLeft(Direction direction) {
        switch (direction) {
            case LEFT:  return DOWN;
            case UP:    return LEFT;
            case RIGHT: return UP;
            case DOWN:  return RIGHT;
            default: throw new IllegalArgumentException();
        }
    }

    public static Direction turnRight(Direction direction) {
        switch (direction) {
            case LEFT:  return UP;
            case UP:    return RIGHT;
            case RIGHT: return DOWN;
            case DOWN:  return LEFT;
            default: throw new IllegalArgumentException();
        }
    }

    public static boolean isOpposite(Direction lhs, Direction rhs) {
        switch (lhs) {
            case LEFT:  return rhs == RIGHT;
            case UP:    return rhs == DOWN;
            case RIGHT: return rhs == LEFT;
            case DOWN:  return rhs == UP;
            default: throw new IllegalArgumentException();
        }
    }
}
