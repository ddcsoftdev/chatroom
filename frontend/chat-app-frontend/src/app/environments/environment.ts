const API_URL = "http://localhost:8080/api/v1/";

export const environment = {
    production: false,
    apiUrl: API_URL,
    loginUrl: `${API_URL}auth/login`,
    conversationUrl: `${API_URL}conversation`,
    messageUrl: `${API_URL}message`
  };