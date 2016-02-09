package compiler;

import rs.etf.pp1.mj.runtime.Run;

public class Compiler {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			if (args.length > 0) {
				MJParserTest.main(args);
				if (MJParserTest.executable) {
					System.out.println("Execution...");
					args[0] = "test/output_files/" + args[0] + ".obj";
					Run.main(args);
				}
			} else {
				System.err.println("Not enough parameters.");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
