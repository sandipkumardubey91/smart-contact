document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("todo-form");
  const input = document.getElementById("todo-input");
  const todoList = document.getElementById("todo-list");

  // ADD TODO via AJAX
  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const taskText = input.value.trim();
    if (taskText === "") return;

    try {
      const response = await fetch("/user/dashboard/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ task: taskText }),
      });

      if (response.ok) {
        const data = await response.json();
        updateUpcomingReminders(data.upcomingReminders);

        const li = createTodoElement(
          data.task,
          data.createdAtFormatted,
          data.id
        );
        todoList.appendChild(li);
        input.value = "";
      } else {
        console.error("Failed to add todo");
      }
    } catch (error) {
      console.error("Error:", error);
    }
  });

  // DELETE TODO via AJAX
  todoList.addEventListener("submit", async (e) => {
    if (e.target.matches("form")) {
      e.preventDefault();
      const form = e.target;
      const li = form.closest("li");

      try {
        const response = await fetch(form.action, {
          method: "DELETE",
        });

        if (response.ok) {
          const data = await response.json();
          li.remove();
          updateUpcomingReminders(data.upcomingReminders);
        } else {
          console.error("Failed to delete todo");
        }
      } catch (error) {
        console.error("Error:", error);
      }
    }
  });

  // Helper to create a todo list item element
  function createTodoElement(task, createdAtFormatted, id) {
    const li = document.createElement("li");
    li.className =
      "text-sm bg-gray-100 dark:bg-gray-700 p-2 rounded flex justify-between items-center";

    const taskSpan = document.createElement("span");
    taskSpan.innerHTML = `📌 <span>${task}</span> <small class="text-gray-400 ml-2">${
      createdAtFormatted || ""
    }</small>`;
    li.appendChild(taskSpan);

    const deleteForm = document.createElement("form");
    deleteForm.action = `/user/dashboard/delete/${id}`;
    deleteForm.method = "post";
    deleteForm.style.display = "inline";

    const deleteBtn = document.createElement("button");
    deleteBtn.type = "submit";
    deleteBtn.className = "text-red-500 hover:text-red-700";
    deleteBtn.innerText = "✕";

    deleteForm.appendChild(deleteBtn);
    li.appendChild(deleteForm);

    return li;
  }

  // Helper to update reminders count
  function updateUpcomingReminders(count) {
    const remindersCount = document.getElementById("upcoming-reminders-count");
    if (remindersCount && count !== undefined) {
      remindersCount.textContent = count;
    }
  }
});
