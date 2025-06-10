const sidebar = document.getElementById("logo-sidebar");
const toggleBtn = document.getElementById("sidebarToggle");
const toggleIcon = document.getElementById("toggleIcon");

toggleBtn.addEventListener("click", () => {
  sidebar.classList.toggle("-translate-x-full");

  // Toggle the arrow direction
  toggleIcon.classList.toggle("rotate-180");
});
