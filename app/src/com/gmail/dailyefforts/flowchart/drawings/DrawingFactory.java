package com.gmail.dailyefforts.flowchart.drawings;

/**
 * Factory class, used to generate drawing.
 */
public class DrawingFactory {
    /**
     * @param drawing The id of the drawing.
     * @return The Drawing instance with the id.
     */
    public Drawing createDrawing(final Drawings drawing) {
        if (drawing == null) {
            return null;
        }
        Drawing mDrawing = null;
        switch (drawing) {
            case PATHLINE:
                mDrawing = new PathLine();
                break;
            case STRAIGHTLINE:
                mDrawing = new StraightLine();
                break;
            case RECT:
                mDrawing = new Process();
                break;
            case OVAL:
                mDrawing = new Oval();
                break;
            case CIRCLE:
                mDrawing = new Circle();
                break;
            case POINTS:
                mDrawing = new Points();
                break;
            case ERASER:
                mDrawing = new Eraser();
                break;
            case DECISION:
                mDrawing = new Decision();
                break;
            case TERMINATOR:
                mDrawing = new Terminator();
                break;
            default:
                break;
        }
        return mDrawing;
    }
}