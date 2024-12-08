#!/bin/bash

# Description: This script searches for the keyword "Application.Run" in all files recursively
# from the current directory and lists the files containing the keyword along with line numbers.

# Define the keyword to search for
KEYWORD="currentUser"

# Find all files and search for the keyword
echo "Searching for '$KEYWORD' in files..."
grep -rnw '.' -e "$KEYWORD" --color=always

# Check if any results were found
if [ $? -ne 0 ]; then
    echo "No occurrences of '$KEYWORD' found."
else
    echo "Search completed. Above are the occurrences of '$KEYWORD'."
fi

