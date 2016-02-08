package compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java_cup.runtime.Symbol;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.mj.runtime.Run;
import rs.etf.pp1.symboltable.Tab;
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
				File sourceCode = null, outputCode = null;
				sourceCode = new File("test/test_files/" + args[0] + ".mj");
				log.info("Compiling source file: " + sourceCode.getAbsolutePath());
				br = new BufferedReader(new FileReader(sourceCode));
				
				Yylex lexer = new Yylex(br);
				MJParser p = new MJParser(lexer);
		        Symbol s = p.parse();  //pocetak parsiranja
		        
		        br.close();
		        
		        log.info("=======================SYNTAX ANALYSIS=======================");
		        if (p.semError)
		        	log.error("Parsing UNSUCCESSFUL!");
		        else {
		        	// nivo A
		        	log.info("Global constants = " + p.globalConstCnt);
			        log.info("Global variables = " + p.globalVarCnt);
			        log.info("Global arrays = " + p.globalArrayCnt);
			        log.info("Local variables in main = " + p.localVarCnt);
  
			        // nivo B
			        log.info("Global functions = " + (p.functionCnt-1));
			        log.info("Formal parameters = " + p.formParCnt);
			        log.info("Function calls in main = " + p.callCnt);
			        log.info("Statement blocks = " + p.blockCnt);			        
			        
			        // nivo C
			        log.info("Classes = " + p.nestedClassCnt);
			        log.info("Class attributes = " + p.nestedClassAttrCnt);
			        log.info("Methods = " + p.nestedClassMethodCnt);
		        	log.info("Parsing SUCCESSFUL!");
		        	
		        	p.dump();
			        
					outputCode = new File("test/output_files/" + args[0] + ".obj");
		        	if (outputCode.exists())
		        		outputCode.delete();
		        	Code.write(new FileOutputStream(outputCode));
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
