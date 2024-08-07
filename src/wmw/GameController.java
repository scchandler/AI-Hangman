package wmw;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * This is the controller for the game screen.
 * This controller interacts with both Game.fxml and the Hangman class to effectively run the game
 */
public class GameController implements Initializable {


    private static int wrongCount = 0;
    private static String knownLetters = "";
    private static char currentGuess;

    @FXML
    public FlowPane flowPane;
    public Label letters;
    public Label locationsText;
    public Label numPossibleText;
    public Button locationsSubmit;
    public Button yesButton;
    public Button noButton;
    public Button nextButton;
    public TextField locationsTextBox;
    public Label centerText;
    public Button resetButton;

    public Circle stickHead;
    public Line stickBody;
    public Line stickArmL;
    public Line stickArmR;
    public Line stickLegL;
    public Line stickLegR;

    public Rectangle blackSquare;

    public Map<Character, Label> letterLabels;

    // Initializes this scene
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        blackSquare.setVisible(true);

        // Set the word (filled with underscores at the start)
        int wordLength = StartController.length;
        Hangman.setup(wordLength);
        letters.setText(Hangman.getCurrentWord());
        numPossibleText.setText(Hangman.getDictionaryLength() + " possible words");

        // Get the stage ready
        clearElements();
        clearHangMan();
        centerText.setVisible(true);
        centerText.setText("Are you ready for the first guess?");
        nextButton.setVisible(true);

        letterLabels = new HashMap<>();

        // Create labels for each letter in the alphabet
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            Label label = new Label(String.valueOf(letter));
            label.setStyle("-fx-text-fill: lightgray; -fx-font-size: 24px;");
            letterLabels.put(letter, label);
            flowPane.getChildren().add(label);
        }

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(.8), blackSquare);
        fadeTransition.setFromValue(1.0); // Full opacity (black)
        fadeTransition.setToValue(0.0); // No opacity (transparent)
        fadeTransition.setOnFinished(event -> blackSquare.setVisible(false)); // Hide the square when animation finishes

        fadeTransition.play();
    }

    // Used for updating the visual element showing all guessed letters
    public void changeLetterColor(char letter, String color) {
        Label label = letterLabels.get(Character.toUpperCase(letter));
        if (label != null) {
            label.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 24px;");
        }
    }

    // Used to quickly clear everything off the screen
    public void clearElements() {
        locationsText.setVisible(false);
        locationsTextBox.setVisible(false);
        locationsSubmit.setVisible(false);
        yesButton.setVisible(false);
        noButton.setVisible(false);
        nextButton.setVisible(false);
        centerText.setVisible(false);
    }

    // Used at the start to clear the stick figure
    public void clearHangMan() {
        stickHead.setVisible(false);
        stickArmL.setVisible(false);
        stickArmR.setVisible(false);
        stickBody.setVisible(false);
        stickLegL.setVisible(false);
        stickLegR.setVisible(false);
    }

    // Used to incrementally add limbs for every wrong guess by the AI
    public void addALimb() {
        switch(wrongCount) {
            case 1: stickHead.setVisible(true); break;
            case 2: stickBody.setVisible(true); break;
            case 3: stickArmL.setVisible(true); break;
            case 4: stickArmR.setVisible(true); break;
            case 5: stickLegL.setVisible(true); break;
            case 6: stickLegR.setVisible(true); break;
            case 7: clearElements(); break;
        }
    }

    // The method for the AI program making a guess
    @FXML
    public void makeGuess() {
        clearElements();
        if(Hangman.getDictionaryLength() == 1) {
            letters.setText(Hangman.currentDictionary.get(0));
            letters.setStyle("-fx-text-fill: lightgreen");
            centerText.setText("Your word is " + Hangman.currentDictionary.get(0));
            centerText.setVisible(true);
            resetButton.setVisible(true);
        } else {
            currentGuess = Hangman.analyze(Hangman.currentDictionary, knownLetters);
            knownLetters = knownLetters + currentGuess;
            centerText.setText("Does the word contain the letter \"" + currentGuess + "\"? (" + String.format("%.2f",Hangman.getPercentage()) + "%)");
            centerText.setVisible(true);
            yesButton.setVisible(true);
            noButton.setVisible(true);
        }
    }

    // User presses YES button
    @FXML
    public void yesPress() {
        clearElements();
        locationsText.setVisible(true);
        locationsTextBox.setVisible(true);
        locationsSubmit.setVisible(true);
        locationsSubmit.setDisable(true);
        changeLetterColor(currentGuess, "lime");
    }

    // User types number into the box
    @FXML
    public void enableLocationSubmit() {
        locationsSubmit.setDisable(false);
    }

    // Receives the correct guess and the locations of the character in the word
    @FXML
    public void finalizeLocations() {
        String[] positions = locationsTextBox.getText().split(",");
        Hangman.newLetters(currentGuess, positions);
        clearElements();
        letters.setText(Hangman.getCurrentWord());
        int numLeft = Hangman.getDictionaryLength();
        numPossibleText.setText(numLeft + " possible words");
        if(numLeft > 1) {
            centerText.setText("Are you ready for the next guess?");
        } else {
            centerText.setText("Hmmm... I wonder what it could be...");
        }
        centerText.setVisible(true);
        nextButton.setVisible(true);
    }

    // User presses NO button
    @FXML
    public void noPress() {
        clearElements();
        wrongCount++;
        addALimb();
        Hangman.badLetter(currentGuess);
        int numLeft = Hangman.getDictionaryLength();
        numPossibleText.setText(numLeft + " possible words");
        if(numLeft > 1) {
            centerText.setText("Are you ready for the next guess?");
        } else {
            centerText.setText("Hmmm... I wonder what it could be...");
        }
        centerText.setVisible(true);
        nextButton.setVisible(true);
        if(wrongCount > 6) {
            centerText.setText("I lost...");
            resetButton.setVisible(true);
        }
        changeLetterColor(currentGuess, "black");
    }

    // Used by the reset button to start over
    @FXML
    protected void start(ActionEvent event) throws IOException {
        wrongCount = 0;
        knownLetters = "";
        Hangman.reset();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("StartScreen.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
