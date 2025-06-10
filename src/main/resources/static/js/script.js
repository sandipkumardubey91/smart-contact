document.addEventListener("DOMContentLoaded", () => {
  const navLinks = document.querySelectorAll(".nav-link");
  const sideLinks = document.querySelectorAll(".side-link");
  const currentPath = window.location.pathname;

  console.log("Current Path:", currentPath);

  navLinks.forEach((link) => {
    const linkPath = link.getAttribute("href");
    console.log("Link Path:", linkPath);
    if (!linkPath) return;

    if (currentPath === linkPath) {
      console.log("Match found for:", linkPath);
      link.classList.remove("dark:text-white", "text-gray-900");
      link.classList.add(
        "text-blue-700",
        "dark:text-blue-500",
        "font-semibold"
      );
    } else {
      link.classList.remove("text-blue-700", "dark:text-blue-500");
      if (!link.classList.contains("dark:text-white")) {
        link.classList.add("dark:text-white");
      }
      if (!link.classList.contains("text-gray-900")) {
        link.classList.add("text-gray-900");
      }
      console.log("No match for:", linkPath);
    }
  });

  sideLinks.forEach((link) => {
    const linkPath = link.getAttribute("href");
    console.log("Link Path:", linkPath);
    if (!linkPath) return;

    if (currentPath === linkPath) {
      console.log("Match found for:", linkPath);
      link.classList.remove("dark:text-white", "text-gray-900");
      link.classList.add(
        "text-blue-700",
        "dark:text-blue-500",
        "dark:bg-gray-700",
        "bg-white"
      );
    } else {
      link.classList.remove(
        "text-blue-700",
        "dark:text-blue-500",
        "dark:bg-gray-700",
        "bg-white"
      );
      if (!link.classList.contains("dark:text-white")) {
        link.classList.add("text-white");
      }
      if (!link.classList.contains("text-gray-900")) {
        link.classList.add("text-gray-900");
      }
      console.log("No match for:", linkPath);
    }
  });

  const changethemeBtn = document.querySelector("#theme_change_btn");
  if (!changethemeBtn) return;

  let currentTheme = getTheme();
  applyTheme(currentTheme); // Set on page load

  changethemeBtn.addEventListener("click", () => {
    const newTheme = currentTheme === "dark" ? "light" : "dark";

    // Add fade animation class to <body>
    document.body.classList.add("animate-fadeTheme");

    setTimeout(() => {
      document.documentElement.classList.remove("light", "dark");
      document.documentElement.classList.add(newTheme);

      setTheme(newTheme);
      currentTheme = newTheme;

      document.body.classList.remove("animate-fadeTheme");
      changethemeBtn.blur();
    }, 200); // Half of animation duration (smooth transition)
  });
});

function setTheme(theme) {
  localStorage.setItem("theme", theme);
}

function getTheme() {
  return localStorage.getItem("theme") || "light";
}

function applyTheme(theme) {
  document.documentElement.classList.remove("light", "dark");
  document.documentElement.classList.add(theme);
}

// const toggleBtn = document.getElementById("sidebarToggleBtn");
// const navbar = document.getElementById("usernavbar");

// const openPadding = navbar.dataset.paddingOpen;
// const closedPadding = navbar.dataset.paddingClosed;

// let isSidebarOpen = false;

// toggleBtn.addEventListener("click", () => {
//   isSidebarOpen = !isSidebarOpen;

//   if (isSidebarOpen) {
//     navbar.classList.remove(closedPadding);
//     navbar.classList.add(openPadding);
//   } else {
//     navbar.classList.remove(openPadding);
//     navbar.classList.add(closedPadding);
//   }
// });
