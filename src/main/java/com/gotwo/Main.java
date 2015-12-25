package com.gotwo;

import com.gotwo.codegen.CodeGenerator;
import com.gotwo.error.*;
import com.gotwo.lexer.Lexer;
import com.gotwo.lexer.Token;
import com.gotwo.parser.Parser;
import com.gotwo.parser.ParsingResult;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by florian on 25/11/15.
 *
 * The main entry to our compiler.
 * For now all it can do is to compile an given input
 * directory to an given output directory.
 *
 * Usage is "inputDir outputDir [options]"
 */
public class Main {

    public static void main(String[] args) {
        if(args.length < 2) {
            System.out.println("Not enough arguments (" + args.length + " given, at least 2 required)." );
            System.out.println("Usage 'inputDir outputDir [options]'.");
            return;
        }

        File inputDir = getInputDirectory(args[0]);
        File outputDir = getOutputDirectory(args[1]);

        List<File> inputFiles = getAllGoTwoFilesInDirectory(inputDir);

        for(File file : inputFiles) {
            String className = file.getAbsolutePath().substring(inputDir.getAbsolutePath().length() + 1, file.getAbsolutePath().length() - 4);
            compileGoTwoFile(file, outputDir, className);
        }

    }

    /**
     * Compiles a given input GoTwo file to an java class.
     *
     * @param inputFile The gotwo file to be compiled.
     * @param outputDirectory The target root directory.
     * @param className The full java class name.
     */
    private static void compileGoTwoFile(File inputFile, File outputDirectory, String className) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            Lexer lexer = new Lexer(br);
            List<Token> tokenList = lexer.lexAll();
            Parser parser = new Parser(tokenList);
            ParsingResult res = parser.parseTokens();
            CodeGenerator codeGenerator = new CodeGenerator(res, className);
            codeGenerator.generateClassFile(outputDirectory.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CompilerException e) {
            System.err.println("Syntax Error: " + e.getMessage());
        }
    }

    /**
     * Gets all *.go2 files in a given directory.
     * Also searches all sub directories.
     *
     * @param directory The directory to be searched.
     * @return All *.go2 files.
     */
    private static List<File> getAllGoTwoFilesInDirectory(File directory) {
        LinkedList<File> files = new LinkedList<>();
        LinkedList<File> directories = new LinkedList<>();

        directories.add(directory);

        File currentDirectory;
        while(!directories.isEmpty()) {
            currentDirectory = directories.removeFirst();
            File[] subFiles = currentDirectory.listFiles();
            for(File file : subFiles) {
                if(file.isDirectory()) {
                    directories.add(file);
                } else {
                    if(file.getName().endsWith(".go2")) {
                        files.add(file);
                    }
                }
            }
        }

        return files;
    }

    /**
     * Gets a File object of the output directory.
     * Exits the program if there are any errors.
     *
     * @param name The name of the output directory
     * @return The output directory File object
     */
    private static File getOutputDirectory(String name) {
        File outputDir = new File(name);

        if(!outputDir.exists()) {
            System.out.println("The output directory does not exist and will be created.");
            if(outputDir.mkdirs()) {
                System.out.println("Output directory created successfully.");
            } else {
                System.out.println("Error while creating the output directory!");
                System.exit(0);
            }
        }

        return outputDir;
    }

    /**
     * Gets a File object of the input directory.
     * Exits the program if there are any errors.
     *
     * @param name The name of the input directory
     * @return The input directory File object
     */
    private static File getInputDirectory(String name) {
        File inputDir = new File(name);

        if(!inputDir.exists()) {
            System.out.println("The input directory does not exist!");
            System.exit(0);
        }

        return inputDir;
    }
}
