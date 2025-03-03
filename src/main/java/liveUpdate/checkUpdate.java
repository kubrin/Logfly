/*
 * Copyright Gil THOMAS
 * This file forms an integral part of Logfly project
 * See the LICENSE file distributed with source code
 * for details of Logfly licence project
 */
package liveUpdate;

import dialogues.alertbox;
import dialogues.dialogbox;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import liveUpdate.objects.Modes;
import liveUpdate.objects.Release;
import liveUpdate.parsers.ReleaseXMLParser;
import liveUpdate.parsers.bundleXMLParser;
import org.xml.sax.SAXException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
import settings.configProg;
import settings.osType;
import settings.privateData;
import systemio.mylogging;
import systemio.tempacess;
import systemio.webdown;
import systemio.webio;


/**
 *
 * @author gil
 * 
 * In Jupar project, Periklis uses xml files with "xml" extension
 * For security, our server don't want xml extensions, we changed for "inf"
 * 
 */
public class checkUpdate {
    
    private I18n i18n;
    private Locale myLocale;
    private StringBuilder sbError;
    
    configProg myConfig = new configProg();
       
    private String updateURL;  
    
    private String tmpUpdateFiles;
    
    public checkUpdate(Release release, configProg mainConfig) throws IOException  {
        this.myConfig = mainConfig;
        i18n = I18nFactory.getI18n("","lang/Messages",alertbox.class.getClass().getClassLoader(),myConfig.getLocale(),0);
        myLocale = myConfig.getLocale();
        updateURL = privateData.updateUrl.toString();
        webio myConnect = new webio();
        if (myConnect.checkConnection()) runChecking(release);        
    }
    
    private void runChecking(Release release)  {
        
        boolean answer = false;
        
        ReleaseXMLParser parser = new ReleaseXMLParser();
        
        try {            
            Release current = parser.parse(updateURL+"/latest.inf", Modes.URL);
            // With current updates, compare result < 1000
            // if severity is longer than current version CompareTo return 1000
            if (current.compareTo(release) < 1000) {
                if (current.compareTo(release) > 0) {
                    if (!myConfig.isUpdateAuto())  {
                        dialogbox dInfo = new dialogbox(i18n);
                        String dHeader = i18n.tr("A new version {0}. {1} is available",current.getpkgver(),current.getPkgrel());
                        String dText = current.getMessage()+"\n"+i18n.tr("Do you want to install it")+" ?";
                        answer = dInfo.YesNo(dHeader,dText); 
                    } else {
                        answer = true;
                    }
                    if (answer == true) {
                        // Ask for a temp path
                        tmpUpdateFiles = tempacess.getTemPath(null);
                        // Download needed files                 
                        Downloader dl = new Downloader();
                        dl.download(updateURL+"/files.inf", tmpUpdateFiles, Modes.URL);                        
                    }
                } 
            } else {
                // major update ->  bundle download required 
                if (myConfig.getOS() == osType.LINUX) {
                    alertbox aInfo = new alertbox(myLocale);
                    String sTitle = i18n.tr("A major update is available [version {0}]",current.getseverity());               
                    // Get bundle.xml with download url
                    bundleXMLParser bParser = new bundleXMLParser();
                    bParser.parse(updateURL+"/bundle.inf");   
                    StringBuilder sbText = new StringBuilder();       
                    String sUrl = bParser.getLinuxUrl();
                    final Clipboard clipboard = Clipboard.getSystemClipboard();
                    final ClipboardContent content = new ClipboardContent();
                    content.putString(sUrl.toString());            
                    clipboard.setContent(content);
                    sbText.append(sUrl).append("   [");
                    sbText.append(i18n.tr("Copy to clipboard")).append("]");
                    aInfo.alertWithTitle(sTitle,sbText.toString());                                            
                } else {
                    dialogbox dInfo = new dialogbox(i18n);
                    String dHeader = i18n.tr("A major update is available [version {0}]",current.getseverity());
                    String dText = current.getMessage()+"\n"+i18n.tr("Do you want to install it")+" ?";
                    answer = dInfo.YesNo(dHeader,dText); 
                    if (answer == true) {
                        tmpUpdateFiles = tempacess.getTemPath(null);
                        String msg = i18n.tr("Run the installation of the new version")+"...";
                        String sUrl=null;
                        // Get bundle.xml with download url
                        bundleXMLParser bParser = new bundleXMLParser();
                        bParser.parse(updateURL+"/bundle.inf");                                        
                        switch (myConfig.getOS()) {
                            case WINDOWS:
                                sUrl = bParser.getWinUrl();
                                break;
                            case MACOS :
                                sUrl = bParser.getMacUrl();
                                break;
                            case LINUX :
                                sUrl = bParser.getLinuxUrl();
                                break;
                        }
                        if (sUrl != null) {
                            webdown myLoad = new webdown(sUrl,tmpUpdateFiles, i18n, msg);
                            if (myLoad.isDownSuccess()) {
                                File downFile = new File(myLoad.getDownPath());
                                try {
                                    Desktop desktop = Desktop.getDesktop();
                                    desktop.open(downFile);
                                    System.exit(0);
                                } catch (Exception e) {
                                }                    
                            }
                        }
                    }
                }
            }
            
        } catch (SAXException ex) {
            alertbox aError = new alertbox(myConfig.getLocale());
            aError.alertError(i18n.tr("Update info could not be loaded"));                      
            answer = false;
        } catch (FileNotFoundException ex) {            
            sbError = new StringBuilder(this.getClass().getName()+"."+Thread.currentThread().getStackTrace()[1].getMethodName());
            sbError.append("\r\n").append(ex.toString());
            mylogging.log(Level.SEVERE, sbError.toString());
        } catch (IOException ex) {            
            sbError = new StringBuilder(this.getClass().getName()+"."+Thread.currentThread().getStackTrace()[1].getMethodName());
            sbError.append("\r\n").append(ex.toString());
            mylogging.log(Level.SEVERE, sbError.toString());
        } catch (InterruptedException ex) {           
            sbError = new StringBuilder(this.getClass().getName()+"."+Thread.currentThread().getStackTrace()[1].getMethodName());
            sbError.append("\r\n").append(ex.toString());
            mylogging.log(Level.SEVERE, sbError.toString());
        } 
        
        /**
         * Start the updating procedure
         */
        if (answer == true) {
            try {
                Updater update = new Updater();               
                update.update("update.inf", tmpUpdateFiles, Modes.FILE);
                StringBuilder sbMsg = new StringBuilder();              
                sbMsg.append(i18n.tr("Logfly has been updated")).append("\n");  
                sbMsg.append(i18n.tr("You need to restart the program")).append("...");  
                alertbox aError = new alertbox(myConfig.getLocale());
                aError.alertInfo(i18n.tr(sbMsg.toString()));     
                System.exit(0);
            } catch (SAXException ex) {
                sbError = new StringBuilder(this.getClass().getName()+"."+Thread.currentThread().getStackTrace()[1].getMethodName());
                sbError.append("\r\n").append(ex.toString());
                mylogging.log(Level.SEVERE, sbError.toString());
            } catch (InterruptedException ex) {
                sbError = new StringBuilder(this.getClass().getName()+"."+Thread.currentThread().getStackTrace()[1].getMethodName());
                sbError.append("\r\n").append(ex.toString());
                mylogging.log(Level.SEVERE, sbError.toString());                
            } catch (FileNotFoundException ex) {
                sbError = new StringBuilder(this.getClass().getName()+"."+Thread.currentThread().getStackTrace()[1].getMethodName());
                sbError.append("\r\n").append(ex.toString());
                mylogging.log(Level.SEVERE, sbError.toString());                
            } catch (IOException ex) {
                sbError = new StringBuilder(this.getClass().getName()+"."+Thread.currentThread().getStackTrace()[1].getMethodName());
                sbError.append("\r\n").append(ex.toString());
                mylogging.log(Level.SEVERE, sbError.toString());                
            } catch (Exception ex) {
                sbError = new StringBuilder(this.getClass().getName()+"."+Thread.currentThread().getStackTrace()[1].getMethodName());
                sbError.append("\r\n").append(ex.toString());
                mylogging.log(Level.SEVERE, sbError.toString());     
            }
            /**
            * Delete tmp directory
            */
            File tmp = new File(tmpUpdateFiles);
            if (tmp.exists()) {
                for (File file : tmp.listFiles()) {
                    file.delete();
                }
                tmp.delete();
            }
        }        
    }
    
}
