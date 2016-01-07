package compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java_cup.runtime.Symbol;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import compiler.util.Log4JUtils;

public class MJParserTest {
	
	static {
		DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
	}
	
	public static void main(String[] args) throws Exception {
		
		Logger log = Logger.getLogger(MJParserTest.class);
		Reader br = null;
		
		try {
			for (int i = 0; i < 11; i++) {
				File sourceCode = null;
				if (i != 10)
					sourceCode = new File("test/test_files/test0" + i + ".mj");
				else
					sourceCode = new File("test/test_files/test10.mj");
				
				log.info("Compiling source file: " + sourceCode.getAbsolutePath());
			
				br = new BufferedReader(new FileReader(sourceCode));
				Yylex lexer = new Yylex(br);
				
				MJParser p = new MJParser(lexer);
		        Symbol s = p.parse();  //pocetak parsiranja
		        
		        br.close();
		        
		        // nivo A
		        log.info("Global variables = " + p.globalVarCnt);
		        log.info("Local variables = " + p.localVarCnt);
		        log.info("Global constants = " + p.globalConstCnt);
		        log.info("Global arrays = " + p.globalArrayCnt);
			}
	        
		} 
		finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e1) {
					log.error(e1.getMessage(), e1);
				}
		}

	}
	
	
}
