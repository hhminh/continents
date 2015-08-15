import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.metal.*;

public class VerticalTabbedPane extends JTabbedPane
{
  boolean m_bVertical;

  public VerticalTabbedPane()
  {
    this(TOP);
  }

  public VerticalTabbedPane(int tabPlacement)
  {
    super(tabPlacement);
    m_bVertical = (tabPlacement == LEFT || tabPlacement == RIGHT);
    setUI(new VerticalTabbedPaneUI(m_bVertical));
  }

  public void addTab(String s, Component c)
  {
    insertTab(
        m_bVertical?null:s, m_bVertical?
        new VerticalTextIcon(s, getTabPlacement() == RIGHT):null,
        c,
        null,
        getTabCount());
  }

  private class VerticalTextIcon implements Icon
  {
    private String m_sText;
    private boolean m_bClockwise;

    public VerticalTextIcon(String sText, boolean bClockwise)
    {
      m_sText = sText;
      m_bClockwise = bClockwise;
    }

    public void paintIcon(Component c, Graphics g, int x, int y)
    {
      Graphics2D g2 = (Graphics2D) g;

      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.rotate (Math.toRadians(m_bClockwise?90:-90), x, y);
      g2.drawString(
          m_sText,
          x - (m_bClockwise?0:getIconHeight()) + 8,
          y + (m_bClockwise?0:getIconWidth()));
      g2.rotate (Math.toRadians(m_bClockwise?-90:90), x, y);
    }

    public int getIconWidth()
    {
      return 10;
    }

    public int getIconHeight()
    {
      return getFontMetrics(getFont()).stringWidth(m_sText) + 20;
    }
  }

  public static void main(String args[])
  {
    JFrame frame = new JFrame("Vertical Text");
    frame.setSize(500, 500);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout());

    VerticalTabbedPane tpane1 = new VerticalTabbedPane(JTabbedPane.RIGHT);

    tpane1.addTab("News", new JPanel());
    tpane1.addTab("Sports", new JPanel());
    tpane1.addTab("Weather", new JPanel());

    VerticalTabbedPane tpane2 = new VerticalTabbedPane(JTabbedPane.LEFT);

    tpane2.addTab("News", new JPanel());
    tpane2.addTab("Sports", new JPanel());
    tpane2.addTab("Weather", new JPanel());

    VerticalTabbedPane tpane3 = new VerticalTabbedPane(JTabbedPane.BOTTOM);

    tpane3.addTab("News", new JPanel());
    tpane3.addTab("Sports", new JPanel());
    tpane3.addTab("Weather", new JPanel());

    VerticalTabbedPane tpane4 = new VerticalTabbedPane(JTabbedPane.TOP);

    tpane4.addTab("News", new JPanel());
    tpane4.addTab("Sports", new JPanel());
    tpane4.addTab("Weather", new JPanel());

    VerticalTabbedPane mainPane = new VerticalTabbedPane();

    mainPane.addTab("Left", tpane2);
    mainPane.addTab("Right", tpane1);
    mainPane.addTab("Top", tpane4);
    mainPane.addTab("Bottom", tpane3);

    frame.getContentPane().add(mainPane);

    frame.setVisible(true);
  }
}

class VerticalTabbedPaneUI extends MetalTabbedPaneUI
{
  private boolean m_bVertical;

  public VerticalTabbedPaneUI(boolean bVertical)
  {
    m_bVertical = bVertical;
  }

  protected void installDefaults()
  {
    super.installDefaults();
    tabAreaInsets = new Insets(0, 0, 0, 0);
    if (m_bVertical)
    {
      tabInsets = new Insets(0, 1, 0, 1);
      selectedTabPadInsets = new Insets(0, 1, 0, 1);
    }
  }
}
