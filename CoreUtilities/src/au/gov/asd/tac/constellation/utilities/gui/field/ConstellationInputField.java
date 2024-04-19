/*
 * Copyright 2010-2024 Australian Signals Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.gov.asd.tac.constellation.utilities.gui.field;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.geometry.Side;
//import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;


/**
 * This class is the base class for all input fields in Constellation. 
 * Inputs have been developed to adhere to a strict format that is highly adaptable to different use cases.
 * To achieve this adaptive layout a number fo nuanced javaFX scructures have been integrsted into the input field 
 * that should be understood before extending this class. 
 * Input fields follow a magic-wand-like layout with three main sections. 
 * +--------------------------------------------+
 * |  Button  |      Input Area      |  Button  |
 * +--------------------------------------------+
 * A central input area and two end buttons.
 * in the case that 1 or both end button is not needed it can be removed and the subsequent Input Area shall adapt to fill the missing space.
 * +--------------------------------------------+
 * |           Input Area            |  Button  |
 * +--------------------------------------------+
 * When extending an input field the following considerations should be made:
 * For single button input fields, the button shall be placed on the Right. 
 * Buttons that initiate a context menu or modify the value or aperance of the text area should be grey.
 * Buttons that initiate a pop-up window shall be blue.
 * Buttons that initiate a pop-up window shall always be positioned on the right.
 * 
 * The construction of the class is as follows 
 *  StackPane
 *      Shape - A rounded rectangle acting as the background
 *      GridPane
 *          for each cell
 *              Group
 *                  cell contents
 *      Shape - A rounded transparent rectangle acting as the border
 * 
 * 
 * @author capricornunicorn123
 */
public class ConstellationInputField extends StackPane {
    
    //types of button
    //Cinotext trigger
    //pop up trigger
    //valueupdater
    final int endCellPrefWidth = 50;
    final int endCellMinWidth = 50;
    final int centerCellPrefWidth = 200;
    final int centerCellMinWidth = 100;
    final int defaultCellHeight = 22;
    
    private TextField field;
    private final Label leftLabel = new Label();
    private final Label rightLabel = new Label();
    private final Rectangle rightButton = new Rectangle(endCellPrefWidth, defaultCellHeight); 
    private final Rectangle leftButton = new Rectangle(); 
    
    private final ReadOnlyDoubleProperty heightBinding;
    

    
    final int corner = 7;
    
    final Color optionColor = Color.color(148/255D, 148/255D, 148/255D);
    final Color fieldColor = Color.color(51/255D, 51/255D, 51/255D);
    final Color buttonColor = Color.color(34/255D, 96/255D, 168/255D);
   
    public ConstellationInputField(){
        this(ConstellationInputFieldLayoutConstants.INPUT_POPUP);
    }
    
    public ConstellationInputField(final ConstellationInputFieldLayoutConstants layout) {
        field = this.createInputField();
        this.heightBinding = field.heightProperty();
        
        GridPane gridPane = getGridPaneWithChildCellPanes(layout);
        
        this.setPrefWidth(500);
        this.setMinWidth(150);
        
        Rectangle clippingMask = new Rectangle(300, 22);
        clippingMask.setArcWidth(corner);
        clippingMask.setArcHeight(corner);        
        clippingMask.setFill(Color.BLACK);
        clippingMask.setStroke(Color.BLACK);
        clippingMask.widthProperty().bind(gridPane.widthProperty());
        
        Rectangle background = new Rectangle(300, 22);
        background.setArcWidth(corner);
        background.setArcHeight(corner);  
        background.setFill(fieldColor);
        background.widthProperty().bind(gridPane.widthProperty());
        
        Rectangle foreground = new Rectangle(300, 22);
        foreground.setArcWidth(corner);
        foreground.setArcHeight(corner);        
        foreground.setFill(Color.TRANSPARENT);
        foreground.setMouseTransparent(true);
        foreground.widthProperty().bind(gridPane.widthProperty());
        this.bindFocusEffect(field, foreground);
        
        for (ContentDisplay area : layout.getAreas()){
            if (null != area) switch (area) {
                case LEFT -> gridPane.add(this.getEndCellGroup(ContentDisplay.LEFT, optionColor, leftLabel), 0, 0);
                case RIGHT -> gridPane.add(this.getEndCellGroup(ContentDisplay.RIGHT, layout.hasButton ? buttonColor : optionColor, rightLabel), layout.getAreas().length - 1, 0);
                case CENTER -> {
                    
                    insertBaseFieldIntoGrid(gridPane, field);
                }
                default -> {
                    //Do Nothing
                }
            }
        }
        
        background.heightProperty().bind(heightBinding);
        foreground.heightProperty().bind(heightBinding);
        clippingMask.heightProperty().bind(heightBinding);
        
        gridPane.setClip(clippingMask);
        gridPane.setAlignment(Pos.CENTER);
        this.getChildren().addAll(background, gridPane, foreground);
        this.setAlignment(Pos.CENTER);
    }
    
    public TextField getBaseField(){
        return this.field;
    }
    
    public final void insertBaseFieldIntoGrid(GridPane gridPane, TextField field){
        for (Node node : gridPane.getChildren()) {
            if (ContentDisplay.CENTER.toString().equals(node.getId())) {
                gridPane.add(field, GridPane.getColumnIndex(node), GridPane.getRowIndex(node));
                gridPane.getChildren().remove(node);
                break;
            }
        }
    }
    
    public final void registerLeftButon(final String labelText){
        
    }
    
    private Pane getEndCellGroup(final ContentDisplay side, final Color color, final Label label) {
        StackPane content = new StackPane();
        content.setId(side.toString());
        Rectangle background;
        background = switch (side) {
            case LEFT -> leftButton;
            case RIGHT -> rightButton;
            default -> new Rectangle(); // never used but a noce way to get rid of errors
        };
        
        background.setFill(color);
//        cellBackground.setStroke(Color.BLACK);
        background.setOnMouseEntered(event -> background.setFill(Color.BLUE));
        background.setOnMouseExited(event -> background.setFill(color));
        background.heightProperty().bind(heightBinding);

        label.setMouseTransparent(true);
        label.setPrefWidth(endCellPrefWidth);
        label.setAlignment(Pos.CENTER);

        content.getChildren().addAll(background, label);
        return content;
        
    }
    
    private void addToGridCellGroup(final GridPane gridPane, final ContentDisplay groupID, final Node item) {
        for (Node node : gridPane.getChildren()) {
            if (groupID.toString().equals(node.getId())) {
                ((Pane) node).getChildren().add(item);
            }
        }
    }

    private GridPane getGridPaneWithChildCellPanes(ConstellationInputFieldLayoutConstants layout) {
        GridPane gridPane = new GridPane();
        
        ColumnConstraints leftConstraint = new ColumnConstraints(50);
        
        
        ColumnConstraints rightConstraint = new ColumnConstraints(50);
        
        ColumnConstraints centerConstraint = new ColumnConstraints();
        centerConstraint.setPrefWidth(400);
        centerConstraint.setMinWidth(100);
        
        ColumnConstraints doubleConstraint = new ColumnConstraints();
        doubleConstraint.setPrefWidth(450);
        doubleConstraint.setMinWidth(100);
        
        ColumnConstraints trippleConstraint = new ColumnConstraints();
        trippleConstraint.setPrefWidth(500);
        trippleConstraint.setMinWidth(100);
        ContentDisplay[] areas = layout.getAreas();
        for (ContentDisplay area : areas){
            switch (area) {
                case LEFT -> gridPane.getColumnConstraints().add(leftConstraint);
                case CENTER -> {
                    switch (areas.length){
                        case 1 -> gridPane.getColumnConstraints().add(trippleConstraint);
                        case 2 -> gridPane.getColumnConstraints().add(doubleConstraint);
                        case 3 -> gridPane.getColumnConstraints().add(centerConstraint);   
                    }
                }
                case RIGHT -> gridPane.getColumnConstraints().add(rightConstraint);
                default -> {
                    //Do Nothing
                }
            }
        }

        for (int i = 0 ; i< areas.length ; i++){
            Pane group = new Pane();
            group.setId(areas[i].toString());
            gridPane.add(group, i, 0);
        }
        
        return gridPane;
    }

    private TextField createInputField() {
        TextField local = new TextField("This is a test text field with lots of text that potensialy overflows");
        local.setBackground(Background.fill(Color.TRANSPARENT));
        local.setBorder(Border.stroke(Color.TRANSPARENT));
        
        return local;
        
    }
    
    private void bindFocusEffect(final TextField local, final Rectangle foreground){
        //Change the border color of the firld to show that it is focused
        local.setOnMouseClicked(event -> {
            foreground.setStroke(Color.CYAN);
            Scene scene = this.getScene();
            
            //Register an event handeler so that the boarder is changed back to black on the next mouse press
            scene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    foreground.setStroke(null);
                    
                    // Dont forget to remove the listerner as it has done its job
                    scene.removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
                }
            });
        });
    }
    
    public void setRightLabel(final String label){
        this.rightLabel.setText(label);
    };
    
    public void setLeftLabel(final String label){
        this.leftLabel.setText(label);
    };
    
    public void registerRightButtonEvent(EventHandler<MouseEvent> event){
        this.rightButton.setOnMouseClicked(event);
    }
    
    public void registerleftButtonEvent(EventHandler<MouseEvent> event){
        this.leftButton.setOnMouseClicked(event);
    }
    
    public enum ConstellationInputFieldLayoutConstants {
    //types of button
    //Cinotext trigger
    //pop up trigger
    //valueupdater
        
        INPUT(false, ContentDisplay.CENTER),
        INPUT_CONTEXT(false, ContentDisplay.CENTER, ContentDisplay.RIGHT),
        CONTEXT_INUT_CONTEXT(false, ContentDisplay.LEFT, ContentDisplay.CENTER, ContentDisplay.RIGHT),
        INPUT_POPUP(true, ContentDisplay.CENTER, ContentDisplay.RIGHT),
        CONTEXT_INPUT_POPUP(true, ContentDisplay.LEFT, ContentDisplay.CENTER, ContentDisplay.RIGHT);
        
        private final ContentDisplay[] areas;
        private final boolean hasButton;

        private ConstellationInputFieldLayoutConstants(final boolean hasButton, final ContentDisplay... areas){
            this.areas = areas;
            this.hasButton = hasButton;
        }
        
        public ContentDisplay[] getAreas(){
            return this.areas;
        }
    }

}
