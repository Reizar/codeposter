# CodePoster Maker

This is a project I've been working on as I learn Java.

It takes an image and some code ( or any text for that matter ) and
redraws the image using the text.

This project was initially inspired by this blog post: http://www.east5th.co/blog/2017/02/13/build-your-own-code-poster-with-elixir/
and it took some early design inspiration from there. However I have come up with my own
way of generating these posters.

I've tried to avoid using external dependencies for the actual image drawing, however
JCommander is used for parsing the command line params.

## Notes

- Currently I don't enforce image sizes, but you should limit the input image to a max resolution of 500x500 px.
- The larger the output, the longer the program will take. The default output size takes roughly 8s to run on my macbook pro.
- The program will automatically pad the input text if its not long enough to cover the image.
- The program uses the font 'Source Code Pro' and required this to be installed on the system to work. 
 
# Sample

This was generated with the java files from this project.

__Input image:__ <br>
![Input Image](https://raw.githubusercontent.com/Reizar/codeposter/master/sample_input.png)
    
__Output image:__ <br>
![Output Image](https://raw.githubusercontent.com/Reizar/codeposter/master/sample_output.png)

## Usage
```
Usage: <main class> [options]
  Options:
  * -code
      Path to input code file or directory.
    -code-file-extension
      Only files with this extension will be loaded when providing an input 
      directory 
      Default: <empty string>
    -comment-characters
      Comment characters to look for to strip from code
      Default: <empty string>
    -height
      Final width of the poster
      Default: 4050
  * -image
      Required - Path to input image.
    -output-file
      Path / Filename for the output file. Default is ./poster.png
      Default: ./poster.png
    -output-format
      File format of outputted poster. Options are: PNG, JPG.
      Default: PNG
    -width
      Final width of the poster/
      Default: 3150
      
```