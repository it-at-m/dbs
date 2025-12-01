<template>
  <div class="form-container">
    <h1>User Information Form</h1>
    
    <form @submit.prevent="saveData" class="user-form">
      <div class="form-group">
        <label for="firstName">First Name:</label>
        <input
          id="firstName"
          v-model="firstName"
          type="text"
          placeholder="Enter your first name"
          class="form-input"
        />
      </div>

      <div class="form-group">
        <label for="lastName">Last Name:</label>
        <input
          id="lastName"
          v-model="lastName"
          type="text"
          placeholder="Enter your last name"
          class="form-input"
        />
      </div>

      <div class="button-group">
        <button type="submit" class="btn btn-primary">Save</button>
        <button type="button" @click="loadData" class="btn btn-secondary">Load</button>
        <button type="button" @click="clearData" class="btn btn-danger">Clear</button>
      </div>
    </form>

    <div v-if="message" class="message" :class="messageType">
      {{ message }}
    </div>

    <div v-if="firstName || lastName" class="display-data">
      <h2>Current Data:</h2>
      <p><strong>First Name:</strong> {{ firstName || '(empty)' }}</p>
      <p><strong>Last Name:</strong> {{ lastName || '(empty)' }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";

const LOCALSTORAGE_KEY_FIRSTNAME = "user.firstName";
const LOCALSTORAGE_KEY_LASTNAME = "user.lastName";

const firstName = ref("");
const lastName = ref("");
const message = ref("");
const messageType = ref("success");

onMounted(() => {
  loadData();
});

function saveData() {
  try {
    localStorage.setItem(LOCALSTORAGE_KEY_FIRSTNAME, firstName.value);
    localStorage.setItem(LOCALSTORAGE_KEY_LASTNAME, lastName.value);
    showMessage("Data saved successfully!", "success");
  } catch (error) {
    showMessage("Error saving data: " + error, "error");
  }
}

function loadData() {
  try {
    const savedFirstName = localStorage.getItem(LOCALSTORAGE_KEY_FIRSTNAME);
    const savedLastName = localStorage.getItem(LOCALSTORAGE_KEY_LASTNAME);
    
    if (savedFirstName !== null) {
      firstName.value = savedFirstName;
    }
    if (savedLastName !== null) {
      lastName.value = savedLastName;
    }
    
    if (savedFirstName || savedLastName) {
      showMessage("Data loaded from localStorage", "success");
    } else {
      showMessage("No saved data found", "info");
    }
  } catch (error) {
    showMessage("Error loading data: " + error, "error");
  }
}

function clearData() {
  try {
    localStorage.removeItem(LOCALSTORAGE_KEY_FIRSTNAME);
    localStorage.removeItem(LOCALSTORAGE_KEY_LASTNAME);
    firstName.value = "";
    lastName.value = "";
    showMessage("Data cleared successfully!", "success");
  } catch (error) {
    showMessage("Error clearing data: " + error, "error");
  }
}

function showMessage(msg: string, type: string) {
  message.value = msg;
  messageType.value = type;
  setTimeout(() => {
    message.value = "";
  }, 3000);
}
</script>

<style scoped>
.form-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
}

h1 {
  color: #333;
  margin-bottom: 2rem;
  text-align: center;
}

h2 {
  color: #555;
  margin-bottom: 1rem;
  font-size: 1.25rem;
}

.user-form {
  background: #f9f9f9;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #333;
}

.form-input {
  width: 100%;
  padding: 0.75rem;
  font-size: 1rem;
  border: 2px solid #ddd;
  border-radius: 4px;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #4CAF50;
}

.button-group {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.btn {
  flex: 1;
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  font-weight: 600;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
}

.btn-primary:hover {
  background-color: #45a049;
}

.btn-secondary {
  background-color: #2196F3;
  color: white;
}

.btn-secondary:hover {
  background-color: #0b7dda;
}

.btn-danger {
  background-color: #f44336;
  color: white;
}

.btn-danger:hover {
  background-color: #da190b;
}

.message {
  margin-top: 1.5rem;
  padding: 1rem;
  border-radius: 4px;
  font-weight: 500;
  text-align: center;
}

.message.success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.message.error {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.message.info {
  background-color: #d1ecf1;
  color: #0c5460;
  border: 1px solid #bee5eb;
}

.display-data {
  margin-top: 2rem;
  padding: 1.5rem;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.display-data p {
  margin: 0.5rem 0;
  color: #555;
}
</style>
