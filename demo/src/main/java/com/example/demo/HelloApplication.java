package com.example.demo;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
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

        VBox initialLayout = createInitialLayout();
        Scene initialScene = new Scene(initialLayout, 500, 400);
        primaryStage.setTitle("Metro de Lyon");
        primaryStage.setScene(initialScene);
        primaryStage.show();
    }

    private VBox createInitialLayout() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #C3F1F6");

        Label titleLabel = new Label("Metro de Lyon");
        titleLabel.setStyle("-fx-font-size: 32px;");
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), titleLabel);
        translateTransition.setFromY(-50); // Initial Y position
        translateTransition.setToY(0);    // Final Y position
        translateTransition.setCycleCount(1); // Play animation once
        translateTransition.play();

        Image image = new Image(getClass().getResourceAsStream("/images/Ville_de_Lyon.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(300); // Set the width as needed
        imageView.setPreserveRatio(true); // Preserve the aspect ratio

        DoubleBinding widthBinding = primaryStage.widthProperty().multiply(0.6); // Adjust the factor as needed
        DoubleBinding heightBinding = primaryStage.heightProperty().multiply(0.6); // Adjust the factor as needed

        imageView.fitWidthProperty().bind(widthBinding);
        imageView.fitHeightProperty().bind(heightBinding);

        Button beginButton = new Button("Begin");
        beginButton.setStyle(
                "-fx-background-color: #EC3333;" + // red background color
                        "-fx-text-fill: white;" +           // White text color
                        "-fx-font-size: 16px;" +            // Font size
                        "-fx-padding: 10px 20px;"            // Padding
        );

        beginButton.setOnAction(e -> primaryStage.setScene(getSelectionScene()));

        layout.getChildren().addAll(titleLabel, imageView, beginButton);

        return layout;
    }

    private VBox createSelectionLayout() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #C3F1F6");
        GridPane buttons=new GridPane();
        GridPane frompane=new GridPane();
        GridPane topane=new GridPane();
        buttons.setAlignment(Pos.CENTER);
        buttons.setHgap(10);
        frompane.setAlignment(Pos.CENTER);
        frompane.setHgap(10);
        topane.setAlignment(Pos.CENTER);
        topane.setHgap(10);
        mode = new ToggleGroup();

        RadioButton distance = new RadioButton("Distancia");
        RadioButton time = new RadioButton("Tiempo");
        RadioButton stop = new RadioButton("Transbordo");

        buttons.addRow(0, distance, time, stop);
        distance.setToggleGroup(mode);
        time.setToggleGroup(mode);
        stop.setToggleGroup(mode);

        Image image = new Image(getClass().getResourceAsStream("/images/MetroDeLyon.jpg"));
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);

        DoubleBinding widthBinding = primaryStage.widthProperty().multiply(0.4); // Adjust the factor as needed
        DoubleBinding heightBinding = primaryStage.heightProperty().multiply(0.4); // Adjust the factor as needed

        imageView.fitWidthProperty().bind(widthBinding);
        imageView.fitHeightProperty().bind(heightBinding);

        Label labelfrom = new Label("Origen:" );
        Label labelto = new Label("Destino:");

        from = new ComboBox<>();
        from.getItems().addAll("Vaulx-en-Velin La Soie", "Laurent Bonnevay Astroballe", "Cusset", "Flachet", "Gratte-Ciel", "République Villeurbanne", "Charpennes Charles Hernu", "Masséna", "Foch", "Hôtel De Ville Louis Pradel", "Cordeliers", "Bellecour", "Ampère Victor Hugo", "Perrache", "Oullins Gare", "Stade de Gerland", "Debourg", "Place Jean Jaurès", "Jean Macé", "Saxe Gambetta", "Place Guichard Bourse du Travail", "Gare Part-Dieu Vivier Merle", "Brotteaux", "Croix-Paquet", "Croix-Rousse", "Hénon", "Cuire", "Gare de Venissieux", "Parilly", "Mermoz Pinel", "Laënnec", "Grange Blanche", "Monplaisir–Lumière", "Sans-Souci", "Garibaldi", "Guillotière", "Fourviere", "Vieux Lyon Cathédrale St. Jean", "Minimes Theatres Romains", "Saint-Just", "Gorge De Loup", "Valmy", "Gare de Vaise");
        from.setPromptText("Elige una opcion");

        to = new ComboBox<>();
        to.getItems().addAll("Vaulx-en-Velin La Soie", "Laurent Bonnevay Astroballe", "Cusset", "Flachet", "Gratte-Ciel", "République Villeurbanne", "Charpennes Charles Hernu", "Masséna", "Foch", "Hôtel De Ville Louis Pradel", "Cordeliers", "Bellecour", "Ampère Victor Hugo", "Perrache", "Oullins Gare", "Stade de Gerland", "Debourg", "Place Jean Jaurès", "Jean Macé", "Saxe Gambetta", "Place Guichard Bourse du Travail", "Gare Part-Dieu Vivier Merle", "Brotteaux", "Croix-Paquet", "Croix-Rousse", "Hénon", "Cuire", "Gare de Venissieux", "Parilly", "Mermoz Pinel", "Laënnec", "Grange Blanche", "Monplaisir–Lumière", "Sans-Souci", "Garibaldi", "Guillotière", "Fourviere", "Vieux Lyon Cathédrale St. Jean", "Minimes Theatres Romains", "Saint-Just", "Gorge De Loup", "Valmy", "Gare de Vaise");
        to.setPromptText("Elige una opcion");

        frompane.addRow(0, labelfrom, from);
        topane.addRow(0,labelto,to);

        Button startButton = new Button("Start");
        startButton.setStyle(
                "-fx-background-color: #EC3333;" + // red background color
                        "-fx-text-fill: white;" +           // White text color
                        "-fx-font-size: 16px;" +            // Font size
                        "-fx-padding: 10px 20px"            // Padding
        );
        startButton.setOnAction(e -> {
            try {
                startHandler();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        });
        layout.setFillWidth(true);
        layout.getChildren().addAll(buttons, imageView, frompane,topane, startButton);
        return layout;
    }

    private void startHandler() throws IOException, ParseException {
        if(mode.getSelectedToggle()==null){
            showAlert("Por favor, elige un cirterio de busqueda");
            return;
        }
        if(from.getValue()==null|| to.getValue()==null){
            showAlert("Por favor, elige un origen y un destino.");
            return;
        }

        System.out.println(from.getValue());
        System.out.println(to.getValue());
        RadioButton selectedMode= (RadioButton) mode.getSelectedToggle();
        System.out.println(selectedMode.getText());

        /*Astar astar = new Astar(from.getValue(), to.getValue(), selectedMode.getText());
        String ans = astar.algoritmo();
        System.out.println(ans);*/



        VBox thankYouLayout = new VBox(20);
        thankYouLayout.setAlignment(Pos.CENTER);
        thankYouLayout.setStyle("-fx-background-color: #C3F1F6");

        Label answer= new Label(selectedMode.getText() + ":");
        answer.setStyle("-fx-font-size: 24px;");

        Button restartButton = new Button("Restart");
        restartButton.setStyle(
                "-fx-background-color: #EC3333;" + // red background color
                        "-fx-text-fill: white;" +           // White text color
                        "-fx-font-size: 16px;" +            // Font size
                        "-fx-padding: 10px 20px"            // Padding
        );
        restartButton.setOnAction(e -> primaryStage.setScene(getSelectionScene()));
        thankYouLayout.getChildren().addAll(answer, restartButton);

        Scene thankYouScene = new Scene(thankYouLayout, primaryStage.getScene().getWidth(), primaryStage.getScene().getHeight());
        primaryStage.setScene(thankYouScene);
    }

    private Scene getSelectionScene() {
        Scene currentScene = primaryStage.getScene();
        Scene selectionScene = new Scene(createSelectionLayout(), currentScene.getWidth(), currentScene.getHeight());

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