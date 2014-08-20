package com.chiorichan.factory.postprocessors;

import java.util.Arrays;
import java.util.List;

import com.chiorichan.factory.CodeMetaData;
import com.google.common.collect.Lists;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;

public class JSMinPostProcessor implements PostProcessor
{
	@Override
	public String[] getHandledTypes()
	{
		return new String[] { "js", "application/javascript-x" };
	}
	
	@Override
	public String process( CodeMetaData meta, String code )
	{
		List<SourceFile> externs = Lists.newArrayList();
		List<SourceFile> inputs = Arrays.asList( SourceFile.fromCode( ( meta.fileName == null || meta.fileName.isEmpty() ) ? "fakefile.js" : meta.fileName, code ) );
		
		Compiler compiler = new Compiler();
		
		CompilerOptions options = new CompilerOptions();
		
		CompilationLevel.ADVANCED_OPTIMIZATIONS.setOptionsForCompilationLevel( options );
		
		compiler.compile(externs, inputs, options );
		
		return compiler.toSource();
	}
	
}
