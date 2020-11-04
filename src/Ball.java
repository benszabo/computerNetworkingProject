public class Ball extends Thread {
    //ball x coordinate
    private int x;
    //ball y coordinate
    private int y;
    //x velocity
    private double xv;
    //y velocity
    private double yv;
    //ball radius
    private int radius;
    private int HEIGHT;
    private int WIDTH;

    @Override
    public void run() {
        while (true) {
            move();
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Ball(int x, int y, double xv, double yv, int radius, int WIDTH, int HEIGHT) {
        super();
        this.x = x;
        this.y = y;
        this.xv = xv;
        this.yv = yv;
        this.radius = radius;
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
    }

    public void move() {
        //screen layer calibration
        if (x + xv > (WIDTH - radius) - 7) {
            //ball position set
            x = (WIDTH - radius) - 7;
            //ball velocity set
            xv = xv * -1;
        }

        //screen layer calibration
        if (x + xv < 9) {
            x = 9;
            xv = xv * -1;
        }

        //screen layer calibration
        if (y + yv < radius / 2 + 7) {
            y = 29;
            yv = yv * -1;
        }

        //screen layer calibration
        if (y + yv > (HEIGHT - radius) - 6)
        {
            y = (HEIGHT - radius) - 6;
            yv = yv * -1;
        }
        x += xv;
        y += yv;

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getXv() {
        return xv;
    }

    public void setXv(double xv) {
        this.xv = xv;
    }

    public int getRadius() {
        return radius;
    }
}
