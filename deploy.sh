#!/bin/bash

# PETCARE Deployment Script
# Run this after adding your images to src/main/webapp/image/

echo "🔨 Building PETCARE application..."
cd /Users/arnubdatta/Desktop/PETCARE

# Build with Maven
mvn clean package

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    
    echo "🗑️  Removing old deployment..."
    rm -rf /opt/homebrew/opt/tomcat/libexec/webapps/PETCARE*
    
    echo "📦 Copying new WAR file..."
    cp target/PETCARE-1.0.0.war /opt/homebrew/opt/tomcat/libexec/webapps/
    
    echo "⏳ Waiting for deployment..."
    sleep 5
    
    echo "🌐 Opening browser..."
    open http://localhost:8080/PETCARE-1.0.0/
    
    echo "✅ Deployment complete!"
    echo "Your website is now live at: http://localhost:8080/PETCARE-1.0.0/"
else
    echo "❌ Build failed. Please check the errors above."
fi
