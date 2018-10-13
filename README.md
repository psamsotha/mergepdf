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