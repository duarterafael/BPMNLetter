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

package poli.mestrado.parser.bpmn2use;

import java.io.File;

import poli.mestrado.parser.uml2use.UmlFileManager;
import poli.mestrado.parser.util.MyConstants;

public class BpmnFileManager {
    
    private static File bpmnFile = null;
    private static BpmnFileManager instance = null;
    
    private BpmnFileManager(){
        File pholder = new File(MyConstants.EXPORT_FOLDER);
        if(!pholder.exists()){
            pholder.mkdirs();
        }
        String path = UmlFileManager.getInstance().getExportXmiFile().getAbsolutePath().substring(0, UmlFileManager.getInstance().getExportXmiFile().getAbsolutePath().lastIndexOf(File.separator)+1)+MyConstants.BPMN_FILE_NAME;
		 bpmnFile = new File(path);
    }
    
    public static BpmnFileManager getInstance(){
        if(instance == null){
            instance = new BpmnFileManager();
        }
        return instance;
    }
    
    public static void deleteExportFile(){
        if(bpmnFile.exists()){
            bpmnFile.delete();
        }
    }
    
    public File getExportFile(){
        return bpmnFile;
    }
    
    public void deleteFile(){
        if(bpmnFile.exists()){
                bpmnFile.delete();
        }
    }

}
