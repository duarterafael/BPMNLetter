/* $Id$
 *******************************************************************************
 * Copyright (c) 2014 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rafael
 *******************************************************************************
 */

package poli.mestrado.parser.uml2use;

import java.io.File;

import poli.mestrado.parser.util.Constants;

public class UmlFileManager {
    
    private static File xmiFile = null;
    private static UmlFileManager instance = null;
    
    private UmlFileManager(){
        File pholder = new File(Constants.EXPORT_FOLDER);
        if(!pholder.exists()){
            pholder.mkdirs();
        }
        xmiFile = new File(Constants.EXPORT_FOLDER+"/"+Constants.CLASS_DIAGRAM_XMI_FILE_NAME);
    }
    
    public static UmlFileManager getInstance(){
        if(instance == null){
            instance = new UmlFileManager();
        }
        return instance;
    }
    
    public static void deleteExportXmiFile(){
        if(xmiFile.exists()){
            xmiFile.delete();
        }
    }
    
    public File getExportXmiFile(){
        return xmiFile;
    }
    
    public void deleteXmiFile(){
        if(xmiFile.exists()){
                xmiFile.delete();
        }
    }

}
