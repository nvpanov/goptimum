import TargetFunctions.Function_DeJong_nD;
import TargetFunctions.Function_Price5_2D;
import TargetFunctions.Function_Rastrigin10_2D;
import TargetFunctions.Function_SixHumpCamelBack_2D;
import Algorithm.Algorithm;
import Algorithm.BaseAlgorithm;


public class TestSuite {

	public static void main(String[] args) {
		//Algorithm.main(args);
		//BaseAlgorithm.main(args);
		//Box.main(args);
		//Chooser.main(args);
		//CurrentBestChooser.main(args);
		//RandomChooser.main(args);
		
		
		//Function.main(args);
		//Function_DeJong_nD.main(args);
		org.junit.runner.JUnitCore.main("TargetFunctions.Function_DeJong_nD");
		org.junit.runner.JUnitCore.main("TargetFunctions.Function_Price5_2D");
		Function_Price5_2D.main(args);
		Function_Rastrigin10_2D.main(args);
		Function_SixHumpCamelBack_2D.main(args);
		
		

	}

}
