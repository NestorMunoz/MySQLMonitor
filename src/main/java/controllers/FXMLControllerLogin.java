package controllers;

import java.io.IOException;
import java.sql.Connection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.ConexionSQL;

public class FXMLControllerLogin {
    
    private Connection myConn = null;
    private ConexionSQL connSQL;
    
    @FXML private TextArea userTextArea = null;
    @FXML private PasswordField passField = null;
    @FXML private TextArea dbTextArea = null;
    
    @FXML
    private void loginEvent(ActionEvent evt){
        
        String url = "jdbc:mysql://localhost:3306/"+dbTextArea.getText();
        String user = userTextArea.getText();
        String pass = passField.getText();
        
        ConexionSQL.setDb(dbTextArea.getText());
        connSQL = new ConexionSQL(url,user,pass);
        myConn = connSQL.createConection();
                
        if (myConn != null) {
            try {
                connSQL.safeClose();
                Stage primaryStage = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/FXMLDatabaseMain.fxml"));
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
                ((Node)(evt.getSource())).getScene().getWindow().hide();
                primaryStage.show();
            } catch (IOException ex) {
                System.out.println(ex+"\n");
            }
        }
    }   
}
