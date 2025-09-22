#!/bin/bash

echo "Compiling Club Management System..."

# Create build directory if it doesn't exist
mkdir -p build

# Create lib directory if it doesn't exist
mkdir -p lib

# Check for SQLite JDBC driver
if [ ! -f "lib/sqlite-jdbc-3.45.0.0.jar" ]; then
    echo "SQLite JDBC driver not found!"
    echo "Please download sqlite-jdbc-3.45.0.0.jar from:"
    echo "https://github.com/xerial/sqlite-jdbc/releases"
    echo "and place it in the lib/ directory"
    exit 1
fi

echo "Compiling Java source files..."

# Compile all Java files
javac -cp "lib/*" -d build \
    src/main/java/com/clubmanagement/*.java \
    src/main/java/com/clubmanagement/models/*.java \
    src/main/java/com/clubmanagement/database/*.java \
    src/main/java/com/clubmanagement/dao/*.java \
    src/main/java/com/clubmanagement/services/*.java \
    src/main/java/com/clubmanagement/security/*.java \
    src/main/java/com/clubmanagement/gui/*.java \
    src/main/java/com/clubmanagement/gui/theme/*.java \
    src/main/java/com/clubmanagement/gui/components/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "To run the application, use: ./run.sh"
else
    echo "Compilation failed!"
    exit 1
fi