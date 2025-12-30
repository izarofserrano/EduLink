#!/bin/bash

# EduLink API Test Script
# This script tests basic CRUD operations on the API

echo "==================================="
echo "EduLink API Test Script"
echo "==================================="
echo ""

BASE_URL="http://localhost:8080/api"

# Test 1: Create a Course
echo "Test 1: Creating a course..."
COURSE_RESPONSE=$(curl -s -X POST $BASE_URL/courses \
  -H "Content-Type: application/json" \
  -d '{
    "courseName": "Data Structures",
    "code": "CS201",
    "teacherName": "Prof. Johnson",
    "semester": 1
  }')
echo "Response: $COURSE_RESPONSE"
echo ""

# Test 2: Get All Courses
echo "Test 2: Getting all courses..."
curl -s $BASE_URL/courses | jq '.'
echo ""

# Test 3: Create an Activity
echo "Test 3: Creating an activity..."
ACTIVITY_RESPONSE=$(curl -s -X POST $BASE_URL/activities \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Study Group Session",
    "description": "Weekly study session for Data Structures",
    "activityType": "STUDY_SESSION",
    "attendance": 0
  }')
echo "Response: $ACTIVITY_RESPONSE"
echo ""

# Test 4: Get All Activities
echo "Test 4: Getting all activities..."
curl -s $BASE_URL/activities | jq '.'
echo ""

echo "==================================="
echo "Tests completed!"
echo "==================================="
