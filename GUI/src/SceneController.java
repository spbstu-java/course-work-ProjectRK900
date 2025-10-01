import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javafx.stage.FileChooser;
import lab1.*;
import lab2.*;
import lab3.*;
import lab4.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class SceneController {
    public enum Labs {
        LAB_1("Лабораторная 1"),
        LAB_2("Лабораторная 2"),
        LAB_3("Лабораторная 3"),
        LAB_4("Лабораторная 4");

        private final String displayName;

        Labs(String displayName) { this.displayName = displayName; }

        public String getDisplayName() { return displayName; }

        public static Labs fromDisplayName(String displayName) {
            for (Labs mode : values()) {
                if (mode.displayName.equals(displayName))
                    return mode;
            }

            throw new IllegalArgumentException("Unknown lab: " + displayName);
        }
    }


    private final String IOEXCEPTION_MESSAGE = "[Error] Can't read file";
    @FXML private ComboBox<Labs> labComboBox;


    // Lab 1 elements
    @FXML private VBox lab1Layer;
    private final ToggleGroup lab1StrategyTG = new ToggleGroup();
    @FXML private RadioButton walkStrategyRB;
    @FXML private RadioButton runStrategyRB;
    @FXML private RadioButton horseStrategyRB;
    @FXML private RadioButton flyStrategyRB;
    @FXML private RadioButton swimStrategyRB;
    @FXML private TextField lab1FromTF;
    @FXML private TextField lab1ToTF;
    @FXML private TextArea lab1OutputTA;
    @FXML private Button lab1StartBut;
    private String prevDestination;
    private Lab1.Hero lab1Hero;
    private Lab1.MoveStrategy curStrategy;


    // Lab 2 elements
    @FXML private VBox lab2Layer;
    @FXML private TextField lab2ClassTF;
    @FXML private ComboBox<Method> lab2MethodsCB;
    @FXML private CheckBox lab2AllMethodsCB;
    @FXML private CheckBox lab2HasPropertyCB;
    @FXML private TextArea lab2OutputTA;
    @FXML private Label lab2ModifierLabel;
    private Lab2.AnnotationTestClass obj;


    // Lab 3 elements
    @FXML private VBox lab3Layer;
    @FXML private TextArea lab3DictionaryTA;
    @FXML private TextArea lab3UserTextTA;
    @FXML private TextArea lab3TranslateTA;
    @FXML private Button lab3ChangeDictFileBut;
    @FXML private Button lab3ChangeUserTextFileBut;
    private File lab3PrevFileDirectory;
    private File lab3DictionaryPath;
    private File lab3UserTextPath;


    // Lab 4 elements
    @FXML private VBox lab4Layer;
    @FXML private ComboBox<Method> lab4MethodsCB;
    @FXML private TextField lab4ArgumentsTF;
    @FXML private Label lab4ArgumentsLabel;
    @FXML private TextArea lab4OutputTA;


    @FXML
    public void initialize() {
        labComboBox.getItems().setAll(Labs.values());
        labComboBox.setCellFactory(lv -> new ListCell<Labs>() {
            @Override
            protected void updateItem(Labs item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getDisplayName());
            }
        });
        labComboBox.setButtonCell(new ListCell<Labs>() {
            @Override
            protected void updateItem(Labs item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getDisplayName());
            }
        });

        labComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, oldValue, newValue) ->
                                switchMode(newValue)
                );

        labComboBox.getSelectionModel().selectFirst();
    }

    private void switchMode(Labs lab) {
        // Hide all layers
        lab1Layer.setVisible(false);
        lab2Layer.setVisible(false);
        lab3Layer.setVisible(false);
        lab4Layer.setVisible(false);

        // Show one layer
        switch (lab) {
            case LAB_1:
                lab1Layer.setVisible(true);
                initializeLab1();
                break;
            case LAB_2:
                lab2Layer.setVisible(true);
                initializeLab2();
                break;
            case LAB_3:
                lab3Layer.setVisible(true);
                initializeLab3();
                break;
            case LAB_4:
                lab4Layer.setVisible(true);
                initializeLab4();
                break;
        }
    }

    // =========================== Lab 1 ===========================
    private void initializeLab1() {
        walkStrategyRB.setToggleGroup(lab1StrategyTG);
        walkStrategyRB.setUserData("WALK");
        runStrategyRB.setToggleGroup(lab1StrategyTG);
        runStrategyRB.setUserData("RUN");
        horseStrategyRB.setToggleGroup(lab1StrategyTG);
        horseStrategyRB.setUserData("HORSE");
        flyStrategyRB.setToggleGroup(lab1StrategyTG);
        flyStrategyRB.setUserData("FLY");
        swimStrategyRB.setToggleGroup(lab1StrategyTG);
        swimStrategyRB.setUserData("SWIM");

        lab1StrategyTG.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null)
                lab1ChangeStrategy((String)newVal.getUserData());
        });

        walkStrategyRB.setSelected(true);

        lab1FromTF.setText(prevDestination);
        lab1OutputTA.setText("");
        lab1StartBut.setDisable(true);
        lab1StartBut.setTextFill(Color.RED);

        ChangeListener<String> tfListener = (observable, oldValue, newValue) -> {
            lab1CheckTextFields();
        };
        lab1FromTF.textProperty().addListener(tfListener);
        lab1ToTF.textProperty().addListener(tfListener);

        lab1Hero = new Lab1.Hero(curStrategy);
    }

    private void lab1ChangeStrategy(String type) {
        switch (type) {
            case "WALK":
                curStrategy = new Lab1.WalkStrategy();
                break;
            case "RUN":
                curStrategy = new Lab1.RunStrategy();
                break;
            case "HORSE":
                curStrategy = new Lab1.HorseRideStrategy();
                break;
            case "FLY":
                curStrategy = new Lab1.FlyStrategy();
                break;
            case "SWIM":
                curStrategy = new Lab1.SwimStrategy();
                break;
        }
    }

    public void lab1StartExpedition(ActionEvent actionEvent) {
        lab1Hero.setMoveStrategy(curStrategy);

        prevDestination = lab1ToTF.getText();
        String message = lab1Hero.move(lab1FromTF.getText(), prevDestination);
        String prevText = lab1OutputTA.getText();
        lab1OutputTA.setText(prevText.isEmpty() ? message : prevText + "\n" + message);

        lab1FromTF.setText(prevDestination);
        lab1ToTF.setText("");
    }

    public void lab1CheckTextFields() {
        if (lab1FromTF.getText().isEmpty() || lab1ToTF.getText().isEmpty()) {
            lab1StartBut.setDisable(true);
            lab1StartBut.setTextFill(Color.RED);
        }
        else {
            lab1StartBut.setDisable(false);
            lab1StartBut.setTextFill(Color.BLACK);
        }

        if (!lab1FromTF.getText().isEmpty())
            prevDestination = lab1FromTF.getText();
    }

    // =========================== Lab 2 ===========================
    private void initializeLab2() {
        obj = new Lab2.AnnotationTestClass();
        var objClass = obj.getClass();

        lab2ClassTF.setText(objClass.getName());

        lab2MethodsCB.setButtonCell(new ListCell<Method>() {
            @Override
            protected void updateItem(Method item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });
        lab2MethodsCB.setCellFactory(lv -> new ListCell<Method>() {
            @Override
            protected void updateItem(Method item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });

        var methods = objClass.getDeclaredMethods();
        lab2MethodsCB.getItems().addAll(methods);
        lab2MethodsCB.getSelectionModel().selectFirst();
        lab2SwitchMethod(Arrays.stream(methods).findFirst().orElse(null));
        lab2MethodsCB.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, oldValue, newValue) ->
                                lab2SwitchMethod(newValue)
                );
    }

    private void lab2SwitchMethod(Method method) {
        if (method == null)
            return;

        lab2HasPropertyCB.selectedProperty().set(method.isAnnotationPresent(Lab2.Repeat.class));
        if (method.isAnnotationPresent(Lab2.Repeat.class))
            lab2HasPropertyCB.setText("Имеет аннотацию Repeat (" + method.getAnnotation(Lab2.Repeat.class).value() + ")");
        else
            lab2HasPropertyCB.setText("Имеет аннотацию Repeat");

        var modifier = method.getModifiers();
        if (Modifier.isPrivate(modifier))
            lab2ModifierLabel.setText("Модификатор доступа: private");
        else if (Modifier.isProtected(modifier))
            lab2ModifierLabel.setText("Модификатор доступа: protected");
        else if (Modifier.isPublic(modifier))
            lab2ModifierLabel.setText("Модификатор доступа: public");
        else
            lab2ModifierLabel.setText("Модификатор доступа:");
    }

    public void lab2ExecuteMethodButton(ActionEvent actionEvent) {
        if (lab2AllMethodsCB.selectedProperty().get())
            lab2OutputTA.setText(Lab2.beginAll(obj));
        else
            lab2OutputTA.setText(Lab2.executeMethod(obj, lab2MethodsCB.getSelectionModel().getSelectedItem()));
    }

    // =========================== Lab 3 ===========================
    private void initializeLab3() {
        if (lab3DictionaryPath != null && lab3DictionaryPath.exists()) {
            try {
                lab3DictionaryTA.setText(Files.readString(Path.of(lab3DictionaryPath.getPath())));
            }
            catch (IOException e) {
                lab3DictionaryTA.setText(IOEXCEPTION_MESSAGE);
            }
        }

        if (lab3UserTextPath != null && lab3UserTextPath.exists()) {
            try {
                lab3UserTextTA.setText(Files.readString(Path.of(lab3UserTextPath.getPath())));
            }
            catch (IOException e) {
                lab3UserTextTA.setText(IOEXCEPTION_MESSAGE);
            }
        }
    }

    public void lab3ChangeDictionaryFile(ActionEvent actionEvent) {
        var fileChooser = new FileChooser();
        if (lab3PrevFileDirectory != null && lab3PrevFileDirectory.exists())
            fileChooser.setInitialDirectory(lab3PrevFileDirectory);

        var extFilter = new FileChooser.ExtensionFilter("Текстовые файлы (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showOpenDialog(lab3ChangeDictFileBut.getScene().getWindow());
        if (selectedFile != null) {
            lab3PrevFileDirectory = selectedFile.getParentFile();
            lab3DictionaryPath = selectedFile;
            try {
                lab3DictionaryTA.setText(Files.readString(Path.of(lab3DictionaryPath.getPath())));
            }
            catch (IOException e) {
                lab3DictionaryTA.setText(IOEXCEPTION_MESSAGE);
            }
        }
    }

    public void lab3ChangeTextFile(ActionEvent actionEvent) {
        var fileChooser = new FileChooser();
        if (lab3PrevFileDirectory != null && lab3PrevFileDirectory.exists())
            fileChooser.setInitialDirectory(lab3PrevFileDirectory);

        var extFilter = new FileChooser.ExtensionFilter("Текстовые файлы (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showOpenDialog(lab3ChangeUserTextFileBut.getScene().getWindow());
        if (selectedFile != null) {
            lab3PrevFileDirectory = selectedFile.getParentFile();
            lab3UserTextPath = selectedFile;
            try {
                lab3UserTextTA.setText(Files.readString(Path.of(lab3UserTextPath.getPath())));
            }
            catch (IOException e) {
                lab3UserTextTA.setText(IOEXCEPTION_MESSAGE);
            }
        }
    }

    public void lab3TranslateText(ActionEvent actionEvent) {
        String userText = lab3UserTextTA.getText();
        if (userText.isEmpty()) {
            lab3TranslateTA.setText("[Error] Не заполнен текст для перевода");
            return;
        }

        String dictionary = lab3DictionaryTA.getText();
        if (dictionary.isEmpty()) {
            lab3TranslateTA.setText("[Error] Отсутствует словар");
            return;
        }

        try {
            var translator = new Translator(Translator.parseDictionary(dictionary));
            lab3TranslateTA.setText(translator.translate(userText));
        }
        catch (InvalidFileFormatException e) {
            lab3TranslateTA.setText(e.toString());
        }
    }

    // =========================== Lab 4 ===========================
    private void initializeLab4() {
        lab4MethodsCB.setButtonCell(new ListCell<Method>() {
            @Override
            protected void updateItem(Method item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });
        lab4MethodsCB.setCellFactory(lv -> new ListCell<Method>() {
            @Override
            protected void updateItem(Method item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });

        var methods = Arrays.stream(Lab4.CollectionStreamAPIExamples.class.getDeclaredMethods())
                .filter(method -> !method.isSynthetic())  // lambda filter
                .toArray(Method[]::new);
        lab4MethodsCB.getItems().addAll(methods);
        lab4MethodsCB.getSelectionModel().selectFirst();
        lab4SwitchMethod(Arrays.stream(methods).findFirst().orElse(null));
        lab4MethodsCB.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, oldValue, newValue) ->
                                lab4SwitchMethod(newValue)
                );
    }

    public void lab4SwitchMethod(Method method) {
        var message = "Аргументы выбранного метода: ";
        message += method != null ? Lab4.getSimpleParameterTypes(method) : "<Не удалось извлечь метод>";
        lab4ArgumentsLabel.setText(message);
    }

    public void lab4ExecuteMethodButton(ActionEvent actionEvent) {
        var method = lab4MethodsCB.getSelectionModel().getSelectedItem();
        try {
            lab4OutputTA.setText(Lab4.invokeMethodSimple(method, null, lab4ArgumentsTF.getText()));
        } catch (Exception e) {
            lab4OutputTA.setText(e.toString());
        }
    }
}
