# Eduterium ChatBot

## Overview
The Eduterium Chatbot is an interactive web application that allows users to communicate with a chatbot powered by ChatGPT. Users can send messages and receive responses, with chat history maintained for each user.

## Features
- User identification
- Chat functionality with ChatGPT
- Chat history retrieval for specific users
- CSRF protection for secure interactions

## Technologies Used
- **Backend**: Java Play Framework
- **Database**: MySQL
- **Frontend**: HTML, JS, CSS
- **Dependency Injection**: Guice
- **Web Services**: Play WS for API calls
- **Logging**: SLF4J with Lombok

## Getting Started

### Prerequisites
- **Java 17 or higher**
- **SBT**
- **MySQL**
- **OpenAI API Key**
- **Git**

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/abyalewab/eduterium-chatbot.git
   cd eduterium-chatbot
   
2. Setting up configuration
- Database Configuration: To configure the database, you'll need to update the **`persistence.xml`** and **`application.conf`** files with your database credentials.

- OpenAI API Key Configuration: Once you have your API key, open the application.conf file and add the following lines:
  ```bash
  # OpenAI API configuration
  openai.apiKey="your-openai-api-key"
  openai.apiUrl="https://api.openai.com/v1/chat/completions"

3. Run the application:
   ```bash
   sbt run
4. Open your web browser and navigate to http://localhost:9000/ to access the application.


## Summary
After configuring both the persistence.xml and application.conf files, your application will be ready to:
- Connect to your database for storing and retrieving chat interactions.
- Communicate with OpenAI's API for generating chatbot responses.
