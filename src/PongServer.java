import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;

public class PongServer extends JFrame implements KeyListener, Runnable, WindowListener {

    private static final long serialVersionUID = 1L;
    //Frame
    private static final String TITLE = "ping-pong::server";
    //window width
    private static final int WIDTH = 800;
    //window height
    private static final int HEIGHT = 460;

    //game vars
    boolean isRunning;
    boolean check = true;
    Ball movingBALL;
    private PlayerServer playerS;
    private PlayerClient playerC;

    //velocity of ball
    private int ballVEL = 4;
    //bar width
    private int barR = 30;
    //bar height
    private int playerH = 120;
    //max score
    private int max_Score = 11;
    //player bar movement
    private int mPLAYER = 5;
    //restart check
    private boolean Restart = false;
    private boolean restartON = false;

    //server vars
    private static Socket clientSoc = null;
    private static ServerSocket serverSoc = null;
    private int portAdd;

    //graphics
    private Graphics g;
    private Font sFont = new Font("TimesRoman", Font.BOLD, 90);
    private Font mFont = new Font("TimesRoman", Font.BOLD, 50);
    private Font nFont = new Font("TimesRoman", Font.BOLD, 32);
    private Font rFont = new Font("TimesRoman", Font.BOLD, 18);
    //array for splitting message
    private String[] message;
    private Thread movB;

    //constructor
    public PongServer(String servername, String portAdd) {
        //player classes
        playerS = new PlayerServer();
        playerC = new PlayerClient("");
        playerS.setName(servername);

        //frame set
        this.portAdd = Integer.parseInt(portAdd);
        this.isRunning = true;
        this.setTitle(TITLE + "::port number[" + portAdd + "]");
        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);

        //ball movement creation
        movingBALL = new Ball(playerS.getBallx(), playerS.getBally(), ballVEL, ballVEL, 45, WIDTH, HEIGHT);

        //listener
        addKeyListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        //socket for server
        try {
            serverSoc = new ServerSocket(portAdd);
            System.out.println("Server has started to running on the " + portAdd + " port.\nWaiting for a player...");
            System.out.println("Waiting for connection...");
            playerS.setImessage("Waiting f   r a player...");
            clientSoc = serverSoc.accept();

            System.out.println("Connected a player...");

            //player connection starts loop
            if (clientSoc.isConnected()) {
                //client check
                boolean notchecked = true;
                movB = new Thread(movingBALL);
                while (true) {

                    //game status check
                    if (playerS.getScoreP() >= max_Score || playerS.getScoreS() >= max_Score && Restart == false) {
                        if (playerS.getScoreS() > playerS.getScoreP()) {
                            playerS.setOmessage("Won               Loss-Play Again: Press any key || Exit: Esc|N");
                            playerS.setImessage("Won               Loss-Play again? ");
                            Restart = true;
                        } else {
                            playerS.setImessage("Loss              Won-Play Again: Press any key || Exit: Esc|N");
                            playerS.setOmessage("Loss              Won-Play Again: Press any key || Exit: Esc|N");
                            Restart = true;
                        }
                        //stops ball object
                        movB.suspend();
                    }

                    //client readiness check
                    if (playerC.ok && notchecked) {
                        playerS.setImessage("");
                        movB.start();
                        notchecked = false;
                    }

                    //ball update
                    updateBall();

                    //stream creation
                    ObjectInputStream getObj = new ObjectInputStream(clientSoc.getInputStream());
                    playerC = (PlayerClient) getObj.readObject();

                    //object send to client
                    ObjectOutputStream sendObj = new ObjectOutputStream(clientSoc.getOutputStream());
                    sendObj.writeObject(playerS);

                    //restart game check
                    if (restartON) {
                        if (playerC.restart) {
                            playerS.setScoreP(0);
                            playerS.setScoreS(0);
                            playerS.setOmessage("");
                            playerS.setImessage("");
                            Restart = false;
                            playerS.setRestart(false);
                            playerS.setBallx(380);
                            playerS.setBally(230);
                            movingBALL.setX(380);
                            movingBALL.setY(230);
                            movB.resume();
                            restartON = false;
                        }
                    }
                    repaint();
                }
            } else {
                System.out.println("Disconnected...");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private Image createImage() {
        //keeps screen frames
        BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = bufferedImage.createGraphics();

        //table
        g.setColor(new Color(15, 9, 9));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        //lines
        g.setColor(Color.white);
        g.fillRect(WIDTH / 2 - 5, 0, 5, HEIGHT);
        g.fillRect(WIDTH / 2 + 5, 0, 5, HEIGHT);

        //score
        g.setFont(sFont);
        g.setColor(new Color(228, 38, 36));
        g.drawString("" + playerS.getScoreS(), WIDTH / 2 - 60, 120);
        g.drawString("" + playerS.getScoreP(), WIDTH / 2 + 15, 120);

        //player name
        g.setFont(nFont);
        g.setColor(Color.white);
        g.drawString(playerS.getName(), WIDTH / 10, HEIGHT - 20);
        g.drawString(playerC.getName(), 600, HEIGHT - 20);

        //players
        g.setColor(new Color(57, 181, 74));
        g.fillRect(playerS.getX(), playerS.getY(), barR, playerH);
        g.setColor(new Color(57, 181, 74));
        g.fillRect(playerC.getX(), playerC.getY(), barR, playerH);

        //ball
        g.setColor(new Color(255, 255, 255));
        g.fillOval(playerS.getBallx(), playerS.getBally(), 45, 45);
        g.setColor(new Color(228, 38, 36));
        g.fillOval(playerS.getBallx() + 5, playerS.getBally() + 5, 45 - 10, 45 - 10);

        //message
        message = playerS.getImessage().split("-");
        g.setFont(mFont);
        g.setColor(Color.white);
        if (message.length != 0) {
            g.drawString(message[0], WIDTH / 4 - 31, HEIGHT / 2 + 38);
            if (message.length > 1) {
                if (message[1].length() > 6) {
                    g.setFont(rFont);
                    g.setColor(new Color(228, 38, 36));
                    g.drawString(message[1], WIDTH / 4 - 31, HEIGHT / 2 + 100);
                }
            }
        }
        return bufferedImage;
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(createImage(), 0, 0, this);
    }

    public void updateBall() {
        //collision check
        checkCol();

        //ball update
        playerS.setBallx(movingBALL.getX());
        playerS.setBally(movingBALL.getY());
    }

    //upward bar movement
    public void playerUP() {
        if (playerS.getY() - mPLAYER > playerH / 2 - 10) {
            playerS.setY(playerS.getY() - mPLAYER);
        }
    }

    //downward bar movement
    public void playerDOWN() {
        if (playerS.getY() + mPLAYER < HEIGHT - playerH - 30) {
            playerS.setY(playerS.getY() + mPLAYER);
        }
    }

    public void checkCol() {
        //checking ball side, when a player got a score check -> false * if ball behind of the players check -> true
        if (playerS.getBallx() < playerC.getX() && playerS.getBallx() > playerS.getX()) {
            check = true;
        }

        //score of server player
        if (playerS.getBallx() > playerC.getX() && check) {
            playerS.setScoreS(playerS.getScoreS() + 1);
            check = false;
        }

        //score of client player
        else if (playerS.getBallx() <= playerS.getX() && check) {
            playerS.setScoreP(playerS.getScoreP() + 1);
            check = false;
        }

        //server player bar check
        if (movingBALL.getX() <= playerS.getX() + barR && movingBALL.getY() + movingBALL.getRadius() >= playerS.getY() && movingBALL.getY() <= playerS.getY() + playerH) {
            movingBALL.setX(playerS.getX() + barR);
            playerS.setBallx(playerS.getX() + barR);
            movingBALL.setXv(movingBALL.getXv() * -1);
        }

        //client player bar check
        if (movingBALL.getX() + movingBALL.getRadius() >= playerC.getX() && movingBALL.getY() + movingBALL.getRadius() >= playerC.getY() && movingBALL.getY() <= playerC.getY() + playerH) {
            movingBALL.setX(playerC.getX() - movingBALL.getRadius());
            playerS.setBallx(playerC.getX() - movingBALL.getRadius());
            movingBALL.setXv(movingBALL.getXv() * -1);
        }
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        int keycode = arg0.getKeyCode();
        if (keycode == KeyEvent.VK_UP) {
            playerUP();
            repaint();
        }
        if (keycode == KeyEvent.VK_DOWN) {
            playerDOWN();
            repaint();
        }
        if (Restart == true) {
            restartON = true;
            playerS.setRestart(true);
        }
        if (keycode == KeyEvent.VK_N || keycode == KeyEvent.VK_ESCAPE && Restart == true) {
            try {
                this.setVisible(false);
                serverSoc.close();
                System.exit(EXIT_ON_CLOSE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressWarnings("deprecation")
    @Override
    public void windowClosing(WindowEvent arg0) {
        Thread.currentThread().stop();
        this.setVisible(false);
        try {
            serverSoc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
    }

    @Override
    public void windowActivated(WindowEvent arg0) {
    }

    @Override
    public void windowClosed(WindowEvent arg0) {
        System.exit(1);
    }

    @Override
    public void windowDeactivated(WindowEvent arg0) {
    }

    @Override
    public void windowDeiconified(WindowEvent arg0) {
    }

    @Override
    public void windowIconified(WindowEvent arg0) {
    }

    @Override
    public void windowOpened(WindowEvent arg0) {
    }
}
