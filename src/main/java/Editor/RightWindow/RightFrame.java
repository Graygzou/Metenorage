package Editor.RightWindow;

import Editor.RightWindow.SubPanels.CenterPanel;
import Editor.RightWindow.SubPanels.TopPanel;
import Engine.GameEngine;
import Engine.Main.Entity;
import Engine.System.Component.Component;
import org.joml.Vector3f;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.List;

public class RightFrame extends JFrame {

    private GameEngine gameEngine;
    private CenterPanel componentsPanel;
    private Entity currentEntitySelected;

    public RightFrame(GameEngine gameEngine) {

        this.gameEngine = gameEngine;

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Get the screen size
        GraphicsConfiguration gc = this.getGraphicsConfiguration();
        Rectangle bounds = gc.getBounds();

        // Create and pack the Elements
        this.getContentPane().setLayout(new BorderLayout());

        // Add the list at the top of the frame
        this.getContentPane().add(new TopPanel(), BorderLayout.NORTH);

        // Add the component Panel
        this.componentsPanel = new CenterPanel(this);
        //Create the scroll pane and add the tree to it.
        JScrollPane scrollPane = new JScrollPane(this.componentsPanel);
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);

        this.pack();

        // Set the Location and Activate
        this.setSize(350, 480);
        int y = (int) ((bounds.getHeight() - this.getHeight()) / 2);
        this.setLocation((int)bounds.getWidth() - this.getWidth(), y);

        this.setResizable(false);
        this.setVisible(true);

    }

    public void showComponentsFromEntity(Entity entity) {
        this.currentEntitySelected = entity;
        this.componentsPanel.cleanPanel();
        this.componentsPanel.showComponents(entity);
    }

    public void updateTransform(Vector3f position, Vector3f rotation, Vector3f scale) {
        this.currentEntitySelected.getTransform().setPosition(position);
        this.currentEntitySelected.getTransform().setRotation(rotation);
        this.currentEntitySelected.getTransform().setScale(scale);
    }
}
