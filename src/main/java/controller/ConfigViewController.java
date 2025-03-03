/* 
 * Copyright Gil THOMAS
 * This file forms an integral part of Logfly project
 * See the LICENSE file distributed with source code
 * for details of Logfly licence project
 */
package controller;

import dialogues.alertbox;
import dialogues.dialogbox;
import java.awt.Toolkit;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import littlewins.winCoord;
import littlewins.winDirChoose;
import littlewins.winFileChoose;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
import settings.configProg;
import settings.fileType;
import settings.listCarte;
import settings.listGPS;
import settings.listLangues;
import settings.listLeague;
import settings.listStartwin;
import settings.listSynthese;
import settings.listTypeYear;
import systemio.filesmove;
import systemio.mylogging;

/**
 * FXML Controller class
 *
 * @author Gil Thomas logfly.org
 */
public class ConfigViewController {

    @FXML
    private Tab tabCarnet;    
    @FXML
    private Label lbWorkingPath;
    @FXML
    private Label lbWorkingLog;   
    @FXML
    private Label lbByYear;
    @FXML
    private Button btNewLog;    
    @FXML
    private Button btMoveLog;    
    @FXML
    private Button btSelectLog;    
    @FXML
    private Button btRestore;     
    @FXML
    private Button btCarnetClose;    
    @FXML
    private Button btCarnetEdit;    
    @FXML
    private Tab tabPilote;   
    @FXML
    private Label lbPilote;
    @FXML
    private Label lbVoile;
    @FXML
    private Label lbGPS;
    @FXML
    private Label lbIntegration;
    @FXML
    private Label lbLimite;
    @FXML
    private Label lbMail;
    @FXML
    private Label lbLeague;
    @FXML
    private Label lbIdentif;
    @FXML
    private Label lbPass;
    @FXML
    private Button btPilotAnnuler;
    @FXML
    private Button btPilotOK;
    @FXML
    private Tab tabCarte;    
    @FXML
    private Tab tabDivers;    
    @FXML
    private Tab tabInternet;    
    @FXML
    private Label lbCurrFolder;    
    @FXML
    private Label lbCurrDbPath;    
    @FXML
    private ChoiceBox chbCarnet;    
    @FXML
    private ChoiceBox chbByYear;
    @FXML
    private Label lbStartwin;
    @FXML
    private ChoiceBox chbStartwin;
    @FXML
    private TextField txPilote;    
    @FXML
    private TextField txVoile;    
    @FXML
    private ChoiceBox chbGPS;    
    @FXML
    private TextField txIntegration;    
    @FXML
    private TextField txLimite;    
    @FXML
    private TextField txMailPilote;    
    @FXML
    private ChoiceBox chbLeague;    
    @FXML
    private TextField txIdentif;    
    @FXML
    private TextField txPass;    
    @FXML
    private CheckBox checkBrowser;    
    @FXML
    private ChoiceBox chbCarte;  
    @FXML
    private Label lbLocMap;
    @FXML
    private TextField txFinderLat;    
    @FXML
    private TextField txFinderLong;    
    @FXML
    private TextField txSeuilAb;    
    @FXML
    private ChoiceBox chbLang;  
    @FXML
    private Label lbSynthese;
    @FXML
    private ChoiceBox chbSynthese;
    @FXML
    private Label lbImpFolder;    
    @FXML
    private Label lbSyride;
    @FXML
    private Label lbSyridePath;
    @FXML
    private TextField txUrlSite;    
    @FXML
    private TextField txUrlIcones;    
    @FXML
    private TextField txVisuUpload;      
    @FXML
    private TextField txVisuGPS;    
    @FXML
    private TextField txMailPass;    
    @FXML
    private TextField txUrlContest;    
    @FXML
    private Label lbContestPath;    
    @FXML
    private TextField txnewdb;    
    @FXML
    private Button btnewcarnetok;    
    @FXML
    private Button btnewcarnetcancel;
    @FXML
    private Label lbNavigateur;
    @FXML
    private Label lbDefaultMap;
    @FXML
    private Label lbLatitude;
    @FXML
    private Label lbLongitude;
    @FXML
    private Label lbAberrants;
    @FXML
    private Button btMapMap;    
    @FXML
    private Button btMapOK;
    @FXML
    private Button btMapAnnuler;
    @FXML
    private Label lbLanguage;
    @FXML
    private Label lbImport;
    @FXML
    private Label lbPhotoAuto;
    @FXML
    private CheckBox checkPhoto;
    @FXML
    private Label lbUpdateAuto;  
    @FXML
    private CheckBox checkUpdate;
    @FXML
    private Label lbDebug;  
    @FXML
    private CheckBox checkDebug;    
    @FXML
    private Button btDiversAnnuler;
    @FXML
    private Button btDiversOk;
    @FXML
    private Label lbLogfly;
    @FXML
    private Label lbIcones;
    @FXML
    private Label lbLoadUrl;
    @FXML
    private Label lbVisuAdress;
    @FXML
    private Label lbSupport;
    @FXML
    private Label lbDeclaration;
    @FXML
    private Label lbExport;
    @FXML
    private Button btWebAnnuler;
    @FXML
    private Button btWebOk;       
    
    // Reference to the main application.
    private RootLayoutController rootController;
    
    private Stage dialogStage;
    
    private configProg myConfig = new configProg();
    private Pattern validDoubleText = Pattern.compile("-?((\\d*)|(\\d+\\.\\d*))");
    private Paint colorBadValue = Paint.valueOf("FA6C04");
    private Paint colorGoodValue = Paint.valueOf("FFFFFF");    
    
    private static I18n i18n;
    private StringBuilder sbError;
            

    @FXML
    private void initialize() {   
        txLimite.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    txLimite.setText(newValue.replaceAll("[^\\d]", ""));
                } else if (newValue != null && !newValue.equals("")) {
                    int iVal = Integer.parseInt(newValue);
                    if (iVal > 100) txLimite.setText("100");
                }
            }
        });
        
        txIntegration.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    txIntegration.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });  

        TextFormatter<Double> fmLat = new TextFormatter<Double>(new DoubleStringConverter(), 0.0, 
            change -> {
                String newText = change.getControlNewText() ;
                if (validDoubleText.matcher(newText).matches()) {
                    return change ;
                } else return null ;
            });                
        fmLat.valueProperty().addListener((obs, oldValue, newValue) -> {            
                if (newValue < -90 || newValue > 90) {                      
                    txFinderLat.setStyle("-fx-control-inner-background: #"+colorBadValue.toString().toString().substring(2));
                    txFinderLat.requestFocus();                
                } else {
                    txFinderLat.setStyle("-fx-control-inner-background: #"+colorGoodValue.toString().substring(2));  
                }            
        });        
        txFinderLat.setTextFormatter(fmLat); 
        
        TextFormatter<Double> fmLong = new TextFormatter<Double>(new DoubleStringConverter(), 0.0, 
            change -> {                                                                                                                                                                                                                                                                                                                                                                                                                       
                String newText = change.getControlNewText() ;
                if (validDoubleText.matcher(newText).matches()) {
                    return change ;
                } else return null ;
            });                
        fmLong.valueProperty().addListener((obs, oldValue, newValue) -> {            
                if (newValue < -180 || newValue > 180) {                      
                    txFinderLong.setStyle("-fx-control-inner-background: #"+colorBadValue.toString().toString().substring(2));
                    txFinderLong.requestFocus();                
                } else {
                    txFinderLong.setStyle("-fx-control-inner-background: #"+colorGoodValue.toString().substring(2));  
                }            
        });        
        txFinderLong.setTextFormatter(fmLong);  
        
        txSeuilAb.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    txSeuilAb.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        
        chbByYear.setOnAction((event) -> {
            myConfig.setIdxTypeYear(chbByYear.getSelectionModel().getSelectedIndex());
        });
        
    }    
    
    public void setMyConfig(configProg mainConfig) {
        this.myConfig = mainConfig;
        i18n = I18nFactory.getI18n("","lang/Messages",ConfigViewController.class.getClass().getClassLoader(),myConfig.getLocale(),0);
        winTraduction();
        
        // Fields will be filled after myConfig reading
        ObservableList <String> listDb;
        int idxDb = 0;                
        
        listDb = FXCollections.observableArrayList();
               
        
        // Search all existing db files in the db folder (pathDb)
        File folderDb = new File(myConfig.getPathDb());
        if (folderDb.exists() && folderDb.isDirectory() ) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                   return name.endsWith(".db");
                }                
            };
            File[] files = new File(myConfig.getPathDb()).listFiles(filter);
            iniChbCarnet(files);            
        }    
        
        // Logbook Tab
        lbCurrFolder.setText(myConfig.getPathW());
        lbCurrDbPath.setText(myConfig.getPathDb());
        iniChbYear();
        
        // Pilot Tab
        txPilote.setText(myConfig.getDefaultPilote());
        txVoile.setText(myConfig.getDefaultVoile());
        iniChbGPS();
        txIntegration.setText(String.valueOf(myConfig.getIntegration()));
        txLimite.setText(String.valueOf(myConfig.getGpsLimit()));
        txMailPilote.setText(myConfig.getPiloteMail());
        iniChbLeague();
        txIdentif.setText(myConfig.getPiloteID());
        txPass.setText(myConfig.getPilotePass());
        
        // Map Tab
        if (myConfig.isVisuGPSinNav())  {
            checkBrowser.setSelected(true);
        } else {
            checkBrowser.setSelected(false);
        }
        iniChbCarte();
        txFinderLat.setText(myConfig.getFinderLat());
        txFinderLong.setText(myConfig.getFinderLong());
        txSeuilAb.setText(String.valueOf(myConfig.getSeuilAberrants()));
        
        // Miscellaneous Tab
        iniChbLang();
        iniChbSynthese();
        iniChbStartwin();
        lbImpFolder.setText(myConfig.getPathImport());
        lbSyridePath.setText(myConfig.getPathSyride());
        if (myConfig.isPhotoAuto())  {
            checkPhoto.setSelected(true);
        } else {
            checkPhoto.setSelected(false);
        }
        if (myConfig.isUpdateAuto())  {
            checkUpdate.setSelected(true);
        } else {
            checkUpdate.setSelected(false);
        }
        if (myConfig.isDebugMode())  {
            checkDebug.setSelected(true);
        } else {
            checkDebug.setSelected(false);
        }        
        // Internet Tab
        txUrlSite.setText(myConfig.getUrlLogfly());
        txUrlIcones.setText(myConfig.getUrlIcones());
        txVisuUpload.setText(myConfig.getUrlLogflyIGC());
        txVisuGPS.setText(myConfig.getUrlVisu());
        txMailPass.setText(myConfig.getMailPass());
        txUrlContest.setText(myConfig.getUrlContest());
        lbContestPath.setText(myConfig.getPathContest());     
        
    }
    
    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
             
    /**
     * Validate changes
     */
    @FXML
    private void handleOk() {
        if (myConfig.isValidConfig()) {                
            // Onglet Pilote
            myConfig.setDefaultPilote(txPilote.getText());
            myConfig.setDefaultVoile(txVoile.getText());
            myConfig.setIdxGPS(chbGPS.getSelectionModel().getSelectedIndex()); 
            myConfig.setIntegration(Integer.parseInt(txIntegration.getText()));
            myConfig.setGpsLimit(Integer.parseInt(txLimite.getText()));
            myConfig.setPiloteMail(txMailPilote.getText());
            myConfig.setIdxLeague(chbLeague.getSelectionModel().getSelectedIndex());
            myConfig.setPiloteID(txIdentif.getText());
            myConfig.setPilotePass(txPass.getText());
            // Onglet Cartes
            myConfig.setVisuGPSinNav(checkBrowser.isSelected());
            myConfig.setIdxMap(chbCarte.getSelectionModel().getSelectedIndex()); 
            myConfig.setFinderLat(txFinderLat.getText());
            myConfig.setFinderLong(txFinderLong.getText());
            myConfig.setSeuilAberrants(Integer.parseInt(txSeuilAb.getText()));
            // Onglet Divers
            myConfig.setIdxLang(chbLang.getSelectionModel().getSelectedIndex());
            myConfig.setIdxSynthese(chbSynthese.getSelectionModel().getSelectedIndex());
            myConfig.setIdxStartwin(chbStartwin.getSelectionModel().getSelectedIndex());
            myConfig.setLocale(chbLang.getSelectionModel().getSelectedIndex());
            myConfig.setPathImport(lbImpFolder.getText());
            myConfig.setPathSyride(lbSyridePath.getText());
            myConfig.setPhotoAuto(checkPhoto.isSelected());
            myConfig.setUpdateAuto(checkUpdate.isSelected());
            myConfig.setDebugMode(checkDebug.isSelected());
            // Onglet Internet
            myConfig.setUrlLogfly(txUrlSite.getText());
            myConfig.setUrlIcones(txUrlIcones.getText());
            myConfig.setUrlLogflyIGC(txVisuUpload.getText());
            myConfig.setUrlVisu(txVisuGPS.getText());
            myConfig.setMailPass(txMailPass.getText());
            myConfig.setUrlContest(txUrlContest.getText());             
        } else {
            alertbox aError = new alertbox(myConfig.getLocale());
            aError.alertInfo(i18n.tr("Unresolved settings problem"));  
            System.exit(0);
        }
        
        dialogStage.close();
    }
    
    /**
     * Discard changes
     */
    @FXML
    private void handleCancel() {
        if (myConfig.isValidConfig()) {
            dialogStage.close();
        } else {
            alertbox aError = new alertbox(myConfig.getLocale());
            aError.alertInfo(i18n.tr("Unresolved settings problem"));  
            System.exit(0);
        }
    }
    
    /**
     * New logbook creation
     */
    @FXML
    private void displayTextCarnet() {
        txnewdb.setVisible(true);
        txnewdb.requestFocus();
        btnewcarnetok.setVisible(true);
        btnewcarnetcancel.setVisible(true);
    }
    
    /**
     * New logbook creation is cancelled     
     */
    @FXML
    private void annulNewCarnet() {
        txnewdb.setVisible(false);
        btnewcarnetok.setVisible(false);
        btnewcarnetcancel.setVisible(false);        
    }
    
    /**
     * Name is checked, SQLIte file is created     
     */
    @FXML
    private void createNewCarnet() {
        // Création nouvelle db
        String dbNewName = checkNewDbName(txnewdb.getText());
        if (dbNewName != null && !dbNewName.isEmpty())  {
            dbNewName = dbNewName+".db";            
            if (myConfig.dbNewOne(dbNewName)) {
                rootController.changeCarnetView();
                txnewdb.setVisible(false);
                btnewcarnetok.setVisible(false);
                btnewcarnetcancel.setVisible(false); 
                myConfig.setValidConfig(true);
            }                      
        } else {
            // No beep with JavaFX, awt is necessary            
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    /**
     * Working folder is modified
     */
    @FXML
    private void changePathW() {
        winDirChoose wd = new winDirChoose(myConfig, i18n, 2, null);
        File selectedDirectory = wd.getSelectedFolder();
        if(selectedDirectory.exists() && selectedDirectory.isDirectory()){
            lbCurrFolder.setText(selectedDirectory.getAbsolutePath());   
            myConfig.setPathW(selectedDirectory.getAbsolutePath());
        }              
    }
    
    /**
     * Import folder is modified
     * Import folder is the usaul folder to import GPS tracks
     */
    @FXML
    private void changePathImport() {
        winDirChoose wd = new winDirChoose(myConfig, i18n, 7, null);
        File selectedDirectory = wd.getSelectedFolder();
        if(selectedDirectory.exists() && selectedDirectory.isDirectory()){
            lbImpFolder.setText(selectedDirectory.getAbsolutePath());   
        }        
    }  
    
    @FXML
    private void handleMapMap() {
        
        String sLat = txFinderLat.getText();
        String sLong = txFinderLong.getText();
        if (sLat != null && sLong != null) {
            try {
                double testLat = Double.parseDouble(sLat);     
                double testLong = Double.parseDouble(sLong);                 
            } catch (Exception e) {
                // settings not valid, we put Annecy Lake
                sLat = "45.863";
                sLong = "6.1725";                       
            } finally {
                winCoord myWinCoord = new winCoord(myConfig, sLat, sLong, null);
                if (myWinCoord.getMapLat() != null) {
                    txFinderLat.setText(myWinCoord.getMapLat());
                }
                if (myWinCoord.getMapLong() != null) {
                    txFinderLong.setText(myWinCoord.getMapLong());
                }
            }                         
        }
    }
    
    @FXML
    private void changePathSyride() {
        winDirChoose wd = new winDirChoose(myConfig, i18n, 8, null);
        File selectedDirectory = wd.getSelectedFolder();
        if(selectedDirectory.exists() && selectedDirectory.isDirectory()){
            lbSyridePath.setText(selectedDirectory.getAbsolutePath());   
        }                       
    }
    
    private void copyInNewFolder(Path srcPath, File selectedFile) {
        
        alertbox aError = new alertbox(myConfig.getLocale());
        
        winDirChoose wd = new winDirChoose(myConfig, i18n, 3, null);
        File selectedDirectory = wd.getSelectedFolder();
        if(selectedDirectory.exists() && selectedDirectory.isDirectory()){
            try {
                Path dstPath = Paths.get(selectedDirectory.getAbsolutePath()+File.separator+selectedFile.getName());
                Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);  
                myConfig.setPathDb(selectedDirectory.getAbsolutePath()+File.separator);  
                myConfig.setFullPathDb(selectedDirectory.getAbsolutePath()+File.separator+selectedFile.getName());
                myConfig.setDbName(selectedFile.getName());    
                if (myConfig.dbCheck(myConfig.getFullPathDb())) {
                    myConfig.setValidConfig(true);
                    rootController.changeCarnetView();
                    myConfig.setValidConfig(true);
                    dialogStage.close();
                } else {                   
                    aError.alertNumError(100); // db connection problem
                }                
            } catch (Exception ex) {
                sbError = new StringBuilder(this.getClass().getName()+"."+Thread.currentThread().getStackTrace()[1].getMethodName());
                sbError.append("\r\n").append(ex.toString());
                mylogging.log(Level.SEVERE, sbError.toString());  
                aError.alertError(ex.getClass().getName() + ": " + ex.getMessage());                 
            }                    
        }        
    }
    
    /*
    * In version
    */
    @FXML
    private void restoreBackup() {
        alertbox aError = new alertbox(myConfig.getLocale());
        
        winFileChoose wf = new winFileChoose(myConfig, i18n, fileType.db, null);  
        File selectedFile = wf.getSelectedFile();
        if (selectedFile != null && selectedFile.exists()) { 
            Path srcPath = Paths.get(selectedFile.getAbsolutePath());
            File folderDb = new File(myConfig.getPathDb());
            if (folderDb.exists() && folderDb.isDirectory() ) {
                dialogbox dConfirm = new dialogbox(i18n);
                StringBuilder sbMsg = new StringBuilder();                 
                sbMsg.append(i18n.tr("Copy in")).append(" :").append(myConfig.getPathDb());
                int myChoice = dConfirm.twoChoices(i18n.tr("Logbook restore"), sbMsg.toString(), i18n.tr("Yes"), i18n.tr("Other folder"), i18n.tr("Cancel"));
                switch (myChoice) {
                    case 1 :
                        try {
                            Path dstPath = Paths.get(myConfig.getPathDb()+File.separator+selectedFile.getName());
                            Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);            
                            changeDb(selectedFile.getName());
                            myConfig.setValidConfig(true);
                            dialogStage.close();
                        } catch (Exception ex) {
                            sbError = new StringBuilder(this.getClass().getName()+"."+Thread.currentThread().getStackTrace()[1].getMethodName());
                            sbError.append("\r\n").append(ex.toString());
                            mylogging.log(Level.SEVERE, sbError.toString());  
                            aError.alertError(ex.getClass().getName() + ": " + ex.getMessage());                          
                        }
                        break;
                    case 2 :
                        copyInNewFolder(srcPath, selectedFile);
                        break;
                }          
            } else {
                // Folder db no longer exists
                copyInNewFolder(srcPath, selectedFile);
            }          
            
            
        }        
    }
    
    /**
     * Path for online contest is modified 
     */
    @FXML
    private void changePathContest() {
        winDirChoose wd = new winDirChoose(myConfig, i18n, 4, null);
        File selectedDirectory = wd.getSelectedFolder();
        if(selectedDirectory.exists() && selectedDirectory.isDirectory()){
            lbContestPath.setText(selectedDirectory.getAbsolutePath());   
            myConfig.setPathContest(selectedDirectory.getAbsolutePath());
        }        
    }  
    
    /**
     * Db folder is modified
     * all SQLire files .db are moved in the new folder
     * @throws InterruptedException 
     */
    @FXML
    private void moveDb() throws InterruptedException {
        winDirChoose wd = new winDirChoose(myConfig, i18n, 5, null);
        File selectedDirectory = wd.getSelectedFolder();
        if(selectedDirectory.exists() && selectedDirectory.isDirectory()){
            File folderDb = new File(myConfig.getPathDb());
            if (folderDb.exists() && folderDb.isDirectory() ) {
                FilenameFilter filter = new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".db");                   
                    }                
                };
                File[] files = new File(myConfig.getPathDb()).listFiles(filter);
                String newPath = selectedDirectory.getAbsolutePath()+myConfig.getOsSeparator();                                       
                filesmove transDb = new filesmove(files, newPath, myConfig.getLocale());
                if (transDb.isTaskOK())  {
                    // On change getpathdb
                    myConfig.setPathDb(newPath);
                    lbCurrDbPath.setText(myConfig.getPathDb());
                    myConfig.setFullPathDb(newPath+myConfig.getDbName());                      
                    // On relance l'affichage
                    rootController.changeCarnetView();                    
                }
            }
        }
    }
    
    /**
     * User choose a new db folder
     * this choice is validated if db files exist in this folder
     * @throws InterruptedException 
     */
    @FXML
    private void selectNewFolderDb() throws InterruptedException {
        
        winDirChoose wd = new winDirChoose(myConfig, i18n, 3, null);
        File selectedDirectory = wd.getSelectedFolder();
        if(selectedDirectory.exists() && selectedDirectory.isDirectory()){
            File folderDb = new File(myConfig.getPathDb());
            if (folderDb.exists() && folderDb.isDirectory() ) {
                FilenameFilter filter = new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".db");                   
                    }                
                };
                File[] files = new File(selectedDirectory.getAbsolutePath()).listFiles(filter);
                int nbDb = files.length;
                if (nbDb > 0) {
                    myConfig.setPathDb(selectedDirectory.getAbsolutePath()+myConfig.getOsSeparator());      
                    lbCurrDbPath.setText(selectedDirectory.getAbsolutePath()+myConfig.getOsSeparator());
                    myConfig.setFullPathDb(selectedDirectory.getAbsolutePath()+myConfig.getOsSeparator()+myConfig.getDbName());
                    iniChbCarnet(files);
                } else {
                    alertbox aError = new alertbox(myConfig.getLocale());
                    aError.alertError(i18n.tr("No logbook in selected folder"));  
                }
            }
        }
    }
    
    /**
     * Choicebox is filled with all db files of the db folder
     * @param files (Array of db files)
     */
    private void iniChbCarnet(File[] files)  { 
        ObservableList <String> listDb;
        int idxDb = -1;
        
        listDb = FXCollections.observableArrayList();
                
        int idx = -1;
        for (File f : files) {
            listDb.add(f.getName());
            idx++;
            if (myConfig.getDbName().equals(f.getName())) 
                idxDb = idx;                    
        }        
        chbCarnet.getItems().clear();
        chbCarnet.setItems(listDb);
        if (idxDb > -1)  {
            // Current db is selected in the choicebox
            chbCarnet.getSelectionModel().select(idxDb);   
        } else {
            // No db file that match the db stored in settings
            // Choicebox is opened
            chbCarnet.show();
        }
        chbCarnet.setOnAction((event) -> {
            String selectDbName = (String)chbCarnet.getSelectionModel().getSelectedItem();
            if (selectDbName != null && !selectDbName.isEmpty())
                changeDb((String)chbCarnet.getSelectionModel().getSelectedItem());
        });
        
    }
    
    /**
     * Choicebox is filled with supported GPS     
     */
    private void iniChbGPS()  { 
        
        listGPS supportedGPS = new listGPS(myConfig.getLocale());
        ObservableList <String> allGPS = listGPS.fill();        
        
        chbGPS.getItems().clear();
        chbGPS.setItems(allGPS);
        chbGPS.getSelectionModel().select(myConfig.getIdxGPS());                       
        
    }
    
    /**
     * Choicebox is fille with online contest supported by scoring module     
     */
    private void iniChbLeague()  { 
        
        listLeague suppLeagues = new listLeague();
        ObservableList <String> allLeagues = suppLeagues.fill();        
        
        chbLeague.getItems().clear();
        chbLeague.setItems(allLeagues);
        chbLeague.getSelectionModel().select(myConfig.getIdxLeague());                       
        
    }
    
    /**
     * Choicebox is filled with supported languages
     */
    private void iniChbLang()  { 
        
        listLangues suppLangues = new listLangues(myConfig.getLocale());
        ObservableList <String> allLangues = suppLangues.fill(i18n);        
        chbLang.getItems().clear();
        chbLang.setItems(allLangues);
        chbLang.getSelectionModel().select(myConfig.getIdxLang());                       
        
    }
    
    private void iniChbSynthese()  { 
        
        listSynthese suppSynthese = new listSynthese(myConfig.getLocale());
        ObservableList <String> allSynthese = suppSynthese.fill(i18n);        
        chbSynthese.getItems().clear();
        chbSynthese.setItems(allSynthese);
        chbSynthese.getSelectionModel().select(myConfig.getIdxSynthese());                       
        
    }
    
    private void iniChbStartwin()  { 
        
        listStartwin suppStartw = new listStartwin(myConfig.getLocale());
        ObservableList <String> allSynthese = suppStartw.fill(i18n);        
        chbStartwin.getItems().clear();
        chbStartwin.setItems(allSynthese);
        chbStartwin.getSelectionModel().select(myConfig.getIdxStartwin());                       
        
    }    
    
    /**
     * Choicebox to choose All years or Year by year
     */
    private void iniChbYear()  { 
        
        listTypeYear suppYears = new listTypeYear(myConfig.getLocale());
        ObservableList <String> allTypeYears = suppYears.fill(i18n);        
        chbByYear.getItems().clear();
        chbByYear.setItems(allTypeYears);
        chbByYear.getSelectionModel().select(myConfig.getIdxTypeYear());                               
    }
    
    /**
     * Choicebox is filled by supported maps     
     */
    private void iniChbCarte()  { 
        
        listCarte suppCartes = new listCarte();
        ObservableList <String> allCartes = suppCartes.fill();        
        
        chbCarte.getItems().clear();
        chbCarte.setItems(allCartes);
        chbCarte.getSelectionModel().select(myConfig.getIdxMap());                               
    }
        
    /**
     * For a newlogbook file, name is checked
     * @param checkName
     * @return 
     */
    private String checkNewDbName(String checkName) {
        String res = null;
        
        // Spaces are replaced by underscore and letters are written in lower case 
        checkName = checkName.replaceAll(" ", "_").toLowerCase(); 
        int dotIndex = checkName.lastIndexOf('.');
        if(dotIndex>=0) {   // to prevent exception if no point
            res = checkName.substring(0,dotIndex);
        } else {
            res = checkName;
        }
        
        return res;        
    }
    
    /**
     * Initialize communication brdige with RootLayoutController 
     * @param rootlayout 
     */
    public void setRootBridge(RootLayoutController rootlayout) {
        this.rootController = rootlayout;        
    }

    /**
     * ConfigProg manage a change of logbook
     * @param selectedDb 
     */
    private void changeDb(String selectedDb) {
        if (myConfig.dbSwitch(selectedDb)) {
            rootController.changeCarnetView();
            myConfig.setValidConfig(true);
        }
    }
    
    /**
     * Translate labels of the window
     */
    private void winTraduction() {
        tabCarnet.setText(i18n.tr("Logbook"));        
        tabCarte.setText(i18n.tr("Map"));
        tabDivers.setText(i18n.tr("Miscellaneous"));
        tabInternet.setText(i18n.tr("Web"));      
        lbWorkingPath.setText(i18n.tr("Working folder path"));
        lbByYear.setText(i18n.tr("Logbook presentation"));
        btCarnetEdit.setText(i18n.tr("Modify"));
        lbWorkingLog.setText(i18n.tr("Logbook(s) path"));
        btNewLog.setText(i18n.tr("Create a new logbook"));
        btMoveLog.setText(i18n.tr("Move logbook(s) to a different folder"));
        btSelectLog.setText(i18n.tr("Choose a new logbook folder"));
        btRestore.setText(i18n.tr("Repatriate a copy"));
        btCarnetClose.setText(i18n.tr("Close"));
        tabPilote.setText(i18n.tr("Pilot"));
        lbPilote.setText(i18n.tr("Pilot name"));
        lbVoile.setText(i18n.tr("Glider"));
        lbGPS.setText(i18n.tr("Usual GPS"));
        lbIntegration.setText(i18n.tr("Integration"));
        lbLimite.setText(i18n.tr("USB limit"));
        lbMail.setText(i18n.tr("Pilot mail"));
        lbLeague.setText(i18n.tr("League"));
        lbIdentif.setText(i18n.tr("Login"));
        lbPass.setText(i18n.tr("Pass"));
        btPilotAnnuler.setText(i18n.tr("Cancel"));
        btPilotOK.setText(i18n.tr("Ok"));
        lbNavigateur.setText(i18n.tr("VisuGPS in browser"));
        lbDefaultMap.setText(i18n.tr("Default map"));
        lbLocMap.setText(i18n.tr("Default map location"));
        lbLatitude.setText(i18n.tr("Latitude"));
        lbLongitude.setText(i18n.tr("Longitude"));
        btMapMap.setText(i18n.tr("Map"));
        lbAberrants.setText(i18n.tr("Outliers threshold for map"));
        btMapOK.setText(i18n.tr("OK"));
        btMapAnnuler.setText(i18n.tr("Cancel"));
        lbLanguage.setText(i18n.tr("Language"));
        lbSynthese.setText(i18n.tr("Overview"));
        lbStartwin.setText(i18n.tr("Start window"));
        lbImport.setText(i18n.tr("Import folder"));
        lbSyride.setText(i18n.tr("Syride folder"));
        lbPhotoAuto.setText(i18n.tr("Automatic display of photos"));
        lbUpdateAuto.setText(i18n.tr("Automatic update"));
        lbDebug.setText(i18n.tr("Mode debug"));
        btDiversAnnuler.setText(i18n.tr("Cancel"));
        btDiversOk.setText(i18n.tr("OK"));
        lbLogfly.setText(i18n.tr("Logfly site url"));
        lbIcones.setText(i18n.tr("Icons url"));
        lbLoadUrl.setText(i18n.tr("Download url"));
        lbVisuAdress.setText(i18n.tr("VisuGPS url"));
        lbSupport.setText(i18n.tr("mail support url"));
        lbDeclaration.setText(i18n.tr("Claim url"));
        lbExport.setText(i18n.tr("Export IGC folder"));
        btWebAnnuler.setText(i18n.tr("Cancel"));
        btWebOk.setText(i18n.tr("OK"));               
    }
}
