document.addEventListener('DOMContentLoaded', () => {
    // Elements
    const calendarSection = document.getElementById('calendar-section');
    const tasksSection = document.getElementById('tasks-section');
    const notesSection = document.getElementById('notes-section');
    const focusSection = document.getElementById('focus-section');
    const settingsSection = document.getElementById('settings-section');
  
    const navLinks = document.querySelectorAll('.nav-item');
    const loginBtn = document.querySelector('.login-btn');
    
    // Functions
    const showSection = (sectionId) => {
      [calendarSection, tasksSection, notesSection, focusSection, settingsSection].forEach(section => {
        section.style.display = section.id === sectionId ? 'block' : 'none';
      });
    };
  
    const setupTaskManagement = () => {
      const taskInput = document.getElementById('new-task');
      const addTaskBtn = document.getElementById('add-task-btn');
      const taskList = document.getElementById('task-list');
  
      addTaskBtn.addEventListener('click', () => {
        const taskText = taskInput.value.trim();
        if (taskText) {
          const taskItem = document.createElement('li');
          taskItem.textContent = taskText;
          taskList.appendChild(taskItem);
          taskInput.value = '';
        }
      });
    };
  
    const setupNotesManagement = () => {
      const noteArea = document.getElementById('note-area');
      const newNoteBtn = document.getElementById('new-note-btn');
  
      newNoteBtn.addEventListener('click', () => {
        const noteText = noteArea.value.trim();
        if (noteText) {
          alert('Note saved!');
          noteArea.value = ''; // Clear the textarea after saving
        }
      });
    };
  
    const setupFocusTimer = () => {
      const timerDisplay = document.getElementById('timer-display');
      const startTimerBtn = document.getElementById('start-timer');
      const stopTimerBtn = document.getElementById('stop-timer');
  
      let timer;
      let timeLeft = 25 * 60; // 25 minutes in seconds
  
      const updateTimerDisplay = () => {
        const minutes = Math.floor(timeLeft / 60);
        const seconds = timeLeft % 60;
        timerDisplay.textContent = `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
      };
  
      const startTimer = () => {
        if (timer) clearInterval(timer);
        timer = setInterval(() => {
          timeLeft--;
          updateTimerDisplay();
          if (timeLeft <= 0) {
            clearInterval(timer);
            alert('Time is up!');
          }
        }, 1000);
      };
  
      const stopTimer = () => {
        clearInterval(timer);
      };
  
      startTimerBtn.addEventListener('click', startTimer);
      stopTimerBtn.addEventListener('click', stopTimer);
    };
  
    const setupThemeToggle = () => {
      const themeToggleBtn = document.getElementById('theme-toggle');
      let darkMode = false;
  
      themeToggleBtn.addEventListener('click', () => {
        darkMode = !darkMode;
        document.body.classList.toggle('dark-mode', darkMode);
        themeToggleBtn.textContent = darkMode ? 'Switch to Light Mode' : 'Switch to Dark Mode';
      });
    };
  
    // Event Listeners
    navLinks.forEach(link => {
      link.addEventListener('click', () => {
        const sectionId = link.getAttribute('href').replace('.html', '-section');
        showSection(sectionId);
      });
    });
  
    loginBtn.addEventListener('click', () => {
      location.href = 'login.html';
    });
  
    // Initialize
    showSection('calendar-section');
    setupTaskManagement();
    setupNotesManagement();
    setupFocusTimer();
    setupThemeToggle();
  });
  document.addEventListener('DOMContentLoaded', function () {
    const calendarContainer = document.getElementById('calendar');
    const reminderForm = document.getElementById('reminder-form');
    const reminderInput = document.getElementById('reminder-text');
    const reminderList = document.getElementById('reminder-list');
  
    function generateCalendar(year, month) {
      const daysInMonth = new Date(year, month + 1, 0).getDate();
      const firstDay = new Date(year, month, 1).getDay();
      
      let html = '<div class="calendar-header">';
      html += '<button class="prev-month" onclick="changeMonth(-1)">&#9664;</button>';
      html += `<span class="month-year">${new Date(year, month).toLocaleDateString('en-US', { month: 'long', year: 'numeric' })}</span>`;
      html += '<button class="next-month" onclick="changeMonth(1)">&#9654;</button>';
      html += '</div>';
      
      html += '<div class="calendar-days">';
      html += '<div class="day">S</div><div class="day">M</div><div class="day">T</div><div class="day">W</div><div class="day">T</div><div class="day">F</div><div class="day">S</div>';
  
      for (let i = 0; i < firstDay; i++) {
        html += '<div class="date"></div>';
      }
      
      for (let day = 1; day <= daysInMonth; day++) {
        html += `<div class="date">${day}</div>`;
      }
  
      html += '</div>';
      calendarContainer.innerHTML = html;
    }
  
    function changeMonth(offset) {
      currentMonth += offset;
      if (currentMonth < 0) {
        currentMonth = 11;
        currentYear--;
      } else if (currentMonth > 11) {
        currentMonth = 0;
        currentYear++;
      }
      generateCalendar(currentYear, currentMonth);
    }
  
    function addReminder(e) {
      e.preventDefault();
      if (reminderInput.value.trim() === '') return;
  
      const reminderText = reminderInput.value.trim();
      const reminderDiv = document.createElement('div');
      reminderDiv.classList.add('reminder');
      reminderDiv.innerHTML = `<span class="reminder-text">${reminderText}</span>`;
  
      reminderList.appendChild(reminderDiv);
      reminderInput.value = '';
    }
  
    let currentYear = new Date().getFullYear();
    let currentMonth = new Date().getMonth();
    
    generateCalendar(currentYear, currentMonth);
  
    reminderForm.addEventListener('submit', addReminder);
  });
  // Toggle Dark Mode
document.getElementById('theme-toggle').addEventListener('change', function() {
  if (this.checked) {
    document.documentElement.setAttribute('data-theme', 'dark');
    localStorage.setItem('theme', 'dark');
  } else {
    document.documentElement.setAttribute('data-theme', 'light');
    localStorage.setItem('theme', 'light');
  }
});

// Apply the saved theme on page load
document.addEventListener('DOMContentLoaded', function() {
  const savedTheme = localStorage.getItem('theme') || 'light';
  document.documentElement.setAttribute('data-theme', savedTheme);
  document.getElementById('theme-toggle').checked = savedTheme === 'dark';
});
// Function to show a specific tab and hide others
function showTab(tabId) {
  const tabs = document.querySelectorAll('.tab-content');
  tabs.forEach(tab => {
    if (tab.id === tabId) {
      tab.style.display = 'block';
    } else {
      tab.style.display = 'none';
    }
  });
}

// Function to show a specific tab and hide others
function showTab(tabId) {
  const tabs = document.querySelectorAll('.tab-content');
  tabs.forEach(tab => {
    if (tab.id === tabId) {
      tab.style.display = 'block';
    } else {
      tab.style.display = 'none';
    }
  });
}

// Function to show a specific tab and hide others
function showTab(tabId) {
  const tabs = document.querySelectorAll('.tab-content');
  tabs.forEach(tab => {
    if (tab.id === tabId) {
      tab.style.display = 'block';
    } else {
      tab.style.display = 'none';
    }
  });
}

// Function to show a specific tab and hide others
function showTab(tabId) {
  const tabs = document.querySelectorAll('.tab-content');
  tabs.forEach(tab => {
    if (tab.id === tabId) {
      tab.style.display = 'block';
    } else {
      tab.style.display = 'none';
    }
  });
}




