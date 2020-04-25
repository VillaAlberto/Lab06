/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.meteo;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.meteo.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class FXMLController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxMese"
    private ChoiceBox<Integer> boxMese; // Value injected by FXMLLoader

    @FXML // fx:id="btnUmidita"
    private Button btnUmidita; // Value injected by FXMLLoader

    @FXML // fx:id="btnCalcola"
    private Button btnCalcola; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

	private Model model;

    @FXML
    void doCalcolaSequenza(ActionEvent event) {
    	try { 
    		txtResult.setText(model.trovaSequenza(boxMese.getValue()));
    	}
    	catch(Exception e) {
    		txtResult.setText("Errore SQL");
    		e.printStackTrace();
    	}
    }

    @FXML
    void doCalcolaUmidita(ActionEvent event) {
    	try { 
    		txtResult.setText(model.getUmiditaMedia(boxMese.getValue()));
    	}
    	catch(Exception e) {
    		txtResult.setText("Errore SQL");
    		e.printStackTrace();
    		
    	}

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnUmidita != null : "fx:id=\"btnUmidita\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCalcola != null : "fx:id=\"btnCalcola\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }

	public void setModel(Model model) {
		this.model=model;
		List<Integer> mesi= new LinkedList<Integer>();
		for (int i=1; i<=12; i++)
			mesi.add(i);
		boxMese.getItems().addAll(mesi);
		boxMese.setValue(1);
	
		txtResult.setStyle("-fx-font-family: monospace");
		
	}
}

