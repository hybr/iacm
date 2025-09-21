#!/bin/bash

echo "Starting Club Management System..."

# Check if build directory exists
if [ ! -d "build" ]; then
    echo "Build directory not found. Please run ./compile.sh first."
    exit 1
fi

# Check for SQLite JDBC driver
if [ ! -f "lib/sqlite-jdbc-3.45.0.0.jar" ]; then
    echo "SQLite JDBC driver not found!"
    echo "Please download sqlite-jdbc-3.45.0.0.jar from:"
    echo "https://github.com/xerial/sqlite-jdbc/releases"
    echo "and place it in the lib/ directory"
    exit 1
fi

# Run the application
java -cp "build:lib/sqlite-jdbc-3.45.0.0.jar:lib/slf4j-api-1.7.36.jar:lib/slf4j-simple-1.7.36.jar" com.clubmanagement.ClubManagementApp

if [ $? -ne 0 ]; then
    echo "Application encountered an error."
fi