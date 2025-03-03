/*
 * Copyright Gil THOMAS
 * This file forms an integral part of Logfly project
 * See the LICENSE file distributed with source code
 * for details of Logfly licence project
 */
package gps;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import settings.osType;
import systemio.textio;

/**
 *
 * @author gil
 * Flynet is a USB GPS where IGC files are stored in root folder 
 * File is named according IGC file specification
 * See gpsutils
 */
public class flynet {
    
    private boolean connected = false;
    private File fFlights; 
    private File fDrive;
    private int idxDrive;    
    private ObservableList <String> driveList;  
    private String closingFlynet;
    private String msgClosingDate; 
    private String verFirmware = null;

    public boolean isConnected() {
        return connected;
    }

    private void setConnected(boolean connected) {
        this.connected = connected;
    }


    public File getfFlights() {
        return fFlights;
    }

    public File getfDrive() {
        return fDrive;
    }

    public int getIdxDrive() {
        return idxDrive;
    }
    
    public String getMsgClosingDate() {
        return msgClosingDate;
    }        
    
    public ObservableList<String> getDriveList() {
        return driveList;
    }

    public String getVerFirmware() {
        return verFirmware;
    }    

    public flynet(osType currOs, int gpsLimit) {
        boolean conn = false;
        fFlights = null;        
        fDrive = null;   
        idxDrive = 0;
        driveList = FXCollections.observableArrayList();        
        
        setDateLevel(gpsLimit);
        
        conn = testConnection(currOs);
        
        setConnected(conn);                
        
    }

    private void setDateLevel(int gpsLimit) { 

        int closingYear;
        int closingMonth;
        int closingDay;
        String sYear;
        String sMonth; 
        String sDay;
        
        if (gpsLimit == 0) gpsLimit = 99;
        Calendar myCalendar = Calendar.getInstance();
        myCalendar.add(Calendar.MONTH, -(gpsLimit));
        SimpleDateFormat sdf = new SimpleDateFormat("YYMMdd");
        SimpleDateFormat ydf = new SimpleDateFormat("YYYY");
        SimpleDateFormat mdf = new SimpleDateFormat("MM"); 
        SimpleDateFormat ddf = new SimpleDateFormat("dd");
        
        
        closingYear = Integer.parseInt(ydf.format(myCalendar.getTime()));
        int iYear = (closingYear-2000) %10;  // value 0 to 9, cycling every 10 years  
        System.out.println("iYear : "+iYear);
        sYear = String.valueOf(iYear);
        closingMonth = Integer.parseInt(mdf.format(myCalendar.getTime()));
        sMonth = gpsutils.listMonth.get(closingMonth);
        closingDay = Integer.parseInt(ddf.format(myCalendar.getTime())) - 1;
        sDay = gpsutils.listDay.get(closingDay);
        closingFlynet = sYear+sMonth+sDay;
        SimpleDateFormat sdfMsg = new SimpleDateFormat("dd/MM/YY");
        msgClosingDate = sdfMsg.format(myCalendar.getTime()); 
        
    }    
    
    public boolean testConnection(osType currOs) {
        
        boolean res = false;
        boolean cond1 = false;
        
        File[] drives = null;
        int nbDrive = 0;
            
        switch (currOs) {
            case WINDOWS:
                drives = File.listRoots();
                break;
            case MACOS :
                drives = new File("/Volumes").listFiles();
                break;
            case LINUX :
                File fmedia = new File("/media");
                for(File fls : fmedia.listFiles()){
                    // first pass : /media/user
                    drives = fls.listFiles();                    
                }
                break;
            default:
                throw new AssertionError();
        }
        if (drives != null && drives.length > 0) {
            driveList.clear();
            for (File aDrive : drives) {       
                long size = aDrive.getTotalSpace();
                float sizeGo = size / 1000000000;    
                StringBuilder sb = new StringBuilder();
                sb.append(aDrive.getName()).append(" ").append(String.format("%4.0f", sizeGo)).append(" Go");
                driveList.add(sb.toString());
                // Capacity > 64 Go jumped
                if (size < 63999999999L) {
                    File listFile[] = aDrive.listFiles();
                    if (listFile != null) {
                        for (int i=0; i<listFile.length; i++)         
                        {
                            if (listFile[i].getName().equals("CONFIG.TXT")) {
                                if (checkConfig(listFile[i])) {
                                fFlights = listFile[i].getParentFile();                                
                                cond1 = true;
                                break;
                                }
                            }                           
                        }
                        if (cond1 == true) {
                            fDrive = aDrive;
                            idxDrive = nbDrive;
                            res = true;
                            break;
                        } else {
                            idxDrive = 0;
                        }
                    }
                }
                nbDrive++;
            }
        }                                    
        return res;                
    } 
    
    private boolean checkConfig(File fSettings) {
        
        boolean res = false;
        
        try {
            Stream<String> lines = Files.lines(Paths.get(fSettings.getAbsolutePath()));
            Optional<String> hasFirmware = lines.filter(s -> s.contains("FirmwareRev = XC")).findFirst();
            if(hasFirmware.isPresent()){
                String[] sFirmware = hasFirmware.get().split(" = ");
                if (sFirmware.length > 1) {
                    verFirmware = sFirmware[1];
                } else {
                    verFirmware = "";
                }
                res = true;
            }
            lines.close();            
        } catch (Exception e) {
            e.printStackTrace();
        }  
                
        return res;        
    }
    
    private void exploreFolder(File dir, ArrayList<String> trackPathList) throws Exception {  
        // Recursivité à vérifier pour le skytraax        
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            if (fileName.endsWith(".igc") || fileName.endsWith(".IGC")) {                                    
                // Problem of dot files writed by MacOS 
                if (files[i].isFile() && !fileName.startsWith("._") && files[i].getName().length() > 9) {
                    if (files[i].getName().substring(0,3).compareTo(closingFlynet) > 0) {
                        trackPathList.add(files[i].getPath());
                    }
                }
            }
        }        
    }    
    
    public void listTracksFiles(ArrayList<String> trackPathList) throws Exception {   

        if (fFlights != null && fFlights.exists())  {        
           exploreFolder(fFlights, trackPathList);
        }
    }     
    
    public String getTrackFile(String igcPath) {
        
        String res = null;
        
        File fIGC = new File(igcPath);
        textio fread = new textio();                                    
        String pFichier = fread.readTxt(fIGC);
        if (pFichier != null && !pFichier.isEmpty())  {
            res = pFichier;
        }
        
        return res;        
    }          
    
}
