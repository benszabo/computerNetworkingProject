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
    private static final String title = "ping-pong::network_game";
    private static final int width = 800;
    private static final int height = 460;
    private String servername = "servername", clientname = "clientname";

    public PongGame() {
    }

    @Override
    public void run() {
        this.setVisible(true);
        this.setTitle(title);
        this.setSize(width, height);
        this.setResizable(false);
        this.addKeyListener(this);
    }

    public static void main(String[] args) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        //set main menu background
        image = tk.getImage("bkgrnd.png");
        PongGame newT = new PongGame();
        newT.run();
    }

    private Image createImage() {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g = bufferedImage.createGraphics();
        g.fillRect(0, 0, width, height);
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

        //creates server
        if (keyCode == KeyEvent.VK_S) {

            // port input
            portAdd = JOptionPane.showInputDialog(null, "ex. 1024", "Enter server port:", 1);

            //alert msg
            if (portAdd != null) {
                if (!isPort(portAdd)) {
                    JOptionPane.showMessageDialog(null, "Enter port number as a right format!", "Error!", 1);
                } else {

                    //server player nickname
                    servername = JOptionPane.showInputDialog(null, "Nick name:", "Enter server name:", 1);
                    servername += "";

                    //alert msg
                    if (servername.length() > 10 || servername.length() < 3 || servername.startsWith("null")) {
                        JOptionPane.showMessageDialog(null, "Enter name as a right format!", "Error!", 1);

                    }

                    //creates server
                    else {

                        PongServer myServer = new PongServer(servername, portAdd);
                        Thread myServerT = new Thread(myServer);
                        myServerT.start();
                        this.setVisible(false);
                    }
                }
            }
        }

        //creates client
        if (keyCode == KeyEvent.VK_C) {

            //IP address input
            ipAdd = JOptionPane.showInputDialog(null, "ex. 127.0.0.1", "Enter server ip:", 1);

            if (ipAdd != null) {

                //alert msg
                if (!isIPAddress(ipAdd)) {
                    JOptionPane.showMessageDialog(null, "Enter ip number as a right format!", "Enter server ip:", 1);
                } else {
                    //port number input
                    portAdd = JOptionPane.showInputDialog(null, "ex. 1024", "Enter server port number:", 1);

                    //alert msg
                    if (portAdd != null) {
                        if (!isPort(portAdd)) {
                            JOptionPane.showMessageDialog(null, "Enter port number as a right format!", "Error!:", 1);
                        }
                        //Client player nickname
                        else {
                            clientname = JOptionPane.showInputDialog(null, "Nick name:", "Enter server name:", 1);
                            clientname += "";
                            if (clientname.length() > 10 || clientname.length() < 3 || clientname.startsWith("null")) {
                                JOptionPane.showMessageDialog(null, "Enter name as a right format!", "Error!", 1);
                            }
                            //Client start
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

    //port check
    private boolean isPort(String str) {
        Pattern pPattern = Pattern.compile("\\d{1,4}");
        return pPattern.matcher(str).matches();
    }

    //IP address check
    private boolean isIPAddress(String str) {
        Pattern ipPattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
        return ipPattern.matcher(str).matches();
    }
}
