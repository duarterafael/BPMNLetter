package poli.mestrado.parser.util;


public interface Constants {
	
	//CLASS DIAGRAM FILES
	public static final String CLASS_DIAGRAM_XMI_FILE_NAME = "conceptModel.xmi";
	public static final String  CLASS_DIAGRAM_SERIALIZABEL_FILE_NAME = "conceptModel.serializabe";
	public static final String CLASS_DIAGRAM_USE_FILE_NAME = "conceptModel.use";
	//BUSSINESS DIAGRAM FILES
	public static final String BPMN_FILE_NAME = "bpmnModel.bpmn";
	public static final String BPMN_PATHS_SERIALIZABEL_FILE_NAME = "bpmnPathsModel.serializabe";
	//FOLDERS
	public static final String EXPORT_FOLDER = "exportUse";
	public static final String ASSL_SCRIPT_FOLDER = "asslScripts";
	//AUX PROGRAM
	public static final String USE_PROGRAMM = "use-4.0.0-323/bin/use.bat";
	public static final String ARGOUML_PROGRAMM = "conceptalModelModeler/argouml.jar";
	
	
	public static final String ALL_PATHS_NAMES = "Todos"; 
	
	
	
	// USER_CMD
	public static final String USER_CMD_CREATE =  "criar";
	public static final String USER_CMD_DELETE =  "deletar";
	public static final String USER_CMD_SET =  "atribuir";
	public static final String USER_CMD_RANDOM_SET =  "atribuir_aleatorio";
	public static final String USER_CMD_ASSOC =  "associar";
	public static final String USER_CMD_DESASSOC =  "desassociar";
//	Comandos especiais do USE
	public static final String USE_SHELL_CMD_RESET =  "reset";
	
	
//	Kind of GATEWAY
	public static final int GATEWAY_FORK = 1;
	public static final int GATEWAY_JOIN = -1;
	public static final int GATEWAY_UNKNOWN = 0;
//	Kind of Edge
	public static final int SOURCE_VERTICE = 0;
	public static final int TARGET_VERTICE = 1;

//	CONFIG CTES
	//Exibe um log detalhado para execução de cada condição
	public static final boolean VERBOSE_CONTION = false;
	//Printa ou não os comandos USE	
	public static final boolean VERBOSE_SHELL_USE_CMDS = false;
	public static final int DELAY_BETEWEEN_USE_CMDS = 150;

	public static final boolean asslScriptWhithLog = false;
	public static final boolean onlyMainInAsslScript = false;
	public static final int WARNING_FONT_CODE = 1;
	public static final int ERRO_FONT_CODE = 2;
	
//	Execution type
	public static final int MANUAL_EXECUTION = -1;
	public static final int UNKNOW_EXECUTION = 0;
	public static final int AUTOMATIC_EXECUTION = 1;
	
//  Execution Erro code
	
	public static final int ERRO_EXECUTION_UNKNOW = -1;
	
	

}
