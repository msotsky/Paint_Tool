package ch09graph.weighted;

public class Position {
	private double x;
	private double y;
	
	public static Position ORIGIN = new Position(0,0);
	
	public Position(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public Position translate(double deltaX, double deltaY) {
		return new Position(x + deltaX, y + deltaY);
	}
	
	public Position rotate(Position center, double angleDegree) {
		double x0 = getX() - center.getX();
		double y0 = getY() - center.getY();
		
		//Drehung mit Drehmatrix berechnet
		double angleRad = Math.toRadians(angleDegree);
		double sinAlpha = Math.sin(angleRad);
		double cosAlpha = Math.cos(angleRad);
		
		double xd = x0 * cosAlpha - y0 * sinAlpha;
		double yd = x0 * sinAlpha + y0 * cosAlpha;
		return new Position(center.getX() + xd, center.getY() + yd);
	}
	
	public double distance(Position pos2) {
		double dx = x - pos2.x;
		double dy = y - pos2.y;
		return Math.sqrt(dx*dx + dy*dy);
	}
	
}
