/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leaflet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import org.xnap.commons.i18n.I18n;
import systemio.mylogging;

/**
 *
 * @author gil
 */
public class map_air_draw {

    private boolean map_OK;
    private int errorCode;
    private String map_HTML;
    private static I18n i18n;
    private final String RC = "\n";
    private StringBuilder sbError;

    public map_air_draw(I18n currLang, String pCoord) {
        map_HTML = null;
        map_OK = false;
        this.i18n = currLang;
        genMap(pCoord);        
    }
    
    public String getMap_HTML() {
        return map_HTML;
    }    

    public boolean isMap_OK() {
        return map_OK;
    }    
    
    /**
    * Generation of HTML code of the map
    * @return 
    */
    private void genMap(String startCoord) {

        StringBuilder sbHTML = new StringBuilder();
        StringBuilder sbComment = new StringBuilder();
        String commentOk;
        
        try {
            try  {
                BufferedReader br = new BufferedReader(new InputStreamReader(map_air_draw.class.getResourceAsStream("/skl/skl_airdraw.txt")));
                String line = null;            
                while ((line = br.readLine()) != null) {
                    sbHTML.append(line).append(RC);                    
                }
                br.close();
            } catch (IOException e) {
                sbError = new StringBuilder(this.getClass().getName()+"."+Thread.currentThread().getStackTrace()[1].getMethodName());
                sbError.append("\r\n").append(e.getMessage());
                System.out.println(sbError.toString());
                mylogging.log(Level.SEVERE, sbError.toString()); 
            }
            if (sbHTML.length() > 500)  {
                String layerHTML = sbHTML.toString();           
                map_HTML = layerHTML.replace("%CoordIni%", startCoord);     
                map_OK = true;
            }
        } catch (Exception e) {
            map_OK = false;
        }         
    }      
}
