package minigames.client.EightBall;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JLabel;


//Initially Coded by James Howell-Smith
public class MyFrame extends JFrame implements KeyListener, MouseListener, MouseMotionListener { //implement KeyListener on the frame class and implement all methods of interface

    JLabel label;

    MyFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 500);
        this.setLayout(null);
        this.setVisible(true);
        this.addKeyListener(this); //adds key listener to frame
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        label = new JLabel();
        label.setBounds(0, 0, 100, 100);
        label.setBackground(Color.BLACK);
        label.setOpaque(true);
        this.add(label);
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println("You pressed the key: " + e.getKeyChar());
        switch (e.getKeyChar()) {
            case 'a':
                label.setLocation(label.getX() - 10, label.getY());
                break;
            case 'd':
                label.setLocation(label.getX() + 10, label.getY());
                break;
            case 's':
                label.setLocation(label.getX(), label.getY() + 10);
                break;
            case 'w':
                label.setLocation(label.getX(), label.getY() - 10);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        System.out.println("X: " + e.getX());
        System.out.println("Y: " + e.getY());
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
    }
}


