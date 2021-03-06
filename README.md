# Merge PDF Files

This is a small program to merge PDF files.

### Usage

Just pass the PDF files as arguments and pass an -o flag with the output
file. For example

    java -jar mergepdf-0.1.jar file1.pdf file2.pdf file3.pdf -o output.pdf
    
There is a script `mergepdf` that you can put into your PATH that will 
call the jar. Th jar should be in the same location as the script. With
this, you will be able to use the command from anywhere

    mergepdf file1.pdf file2.pdf file3.pdf -o output.pdf
    
Directories can also be listed. If a directory is listed, the files
in the directory will traversed alphabetically first, and then any other
directories in that directory will be recursively traversed. If you want
to list a directory, the best thing to do would be to rename the files
prefixed with numbers so that you can get the correct ordering.

#### Options
 
* `-o` - output file
* `-v` - verbose output
* `-h` - show usage
    
### Manual Testing

There is a `DummyFileGenerator` and a `TestRunner` class. First run the
`DummyFileGenerator` class which will create some dummy PDF files in
a `data` directory, and then run the `TestRunner` class which will merge
those files into a `output/merged.pdf` file.