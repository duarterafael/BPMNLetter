import poli.mestrado.parser.bpmn2use.ParserBPMNHelper;
import poli.mestrado.parser.bpmn2use.tag.ProcessDiagram;
import poli.mestrado.parser.bpmn2use.tag.ProcessTag;


public class MainAux {

	public static void main(String[] args) {
		ProcessDiagram process = ParserBPMNHelper.getInstance().getProcessDiagram();
		System.out.println(process.toString());
//		ParserBPMNHelper.getInstance().saveModel();

	}

}
