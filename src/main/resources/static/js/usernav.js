const buttons = document.getElementsByName("removePadding");
const profbtn = document.getElementById("prof-logo");

console.log("user-nav");

// Loop through all buttons and assign the same click handler
for (let btn of buttons) {
  btn.addEventListener("click", function () {
    const nav = document.getElementById("u-nav");

    if (nav) {
      nav.classList.remove("my-custom-pl-64");
      if (!nav.classList.contains("my-custom-pl-16")) {
        nav.classList.add("my-custom-pl-16");
      }
    }
  });
}

profbtn.addEventListener("click", function () {
  const nav = document.getElementById("u-nav");

  console.log("Hello");

  if (nav) {
    nav.classList.remove("my-custom-pl-16");
    if (!nav.classList.contains("my-custom-pl-64")) {
      nav.classList.add("my-custom-pl-64");
    }
  }
});
