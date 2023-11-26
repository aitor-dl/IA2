package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class HelloApplication extends Application {

    private Stage primaryStage;
    private ComboBox<String> from;
    private ComboBox<String> to;
    private ToggleGroup mode;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Initial page with title and "Begin" button
        VBox initialLayout = createInitialLayout();
        Scene initialScene = new Scene(initialLayout, 500, 400);

        // Page with 3 buttons, 2 boxes, and "Start" button
        VBox selectionLayout = createSelectionLayout();
        Scene selectionScene = new Scene(selectionLayout, 500, 400);

        // Set the initial scene
        primaryStage.setTitle("Metro de Lyon");
        primaryStage.setScene(initialScene);
        primaryStage.show();
    }

    private VBox createInitialLayout() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Metro de Lyon");
        titleLabel.setStyle("-fx-font-size: 24px;");

        Button beginButton = new Button("Begin");
        beginButton.setOnAction(e -> primaryStage.setScene(getSelectionScene()));

        layout.getChildren().addAll(titleLabel, beginButton);
        return layout;
    }

    private VBox createSelectionLayout() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);

        mode = new ToggleGroup();

        RadioButton distance = new RadioButton("Distancia");
        RadioButton time = new RadioButton("Tiempo");
        RadioButton stop = new RadioButton("Transbordo");

        distance.setToggleGroup(mode);
        time.setToggleGroup(mode);
        stop.setToggleGroup(mode);

        Label labelfrom = new Label("From:");
        Label labelto = new Label("To:");

        from = new ComboBox<>();
        from.getItems().addAll("Vaulx-en-Velin La Soie", "Laurent Bonnevay Astroballe", "Cusset", "Flachet", "Gratte-Ciel", "République Villeurbanne", "Charpennes Charles Hernu", "Masséna", "Foch", "Hôtel De Ville Louis Pradel", "Cordeliers", "Bellecour", "Ampère Victor Hugo", "Perrache", "Oullins Gare", "Stade de Gerland", "Debourg", "Place Jean Jaurès", "Jean Macé", "Saxe Gambetta", "Place Guichard Bourse du Travail", "Gare Part-Dieu Vivier Merle", "Brotteaux", "Croix-Paquet", "Croix-Rousse", "Hénon", "Cuire", "Gare de Venissieux", "Parilly", "Mermoz Pinel", "Laënnec", "Grange Blanche", "Monplaisir–Lumière", "Sans-Souci", "Garibaldi", "Guillotière", "Fourviere", "Vieux Lyon Cathédrale St. Jean", "Minimes Theatres Romains", "Saint-Just", "Gorge De Loup", "Valmy", "Gare de Vaise");
        from.setPromptText("Choose one");

        to = new ComboBox<>();
        to.getItems().addAll("Vaulx-en-Velin La Soie", "Laurent Bonnevay Astroballe", "Cusset", "Flachet", "Gratte-Ciel", "République Villeurbanne", "Charpennes Charles Hernu", "Masséna", "Foch", "Hôtel De Ville Louis Pradel", "Cordeliers", "Bellecour", "Ampère Victor Hugo", "Perrache", "Oullins Gare", "Stade de Gerland", "Debourg", "Place Jean Jaurès", "Jean Macé", "Saxe Gambetta", "Place Guichard Bourse du Travail", "Gare Part-Dieu Vivier Merle", "Brotteaux", "Croix-Paquet", "Croix-Rousse", "Hénon", "Cuire", "Gare de Venissieux", "Parilly", "Mermoz Pinel", "Laënnec", "Grange Blanche", "Monplaisir–Lumière", "Sans-Souci", "Garibaldi", "Guillotière", "Fourviere", "Vieux Lyon Cathédrale St. Jean", "Minimes Theatres Romains", "Saint-Just", "Gorge De Loup", "Valmy", "Gare de Vaise");
        to.setPromptText("Choose one");

        Button startButton = new Button("Start");
        startButton.setOnAction(e -> {
            try {
                startHandler();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        });

        layout.getChildren().addAll(distance, time, stop, labelfrom, from, labelto, to, startButton);
        return layout;
    }

    private void startHandler() throws IOException, ParseException {
        if(mode.getSelectedToggle()==null){
            showAlert("Please select a mode");
            return;
        }
        if(from.getValue()==null|| to.getValue()==null){
            showAlert("Please fill in both boxes.");
            return;
        }
        // Get the selected values from the boxes
        System.out.println(from.getValue());
        System.out.println(to.getValue());
        RadioButton selectedMode= (RadioButton) mode.getSelectedToggle();
        System.out.println(selectedMode.getText());

        Astar astar = new Astar(from.getValue(), to.getValue(), selectedMode.getText());
        String answer = astar.algoritmo();
        System.out.println(answer);


        // Display a new page with a "Thank you!" message
        VBox thankYouLayout = new VBox(20);
        thankYouLayout.setAlignment(Pos.CENTER);

        Label thankYouLabel = new Label("Thank you!");
        thankYouLabel.setStyle("-fx-font-size: 24px;");

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> primaryStage.setScene(getSelectionScene()));
        thankYouLayout.getChildren().addAll(thankYouLabel, restartButton);

        Scene thankYouScene = new Scene(thankYouLayout, 500, 400);
        primaryStage.setScene(thankYouScene);
    }

    private Scene getSelectionScene() {
        Scene selectionScene = new Scene(createSelectionLayout(), 500, 400);

        return selectionScene;
    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}