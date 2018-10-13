package com.peeskillet.mergepdf;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;

/**
 * Program to merge multiple PDF files into one. The arguments to the
 * program are the input file names and an -o flag with the output
 * file name, e.g.
 *
 *     file1.pdf file2.pdf file3.pdf -o output.pdf
 */
public class Main {

    public static void main(String[] args) throws Exception {
        if ("-h".equals(args[0]) || "--help".equals(args[0])) {
            printUsage();
            System.exit(0);
        }
        if (args.length < 3) {
            printUsage();
            System.exit(1);
        }

        Arguments arguments = extractArguments(args);

        makeOutputDirectoriesIfNeeded(arguments.getOutputFile());
        mergeFiles(arguments.getInputFilesAsArray(), arguments.getOutputFile(), true);

        if (arguments.hasFlag("-v")) {
            printMergedFiles(arguments.getInputFiles());
        }
    }

    private static Arguments extractArguments(String[] args) {
        Arguments arguments = new Arguments();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("-o".equals(arg)) {
                if (i + 1 != args.length) {
                    String outputFile = args[i + 1];
                    arguments.setOutputFile(outputFile);
                    i++;
                } else {
                    printUsage();
                    System.exit(0);
                }
            } else if ("-v".equals(arg)) {
                arguments.addFlag(arg);
            } else {
                arguments.addInputFile(arg);
            }
        }
        return arguments;
    }

    private static void mergeFiles(String[] files, String result, boolean smart) throws Exception {
        Document document = new Document();
        PdfCopy copy;

        if (smart) {
            copy = new PdfSmartCopy(document, new FileOutputStream(result));
        } else {
            copy = new PdfCopy(document, new FileOutputStream(result));
        }

        document.open();
        PdfReader[] readers = new PdfReader[files.length];
        for (int i = 0; i < files.length; i++) {
            readers[i] = new PdfReader(files[i]);
            copy.addDocument(readers[i]);
            copy.freeReader(readers[i]);
            readers[i].close();
        }
        document.close();
    }

    private static void makeOutputDirectoriesIfNeeded(String result) {
        File outputFile = new File(result);
        File parentDir = outputFile.getParentFile();
        if (parentDir != null) {
            if (!parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    String exMsg = String.format(
                            "Output directory %s could not be created.",
                            parentDir.getAbsoluteFile());
                    throw new IllegalStateException(exMsg);
                }
            }
        }
    }

    private static void printUsage() {
        System.out.println("Usage: mergepdf file1.pdf file2.pdf -o output.pdf");
    }

    private static void printMergedFiles(List<String> files) {
        System.out.println("Merged files:");
        for (String file: files) {
            System.out.println("\t" + file);
        }
    }

    private static class Arguments {

        private String outputFile;
        private List<String> inputFiles = new ArrayList<>();
        private Set<String> flags = new HashSet<>();

        private String getOutputFile() {
            return outputFile;
        }

        private void setOutputFile(String outputFile) {
            this.outputFile = outputFile;
        }

        private List<String> getInputFiles() {
            return inputFiles;
        }

        public void addInputFile(String file) {
            inputFiles.add(file);
        }

        private String[] getInputFilesAsArray() {
            String[] files = new String[inputFiles.size()];
            return inputFiles.toArray(files);
        }

        private void addFlag(String flag) {
            flags.add(flag);
        }

        private boolean hasFlag(String flag) {
            return flags.contains(flag);
        }

        @Override
        public String toString() {
            return "Arguments {\n" +
                    "  outputFile='" + outputFile + '\'' +
                    ",\n  inputFiles=" + Arrays.toString(inputFiles.toArray()) +
                    "\n}";
        }
    }
}
