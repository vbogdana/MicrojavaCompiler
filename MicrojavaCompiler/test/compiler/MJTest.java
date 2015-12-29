package compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java_cup.runtime.Symbol;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import compiler.util.Log4JUtils;

public class MJTest {

	static {
		DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
	}
	
	public static void main(String[] args) throws IOException {
		Logger log = Logger.getLogger(MJTest.class);
		Reader br = null;
		try {
			for (int i = 0; i < 11; i++) {
				File sourceCode = null;
				if (i != 10)
					sourceCode = new File("test/test0" + i + ".mj");
				else
					sourceCode = new File("test/test10.mj");
				log.info("Compiling source file: " + sourceCode.getAbsolutePath());
				
				br = new BufferedReader(new FileReader(sourceCode));
				
				Yylex lexer = new Yylex(br);
				Symbol currToken = null;
				while ((currToken = lexer.next_token()).sym != sym.EOF) {
					if (currToken != null)
						log.info(currToken.toString() + " " + currToken.value.toString());
				}
				
				br.close();
				//log = Logger.getLogger(MJTest.class);
			}
			
		} 
		finally {
			if (br != null) try { br.close(); } catch (IOException e1) { e1.printStackTrace(); }
		}
	}
	
}
