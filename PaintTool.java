package ch09graph.weighted;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class PaintTool {
	private Position position;
	private double direction = 0; // Winkel in Grad
	private boolean isDown = true;
	private Color penColor = Color.BLACK;
	private Color bgColor = Color.WHITE;
	private JFrame frame;
	private TurtlePanel tpanel;
	
	@SuppressWarnings("serial")
	private class TurtlePanel extends JPanel {
		private int xOffset = 0;
		private int yOffset = 0;
		private double scale = 1;
		int mouseX = 0;
		int mouseY = 0;
		
		public TurtlePanel(int defaultWidth, int defaultHeight) {
			super();
			setPreferredSize(new Dimension(defaultWidth, defaultHeight));
			addMouseListener(new MouseAdapter() {
				
				@Override
				public void mousePressed(MouseEvent e) {
					super.mousePressed(e);
					mouseX = e.getX();
					mouseY = e.getY();
				}
					

				@Override
				public void mouseReleased(MouseEvent e) {
					super.mouseReleased(e);
					xOffset += (e.getX() - mouseX);
					yOffset += (e.getY() - mouseY);
					repaint();
					
				}
			});
			
			addMouseWheelListener(new MouseAdapter() {
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					int rot = e.getWheelRotation();
					if (rot > 0) {
						scale *= 0.9;
					} else if (rot < 0) {						
						scale /= 0.9;
					}
					repaint();
				}
				
			});
		}
				
		public void paint(Graphics g) {
			int width = getWidth();
			int height = getHeight();
			g.setColor(bgColor);
			g.fillRect(0, 0, width, height);
			g.setColor(penColor);
			for (int i = 0; i < elements.size(); i++) {
				DrawableElement de = elements.get(i);
				de.draw(g, this);				
			}
		}
		
		public Position drawPos(Position pos) {
			double xDraw = xOffset + scale * pos.getX();
			double yDraw = yOffset + scale * pos.getY();
			return new Position(xDraw, yDraw);
		}
		
		public double scale(double len) {
			return scale * len;
		}
	}
	
	private static abstract class DrawableElement {
		protected Position position;
		private Color col;

		
		public DrawableElement(Color col, Position position) {
			this.position = position;
			this.col = col;
		}

		
		public void draw(Graphics g, TurtlePanel tp) {
			g.setColor(col);
		}
	}

	private static class Line extends DrawableElement {
		private Position pos2;

		public Line(Color col, Position pos1,  Position pos2) {
			super(col, pos1);
			this.pos2 = pos2;
		}

		public void draw(Graphics g,  TurtlePanel tp) {
			super.draw(g, tp);
			Position drawPos1 = tp.drawPos(position);
			Position drawPos2 = tp.drawPos(pos2);
			g.drawLine((int) drawPos1.getX(), (int) drawPos1.getY(), 
					    (int)drawPos2.getX(), (int) drawPos2.getY());
		}
	}

	private static class Text extends DrawableElement {
		private String str;

		public Text(Color col, Position position, String str) {
			super(col, position);
			this.str = str;
		}

		public void draw(Graphics g, TurtlePanel tp) {
			super.draw(g, tp);
			Position drawPos = tp.drawPos(position);
			g.drawString(str, (int) drawPos.getX(), (int) drawPos.getY());
		}
	}

	private static class Rectangle extends DrawableElement {
		private double rectWidth;
		private double rectHeigth;
		private boolean filled = false;
		
		public Rectangle(Color col, Position position, double width, double height, boolean filled) {
			super(col, position);
			this.rectWidth = width;
			this.rectHeigth = height;
			this.filled = filled;
		}

		public void draw(Graphics g,  TurtlePanel tp) {
			super.draw(g, tp);
			Position drawPos = tp.drawPos(position);
			double drawWidth = tp.scale(rectWidth);
			double drawHeight = tp.scale(rectHeigth);
			
			double x1 = drawPos.getX();
			double y1 = drawPos.getY();
			if (filled) {
				g.fillRect((int) x1, (int) y1,	(int) drawWidth, (int) drawHeight);
			}
			else {
				g.drawRect((int) x1, (int) y1,	(int) drawWidth, (int) drawHeight);
			}
		}
	}

	private static class Oval extends DrawableElement {
		private double ovalWidth;
		private double ovalHeigth;
		private boolean filled = false;
		
		public Oval(Color col, Position position, double width, double height, boolean filled) {
			super(col, position);
			this.ovalWidth = width;
			this.ovalHeigth = height;
			this.filled = filled;
		}

		public void draw(Graphics g, TurtlePanel tp) {
			super.draw(g, tp);
			Position drawPos = tp.drawPos(position);
			int x1 = (int) drawPos.getX();
			int y1 = (int) drawPos.getY();
			int drawWidth = (int) tp.scale(ovalWidth);
			int drawHeight = (int) tp.scale(ovalHeigth);

			if (filled) {
				g.fillOval(x1, y1, drawWidth, drawHeight);
			}
			else {
				g.drawOval(x1, y1, drawWidth, drawHeight);
			}
		}
	}
	
	
	private List<DrawableElement> elements;

	public PaintTool(String title, int width, int height) {
		frame = new JFrame(title);
		frame.setLayout(new BorderLayout());
		tpanel = new TurtlePanel(width, height);
		frame.add(new JScrollPane(tpanel), BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		frame.pack();
		position = new Position(0,0);
		elements = new ArrayList<DrawableElement>();
	}


	/** Bringt die Turtle zum Koordinatenursprung */
	public void home() {
		position = Position.ORIGIN;
		direction = 0.0;
	}

	// Die Turtle geht laenge Einheiten in aktueller Blickrichtung nach vorne.
	public void move(double length) {
		double dirRad = Math.toRadians(direction);
		double posX = position.getX();
		double posY = position.getY();
		double xNeu = (posX + Math.cos(dirRad) * length);
		double yNeu = (posY + Math.sin(dirRad) * length);
		Position posNeu = new Position(xNeu, yNeu);
		if (isDown) {
			elements.add(new Line(penColor, position, posNeu));
		}
		position = posNeu;
	}
	
	public void turnLeft(double grad) {
		direction -= grad;
	}

	public void turnLeft() {
		turnLeft(90.0);
	}

	public void turnRight(double grad) {
		direction += grad;
	}

	public void turnRight() {
		turnRight(90.0);
	}

	public void setPosition(Position posNew) {
		position = posNew;
	}

	public void reset() {
		elements.clear();
		position = Position.ORIGIN;
		direction = 0;
		frame.repaint();
	}

	public void clear() {
		elements.clear();
		frame.repaint();
	}

	public void setDirection(double grad) {
		direction = grad;
	}


	public void setColor(Color c) {
		penColor = c;
	}

	public void penUp() {
		isDown = false;
	}

	public void penDown() {
		isDown = true;

	}

	public double getDirection() {
		return direction;
	}

	public Position getPosition() {
		return position;
	}

	public Color getColor() {
		return penColor;
	}
	
	public int getHeight() {
		return tpanel.getHeight();
	}
	
	public int getWidth() {
		return tpanel.getWidth();
	}

	public void write(Position pos, String str) {
		elements.add(new Text(penColor, pos, str));
		frame.repaint();
	}
	
	public void write(String str) {
		write(position, str);
	}

	public void drawLine(Position pos1, Position pos2) {
		elements.add(new Line(penColor, pos1, pos2));
		frame.repaint();
	}

	public void drawDot(Position pos) {
		elements.add(new Rectangle(penColor, pos, 1, 1, true));
		frame.repaint();
	}
	
	public void drawRectangle(Position pos, double width, double height) {
		elements.add(new Rectangle(penColor, pos, width, height, false));
		frame.repaint();
	}

	public void fillRectangle(Position pos, double width, double height) {
		elements.add(new Rectangle(penColor, pos, width, height, true));
		frame.repaint();
	}

	public void drawOval(Position pos, double width, double height) {
		elements.add(new Oval(penColor, pos, width, height, false));
		frame.repaint();
	}

	public void fillOval(Position pos, double width, double height) {
		elements.add(new Oval(penColor, pos, width, height, true));
		frame.repaint();
	}
	
	public void setBackground(Color c) {
		bgColor = c;
	}
	
	public void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			System.out.println("### Fehler: " + e);
		}
	}
	
	public void selectRandomColor() {
		penColor = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
	}
}

