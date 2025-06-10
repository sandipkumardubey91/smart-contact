function handleLogout(e) {
  e.preventDefault(); // Prevent the default behavior of the anchor tag (navigation)

  Swal.fire({
    title: "Are you sure you want to log out?",
    text: "You will be logged out from your session.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Yes",
    cancelButtonText: "No",
    reverseButtons: false, // Reverse button positions
    customClass: {
      popup:
        "bg-gray-200 shadow-lg dark:bg-gray-700 dark:text-gray-100 pb-10 rounded-lg",
      actions: "flex gap-4 justify-center mt-6", // added top margin here
      confirmButton:
        "bg-red-700 hover:bg-red-800 shadow-none dark:bg-red-500 dark:hover:bg-red-600 text-white py-3 px-8 rounded-full",
      cancelButton:
        "bg-green-700 hover:bg-green-800 dark:bg-green-500 dark:hover:bg-green-600 text-white py-3 px-8 rounded-full",
    },
  }).then((result) => {
    if (result.isConfirmed) {
      // Execute the logout logic (for example, redirect to logout endpoint)
      // const el = document.getElementById("u-nav");
      // nav.classList.remove("my-custom-pl-16");
      // if (!el.className.includes("my-custom-pl-64")) {
      //   el.className += " my-custom-pl-64";
      // }
      window.location.href = "/do-logout"; // Replace with your actual logout URL or logic
    } else {
      Swal.fire({
        title: "Cancelled",
        text: "You are still logged in",
        icon: "info",
        showConfirmButton: false, // Hide the confirm button
        timer: 1000, // Set the timer for 1 second (1000 milliseconds)
        timerProgressBar: true, // Show progress bar (optional)
        customClass: {
          popup:
            "bg-gray-200 shadow-lg dark:bg-gray-700 text-green-600 dark:text-gray-200 pb-10 rounded-lg",
          actions: "flex gap-4 justify-center mt-6", // added top margin here
        },
      });
    }
  });
}

// Select all elements with the class 'logout-btn'
const logoutButtons = document.querySelectorAll(".logout-btn");

// Add event listener to each logout button
logoutButtons.forEach((button) => {
  button.addEventListener("click", handleLogout);
});
