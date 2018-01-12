import com.sun.javafx.geom.Curve;
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.collections.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.Label;

import javafx.scene.input.MouseEvent;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * Example of drawing text along a cubic curve.
 * Drag the anchors around to change the curve.
 */


public class cubicCurve extends Application {
    private static final String CURVED_TEXT = "A";
    Label edgeText = new Label("LINK");
    public static void main(String[] args) throws Exception {
        launch(args);
    }
    final ObservableList<Node> parts = FXCollections.observableArrayList();
    final ObservableList<PathTransition> transitions = FXCollections.observableArrayList();
    @Override
    public void start(final Stage stage) throws Exception {
        final QuadCurve curve = createStartingCurve();



        Anchor start = new Anchor(Color.PALEGREEN, curve.startXProperty(), curve.startYProperty());

        Anchor part = new Anchor(Color.GOLDENROD, curve.controlXProperty(), curve.controlYProperty());
        Anchor end = new Anchor(Color.TOMATO, curve.endXProperty(), curve.endYProperty());



        //Group part = new Group();


        //edgeText.setStyle("-fx-padding: 20 0 0 0");

        //part.getChildren().addAll(edgeText,ctrl);



        parts.add(part);
        part.setVisible(false);

       // parts.add(edgeText);
        transitions.add(createPathTransition(curve, part));

        final ObservableList<Node> controls = FXCollections.observableArrayList();
        controls.setAll(curve, start, end,edgeText);




        curve.setOnMouseClicked((event -> {


            part.enableDrag();
            // buttonPressed();

            edgeText.setTranslateX(part.getTranslateX());
            edgeText.setTranslateY(part.getTranslateY());
            plotHandler(parts, transitions);


            System.out.println("Mouse Pressed on Rectangle");
        }));



        part.setOnMouseDragged((event -> {


            part.enableDrag();


            // buttonPressed();
            plotHandler(parts, transitions);


            System.out.println("Mouse Pressed on Rectangle");
        }));



        Group content = new Group( curve, start,end,edgeText);
        content.getChildren().addAll(parts);

        stage.setTitle("Cubic Curve Manipulation Sample");
        stage.setScene(new Scene(content, 2048, 1024, Color.ALICEBLUE));
        stage.show();
    }

    private PathTransition createPathTransition(QuadCurve curve, Node text) {
        final PathTransition transition = new PathTransition(Duration.seconds(10), curve, text);

        transition.setAutoReverse(false);
        transition.setCycleCount(PathTransition.INDEFINITE);
        transition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        transition.setInterpolator(Interpolator.LINEAR);

        return transition;
    }

    private QuadCurve createStartingCurve() {
        QuadCurve curve = new QuadCurve();
        curve.setStartX(200);
        curve.setStartY(400);
        curve.setControlX(150);
        curve.setControlY(300);

        curve.setEndX(400);
        curve.setEndY(400);
        curve.setStroke(Color.FORESTGREEN);
        curve.setStrokeWidth(4);
        curve.setStrokeLineCap(StrokeLineCap.ROUND);
       // curve.setFill(Color.WHITE.deriveColor(0, 1.2, 1, 0.6));
        curve.setFill(Color.TRANSPARENT);
        return curve;
    }

    // a draggable anchor displayed around a point.
    class Anchor extends Circle {
        Anchor(Color color, DoubleProperty x, DoubleProperty y) {
            super(x.get(), y.get(), 5);
            setFill(color.deriveColor(1, 1, 1, 0.5));
            setStroke(color);
            setStrokeWidth(2);
            setStrokeType(StrokeType.OUTSIDE);



            x.bind(centerXProperty());
            y.bind(centerYProperty());
            enableDrag();
        }

        // make a node movable by dragging it around with the mouse.
        private void enableDrag() {
            final Delta dragDelta = new Delta();
            setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {


                    // record a delta distance for the drag and drop operation.
                    dragDelta.x = getCenterX() - mouseEvent.getX();
                    dragDelta.y = getCenterY() - mouseEvent.getY();
                    getScene().setCursor(Cursor.MOVE);



                    plotHandler(parts, transitions);
                }
            });
            setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    getScene().setCursor(Cursor.HAND);
                    plotHandler(parts, transitions);
                }
            });
            setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {

                    double newX = mouseEvent.getX() + dragDelta.x;
                    if (newX > 0 && newX < getScene().getWidth()) {
                        setCenterX(newX);
                        plotHandler(parts, transitions);

                    }
                    double newY = mouseEvent.getY() + dragDelta.y;
                    if (newY > 0 && newY < getScene().getHeight()) {
                        setCenterY(newY);
                        plotHandler(parts, transitions);
                    }

                }
            });
            setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (!mouseEvent.isPrimaryButtonDown()) {
                        getScene().setCursor(Cursor.HAND);
                    }
                }
            });
            setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (!mouseEvent.isPrimaryButtonDown()) {
                        getScene().setCursor(Cursor.DEFAULT);
                    }
                }
            });
        }

        // records relative x and y co-ordinates.
        private class Delta {
            double x, y;
        }
    }



        public static void plotHandler(ObservableList<Node> parts, ObservableList<PathTransition> transitions) {


                for (int i = 0; i < parts.size(); i++) {
                    parts.get(i).setVisible(true);
                    final Transition transition = transitions.get(i);
                    transition.stop();
                    transition.jumpTo(Duration.seconds(10).multiply((i + 0.5) * 1.0 / parts.size()));
                    // just play a single animation frame to display the curved text, then stop
                    AnimationTimer timer = new AnimationTimer() {
                        int frameCounter = 0;

                        @Override
                        public void handle(long l) {
                            frameCounter++;
                            if (frameCounter == 1) {
                                transition.stop();
                                stop();
                            }
                        }
                    };
                    timer.start();
                    transition.play();
                }

        }
    }
