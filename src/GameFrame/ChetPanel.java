package GameFrame;

import javax.swing.*;
import java.awt.*;

public class ChetPanel extends JPanel {
    private static ChetPanel instance;

    public static ChetPanel getInstance() {
        if(instance == null){
            instance = new ChetPanel();
        }
        return instance;
    }

    public ChetPanel() {
        this.setBounds(420,0,200,420);
        this.setBackground(Color.CYAN);
    }
}
