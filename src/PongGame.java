import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class PongGame extends JFrame implements KeyListener, Runnable {

    private static final long serialVersionUID = 1L;
    private static Image image;
    private Graphics g;
    private static final String TITLE = "Pong Network Game";
    //window width
    private static final int WIDTH = 800;
    //window height
    private static final int HEIGHT = 460;
    private String servername = "servername", clientname = "clientname";

    public PongGame() {
    }

    @Override
    public void run() {
        this.setVisible(true);
        this.setTitle(TITLE);
        this.setSize(WIDTH, HEIGHT);
        this.setResizable(false);
        this.addKeyListener(this);
    }

    public static void main(String[] args) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        //main menu background check
        image = tk.getImage("bkgrnd.png");
        PongGame newT = new PongGame();
        newT.run();
    }

    private Image createImage() {
        BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = bufferedImage.createGraphics();
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.drawImage(image, 0, 0, this);
        return bufferedImage;
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(createImage(), 0, 20, this);
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        int keyCode = arg0.getKeyCode();
        String portAdd;
        String ipAdd;

        //server creation
        if (keyCode == KeyEvent.VK_S) {
            //port address for dialog input
            portAdd = JOptionPane.showInputDialog(null, "ex. 1024", "Enter server port:", JOptionPane.INFORMATION_MESSAGE);

            //alert message
            if (portAdd != null) {
                if (isPort(portAdd)) {
                    JOptionPane.showMessageDialog(null, "Enter port number as a right format!", "Error!", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    //server player nickname input dialog
                    servername = JOptionPane.showInputDialog(null, "Nick name:", "Enter server name:", JOptionPane.INFORMATION_MESSAGE);
                    servername += "";

                    //alert message
                    if (servername.length() > 10 || servername.length() < 3 || servername.startsWith("null")) {
                        JOptionPane.showMessageDialog(null, "Enter name as a right format!", "Error!", JOptionPane.INFORMATION_MESSAGE);
                    }

                    //server creation
                    else {
                        PongServer myServer = new PongServer(servername, portAdd);
                        Thread myServerT = new Thread(myServer);
                        myServerT.start();
                        this.setVisible(false);
                    }
                }
            }
        }

        //client creation
        if (keyCode == KeyEvent.VK_C) {

            //dialog input for IP address
            ipAdd = JOptionPane.showInputDialog(null, "ex. 127.0.0.1", "Enter server ip:", JOptionPane.INFORMATION_MESSAGE);

            if (ipAdd != null) {
                //alert message
                if (!isIPAddress(ipAdd)) {
                    JOptionPane.showMessageDialog(null, "Enter ip number as a right format!", "Enter server ip:", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    //part number input dialog
                    portAdd = JOptionPane.showInputDialog(null, "ex. 1024", "Enter server port number:", JOptionPane.INFORMATION_MESSAGE);

                    //alert message
                    if (portAdd != null) {
                        if (isPort(portAdd)) {
                            JOptionPane.showMessageDialog(null, "Enter port number as a right format!", "Error!:", JOptionPane.INFORMATION_MESSAGE);
                        }
                        //dialog input for client nickname
                        else {
                            clientname = JOptionPane.showInputDialog(null, "Nick name:", "Enter server name:", JOptionPane.INFORMATION_MESSAGE);
                            clientname += "";
                            if (clientname.length() > 10 || clientname.length() < 3 || clientname.startsWith("null")) {
                                JOptionPane.showMessageDialog(null, "Enter name as a right format!", "Error!", JOptionPane.INFORMATION_MESSAGE);
                            }
                            //client start
                            else {
                                PongClient myClient = new PongClient(clientname, portAdd, ipAdd);
                                Thread myClientT = new Thread(myClient);
                                myClientT.start();
                                this.setVisible(false);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
    }

    //port type check
    private boolean isPort(String str) {
        Pattern pPattern = Pattern.compile("\\d{1,4}");
        return !pPattern.matcher(str).matches();
    }

    //IP address type check
    private boolean isIPAddress(String str) {
        Pattern ipPattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
        return ipPattern.matcher(str).matches();
    }
}
