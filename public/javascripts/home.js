 document.addEventListener("DOMContentLoaded", function () {
    const questionsList = document.getElementById("questions-list");
    const chatHistory = document.getElementById("chat-history");
    const sendButton = document.querySelector(".send-btn");
    const roleInput = document.getElementById("role");
    const messageInput = document.getElementById("message");

    function createMessageContent(role, message, isResponse) {
        const content = document.createElement("p");

        if (isResponse) {
            content.classList.add("bot-response-value");
            content.textContent = message;
        } else {
            content.innerHTML = `<strong class="chat-bot-role">Role</strong> 
                             <span class="role-value">${role}</span><br><br>
                             <strong class="chat-bot-message">Message</strong> 
                             <span class="message-value">${message}</span>`;
        }

        return content;
    }

    function createBotAvatar() {
        const botAvatar = document.createElement("img");
        botAvatar.src = botLogoUrl;
        botAvatar.alt = "Bot Logo";
        botAvatar.classList.add("avatar");
        return botAvatar;
    }

    function appendChat(role, message, isResponse = false) {
        const messageDiv = document.createElement("div");
        messageDiv.classList.add("chat-message");

        if (isResponse) {
            messageDiv.classList.add("bot-response");
            messageDiv.appendChild(createBotAvatar());
        } else {
            messageDiv.classList.add("user-message");
        }

        const content = createMessageContent(role, message, isResponse);
        messageDiv.appendChild(content);
        chatHistory.appendChild(messageDiv);

        chatHistory.scrollTop = chatHistory.scrollHeight;
    }

    function loadPreviousChats() {
        const username = sessionStorage.getItem("chatBotUsername");
        axios.get(`/chats/user?username=${encodeURIComponent(username)}`)
            .then(response => {
                const chats = response.data;
                chats.forEach(chat => {
                    appendPreviousQuestions(chat.message, chat.submittedAt);
                    appendChat(chat.chatRole, chat.message);
                    appendChat("Bot", chat.response, true);
                });
                checkChatHistoryVisibility();
            })
            .catch(error => {
                console.log("Error fetching chats:", error);
            });
    }

    function appendPreviousQuestions(question, date) {
        const questionDate = new Date(date);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        questionDate.setHours(0, 0, 0, 0);

        const daysDifference = (today.getTime() - questionDate.getTime()) / (1000 * 3600 * 24);
        const categoryLabel = getCategoryLabel(Math.round(daysDifference));

        let categoryElement = document.querySelector(`.category[data-label="${categoryLabel}"]`);
        if (!categoryElement) {
            categoryElement = createCategorySection(categoryLabel);
        }

        const questionItem = document.createElement("li");
        questionItem.innerHTML = `<span class="prev-questions-value">${question}</span>`;
        categoryElement.appendChild(questionItem);

        questionsList.scrollTop = questionsList.scrollHeight;
    }

    function getCategoryLabel(daysDifference) {
        if (daysDifference === 0) return "Today";
        if (daysDifference === 1) return "Yesterday";
        if (daysDifference <= 3) return "Last 3 Days";
        return "Older";
    }

    function createCategorySection(categoryLabel) {
        const categoryHeader = document.createElement("h4");
        categoryHeader.textContent = categoryLabel;

        const categoryElement = document.createElement("ul");
        categoryElement.classList.add("category");
        categoryElement.setAttribute("data-label", categoryLabel);

        questionsList.appendChild(categoryHeader);
        questionsList.appendChild(categoryElement);

        return categoryElement;
    }

    function typeOutMessage(element, message, index = 0, delay = 10) {
        if (index < message.length) {
            element.textContent += message.charAt(index);
            index++;
            setTimeout(() => typeOutMessage(element, message, index, delay), delay);
        } else {
            chatHistory.scrollTop = chatHistory.scrollHeight;
        }
    }

    function checkChatHistoryVisibility() {
        if (chatHistory.children.length === 0) {
            chatHistory.style.display = "none";
        } else {
            chatHistory.style.display = "block";
        }
    }

     function sendMessageToChatBot(role, message) {
         const username = sessionStorage.getItem("chatBotUsername");
         const data = { chatRole: role, message: message };

         return axios.get('/csrf-token')
                     .then(function (response) {
                         const csrfToken = response.data.csrfToken;

                         console.log("---- CSRF-Token: ", csrfToken)

                         const headers = {
                             'X-CSRF-Token': csrfToken,
                             'Content-Type': 'application/json'
                         };

                         return axios.post(`/chat/add?username=${encodeURIComponent(username)}`, data, { headers });
                     })
                     .catch(function (error) {
                         console.error('Error retrieving CSRF token or sending message:', error);
                         throw error;
                     });
     }

     function createBotMessage(botMessage) {
        const messageDiv = document.createElement("div");
        messageDiv.classList.add("chat-message", "bot-response");

        const botAvatar = document.createElement("img");
        botAvatar.src = botLogoUrl;
        botAvatar.alt = "Bot Logo";
        botAvatar.classList.add("avatar");

        const content = document.createElement("p");
        content.classList.add("bot-response-value");

        messageDiv.appendChild(botAvatar);
        messageDiv.appendChild(content);
        chatHistory.appendChild(messageDiv);

        typeOutMessage(content, botMessage);
    }

    sendButton.addEventListener("click", function () {
        const role = roleInput.value.trim();
        const message = messageInput.value.trim();

        if (message === "") return;

        appendChat(role, message);
        messageInput.value = "";

        sendMessageToChatBot(role, message)
            .then(response => {
                const botMessage = response.data.response;
                createBotMessage(botMessage);
                checkChatHistoryVisibility();
                appendPreviousQuestions(message, new Date().toLocaleDateString());
            })
            .catch(error => {
                console.error(error);
                appendChat("Bot", 'Error: Could not fetch response', true);
            });
    });

    document.querySelector(".logout-btn").addEventListener("click", function() {
        sessionStorage.clear();
        window.location.href = "/";
    });

    loadPreviousChats();
});

 (function() {
     const username = sessionStorage.getItem("chatBotUsername");

     if (!username) {
         window.location.href = "/";
     }
 })();
