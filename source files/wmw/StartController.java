package wmw;

import javafx.animation.*;
import javafx.application.Platform;
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
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This is the controller for the first scene.
 * It displays a welcome screen, shows a little animation, and queries the user for the word length
 */
public class StartController implements Initializable {
    public static int length;
    private final String LABEL_TEXT = "// You will think of an English word, and the AI program will // try to guess it! The AI will always make the best possible // guess. Just enter the number of letters in your word below // and then start the game.";


    @FXML
    public Label descriptionText;
    public TextField lengthPrompt;
    public Button startButton;

    //ensures that the user does not start the game without entering a number
    @FXML
    protected void enableButton() {
        startButton.setDisable(false);
    }

    // Loads the main game screen
    @FXML
    protected void start(ActionEvent event) throws IOException {
        length = Integer.parseInt(lengthPrompt.getText());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Game.fxml"));
        Parent root = loader.load();
        //GameController mc = loader.getController();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    //Initializes the start screen
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> startTypingAnimation(descriptionText));
    }

    // A little typing animation for aesthetics
    private void startTypingAnimation(Label label) {
        Timeline timeline = new Timeline();

        // Create a keyframe for each letter in the label text
        for (int i = 0; i < LABEL_TEXT.length(); i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(calculateDelay(index)), e -> {
                String text = LABEL_TEXT.substring(0, index + 1);
                label.setText(text + "|");
            });

            timeline.getKeyFrames().add(keyFrame);
        }

        // Set the final keyframe to display the complete text
        final KeyFrame finalKeyFrame = new KeyFrame(Duration.millis(calculateDelay(LABEL_TEXT.length())), e -> {
            label.setText(LABEL_TEXT + "|");
            // Create an animation for the blinking line
            final Animation animation = new Transition() {
                {
                    setCycleDuration(Duration.millis(1000));
                    setCycleCount(INDEFINITE);
                }

                @Override
                protected void interpolate(double fraction) {
                    if (fraction < 0.5) {
                        label.setText(LABEL_TEXT + "|");
                    } else {
                        label.setText(LABEL_TEXT);
                    }
                }
            };
            animation.play();
        });

        timeline.getKeyFrames().add(finalKeyFrame);
        timeline.play();
    }

    // Used for the delay on the typing animation
    private int calculateDelay(int index) {
        int letterDelay = 50;
        int pauseDelay = 220;
        int lettersPerPause = 60;

        int pauseCount = index / lettersPerPause;
        return (index + 1) * letterDelay + pauseCount * pauseDelay;
    }
}
