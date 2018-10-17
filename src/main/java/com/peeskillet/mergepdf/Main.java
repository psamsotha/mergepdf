package com.peeskillet.mergepdf;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
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
 *     file1.pdf file2.pdf file3.pdf -o output.pdf [-v]
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
            printMergedFiles(arguments.getInputFiles(), arguments.getOutputFile());
        }
    }

    private static Arguments extractArguments(String[] args) {
        Arguments arguments = new Arguments();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("-o".equals(arg)) {
                String outputFile = null;
                try {
                    outputFile = args[i + 1];
                } catch (ArrayIndexOutOfBoundsException ex) {
                    System.out.println("No output file entered.");
                    printUsage();
                    System.exit(1);
                }
                arguments.setOutputFile(outputFile);
                i++;
            } else if ("-v".equals(arg)) {
                arguments.addFlag(arg);
            } else {
                File file = checkFile(arg);
                if (file.isDirectory()) {
                    extractDirectory(arguments.getInputFiles(), file);
                } else {
                    arguments.addInputFile(file.getAbsolutePath());
                }
            }
        }
        return arguments;
    }

    private static File checkFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new IllegalArgumentException(fileName + " does not exist.");
        }
        return file;
    }

    /**
     * When a directory is listed, the directory will be traversed alphabetically
     * and recursively. Files are added first, then directories are traversed
     * recursively.
     *
     * @param fileNames the list of file names to merge.
     * @param file the directory File
     */
    private static void extractDirectory(List<String> fileNames, File file) {
        if (file.isDirectory()) { ;
            File[] files = file.listFiles(getFileFilter());
            if (files == null) {
                throw new IllegalStateException("files must not be null.");
            }
            Arrays.sort(files);

            List<File> directories = new ArrayList<>();

            for (File f: files) {
                if (f.isDirectory()) {
                    directories.add(f);
                } else {
                    fileNames.add(f.getAbsolutePath());
                }
            }

            for (File dir: directories) {
                extractDirectory(fileNames, dir);
            }
        } else {
            fileNames.add(file.getAbsolutePath());
        }
    }

    /**
     * Get FileFilter to filter out hidden files. On Windows systems,
     * also filter out system files.
     */
    private static FileFilter getFileFilter() {
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            // see https://stackoverflow.com/a/15646429/2587435
            return (File f) -> {
                Path path = Paths.get(f.getAbsolutePath());
                DosFileAttributes dfa;
                try {
                    dfa = Files.readAttributes(path, DosFileAttributes.class);
                } catch (IOException e) {
                    // bad practice
                    return false;
                }
                return (!dfa.isHidden() && !dfa.isSystem());
            };
        } else {
            return (File file) -> !file.isHidden();
        }
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
        System.out.println("Usage: mergepdf file1.pdf file2.pdf -o output.pdf [-v]" +
                           "  -o  the output file" +
                           "  -v  verbose output");
    }

    private static void printMergedFiles(List<String> files, String outputFile) {
        System.out.println("Merged files:");
        for (int i = 1; i <= files.size(); i++) {
            System.out.println(String.format("  %d. %s", i, files.get(i - 1)));
        }
        System.out.println("Output: " + outputFile);
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
