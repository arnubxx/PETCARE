# Image Folder

This folder should contain the following images for your PETCARE website:

## Required Images:

### Homepage (index.html):
- `dog1.png` - Dog image for main section
- `cat.png` - Cat image for main section
- `Capture.PNG` - Service photo 1
- `photo2.PNG` - Service photo 2
- `photo3.PNG` - Service photo 3
- `photo4.PNG` - Service photo 4
- `r1.jpg` - Client review photo 1
- `r2.jpg` - Client review photo 2
- `r4.jpg` - Client review photo 3

### About Page (about.html):
- `veterinarian-check-ing-puppy-s-health_23-2148728396.jpg` - About page image

## How to Add Images:

1. Place all your image files in this `src/main/webapp/image/` folder
2. Make sure the filenames match exactly (including capitalization for `.PNG` vs `.png`)
3. Rebuild the project: `mvn clean package`
4. Redeploy to Tomcat

The images will then be accessible at: `http://localhost:8080/PETCARE-1.0.0/image/filename.ext`
